package dev.kaepsis.cachedeconomy.services;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import dev.kaepsis.cachedeconomy.Main;
import dev.kaepsis.cachedeconomy.config.GeneralConfig;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DatabaseService {

    private static final int BATCH_SIZE = 100;
    private static final int CACHE_SYNC_INTERVAL = 5;
    private static DatabaseService instance = null;
    private final Map<String, PreparedStatement> preparedStatements = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private HikariDataSource connectionPool;

    private DatabaseService() {
        setupConnectionPool();
        createDefaultTable();
        setupCacheSyncScheduler();
    }

    public static DatabaseService getInstance() {
        if (instance == null) {
            instance = new DatabaseService();
        }
        return instance;
    }

    private void setupConnectionPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(String.format("jdbc:mysql://%s:%d/%s?characterEncoding=latin1&useSSL=false",
                GeneralConfig.getInstance().host,
                GeneralConfig.getInstance().port,
                GeneralConfig.getInstance().database));
        config.setUsername(GeneralConfig.getInstance().username);
        config.setPassword(GeneralConfig.getInstance().password);
        config.setMaximumPoolSize(10);
        config.setMinimumIdle(5);
        config.setIdleTimeout(300000);
        config.setConnectionTimeout(10000);
        config.setAutoCommit(true);

        try {
            connectionPool = new HikariDataSource(config);
            Main.logger.info("Database connection pool established");
        } catch (Exception e) {
            Main.logger.severe("Failed to initialize connection pool: " + e.getMessage());
        }
    }

    private void setupCacheSyncScheduler() {
        scheduler.scheduleAtFixedRate(this::syncCache,
                CACHE_SYNC_INTERVAL, CACHE_SYNC_INTERVAL, TimeUnit.MINUTES);
    }

    public Connection getConnection() throws SQLException {
        return connectionPool.getConnection();
    }

    private void createDefaultTable() {

        String query = """
                CREATE TABLE IF NOT EXISTS players (
                    id INT NOT NULL PRIMARY KEY AUTO_INCREMENT,
                    name VARCHAR(17),
                    uuid VARCHAR(36),
                    balance DOUBLE(10, 2),
                    INDEX idx_name (name),
                    INDEX idx_balance (balance)
                );
                """;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.execute();
        } catch (SQLException e) {
            Main.logger.severe("Cannot create default table: " + e.getMessage());
        }
    }

    public void cachePlayers() {
        String query = "SELECT name, balance FROM players";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            Main.savedPlayers.clear();
            while (rs.next()) {
                Main.savedPlayers.put(
                        rs.getString("name"),
                        rs.getDouble("balance")
                );
            }
        } catch (SQLException e) {
            Main.logger.severe("Error while loading players: " + e.getMessage());
        }
    }

    public void syncCache() {
        try (Connection conn = getConnection()) {
            conn.setAutoCommit(false);
            String query = "UPDATE players SET balance = ? WHERE name = ?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                int count = 0;

                for (Map.Entry<String, Double> entry : Main.savedPlayers.entrySet()) {
                    stmt.setDouble(1, entry.getValue());
                    stmt.setString(2, entry.getKey());
                    stmt.addBatch();

                    if (++count % BATCH_SIZE == 0) {
                        stmt.executeBatch();
                    }
                }

                if (count % BATCH_SIZE != 0) {
                    stmt.executeBatch();
                }

                conn.commit();
            }
        } catch (SQLException e) {
            Main.logger.severe("Error while syncing cache: " + e.getMessage());
        }
    }

    public void shutdown() {
        scheduler.shutdown();
        syncCache();
        if (connectionPool != null && !connectionPool.isClosed()) {
            connectionPool.close();
        }
    }
}
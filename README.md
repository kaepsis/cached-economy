# CachedEconomy

A lightweight Minecraft plugin that hooks into the default [Vault](https://www.spigotmc.org/resources/vault.34315/) economy system and **caches player balances** to significantly improve performance.

Compatible with **Spigot / Paper** on **1.21+**.

![img](https://img.shields.io/github/v/release/kaepsis/cached-economy) ![img](https://img.shields.io/github/downloads/kaepsis/cached-economy/total)

---

## Features

- Caches all player balances for faster access
- Supports core Vault economy commands
- Optimized for servers with a large number of players
- PlaceholderAPI support

---

## Installation

- Download the latest `.jar` file from the [Releases](https://github.com/kaepsis/cached-economy/releases) page
- Drop it into your server's `/plugins/` folder
- Make sure to have **PlaceholderAPI** and **Vault** installed
- Restart the server
- Modify the configuration files as needed

---

## Commands & Permissions

```text
/balance [player]            - cachedeconomy.balance.others (for viewing others)
/eco <give/set/reset> <name> - cachedeconomy.eco
/pay <player> <amount>       - uses Vault's standard permissions
/ace [reload]                - cachedeconomy.admin
```

---

## Placeholders

CachedEconomy's placeholders are registered internally.
```text
%cachedeconomy_balance% - returns the cached balance amount
```

## Contribuiting

Suggestions, bug reports or contribuitions are welcome!
Open an issue or submit a pull request.

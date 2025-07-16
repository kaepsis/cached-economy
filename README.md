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
/pay <player> <amount>       - no permission needed
/ace [reload]                - cachedeconomy.admin
/baltop                      - no permission needed
```

---

## Placeholders

CachedEconomy's placeholders are registered internally.
```text
%cachedeconomy_balance%                             - returns the cached balance amount
%cachedeconomy_balance_formatted%                   - returns the cached balance amount but **formatted**
%cachedeconomy_baltop_<1 to 10>_name%               - returns the player name in baltop at position (from 1 to 10)
%cachedeconomy_baltop_<1 to 10>_value%              - returns the amount of the player in baltop at position (from 1 to 10)
%cachedeconomy_baltop_<1 to 10>_value_formatted%    - returns the formatted amount of the player in baltop at position (from 1 to 10)
```

---

## Contribuiting

Suggestions, bug reports or contribuitions are welcome!
Open an issue or submit a pull request.

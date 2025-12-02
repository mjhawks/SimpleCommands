# SimpleCommands – Moderation Automation Plugin for Minecraft (Paper 1.21.9+)

SimpleCommands is a lightweight, highly streamlined moderation plugin designed to automate and standardize punishment actions for Minecraft servers. Moderators can issue consistent, data-backed punishments with a single command, while the plugin tracks player behavior over time using a custom ELO-style system stored in SQLite.

---

## ⭐ Features

### 🔧 Streamlined Moderation Command
Use one unified command to handle all punishments:

```
/punish <player> <reason>
```

Supported reasons:

- `xray`
- `abusivecoms`
- `massgrief`
- `theft`
- `screentime`

or use the `custom` reason to use a specific elo value
```
/punish <player> custom <elo_value>
```
Each reason has a configurable penalty in the generated config.yml file.

---

### 📊 Player ELO Behavior System
The plugin includes a built-in JDBC SQLite database that tracks:

- Player UUID
- Current ELO score
- Each prior punishment
- Playtime at last update, used to calculate elo degredation.

Server owners can configure:

- How many ELO points each violation inflicts
- How quickly ELO decays with playtime
- The ELO ban threshold that results in an automatic ban

---

### 🕒 Automatic ELO Degradation
Players naturally recover over time — ELO slowly decays based on actual playtime, allowing players who behave well to gradually earn forgiveness within the system.

---

### 🔨 Automated Punishment Actions
Depending on severity and configuration, the plugin can automatically apply:

`xray` `theft` `abusivecoms` `massgrief`
 - 30 day temporary ban
 - inventory and enderchest wipe
 - configurable elo infraction

`screentime`

this command is intended for content creators to deal with players crowding them
- Teleports player to spawn
- sends warning message to player
- configurable elo infraction





This ensures moderators remain consistent and reduces the chance of human error.

---

## 🗂 Database Structure

The plugin automatically creates and manages a local SQLite database containing:

| Column                 | Description                                  |
|------------------------|----------------------------------------------|
| `uuid`                | Player UUID (primary key)                    |
| `scorefactor`         | Current ELO score                             |
| `playtimewhenupdated` | Player playtime at last update                |
| `reason`              | Most recent punishment reason                 |

All reads and writes use prepared statements for safety and performance.

---

## ❤️ Why Use SimpleCommands?

- One command instead of many manual steps
- Ensures fair and consistent moderation
- Tracks player behavior over time
- Automatic decay rewards good behavior
- Lightweight and database-backed
- No external dependencies

---

## ⚙️ Installation

1. Download the plugin `.jar`
2. Place it in your server’s `/plugins` folder
3. Start the server to generate config + database
4. Adjust values in `config.yml` as desired
5. Start moderating with:

```
/punish <player> <reason>
```

---

## 📝 Configuration Example

```yaml
# Penalty points for each reason
xrayPen: 25
abusivecomsPen: 50
massgriefPen: 40
theftPen: 15
screentimePen: 5
customDefaultPen: 10

# Ban threshold – player is banned once their ELO exceeds this
banThreshold: 100

# ELO decay settings (example)
eloDecayMinutesPerPoint: 60
```

---

## ⏳ Planned Features

- more extensive custom punishment generator
- more customization settings in config
- coreprotect rollback integration

---

## 💬 Support & Contributing

Feedback, feature requests, and pull requests are always welcome.  
If you find a bug or want a new feature, open an issue on GitHub.

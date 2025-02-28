# Bridger Sumo (v3.0.1) - Primary

## Overview
Bridger Sumo was originally commissioned for a Minecraft network. This plugin brings both the traditional Sumo Duel and the Block Sumo minigame into a single plugin. Block Sumo is a game mode for 2-16 players, where players spawn on a medium-sized void map with five lives. Players lose a life each time they fall, and the last player remaining wins. Additionally, a gold block at the center of the platform provides an alternative win condition—if a player stands on it for 20 uninterrupted seconds, they win the game.

## The Golden Block
The golden block is the central feature of Block Sumo. It serves as both a tactical and strategic focal point:
- If a player remains on the golden block for **20 uninterrupted seconds**, they instantly win the game.
- The block is surrounded by a **ring of flames**, preventing players from placing permanent blocks nearby.
- Blocks placed within the golden block's radius will slowly self-destruct, creating a **smooth animated effect**.
- A **Major Event item** spawns on top of the golden block every 320 seconds, providing powerful enhancements to the first player who claims it.

### Golden Block Flame Visualization
Below is a mathematical representation [Reference](https://help.desmos.com/hc/en-us/articles/4406895312781-Polar-Graphing) of the flame ring surrounding the golden block, generated using a polar equation:

![Equation Representation](https://help.desmos.com/hc/article_attachments/28782670480141)

### Animated Golden Block Flames

![Flame Animation](https://images-ext-1.discordapp.net/external/IbufZAr9jfoDIaTPkI5-O47pBpYIJz78mJB7sJxgWWc/https/i.imgur.com/LwnzhiI.mp4)

## Game Features
### Events
Bridger Sumo includes both Major and Minor Events:
- **Minor Events**: Occur every 120 seconds (configurable) and give all players a random powerful item, such as a one-hit knockback sword.
- **Major Events**: Occur every 320 seconds (configurable) and spawn a powerful item at the center gold block, available to the first player to pick it up. Examples include a Knockback X sword.
- Players cannot place permanent blocks within the golden block radius, as blocks placed there slowly self-destruct, creating an animated effect.

### Statistics Tracking
The plugin tracks and stores the following statistics per game mode in a MySQL database:
- Wins
- Kills
- Deaths
- Win Streak
- Games Played

### Damage Management
Block Sumo allows multiple players to hit each other, making kill tracking more complex. The plugin utilizes a **Damage Manager**:
- A `LastDamageInfo` object stores the last damager and timestamp.
- If the same player continues dealing damage, only the timestamp is updated, improving performance.
- If a player runs away and is no longer in combat, their death is recorded as a void death rather than a player kill.

### Map Creation
Map creation is simple and user-friendly:
- Enter setup mode and follow on-screen instructions.
- Use hotbar items such as 'Add a spawn-point,' 'Set waiting lobby,' and 'Finish setup.'
- Setup cannot be completed unless all required spawn points are set.

## Configuration
The plugin offers extensive configurability via `config.yml`:
```yml
MySQL:
  host: localhost
  port: 3306
  user: root
  password: ''
  database: s24_BridgerSumo

socket_address: 0.0.0.0
bungee: true

normal_void_height: 1
block_sumo_void_height: 1

block_sumo_max_distance_from_center: 20
max_place_height: 12
game_chat_format: "&7[GAME] %luckperms_prefix%%player_name%&f: %message%"

minor_events_schedule: 120
major_events_schedule: 320
start_major_particles_animation_before: 10

fireball:
  explosion_size: 3
  speed_multiplier: 10
  make_fire: false
  horizontal: 1.0
  vertical: 0.65

tnt:
  bary_center_alternation_in_y: 0.5
  strength_reduction_constant: 3
  y_axis_reduction_constant: 2

scoreboards:
  normal_sumo:
    title: ""
    lines:
      - ""
  block_sumo:
    title: ""
    players_format: "%player_color%%player_name%: %player_lives%"
    lines:
      - ""

lobby_command: 'lobby'
version: 1.1
```

### PlaceholderAPI Support
The plugin integrates with PlaceholderAPI and supports the following placeholders:
- `%bridgersumo_team_color%`
- `%bridgersumo_team_color_letter%`

## Messages System
The messages file is initially empty but is automatically populated with default messages on the first plugin startup. Messages are managed in the `Messages.java` enum class.

## Game Data Management
After a map is created, game settings are saved in `games.yml`, storing details such as:
- Game type
- Display name
- Maximum players
- Spawn points

Spawning and respawning are randomized, ensuring no two players spawn in the same location.

## Conclusion
Bridger Sumo is structured with clean and efficient code. While there is always room for improvement, it is designed to provide an optimal gameplay experience for players and server administrators alike.

## Media

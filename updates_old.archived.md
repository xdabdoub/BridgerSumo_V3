1. # Bridger Sumo (42% Done)
## Done
- Base structure of both of the games is done, from giving kits, death, win, quite, and start logics.
- Game Listener is done as well, that includes, players falling into the void, setting the damage to 0 when they're damaged, if they quit the server while in a game, and block placing, block breaking only the blocks that were placed by players.
- Messages system is done as well, all messages are configurated.
- SQL Database system is done, creating the tables for each game, and saving the stats as follows: (UUID, KILLS, DEATHS, WINS, WIN_STREAK, GAMES_PLAYED)
- Games management is done
- Players management is done
- And a lot more minor stuff

## To-do
- Commands
- GUIs
- Saving the games in a file
- Loading the games from a file
- Changes to the games, such as, random color wool when placing a block in the BlockSumo, standing ontop of the golden block for 20 seconds wins you the game etc..
- Titles, waiting lobby for the games, and sending death, kill, etc. messages
- Cosmetics
- Lastly, saving the player's statistics in the DB

# How will the data be saved/loaded?
- Well, for this, I am going to be loading the player's data pre-login (like how LuckPerms does). This event is ran async.
- Now for saving the data, normally, the data is saved once the player quits, which helps reduce the amount of queries to the SQL resulting in better performance. And there will be an option in the config to enable/disable scheduled data saving with a configured time.

Find out more: https://github.com/xdabdoub/BridgerSumo

*Last edited: 2024/1/10 @ 5:16 PM GMT+2*

-------

2. # How to setup a game (Video Included)
- Command: /bridgersumo create <gameType:BLOCK_SUMO,NORMAL> <displayName> <maxPlayers> (Max players for normal sumo is always 2 regardless of what is entered as a parameter)

## Steps
- Run the command above
- Place the armor stand at certain positions which will be used as spawn points for players
- Place the redstone to set the waiting lobby
- Place the bed to finish the game setup

*Simple as that*

### More Info
- When a player dies, a random spawn point will be chosen for them to spawn at
- The games are saved in a file called 'games.yml' (One file for all games because not a lot of data is saved for each game)
- The armor stands are there to better inform you on where the spawn point was exactly set at, therefore, they dispawn after 10 seconds of their spawning.

***More videos will be sent later***

Find out more: https://github.com/xdabdoub/BridgerSumo

3. # 2024/11/1 - Saturday - Progress (57%)
## Done
- Creating a game
- Setting up a game
- Saving a game's data in the files
- Loading games from the 'games.yml' file
- Creation of the messages.yml file
- Updating game state (SETUP, WAITING, PLAYING, ENDING, ENDED)
- Commands (Join, Leave, Create, SetLobby, and CancelSetup)
- Data Handling is done, creating a 'DabPlayer' object on async pre login which holds all of the player's data, and statistics
- Some messages, such as join messages, leave messages, game not found, fell into the void, pushed into the void, game cancelled, etc. (Not a priority)
- Game Startup Runnable -> The game countdown, and cancellation if a player leaves etc.
- Fixed some bugs that were found while testing

## To-do
- GUIS
- Game QOL, from random wool color, golden block thingy etc.
- Titles and all messages (Not a priority)
- Cosmetics
- Saving players's data in the DB
- Finding bugs and fixing them
- Game Events
- And a few other minor things

Find out more: https://github.com/xdabdoub/BridgerSumo
Last edited: 2024/1/11 @ 6:55 PM GMT+2

4. Couldn't provide updates the past 2 days, but here's what has been done in those 2 days:

## 2024/12/01 & 2024/13/01 Progress (66%)
- Scoreboard System (CONFIGURABLE)
- Random Color Wool when a wool block is placed
- Introduced a lockdown system, it's purpose is, when you want to restart the server, you start the lockdown, which will disable matchmaking, preventing players from playing games. It will not stop current active games however.
- Fixed multiple bugs, such as announcing the winner multiple times, game breaking bugs etc.
- Remove all blocks placed by players after the game has ended
- Started the implementation of the player's statistics. Each player will have two objects of type Statistics one for the block sumo stats, and one for the normal sumo stats, in these instances, the data is saved. (WINS, KILLS, DEATHS, WINSTREAK, and GAMES_PLAYED)
- And some other minor things

Find out more: https://github.com/xdabdoub/BridgerSumo

4. # 2024/16/01 Progress (73%)
- Tested the game, works like a charm.
- Implemented some data loading features, such as, kicking the player from the server if the player's data loaded incorrectly or didn't load at all
- Fully implemented the lockdown system
- Fixed multiple bugs here and there while testing
- Leaving the game using /bridgersumo leave, or quitting the server will not break and the game will end (depends)
- Scoreboards are functional now
- Started the implementation of Team Colors (Blue, Red, etc)
- Added a feature to the messages system that adds the new messages to the messages.yml file automatically without having to put it there manually. Meaning that, if I was to add a message to the plugin, it'll automatically be added to the messages.yml file and you don't have to put it there yourself.
- Same with the config ^
- Added a command to show you information on all of the games
- And a lot more...

Find out more: https://github.com/xdabdoub/BridgerSumo

5. # 2024/17/01 Progress (82%)
- Sorted the players on the scoreboard based on their lives
- Made the shears unbreakable
- Added titles and sounds
- Implemented a disable logic for the plugin to prevent issues occurring
- Fixed a bug where spectators can break blocks, and pvp other players
- Started the implementation of the gold block
- Fixed a bug where players were able to join games in the state PLAYING
- Fully finished the normal sumo mode. Implemented the death, win, quit, and start logics
- Added papi support to the normal sumo's scoreboard for the opponent player
- Added a new placeholder to the scoreboards ("%map_name%) which returns the display name of the game (specified once created)
- Re-done the setup system, because as you've seen in the video, the locations were not accurate. Therefore, I made it so you right click air to set your location as a spawnpoint or the waiting lobby instead of placing the armorstand/redstone
- Introduced '`build_height`' option in the config to set the build limit from the first spawn point set. (Similar system to the void height)
  - Further explanation:  Both the build_height and the void_height do not represent a Y level which the player should pass to be teleported back, however, it indicates how many blocks BELOW the first SPAWN_POINT the player should be teleported back, meaning that you can have maps on a different Y level and all work the same way with no 'fixed' Y level.
- Fixed a bug with the scoreboard where the scoreboard title wasn't showing
- Fixed a few bugs with the game state. For example, players weren't being teleported correctly to the main lobby when finishing the game
- Fixed a bug with the random colored wool, going over the 64 stack limit in the player's inventory
- And a lot more

Find out more: https://github.com/xdabdoub/BridgerSumo
- Commit: https://github.com/xdabdoub/BridgerSumo/commit/6adec46e9568c1a9e35d6308c1c65125ef3859bc

6. # 2024/18/01 Progress (88%)
- Added sounds to dying, and winning
- Added titles to winning, and respawning (REPAWNING IN etc)
- Implemented a combat system which is used to track last damager, and who pushed who into the void, etc.
- Fully implemented the statistics system
- Overrid the equals and hashCode methods in the DabPlayer class to better data saving and memory handling
- Added a spectator lobby which is the waiting lobby but 5 blocks higher to spawn spectators at instead of the waiting lobby
- Added the gametype to the view games details command
- Removed the timer in the chat
- Replaced the CLICK sound with the NOTE_PLING sound for the timer
- Added a 'GAME_STARTED' title
- And fix a few bugs that I found while testing (not a lot of bugs! and most of them were just incorrect timing of title sending etc. (like teleporting then sending the title))

7. # 2024/19/01 Progress (93%)
- Fully implemented the gold block win feature
- It works as follows, the player stands on the gold block, a timer starts counting (players can view the timer in their xp bar), then every 5 seconds, a message will be announced (configureable: "xDabDoub has been on the centre block for 15 seconds!").
- Players can place blocks around the gold block, however, there's a block break animation that lasts for a few seconds then the block disappears.
- Added a gold_block field in the games.yml to block sumo games which contains the location of the gold_block
- Added 'gold_ingot' to the setup of block_Sumo games to use to set the location of the gold_block
- And some few minor touches

Find out more: https://github.com/xdabdoub/BridgerSumo
- Commit: https://github.com/xdabdoub/BridgerSumo/commit/a14f80ffabc87d3102650e99c4c677511cb0d5f9

8. ## Added team colors to block sumo:
- 12 Colors
- Colored Leather Armor
- Color Letters (e.g Red: R, Blue: B, Dark Blue: DB, etc)
- Can use the color in nametags, scoreboard, chat using placeholders

- Note: Since there are only 12 colors, if you were to make an event (a game with a lot of players) or whatever, players who aren't put in a Team Color will be randomly put it one of the 12 colors. So expect 2+ players in some colors. They can still hit each other, they only have the same color

9. - Added a seperate chat for each game
- Hid players from other games. (game 2 players cannot see game 1 players on tab)
- Hid players completely and not only giving them invis when they die. (so they dont block other players from playing or placing blocks)
- Started  the implementation of the events
- Added max place height (its similar to the void height system. the value of this field is related to the spawn points of the game. so by default the max place height is 12, meaning that players can place only 12 blocks high above the spawnpoints Y axis not a fixed Y level.)

10. Okay so, here's what's been done today;

# 2024/06/02 Update
- Added MINOR and MAJOR events. The minor events are items that are added to the player's inventory, such as tnts, fireballs, snowballs, etc. As for the major events, those are also items, however, these items have special abilities, and those items only spawn on the gold block. (e.g: kb swords, etc.)
Furthermore, these events are scheduled to run every (x) seconds *configurable*.
- In order to create an item to be added to either the MINOR events or the MAJOR events, you must use the following command; /bridgersumo addeventitem <eventType> <displayName>. Please note that the item you're holding is what the players will get, including all of its meta data. Additionally, for the minor events, the items are given normally with no special effects (excluding the tnt, and fireballs), however, when you want to add a major event item, its ability must be coded, therefore, you must contact me so I can implement its ability in the code.
- The displayName for the event items, are used to set cool names or whatever for this item. For example, in bedwars practice the TNT event announcement is 'All players received &ctnt powerup', same goes for the fireball 'All players received &6fireball powerup' and so on, you can customize the display names for each item and also configure and change the message through the messages.yml file.
- Tested the max build height, and it works like a charm
- Stopped TNT and Fireballs explosions from breaking blocks
- TNTs auto ignite when placed
- Fireballs shoot when interacted with
- Handled damage using fireballs, and snowballs, meaning that, if a player pushes a player into the void using a snowball or a fireball, it'll say the pusher's name rather than saying 'fell into the void'
- Event Items are randomly chosen
- Added lives colors to the scoreboard, with the states: HIGH, MEDIUM, and LOW, while HIGH being 4+ lives, medium between 2 and 3, and low being lower than 2.

11. # ~~To-do~~ Done
- Track the player who placed a TNT so the player who placed that TNT gets a kill added to their stat, rather than handling it as 'fell into the void'
- TNT/Fireball KB
- Do some more testing (never enough)

12. - Added borders to games (`block_sumo_max_distance_from_center: 30 # This means that the player can travel a max of 20 blocks from the center (gold block, and this goes for all 4 Axies. You could think of it as a square) and that they cannot place blocks outside that region`)
- Disallowed placement ontop of spawnpoints
- Reduced the join game delay to 2 ticks instead of 3 and it now is perfect, hardly noticeable or even not noticeable at all
- Fixed a bug where if a player hit themselves with a tnt or a fireball then fell into the void, it would say that 'xDabDoub was pushed into the void by xDabDoub' to 'xDabDoub fell into the void'

13. - Made the particle effect configurable under the major_events section.
- Fixed fireballs/tnts not breaking wool
- Fixed players keeping their armor after the game ends
- Fixed fireballs causing fires and breaking the map
- Started working on the Bungeecord /join command (it’s almost done, and will change the command from /join to whatever you want (so lmk))
- In the bungee cord plugin I disabled the access of the command through the sumo server

14. - Stopped spectators from getting event items and using them such as TNTs and Fireballs
- Disabled picking up items as spectators
- Added explosion sound when the major event item spawns (The sound plays near the gold block and not for every players, so only players who are near the gold block can hear that sound, if you want me to change it to be played for all players, let me know.)
- Added explosion particles when the major event item spawns
- Added duration to the flame particle animation. Meaning that you could adjust the duration of the flame particle animation that play prior to the spawning of the item
- Fixed some bugs that I found while testing yesterday
- Finished the BungeeCord plugin. /bs is the command
- Regarding the previous update, do you still need the Velocity server? If not, can you make it bungeecord so we can test the BungeeCord plugin

### Bugs to fix
- Scoreboard sorting is incorrect when a player redeems the additional life major event item

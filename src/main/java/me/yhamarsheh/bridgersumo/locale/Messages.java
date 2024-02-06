package me.yhamarsheh.bridgersumo.locale;

import me.yhamarsheh.bridgersumo.utilities.ChatUtils;

public enum Messages {

    DUMMY("dummy", ""),
    THROUGH_CONSOLE("through_console", "&cThis command is not accessible through console."),
    NOT_ENOUGH_ARGUMENTS("not_enough_arguments", "&cNot enough arguments"),
    INVALID_GAME_TYPE("invalid_game_type", "&cInvalid Game Type! Choose one of the following: NORMAL, BLOCK_SUMO"),
    CREATED_GAME("created_game", "&a&lSUCCESS! &aCreated the game %s successfully!" +
            "\n\n&7You have received the set-up items in your inventory. Use them to setup the game correctly!"),
    ALREADY_IN_SETUP("already_in_setup", "&cYou are currently already in a game setup! Game: %s"),
    SETUP_COMPLETE("setup_complete", "&a&lCOMPLETED! &aYou completed the setup for the game %s."),
    SPAWN_POINT_ADDED("spawn_point_added", "&b&l+ SPAWN POINT! &bYou added a spawn point to the location %s. (%s/%s)"),
    DATA_LOADING_FAILED("data_loading_failed", "&cAn error has occurred while attempting to load your data. Please re-connect!"),
    ALREADY_IN_GAME("already_in_game", "&cYou are already in an on-going game!"),
    GAME_CANCELLED("game_cancelled", "&cCancelling startup! Not enough players."),
    TIMER("timer", "&c%s"),
    GAME_STARTED("game_started", "&a&lGAME STARTED!"),
    NOT_IN_GAME("not_in_game", "&cYou are not in a game!"),
    WAITING_LOBBY_SET("waiting_lobby_set", "&b&l+ WAITING LOBBY! &bSet the waiting lobby to %s."),
    GAME_NOT_FOUND("game_not_found", "&cGame not found!"),
    WINNER("winner", "&6&lWINNER! %luckperms_prefix%%player_name%"),
    JOINED_GAME("joined_game", "&e%luckperms_prefix%%player_name% &ehas joined. &7(%players%/%maxplayers%)"),
    LEFT_GAME("left_game", "&e%luckperms_prefix%%player_name% &ehas left. &7(%players%/%maxplayers%)"),
    FELL_INTO_THE_VOID("fell_into_the_void", "%pusher_color%%player_name% &7fell into the void"),
    PUSHED_INTO_THE_VOID("pushed_into_the_void", "%player_color%%player_name% &7was pushed into the void by %pusher_color%%pusher_name%"),
    IN_LOCKDOWN("in_lockdown", "&c&lMATCHMAKING DISABLED! &cYou cannot join a game at this time."),
    GAME_NOT_WAITING("game_not_waiting", "&cYou cannot join this on-going game."),
    LEFT_DURING_GAME("left_during_game", "%luckperms_prefix%%player_name% &7has disconnected."),
    RESPAWN_TITLE("respawn_title", "&6&lRESPAWNING IN"),
    RESPAWN_SUBTITLE("respawn_subtitle", "&7%s seconds"),
    RESPAWNED_TITLE("respawned_title", "&7"),
    RESPAWNED_SUBTITLE("respawned_subtitle", "&a&lRESPAWNED"),
    GOLD_BLOCK_WIN_TIMER("gold_block_win_timer", "%player_color%%player_name% has been on the &6centre block &efor &a%seconds% seconds&e!"),
    CLINCHED_THE_WIN("clinched_the_win", "&7\n&7\n%player_color%%player_name% has clinched the win by holding the gold block for &a20 seconds&e!\n&7\n&7"),
    GOLD_BLOCK_SET("gold_block_set", "&e&l+ GOLD BLOCK! &eSet the gold block location to %s."),
    GAME_NOT_FOUND_KICK("game_not_found_kick", "&cCouldn't find a game."),
    INVALID_EVENT_TYPE("invalid_event_type", "&cInvalid event type!"),
    EMPTY_HAND("empty_hand", "&cYou must be holding an item to add it to the events items!"),
    MINOR_EVENT_ANNOUNCEMENT("minor_event_announcement", "&7All players have received %s&7!"),
    MAJOR_EVENT_ANNOUNCEMENT("major_event_announcement", "No message set yet. &o[major_event_announcement]"),
    HIGH_LIVES_COLOR("high_lives_color", "&a%s"),
    MEDIUM_LIVES_COLOR("medium_lives_color", "&6%s"),
    LOW_LIVES_COLOR("low_lives_color", "&c%s"),
    DEAD_SYMBOL("dead_symbol", "&c✘");

    final String path;
    String s;

    Messages(String path, String s) {
        this.path = path;
        this.s = ChatUtils.color(s);
    }

    public void setMessage(String s) {
        this.s = s;
    }

    public String getPath() {
        return path;
    }

    @Override
    public String toString() {
        return s;
    }
}

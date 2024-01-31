package me.yhamarsheh.bridgersumo.commands;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.commands.handler.RegisterAsCommand;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.GameType;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.game.modes.NormalSumo;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.runnables.GameStartup;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import me.yhamarsheh.bridgersumo.utilities.LocationUtils;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class MainCMD {

    private final BridgerSumo plugin;
    private static Map<Player, Game> setups;
    public MainCMD(BridgerSumo plugin) {
        this.plugin = plugin;
        setups = new HashMap<>();
    }

    @RegisterAsCommand(
            command = "/bridgersumo create",
            parameters = 3,
            permission = "bridgersumo.admin",
            disallowNonPlayer = true
    )
    public void setupGame(CommandSender sender, String[] params) {
        Player player = (Player) sender;

        if (setups.containsKey(player)) {
            Game game = setups.get(player);
            player.sendMessage(String.format(Messages.ALREADY_IN_SETUP.toString(), game.getDisplayName()));
            return;
        }

        GameType type;

        try {
            type = GameType.valueOf(params[0].toUpperCase());
        } catch (IllegalArgumentException ex) {
            sender.sendMessage(Messages.INVALID_GAME_TYPE.toString());
            return;
        }

        String displayName = params[1];
        int maxPlayers = 0;

        try {
            maxPlayers = Integer.parseInt(params[2]);
        } catch (NumberFormatException ex) {
            player.sendMessage(ChatUtils.color("&cInvalid integer entered. (MAX_PLAYERS) must be a valid integer."));
            return;
        }

        Game game = null;
        if (type == GameType.BLOCK_SUMO) {
            game = new BlockSumo(plugin, displayName, maxPlayers);
            player.getInventory().setItem(2,
                    ItemBuilder.from(Material.GOLD_INGOT)
                            .name(ChatUtils.component("&aSet the gold block location &7(BLOCK SUMO)"))
                            .glow().build());
        } else {
            game = new NormalSumo(plugin, displayName);
        }

        game.setState(GameState.SETUP);
        plugin.getGamesManager().addGame(game);
        sender.sendMessage(String.format(Messages.CREATED_GAME.toString(), displayName));

        player.getInventory().setItem(0,
                ItemBuilder.from(Material.ARMOR_STAND)
                        .name(ChatUtils.component("&aAdd a spawn-point"))
                        .glow().build());

        player.getInventory().setItem(1,
                ItemBuilder.from(Material.REDSTONE)
                        .name(ChatUtils.component("&aSet waiting lobby"))
                        .glow().build());

        player.getInventory().setItem(8,
                ItemBuilder.from(Material.BED)
                        .name(ChatUtils.component("&aFinish Setup")).build());

        setups.put(player, game);
    }

    @RegisterAsCommand(
            command = "/bridgersumo cancelsetup",
            permission = "bridgersumo.admin",
            disallowNonPlayer = true
    )
    public void cancelSetup(CommandSender commandSender, String[] params) {
        Player player = (Player) commandSender;
        if (!setups.containsKey(player)) {
            player.sendMessage(ChatUtils.color("&cYou are not in a setup!"));
            return;
        }

        player.getInventory().clear();
        Game game = setups.get(player);

        plugin.getGamesManager().removeGame(game);
        setups.remove(player);
        player.sendMessage(ChatUtils.color("&c&lCANCELLED! &cThe game setup has been cancelled."));
    }

    @RegisterAsCommand(
            command = "/bridgersumo setlobby",
            permission = "bridgersumo.admin",
            disallowNonPlayer = true
    )
    public void setLobby(CommandSender commandSender, String[] params) {
        Player player = (Player) commandSender;

        BridgerSumo.LOBBY_LOCATION = player.getLocation();
        plugin.getConfig().set("lobby", LocationUtils.encodeLocation(player.getLocation()));
        plugin.saveConfig();

        player.sendMessage(ChatUtils.color("&a&lSUCCESS! &aLobby location set."));
    }

    @RegisterAsCommand(
            command = "/bridgersumo join",
            parameters = 1,
            disallowNonPlayer = true
    )
    public void joinGame(CommandSender commandSender, String[] params) {
        Player player = (Player) commandSender;
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game != null) {
            player.sendMessage(Messages.ALREADY_IN_GAME.toString());
            return;
        }

        if (BridgerSumo.LOCKDOWN) {
            player.sendMessage(Messages.IN_LOCKDOWN.toString());
            return;
        }

        int id = 0;
        try {
            id = Integer.parseInt(params[0]);
        } catch (NumberFormatException ex) {
            player.sendMessage(Messages.GAME_NOT_FOUND.toString());
            return;
        }

        game = plugin.getGamesManager().getGameById(id);
        if (game == null) {
            player.sendMessage(Messages.GAME_NOT_FOUND.toString());
            return;
        }

        if (game.getState() != GameState.WAITING) {
            player.sendMessage(Messages.GAME_NOT_FOUND.toString());
            return;
        }

        game.joinLogic(dabPlayer);
        game.announce(ChatUtils.placeholders(player, Messages.JOINED_GAME.toString()
                .replace("%players%", game.getPlayerList().size() + "")
                .replace("%maxplayers%", game.getMaxPlayers() + "")));
    }

    @RegisterAsCommand(
            command = "/bridgersumo leave",
            disallowNonPlayer = true
    )
    public void leaveGame(CommandSender commandSender, String[] params) {
        Player player = (Player) commandSender;
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) {
            player.sendMessage(Messages.NOT_IN_GAME.toString());
            return;
        }

        if (!plugin.isBungeeEnabled()) game.quitLogic(dabPlayer);
        else player.performCommand(plugin.getConfig().getString("lobby_command"));
    }

    @RegisterAsCommand(
            command = "/bridgersumo lockdown",
            permission = "bridgersumo.admin",
            disallowNonPlayer = false
    )
    public void lockdown(CommandSender commandSender, String[] params) {
        BridgerSumo.LOCKDOWN = !BridgerSumo.LOCKDOWN;

        commandSender.sendMessage(ChatUtils.color("&c&lLOCKDOWN! &cThe status of the matchmaking lockdown is now: &e" + BridgerSumo.LOCKDOWN));
        commandSender.sendMessage(ChatUtils.color("&7There are &b" + plugin.getGamesManager().getPlayingGames() + " &7active games at the moment." +
                " For more details on all of the games, run &b'/bridgersumo vgdetails'&7."));
    }

    @RegisterAsCommand(
            command = "/bridgersumo vgdetails",
            permission = "bridgersumo.admin",
            disallowNonPlayer = false
    )
    public void viewGamesDetails(CommandSender commandSender, String[] params) {
        commandSender.sendMessage(plugin.getGamesManager().toString());
    }

    @RegisterAsCommand(
            command = "/bridgersumo forcestart",
            permission = "bridgersumo.admin",
            disallowNonPlayer = false
    )
    public void forceStart(CommandSender commandSender, String[] params) {
        Player player = (Player) commandSender;
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) {
            player.sendMessage(Messages.NOT_IN_GAME.toString());
            return;
        }

       new GameStartup(plugin, game, true);
    }

    public static Map<Player, Game> getSetups() {
        return setups;
    }
}

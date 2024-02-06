package me.yhamarsheh.bridgersumo.managers;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.attributes.Disableable;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.GameType;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.game.modes.NormalSumo;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import me.yhamarsheh.bridgersumo.utilities.LocationUtils;
import me.yhamarsheh.bridgersumo.utilities.Logger;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class GamesManager implements Disableable {

    private final BridgerSumo plugin;
    private final List<Game> gameList;

    private final File file;
    private YamlConfiguration configuration;
    private int totalGames;

    public GamesManager(BridgerSumo plugin) {
        this.plugin = plugin;
        this.gameList = new ArrayList<>();

        this.file = new File(plugin.getDataFolder(), "games.yml");
        this.configuration = YamlConfiguration.loadConfiguration(file);

        init();
    }

    private void init() {
        for (String key : configuration.getKeys(false)) {
            String sType = configuration.getString(key + ".type");

            if (sType == null || sType.isEmpty()) {
                BridgerSumo.LOGGER.error(Logger.Reason.ERROR, "The game " + key + " couldn't be loaded. The game does not belong to a game type.");
                return;
            }

            GameType type = GameType.valueOf(sType);
            String displayName = configuration.getString(key + ".display_name");

            Game game;
            if (type == GameType.BLOCK_SUMO) {
                int maxPlayers = configuration.getInt(key + ".max_players");
                Location goldBlock = LocationUtils.decodeLocation(configuration.getString(key + ".gold_block"));

                if (maxPlayers < 2) {
                    BridgerSumo.LOGGER.error(Logger.Reason.ERROR, "The game " + key + " couldn't be loaded. The game cannot have max players of less than 2.");
                    return;
                }
                    game = new BlockSumo(plugin, displayName, maxPlayers);

                BlockSumo blockSumo = (BlockSumo) game;
                blockSumo.setGoldBlock(goldBlock);
            } else  {
                game = new NormalSumo(plugin, displayName);
            }

            List<Location> spawnPoints = configuration.getStringList(key + ".spawn_points").stream()
                    .map(LocationUtils::decodeLocation).collect(Collectors.toList());
            game.setSpawnPoints(spawnPoints);

            game.setWaitingLobby(LocationUtils.decodeLocation(configuration.getString(key + ".waiting_lobby")));

            BridgerSumo.LOGGER.log(Logger.Reason.GENERIC, "Loaded the game " + key + " successfully!");
            game.setId(Integer.parseInt(key));
            addGame(game);
        }
    }

    public void saveGame(Game game) {
        String key = totalGames + ".";
        configuration.set(key + "type", game.getGameType().name());
        configuration.set(key + "display_name", game.getDisplayName());

        if (game.getGameType() == GameType.BLOCK_SUMO) {
            configuration.set(key + "max_players", game.getMaxPlayers());
            configuration.set(key + "gold_block", LocationUtils.encodeLocation(((BlockSumo)game).getGoldBlock()));
        }

        configuration.set(key + "waiting_lobby", LocationUtils.encodeLocation(game.getWaitingLobby()));
        configuration.set(key + "spawn_points", game.getSpawnPoints().stream()
                .map(LocationUtils::encodeLocation).collect(Collectors.toList()));

        try {
            configuration.save(file);
        } catch (IOException e) {
            BridgerSumo.LOGGER.error(Logger.Reason.ERROR, "Failed to save the game " +
                    game.getDisplayName() + ". More info: " + e.getMessage());
        }

        game.setState(GameState.WAITING);
    }

    public void addGame(Game game) {
        game.setId(++totalGames);
        gameList.add(game);
    }

    public void removeGame(Game game) {
        gameList.remove(game);
    }

    public Game getPlayerGame(DabPlayer player) {
        for (Game game : gameList) {
            if (game.getPlayerList().containsKey(player)) return game;
        }

        return null;
    }

    public Game getGameById(int id) {
        for (Game game : gameList) {
            if (game.getId() == id) return game;
        }

        return null;
    }

    public List<Game> getGameList() {
        return gameList;
    }

    public int getPlayingGames() {
        int sum = 0;
        for (Game game : gameList) {
            if (game.getState() != GameState.WAITING) sum++;
        }

        return sum;
    }

    public int getTotalGames() {
        int sum = 1;
        for (Game game : gameList) {
            if (game.getState() == GameState.PLAYING) sum++;
        }

        return sum;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        int i = 1;
        for (Game game : gameList) {
            sb.append(ChatUtils.color("&b" + i + ". ")).append(game.toString())
                    .append("\n\n");
            i++;
        }

        return sb.toString();
    }

    @Override
    public void disable() {
        for (Game game : gameList) {
            game.getPlayerList().forEach((dabPlayer, playerState) -> dabPlayer.getPlayer().getInventory().clear());
            game.getPlayerList().clear();
            game.getBlocks().forEach(block -> {
                block.setType(Material.AIR);
            });
        }

        gameList.clear();
    }
}

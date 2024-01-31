package me.yhamarsheh.bridgersumo.managers;

import fr.mrmicky.fastboard.FastBoard;
import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.attributes.Disableable;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.game.modes.NormalSumo;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class ScoreboardManager implements Disableable {

    private final BridgerSumo plugin;
    private final Map<UUID, FastBoard> scoreboards;

    private final List<String> BLOCK_SUMO_LINES;
    private final String BLOCK_SUMO_TITLE;
    private final String BLOCK_SUMO_PLAYERS_FORMAT;

    private final List<String> NORMAL_SUMO_LINES;
    private final String NORMAL_SUMO_TITLE;

    public ScoreboardManager(BridgerSumo plugin) {
        this.plugin = plugin;
        this.scoreboards = new HashMap<>();

        BLOCK_SUMO_LINES = plugin.getConfig().getStringList("scoreboards.block_sumo.lines").stream().map(ChatUtils::color).collect(Collectors.toList());
        BLOCK_SUMO_TITLE = ChatUtils.color(plugin.getConfig().getString("scoreboards.block_sumo.title"));
        BLOCK_SUMO_PLAYERS_FORMAT = plugin.getConfig().getString("scoreboards.block_sumo.players_format");

        NORMAL_SUMO_LINES = plugin.getConfig().getStringList("scoreboards.normal_sumo.lines").stream().map(ChatUtils::color).collect(Collectors.toList());
        NORMAL_SUMO_TITLE = ChatUtils.color(plugin.getConfig().getString("scoreboards.normal_sumo.title"));

        startUpdating();
    }

    private void startUpdating() {
        new BukkitRunnable() {

            @Override
            public void run() {
                for (Game game : plugin.getGamesManager().getGameList()) {
                    if (game.getState() != GameState.PLAYING) continue;
                    for (DabPlayer dabPlayer : game.getPlayerList().keySet()) {
                        createBoard(game, dabPlayer);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 10L);
    }

    public void createBoard(Game game, @NotNull DabPlayer dabPlayer) {
        FastBoard board = null;
        boolean firstTime = false;
        if (scoreboards.containsKey(dabPlayer.getUniqueId())) {
            board = scoreboards.get(dabPlayer.getUniqueId());
            firstTime = true;
        } else {
            board = new FastBoard(dabPlayer.getPlayer());
            scoreboards.put(dabPlayer.getUniqueId(), board);
        }

        switch (game.getGameType()) {
            case BLOCK_SUMO:
                BlockSumo blockSumo = (BlockSumo) game;
                if (firstTime) board.updateTitle(BLOCK_SUMO_TITLE);

                List<String> lines = new ArrayList<>();
                for (String l : BLOCK_SUMO_LINES) {
                    if (l.contains("%players%")) {
                        String s = l.replace("%players%", BLOCK_SUMO_PLAYERS_FORMAT);
                        int i = 0;
                        for (DabPlayer player : blockSumo.getLives().keySet()) {
                            lines.add(ChatUtils.placeholders(player.getPlayer(),
                                    s.replace("%player_lives%",
                                            "" + blockSumo.getPlayerLives(player))));
                            i++;
                            if (i == 15 - BLOCK_SUMO_LINES.size()) break;
                        }
                        continue;
                    }
                    lines.add(l);
                }

                board.updateLines(lines);
                break;
            case NORMAL:
                NormalSumo normalSumo = (NormalSumo) game;
                if (firstTime) board.updateTitle(NORMAL_SUMO_TITLE);
                board.updateLines(
                        NORMAL_SUMO_LINES.stream()
                                .map(line -> ChatUtils.placeholders(normalSumo.getOpponent(dabPlayer).getPlayer(), line
                                        .replace("%map_name%", normalSumo.getDisplayName())))
                                .collect(Collectors.toList()));
                break;
        }

    }

    public void removeBoard(DabPlayer dabPlayer) {
        if (!scoreboards.containsKey(dabPlayer.getUniqueId())) return;
        FastBoard board = scoreboards.get(dabPlayer.getUniqueId());
        board.delete();
        scoreboards.remove(dabPlayer.getUniqueId());
    }


    @Override
    public void disable() {
        for (FastBoard board : scoreboards.values()) {
            board.delete();
        }

        scoreboards.clear();
    }
}

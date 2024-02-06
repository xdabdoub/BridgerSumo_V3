package me.yhamarsheh.bridgersumo.hooks;

import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.GameType;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.objects.TeamColor;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PAPI extends PlaceholderExpansion {

    private final BridgerSumo plugin;
    public PAPI(BridgerSumo plugin) {
        this.plugin = plugin;
        register();
    }

    @Override
    public @NotNull String getIdentifier() {
        return "bridgersumo";
    }

    @Override
    public @NotNull String getAuthor() {
        return "xDabDoub";
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0";
    }

    @Override
    public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
        if (player == null) return "";
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());
        if (dabPlayer == null) {
            char rankColor = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%").toCharArray()[1];
            return ChatUtils.color("&" + rankColor);
        }

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) {
            char rankColor = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%").toCharArray()[1];
            return ChatUtils.color("&" + rankColor);
        }

        if (game.getGameType() != GameType.BLOCK_SUMO) return "";
        BlockSumo blockSumo = (BlockSumo) game;
        TeamColor teamColor = blockSumo.getPlayerTeam(dabPlayer);

        if (teamColor == null) {
            char rankColor = PlaceholderAPI.setPlaceholders(player, "%luckperms_prefix%").toCharArray()[1];
            return ChatUtils.color("&" + rankColor);
        }

        switch (params) {
            case "team_color":
                return teamColor.getColor().getChatColor() + "";
            case "team_color_letter":
                return teamColor.getColor().getLetter() + " ";
        }
        return super.onPlaceholderRequest(player, params);
    }
}

package me.yhamarsheh.bridgersumo.game.modes;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.enums.PlayerState;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.GameType;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class NormalSumo extends Game {

    public NormalSumo(BridgerSumo plugin, String displayName) {
        super(plugin, GameType.NORMAL, displayName, 2);
    }

    public DabPlayer getOpponent(DabPlayer player) {
        for (DabPlayer dabPlayer : getPlayerList().keySet()) {
            if (player == dabPlayer) continue;
            return dabPlayer;
        }

        return null;
    }

    @Override
    public void startLogic() {
        int i = 0;
        for (DabPlayer dabPlayer : getPlayerList().keySet()) {
            dabPlayer.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
            dabPlayer.teleport(getSpawnPoints().get(i++));
            plugin.getScoreboardManager().createBoard(this, dabPlayer);

            dabPlayer.getSumoStatistics().addGamesPlayed();
        }

        setState(GameState.PLAYING);
    }

    @Override
    public void deathLogic(DabPlayer player, boolean killed) {
        player.getSumoStatistics().addDeath();
        player.getSumoStatistics().setWinStreak(0);

        getPlayerList().replace(player, PlayerState.DEAD);
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        player.getPlayer().getInventory().clear();

        player.getPlayer().setAllowFlight(true);
        player.getPlayer().setFlying(true);
        player.sendTitle("&c&lYOU DIED", "&7Oh oh!", 1, 3, 1);
        player.playSound(Sound.ANVIL_LAND);

        setState(GameState.ENDING);
        winLogic();
    }

    @Override
    public void winLogic() {
        DabPlayer winner = null;
        for (DabPlayer dabPlayer : getPlayerList().keySet()) {
            if (isAlive(dabPlayer)) {
                winner = dabPlayer;

                winner.getSumoStatistics().addWin();
                winner.getSumoStatistics().addWinStreak();
                winner.getSumoStatistics().addKill();

                winner.playSound(Sound.LEVEL_UP);
                winner.sendTitle("&a&lYOU WON!", "&7Winner winner, chicken dinner", 0, 1, 0);
            }

            plugin.getScoreboardManager().removeBoard(dabPlayer);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (dabPlayer.isOnline() && plugin.getGamesManager().getPlayerGame(dabPlayer) == this) {
                    dabPlayer.getPlayer().getInventory().clear();
                    dabPlayer.getPlayer().setAllowFlight(false);
                    dabPlayer.getPlayer().setFlying(false);
                    dabPlayer.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);

                    if (!plugin.isBungeeEnabled()) dabPlayer.teleport(BridgerSumo.LOBBY_LOCATION);
                    else dabPlayer.getPlayer().performCommand(plugin.getConfig().getString("lobby_command"));
                }
            }, 20 * 5);
        }

        assert winner != null;
        announce(ChatUtils.placeholders(winner.getPlayer(), Messages.WINNER.toString()));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            getPlayerList().clear();
            setState(GameState.WAITING);
        }, 20 * 6);

        setState(GameState.ENDED);
    }

    @Override
    public void quitLogic(DabPlayer player) {
        if (getState() == GameState.PLAYING) {
            announce(ChatUtils.placeholders(player.getPlayer(), Messages.LEFT_DURING_GAME.toString()
                    .replace("%players%", (getPlayerList().size()) + "")
                    .replace("%maxplayers%", getMaxPlayers() + "")));
            deathLogic(player, true);
        }

        getPlayerList().remove(player);

        if (getState() == GameState.WAITING) {
            announce(ChatUtils.placeholders(player.getPlayer(), Messages.LEFT_GAME.toString()
                    .replace("%players%", (getPlayerList().size()) + "")
                    .replace("%maxplayers%", getMaxPlayers() + "")));
        }

        plugin.getScoreboardManager().removeBoard(player);
        player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        player.getPlayer().setAllowFlight(false);
        player.getPlayer().setFlying(false);
        player.teleport(BridgerSumo.LOBBY_LOCATION);
    }

    @Override
    public void giveKit(DabPlayer player) {
        // No body. (KITS NOT NEEDED)
    }
}

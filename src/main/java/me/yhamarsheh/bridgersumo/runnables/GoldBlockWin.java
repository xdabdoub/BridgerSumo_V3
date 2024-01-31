package me.yhamarsheh.bridgersumo.runnables;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.enums.PlayerState;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.scheduler.BukkitRunnable;

public class GoldBlockWin extends BukkitRunnable {

    private final BridgerSumo plugin;
    private final DabPlayer dabPlayer;
    private final BlockSumo game;
    private int ticksRemaining = 0;

    public GoldBlockWin(BridgerSumo plugin, DabPlayer dabPlayer, Game game) {
        this.plugin = plugin;
        this.dabPlayer = dabPlayer;
        this.game = (BlockSumo) game;

        runTaskTimer(plugin, 0, 1);
    }

    @Override
    public void run() {
        if (dabPlayer.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.GOLD_BLOCK
        || game.getState() != GameState.PLAYING) {
            game.setClinchWinPlayer(null);
            dabPlayer.getPlayer().setLevel(0);
            cancel();
            return;
        }

        if (game.getClinchWinPlayer() == null || !game.getClinchWinPlayer().toString().equals(dabPlayer.getUniqueId().toString())) {
            dabPlayer.getPlayer().setLevel(0);
            cancel();
            return;
        }

        int timeInSeconds = ticksRemaining / 20;

        if (ticksRemaining > 0 && ticksRemaining % 20 == 0) {
            dabPlayer.getPlayer().setLevel(timeInSeconds);
        }

        if (timeInSeconds == 20) {
            game.announce(ChatUtils.placeholders(dabPlayer.getPlayer(), Messages.CLINCHED_THE_WIN.toString()));
            for (DabPlayer player : game.getPlayerList().keySet()) {
                if (player == dabPlayer) continue;
                game.getLives().replace(player, 1);
                game.deathLogic(player, false);
            }

            dabPlayer.getPlayer().setLevel(0);
            cancel();
            return;
        }

        if (ticksRemaining > 0 && ticksRemaining % 100 == 0) {
            game.announce(ChatUtils.placeholders(dabPlayer.getPlayer(), Messages.GOLD_BLOCK_WIN_TIMER.toString()
                    .replace("%seconds%", timeInSeconds + "")));
            game.playSound(Sound.CLICK);
        }

        ticksRemaining++;
    }
}

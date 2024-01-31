package me.yhamarsheh.bridgersumo.runnables;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class RespawnRB extends BukkitRunnable {

    private final BridgerSumo plugin;
    private final Game game;
    private final DabPlayer dabPlayer;

    public RespawnRB(BridgerSumo plugin, Game game, DabPlayer dabPlayer) {
        this.plugin = plugin;
        this.game = game;
        this.dabPlayer = dabPlayer;

        runTaskTimer(plugin, 0, 20);
    }

    int i = 3;
    @Override
    public void run() {
        if (!dabPlayer.isOnline() || plugin.getGamesManager().getPlayerGame(dabPlayer) != game) {
            cancel();
            return;
        }

        dabPlayer.sendTitle(String.format(Messages.RESPAWN_TITLE.toString(),
                i), String.format(Messages.RESPAWN_SUBTITLE.toString(), i), 0, 3, 0);
        dabPlayer.playSound(Sound.NOTE_PLING);

        i--;

        if (i == 0) {
            i = 3;
            cancel();
        }
    }
}

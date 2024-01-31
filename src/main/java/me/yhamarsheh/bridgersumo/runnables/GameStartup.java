package me.yhamarsheh.bridgersumo.runnables;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.locale.Messages;
import org.bukkit.Sound;
import org.bukkit.scheduler.BukkitRunnable;

public class GameStartup extends BukkitRunnable {

    private final BridgerSumo plugin;
    private final Game game;

    private final boolean forced;
    public GameStartup(BridgerSumo plugin, Game game, boolean forced) {
        this.plugin = plugin;
        this.game = game;
        this.forced = forced;

        this.game.setState(GameState.STARTING);
        runTaskTimer(plugin, 0, 1);
    }

    int i = 100;
    @Override
    public void run() {
        if ((game.getPlayerList().size() < game.getMaxPlayers()) && !forced) {
            game.announce(Messages.GAME_CANCELLED.toString());
            cancel();
            return;
        }

        if (i % 20 == 0) {
            game.announceTitle("&7", String.format(Messages.TIMER.toString(),
                    i / 20), 0, 2, 0);
            game.playSound(Sound.NOTE_PLING);
        }

        if (i == 20) {
            game.startLogic();
            game.announce(Messages.GAME_STARTED.toString());
            game.announceTitle("&7", "&a&lGAME STARTED", 0, 1, 0);
            cancel();
        }

        i--;
    }
}

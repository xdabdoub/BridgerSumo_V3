package me.yhamarsheh.bridgersumo.runnables.events;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.EventType;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import org.bukkit.scheduler.BukkitRunnable;

public class MajorEventsRB extends BukkitRunnable {

    private final BridgerSumo plugin;
    private final BlockSumo game;
    private final int FLAME_ANIMATION_DURATION;

    public MajorEventsRB(BridgerSumo plugin, BlockSumo game) {
        this.plugin = plugin;
        this.game = game;

        int SCHEDULE_TIME = plugin.getConfig().getInt("major_events.schedule");
        this.FLAME_ANIMATION_DURATION = plugin.getConfig().getInt("major_events.flame_animation_duration");
        runTaskTimer(plugin, SCHEDULE_TIME * 20L, SCHEDULE_TIME * 20L);
    }

    int i = 0;
    @Override
    public void run() {
        if (plugin.getEventsManager().getItems(EventType.MAJOR).isEmpty()) {
            cancel();
            return;
        }

        if (game.getState() != GameState.PLAYING) {
            cancel();
            return;
        }

        new FlameParticlesAnimation(plugin, game, FLAME_ANIMATION_DURATION);
    }
}

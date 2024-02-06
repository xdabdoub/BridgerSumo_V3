package me.yhamarsheh.bridgersumo.runnables.events;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.EventType;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.objects.EventItem;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class MajorEventsRB extends BukkitRunnable {

    private final BridgerSumo plugin;
    private final BlockSumo game;
    private final int SCHEDULE_TIME;
    private final Random r;

    public MajorEventsRB(BridgerSumo plugin, BlockSumo game) {
        this.plugin = plugin;
        this.game = game;
        this.r = new Random();

        this.SCHEDULE_TIME = plugin.getConfig().getInt("major_events_schedule");
        runTaskTimer(plugin, SCHEDULE_TIME * 20L, SCHEDULE_TIME * 20L);
    }

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

        EventItem eventItem = plugin.getEventsManager().getItems(EventType.MAJOR)
                .get(r.nextInt(plugin.getEventsManager().getItems(EventType.MAJOR).size()));

        game.getGoldBlock().getWorld().dropItemNaturally(
                game.getGoldBlock(), eventItem.getItemStack());

        game.announce(String.format(Messages.MAJOR_EVENT_ANNOUNCEMENT.toString(),
                eventItem.getDisplayName()));
    }
}

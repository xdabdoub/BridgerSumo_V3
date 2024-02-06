package me.yhamarsheh.bridgersumo.runnables.events;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.EventType;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.objects.EventItem;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;

public class MinorEventsRB extends BukkitRunnable {

    private final BridgerSumo plugin;
    private final BlockSumo game;
    private final int SCHEDULE_TIME;
    private final Random r;
    public MinorEventsRB(BridgerSumo plugin, BlockSumo game) {
        this.plugin = plugin;
        this.game = game;
        this.r = new Random();

        this.SCHEDULE_TIME = plugin.getConfig().getInt("minor_events_schedule");
        runTaskTimer(plugin, SCHEDULE_TIME * 20L, SCHEDULE_TIME * 20L);
    }

    @Override
    public void run() {
        if (plugin.getEventsManager().getItems(EventType.MINOR).isEmpty()) {
            cancel();
            return;
        }

        if (game.getState() != GameState.PLAYING) {
            cancel();
            return;
        }

        EventItem eventItem = plugin.getEventsManager().getItems(EventType.MINOR)
                .get(r.nextInt(plugin.getEventsManager().getItems(EventType.MINOR).size()));

        for (DabPlayer dabPlayer : game.getPlayerList().keySet()) {
            dabPlayer.getPlayer().getInventory().addItem(eventItem.getItemStack());
        }

        game.announce(String.format(Messages.MINOR_EVENT_ANNOUNCEMENT.toString(),
                eventItem.getDisplayName()));
    }
}

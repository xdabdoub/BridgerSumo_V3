package me.yhamarsheh.bridgersumo.runnables.events;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.EventType;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.objects.EventItem;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.Random;

public class FlameParticlesAnimation extends BukkitRunnable {

    private final BlockSumo blockSumo;
    private final int DURATION;
    private final BridgerSumo plugin;
    private final Random r;
    private final Location BLOCK_LOCATION;
    private final EnumParticle PARTICLE;
    public FlameParticlesAnimation(BridgerSumo plugin, BlockSumo blockSumo, int DURATION) {
        this.plugin = plugin;
        this.blockSumo = blockSumo;
        this.DURATION = DURATION;
        this.BLOCK_LOCATION = blockSumo.getGoldBlock().clone().add(0, 1, 0);
        this.PARTICLE = EnumParticle.valueOf(plugin.getConfig().getString("major_events.particle"));

        this.r = new Random();
        runTaskTimer(plugin, 0, 1);
    }

    double t = 0;

    int i = 0;
    @Override
    public void run() {
        if (plugin.getEventsManager().getItems(EventType.MAJOR).isEmpty()) {
            cancel();
            return;
        }

        if (blockSumo.getState() != GameState.PLAYING) {
            cancel();
            return;
        }

        t = t + Math.PI/32;
        double x = 2*Math.cos(t) + Math.cos(5*t);
        double z = 2*Math.sin(t) + Math.sin(5*t);
        BLOCK_LOCATION.add(x, 0, z);

        PacketPlayOutWorldParticles packet =
                new PacketPlayOutWorldParticles(PARTICLE,true, (float) (BLOCK_LOCATION.getX()),
                        (float) (BLOCK_LOCATION.getY()), (float) (BLOCK_LOCATION.getZ()), 0, 0, 0, 0, 1);

        for (DabPlayer dabPlayer : blockSumo.getPlayerList().keySet()) {
            ((CraftPlayer) dabPlayer.getPlayer()).getHandle().playerConnection.sendPacket(packet);
        }

        BLOCK_LOCATION.subtract(x, 0, z);

        if (i == DURATION * 20) {
            EventItem eventItem = plugin.getEventsManager().getItems(EventType.MAJOR)
                    .get(r.nextInt(plugin.getEventsManager().getItems(EventType.MAJOR).size()));

            blockSumo.getGoldBlock().getWorld().dropItemNaturally(
                    BLOCK_LOCATION.add(+0.5, +1, +0.5), eventItem.getItemStack())
                    .setVelocity(new Vector(0, 0, 0));

            blockSumo.announce(String.format(Messages.MAJOR_EVENT_ANNOUNCEMENT.toString(),
                    eventItem.getDisplayName()));

            PacketPlayOutWorldParticles particle =
                    new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_NORMAL,true, (float) (BLOCK_LOCATION.getX()),
                            (float) (BLOCK_LOCATION.getY()), (float) (BLOCK_LOCATION.getZ()), 0, 0, 0, 0, 1);

            for (DabPlayer dabPlayer : blockSumo.getPlayerList().keySet()) {
                ((CraftPlayer) dabPlayer.getPlayer()).getHandle().playerConnection.sendPacket(particle);
                dabPlayer.playSound(Sound.EXPLODE, BLOCK_LOCATION);
            }


            cancel();
        }
        i++;
    }
}

package me.yhamarsheh.bridgersumo.storage.objects;

import me.clip.placeholderapi.PlaceholderAPI;
import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.objects.stats.BlockSumoStatistics;
import me.yhamarsheh.bridgersumo.objects.stats.SumoStatistics;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Objects;
import java.util.UUID;

public class DabPlayer {

    private final BridgerSumo plugin;

    private UUID uuid;
    private Player player;

    private BlockSumoStatistics blockSumoStatistics;
    private SumoStatistics sumoStatistics;

    public DabPlayer(BridgerSumo plugin, UUID uuid) {
        this.plugin = plugin;
        this.uuid = uuid;

        this.blockSumoStatistics = new BlockSumoStatistics(this);
        this.sumoStatistics = new SumoStatistics(this);
    }

    /*
     * Statistics
     */

    public BlockSumoStatistics getBlockSumoStatistics() {
        return blockSumoStatistics;
    }

    public SumoStatistics getSumoStatistics() {
        return sumoStatistics;
    }

    /*
     * Bukkit Functions
     */

    public void setPlayer(Player player) {
        this.player = player;
    }

    public void sendMessage(String s) {
        player.sendMessage(ChatUtils.color(s));
    }

    public void sendTitle(String mTitle, String mSub, int fadeIn, int time, int fadeOut) {
        ChatUtils.sendTitle(mTitle, mSub, player, fadeIn, time, fadeOut);
    }

    public void playSound(Sound sound) {
        player.playSound(player.getLocation(), sound, 1f, 1f);
    }

    public void playSound(Sound sound, Location location) {
        player.playSound(location, sound, 1f, 1f);
    }

    public void sendMessageWithPAPI(String s) {
        sendMessage(PlaceholderAPI.setPlaceholders(player, s));
    }

    public void teleport(Location location) {
        player.teleport(location);
    }

    public UUID getUniqueId() {
        return uuid;
    }

    public Location getLocation() {
        return player.getLocation();
    }

    public boolean isOnline() {
        if (player == null) return false;
        return player.isOnline();
    }

    public Player getPlayer() {
        return player;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DabPlayer dabPlayer = (DabPlayer) o;

        return getUniqueId().equals(dabPlayer.getUniqueId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getUniqueId());
    }
}

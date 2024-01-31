package me.yhamarsheh.bridgersumo.listeners;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class DataHandlingListener implements Listener {

    private final BridgerSumo plugin;
    public DataHandlingListener(BridgerSumo plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPreLogin(AsyncPlayerPreLoginEvent e) {
        DabPlayer dabPlayer = new DabPlayer(plugin, e.getUniqueId());
        plugin.getPlayersManager().addPlayer(dabPlayer);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        if (dabPlayer == null) {
            player.kickPlayer(Messages.DATA_LOADING_FAILED.toString());
            return;
        }

        dabPlayer.setPlayer(player);

        plugin.getLogger().info(player.getUniqueId().toString());
        if (!plugin.isBungeeEnabled()) return;
        if (!plugin.getRedisManager().getGameMap().containsKey(player.getUniqueId())) {
            player.kickPlayer(Messages.GAME_NOT_FOUND_KICK.toString());
            return;
        }

        player.performCommand("bridgersumo join " + plugin.getRedisManager().getGameMap().get(player.getUniqueId()).getId());
        plugin.getRedisManager().getGameMap().remove(player.getUniqueId());
    }
}

package me.yhamarsheh.bridgersumo.managers;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.attributes.Disableable;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;

import java.util.*;

public class PlayersManager implements Disableable {

    private final BridgerSumo plugin;
    private final Map<UUID, DabPlayer> players;

    private final DamageManager damageManager;

    public PlayersManager(BridgerSumo plugin) {
        this.plugin = plugin;
        this.players = new HashMap<>();

        this.damageManager = new DamageManager(plugin);
    }

    public void addPlayer(DabPlayer player) {
        players.put(player.getUniqueId(), player);
    }

    public void removePlayer(DabPlayer player) {
        players.remove(player.getUniqueId());
    }

    public DabPlayer getPlayer(UUID uuid) {
        return players.get(uuid);
    }
    public Map<UUID, DabPlayer> getPlayerList() {
        return players;
    }

    @Override
    public void disable() {
        for (DabPlayer dabPlayer : players.values()) {
            // dabPlayer.save();
        }

        players.clear();
    }

    public DamageManager getDamageManager() {
        return damageManager;
    }
}

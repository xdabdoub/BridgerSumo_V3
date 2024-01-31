package me.yhamarsheh.bridgersumo.managers;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DamageManager {

    private final BridgerSumo plugin;
    private final Map<UUID, LastDamageInfo> data;

    public DamageManager(BridgerSumo plugin) {
        this.plugin = plugin;
        this.data = new HashMap<>();
    }

    public void handleDamage(DabPlayer dabPlayer, DabPlayer damager) {
        if (!data.containsKey(dabPlayer.getUniqueId())) {
            data.put(dabPlayer.getUniqueId(), new LastDamageInfo(damager, System.currentTimeMillis()));
            return;
        }

        LastDamageInfo lastDamageInfo = data.get(dabPlayer.getUniqueId());
        lastDamageInfo.update(damager, System.currentTimeMillis());
    }

    public boolean isValidDamage(DabPlayer dabPlayer) {
        if (!data.containsKey(dabPlayer.getUniqueId())) return false;

        LastDamageInfo lastDamageInfo = data.get(dabPlayer.getUniqueId());
        return lastDamageInfo.getLastDamageTime() + 10000 >= System.currentTimeMillis();
    }

    public LastDamageInfo getLastDamageInfo(DabPlayer dabPlayer) {
        return data.get(dabPlayer.getUniqueId());
    }

    public Map<UUID, LastDamageInfo> getData() {
        return data;
    }

    public static class LastDamageInfo {
        private DabPlayer lastDamager;
        private long lastDamageTime;

        public LastDamageInfo(DabPlayer lastDamager, long lastDamageTime) {
            this.lastDamager = lastDamager;
            this.lastDamageTime = lastDamageTime;
        }

        public void update(DabPlayer lastDamager, long lastDamageTime) {
            this.lastDamager = lastDamager;
            this.lastDamageTime = lastDamageTime;
        }

        public DabPlayer getLastDamager() {
            return lastDamager;
        }

        public long getLastDamageTime() {
            return lastDamageTime;
        }
    }
}

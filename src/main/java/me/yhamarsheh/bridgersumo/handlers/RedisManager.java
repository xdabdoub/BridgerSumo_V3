package me.yhamarsheh.bridgersumo.handlers;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.GameType;
import org.jetbrains.annotations.Nullable;
import org.json.JSONException;
import org.json.JSONObject;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPubSub;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class RedisManager {
    private final BridgerSumo plugin;
    private final Map<UUID, Game> gameMap;
    private final String CHANNEL;

    private JedisPool subscriber;

    public RedisManager(BridgerSumo plugin) {
        this.plugin = plugin;
        this.CHANNEL = "dabdoub:bridgersumo";
        this.gameMap = new HashMap<>();

        subscribe();
    }

    public void subscribe(){
        subscriber = new JedisPool("168.119.212.45", 25569);
        subscriber.setMaxWait(Duration.ofSeconds(5));
        subscriber.setMinIdle(Integer.MAX_VALUE);

        plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> {
            try (Jedis jedis = subscriber.getResource()) {
                jedis.auth("ai0E+EO0DOF03454T+%f:CCLCLCLLCD;DXzorOl104");
                jedis.subscribe(new ReceiveMessageHandler(), CHANNEL);
            }
        });
    }


    public void sendGameFound(UUID uuid, boolean f) throws JSONException {
        JSONObject data = new JSONObject();
        data.put("uuid", uuid.toString());
        data.put("subchannel", "gameFound");
        data.put("response", f);

        try (Jedis publisher = subscriber.getResource()) {
            publisher.auth("ai0E+EO0DOF03454T+%f:CCLCLCLLCD;DXzorOl104");
            publisher.publish(CHANNEL, data.toString());
        }
    }

    public void closeConnection() {
        subscriber.close();
    }

    public Map<UUID, Game> getGameMap() {
        return gameMap;
    }

    class ReceiveMessageHandler extends JedisPubSub {

        @Override
        public void onMessage(String channel, String message) {
            if (!channel.equals(CHANNEL)) return;

            UUID uuid = null;
            try {
                JSONObject jsonObject = new JSONObject(message);
                uuid = UUID.fromString(jsonObject.getString("uuid"));

                String subChannel = jsonObject.getString("subchannel");
                if (!subChannel.equals("gameType")) return;

                GameType type = GameType.valueOf(jsonObject.getString("response"));

                Game maxWaitingPlayersGame = getGame(type);

                if (maxWaitingPlayersGame != null) {
                    UUID finalUuid = uuid;
                    gameMap.put(uuid, maxWaitingPlayersGame);
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> sendGameFound(finalUuid, true));
                } else {
                    UUID finalUuid1 = uuid;
                    plugin.getServer().getScheduler().runTaskAsynchronously(plugin, () -> sendGameFound(finalUuid1, false));
                }
            } catch (JSONException e) {
                assert uuid != null;
                sendGameFound(uuid, false);
                plugin.getLogger().severe("Couldn't respond to the message from the proxy server. More Info: " + e.getMessage()
                        + "\nResponse: " + message);
            }
        }

        @Nullable
        private Game getGame(GameType type) {
            Game maxWaitingPlayersGame = null;
            int maxWaitingPlayersCount = -1;

            for (Game game : plugin.getGamesManager().getGameList()) {
                if (game.getGameType() != type) continue;

                if (game.getState() == GameState.WAITING) {
                    int waitingPlayersCount = game.getPlayerList().size();
                    if (waitingPlayersCount > maxWaitingPlayersCount) {
                        maxWaitingPlayersCount = waitingPlayersCount;
                        maxWaitingPlayersGame = game;
                    }
                }
            }
            return maxWaitingPlayersGame;
        }
    }
}
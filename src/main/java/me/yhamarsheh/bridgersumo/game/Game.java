package me.yhamarsheh.bridgersumo.game;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.Debug;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.enums.PlayerState;
import me.yhamarsheh.bridgersumo.runnables.GameStartup;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import java.util.*;

public abstract class Game {

    private int id;

    protected final BridgerSumo plugin;
    private final GameType gameType;
    private final Map<DabPlayer, PlayerState> playerList;
    private final List<Block> blocks;
    private final int maxPlayers;

    private GameState state;

    private List<Location> spawnPoints;
    private final String displayName;
    private Location waitingLobby;

    private Location spectatorLobby;

    private UUID clinchWinPlayer;

    public Game(BridgerSumo plugin, GameType gameType, String displayName, int maxPlayers) {
        this.plugin = plugin;
        this.gameType = gameType;
        this.maxPlayers = maxPlayers;
        this.displayName = displayName;

        this.state = GameState.WAITING;

        this.playerList = new HashMap<>();
        this.blocks = new ArrayList<>();
        this.spawnPoints = new ArrayList<>();

        this.clinchWinPlayer = Debug.DUMMY_UUID.getUuid();
    }

    public void announce(String s) {
        for (DabPlayer player : getPlayerList().keySet()) {
            player.sendMessage(s);
        }
    }

    public void announceTitle(String mTitle, String mSub, int fadeIn, int time, int fadeOut) {
        for (DabPlayer player : getPlayerList().keySet()) {
            player.sendTitle(mTitle, mSub, fadeIn, time, fadeOut);
        }
    }

    public void playSound(Sound sound) {
        for (DabPlayer player : getPlayerList().keySet()) {
            player.playSound(sound);
        }
    }

    public void addPlayer(DabPlayer dabPlayer) {
        playerList.put(dabPlayer, PlayerState.ALIVE);
        showToOtherPlayers(dabPlayer);
    }

    public GameType getGameType() {
        return gameType;
    }

    public Map<DabPlayer, PlayerState> getPlayerList() {
        return playerList;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setSpawnPoints(List<Location> spawnPoints) {
        this.spawnPoints = spawnPoints;
    }

    public List<Location> getSpawnPoints() {
        return spawnPoints;
    }

    public void setWaitingLobby(Location location) {
        waitingLobby = location;
        spectatorLobby = waitingLobby.clone().add(0, 5, 0);
    }

    public Location getWaitingLobby() {
        return waitingLobby;
    }

    public int getAlive() {
        int sum = 0;
        for (PlayerState state : playerList.values()) {
            if (state == PlayerState.ALIVE) sum++;
        }

        return sum;
    }

    public int getDead() {
        int sum = 0;
        for (PlayerState state : playerList.values()) {
            if (state == PlayerState.DEAD) sum++;
        }

        return sum;
    }

    public boolean isAlive(DabPlayer player) {
        return playerList.get(player) == PlayerState.ALIVE;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void joinLogic(DabPlayer player) {
        addPlayer(player);
        player.teleport(getWaitingLobby());

        if (getPlayerList().size() == getMaxPlayers()) {
            new GameStartup(plugin, this, false);
        }
    }

    public void setClinchWinPlayer(UUID uuid) {
        this.clinchWinPlayer = uuid;
    }

    public UUID getClinchWinPlayer() {
        return clinchWinPlayer;
    }

    public abstract void startLogic();

    public abstract void deathLogic(DabPlayer player, boolean killed);

    public abstract void winLogic();

    public abstract void quitLogic(DabPlayer player);

    public abstract void giveKit(DabPlayer player);

    public void hideFromOtherPlayers(DabPlayer player) {
        for (DabPlayer dabPlayer : getPlayerList().keySet()) {
            dabPlayer.getPlayer().hidePlayer(player.getPlayer());
        }
    }

    public void showToOtherPlayers(DabPlayer player) {
        for (DabPlayer dabPlayer : getPlayerList().keySet()) {
            dabPlayer.getPlayer().showPlayer(player.getPlayer());
        }
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return ChatUtils.color("&f&lGame " + id + ":"
        + "\n"
        + "Type: " + gameType.name()
        + "\n"
        + "&7Map: &b" + displayName
        + "\n"
        + "&7Status: &b" + state.name()
        + "\n"
        + "&7Players: &b" + getPlayerList().size());
    }

    public Location getSpectatorLobby() {
        return spectatorLobby;
    }
}

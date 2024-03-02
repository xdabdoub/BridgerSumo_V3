package me.yhamarsheh.bridgersumo.game.modes;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.DColor;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.enums.PlayerState;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.GameType;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.managers.DamageManager;
import me.yhamarsheh.bridgersumo.objects.TeamColor;
import me.yhamarsheh.bridgersumo.runnables.RespawnRB;
import me.yhamarsheh.bridgersumo.runnables.events.MajorEventsRB;
import me.yhamarsheh.bridgersumo.runnables.events.MinorEventsRB;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import org.bukkit.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;
import java.util.stream.Collectors;

public class BlockSumo extends Game {

    private Map<DabPlayer, Integer> lives;
    private final List<TeamColor> teamColorList;
    private Location goldBlock;

    private final Random r;

    public BlockSumo(BridgerSumo plugin, String displayName, int maxPlayers) {
        super(plugin, GameType.BLOCK_SUMO, displayName, maxPlayers);
        this.lives = new HashMap<>();
        this.r = new Random();

        this.teamColorList = new ArrayList<>();
        for (DColor dColor : DColor.values()) {
            teamColorList.add(new TeamColor(dColor));
        }
    }

    @Override
    public void addPlayer(DabPlayer dabPlayer) {
        super.addPlayer(dabPlayer);
        lives.put(dabPlayer, 5);
    }

    @Override
    public void startLogic() {
        int i = 0;
        for (DabPlayer dabPlayer : getPlayerList().keySet()) {
            dabPlayer.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
            dabPlayer.getPlayer().setGameMode(GameMode.SURVIVAL);
            dabPlayer.teleport(getSpawnPoints().get(i++));

            if (i < teamColorList.size())
                teamColorList.get(i).getPlayers().add(dabPlayer);
            else teamColorList.get(r.nextInt(teamColorList.size())).getPlayers().add(dabPlayer);

            giveKit(dabPlayer);
            plugin.getScoreboardManager().createBoard(this, dabPlayer);

            dabPlayer.getBlockSumoStatistics().addGamesPlayed();
        }

        setState(GameState.PLAYING);

        new MinorEventsRB(plugin, this);
        new MajorEventsRB(plugin, this);
    }

    @Override
    public void deathLogic(DabPlayer player, boolean killed) {
        int playerLives = this.lives.get(player);
        playerLives--;
        this.lives.replace(player, playerLives);

        sortPlayerListByLives();

        player.getBlockSumoStatistics().addDeath();

        hideFromOtherPlayers(player);
        player.getPlayer().setAllowFlight(true);
        player.getPlayer().setFlying(true);
        player.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 1));
        player.getPlayer().getInventory().clear();

        player.getPlayer().getInventory().setHelmet(null);
        player.getPlayer().getInventory().setChestplate(null);
        player.getPlayer().getInventory().setLeggings(null);
        player.getPlayer().getInventory().setBoots(null);

        player.getPlayer().updateInventory();

        player.teleport(getSpectatorLobby());

        if (playerLives == 0) {
            player.getBlockSumoStatistics().setWinStreak(0);
            player.sendTitle("&c&lYOU DIED", "&7Oh oh!", 1, 3, 1);
            getPlayerList().replace(player, PlayerState.DEAD);
            player.playSound(Sound.ANVIL_LAND);
        } else {
            player.playSound(Sound.HURT_FLESH);
            new RespawnRB(plugin, this, player);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (player.isOnline() && plugin.getGamesManager().getPlayerGame(player) == this) {

                    showToOtherPlayers(player);
                    player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                    player.getPlayer().setAllowFlight(false);
                    player.getPlayer().setFlying(false);
                    giveKit(player);

                    player.teleport(getSpawnPoints().get(new Random().nextInt(getSpawnPoints().size())));
                    player.sendTitle(Messages.RESPAWNED_TITLE.toString(), Messages.RESPAWNED_SUBTITLE.toString(), 0, 1, 0);
                }
            }, 3 * 20);
        }

        if (killed) {
            if (plugin.getPlayersManager().getDamageManager().isValidDamage(player)) {
                DamageManager.LastDamageInfo lastDamageInfo = plugin.getPlayersManager().getDamageManager().getLastDamageInfo(player);

                if (lastDamageInfo.getLastDamager() == player) {
                    announce(ChatUtils.placeholders(player.getPlayer(), Messages.FELL_INTO_THE_VOID.toString()));
                } else {

                    lastDamageInfo.getLastDamager().getBlockSumoStatistics().addKill();
                    lastDamageInfo.getLastDamager().playSound(Sound.SUCCESSFUL_HIT);

                    plugin.getPlayersManager().getDamageManager().getData().remove(player.getUniqueId());

                    announce(ChatUtils.placeholders(player.getPlayer(), Messages.PUSHED_INTO_THE_VOID.toString()
                            .replace("%pusher_name%", lastDamageInfo.getLastDamager().getPlayer().getName())
                            .replace("%pusher_color%", getPlayerTeam(lastDamageInfo.getLastDamager()).getColor().getChatColor() + "")));
                }
            } else {
                announce(ChatUtils.placeholders(player.getPlayer(), Messages.FELL_INTO_THE_VOID.toString()));
            }
        }

        if (getAlive() == 1) {
            setState(GameState.ENDING);
            winLogic();
        }
    }

    @Override
    public void winLogic() {
        getBlocks().forEach(block -> block.setType(Material.AIR));
        getBlocks().clear();

        setClinchWinPlayer(null);

        DabPlayer winner = null;
        for (DabPlayer dabPlayer : getPlayerList().keySet()) {
            if (isAlive(dabPlayer)) {
                winner = dabPlayer;
                winner.getBlockSumoStatistics().addWin();

                winner.playSound(Sound.LEVEL_UP);
                winner.sendTitle("&a&lYOU WON!", "&7Winner winner, chicken dinner", 0, 1, 0);
            }

            plugin.getScoreboardManager().removeBoard(dabPlayer);
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                if (dabPlayer.isOnline() && plugin.getGamesManager().getPlayerGame(dabPlayer) == this) {
                    dabPlayer.getPlayer().getInventory().clear();
                    dabPlayer.getPlayer().getInventory().setHelmet(null);
                    dabPlayer.getPlayer().getInventory().setChestplate(null);
                    dabPlayer.getPlayer().getInventory().setLeggings(null);
                    dabPlayer.getPlayer().getInventory().setBoots(null);

                    dabPlayer.getPlayer().updateInventory();
                    dabPlayer.getPlayer().setAllowFlight(false);
                    dabPlayer.getPlayer().setFlying(false);
                    dabPlayer.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
                    showToOtherPlayers(dabPlayer);

                    if (!plugin.isBungeeEnabled()) dabPlayer.teleport(BridgerSumo.LOBBY_LOCATION);
                    else dabPlayer.getPlayer().performCommand(plugin.getConfig().getString("lobby_command"));
                }
            }, 20 * 5);
        }

        assert winner != null;
        announce(ChatUtils.placeholders(winner.getPlayer(), Messages.WINNER.toString()));

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            lives.clear();
            getPlayerList().clear();
            teamColorList.forEach(teamColor -> teamColor.getPlayers().clear());
            setState(GameState.WAITING);
        }, 20 * 6);

        setState(GameState.ENDED);
    }

    @Override
    public void quitLogic(DabPlayer player) {
        if (getState() == GameState.PLAYING) {
            announce(ChatUtils.placeholders(player.getPlayer(), Messages.LEFT_DURING_GAME.toString()
                    .replace("%players%", (getPlayerList().size()) + "")
                    .replace("%maxplayers%", getMaxPlayers() + "")));

            this.lives.replace(player, 1);
            deathLogic(player, true);
        }

        TeamColor teamColor = getPlayerTeam(player);
        if (teamColor != null) teamColor.getPlayers().remove(player);

        getPlayerList().remove(player);
        lives.remove(player);

        if (getState() == GameState.WAITING) {
            announce(ChatUtils.placeholders(player.getPlayer(), Messages.LEFT_GAME.toString()
                    .replace("%players%", (getPlayerList().size()) + "")
                    .replace("%maxplayers%", getMaxPlayers() + "")));
        }

        player.getPlayer().removePotionEffect(PotionEffectType.INVISIBILITY);
        player.getPlayer().setAllowFlight(false);
        player.getPlayer().setFlying(false);
        player.teleport(BridgerSumo.LOBBY_LOCATION);
    }

    @Override
    public void giveKit(DabPlayer player) {
        player.getPlayer().getInventory().setItem(0, ItemBuilder.from(Material.SHEARS).unbreakable().build());
        player.getPlayer().getInventory().setItem(1, new ItemStack(Material.WOOL, 64));

        TeamColor teamColor = getPlayerTeam(player);
        player.getPlayer().getInventory().setHelmet(teamColor.getArmor()[0]);
        player.getPlayer().getInventory().setChestplate(teamColor.getArmor()[1]);
        player.getPlayer().getInventory().setLeggings(teamColor.getArmor()[2]);
        player.getPlayer().getInventory().setBoots(teamColor.getArmor()[3]);
    }

    public void setGoldBlock(Location goldBlock) {
        this.goldBlock = goldBlock;
    }

    public Location getGoldBlock() {
        return goldBlock;
    }

    public int getPlayerLives(DabPlayer dabPlayer) {
        return lives.get(dabPlayer);
    }
    public String getPlayerLivesAsString(DabPlayer dabPlayer) {
        int lives = getPlayerLives(dabPlayer);

        if (lives >= 4) {
            return String.format(Messages.HIGH_LIVES_COLOR.toString(), lives);
        } else if (lives >= 2) {
            return String.format(Messages.MEDIUM_LIVES_COLOR.toString(), lives);
        } else if (lives == 1) {
            return String.format(Messages.LOW_LIVES_COLOR.toString(), lives);
        } else {
            return Messages.DEAD_SYMBOL.toString();
        }
    }

    public void sortPlayerListByLives() {
        lives = getLives().entrySet().stream()
                .sorted(Comparator.comparingInt((Map.Entry<DabPlayer, Integer> entry) -> entry.getValue()).reversed())
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (a, b) -> b,
                        LinkedHashMap::new
                ));
    }

    public TeamColor getPlayerTeam(DabPlayer dabPlayer) {
        for (TeamColor teamColor : teamColorList) {
            if (teamColor.getPlayers().contains(dabPlayer)) return teamColor;
        }

        return null;
    }

    public Map<DabPlayer, Integer> getLives() {
        return lives;
    }
}

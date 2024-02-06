package me.yhamarsheh.bridgersumo.game;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.Debug;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.runnables.GoldBlockWin;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.PacketPlayOutBlockBreakAnimation;
import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Random;
import java.util.UUID;

public class GameListener implements Listener {

    private final BridgerSumo plugin;
    private final int NORMAL_VOID_HEIGHT;
    private final int BLOCK_SUMO_VOID_HEIGHT;
    private final int MAX_BLOCK_HEIGHT;

    private final String CHAT_FORMAT;

    public GameListener(BridgerSumo plugin) {
        this.plugin = plugin;
        this.NORMAL_VOID_HEIGHT = plugin.getConfig().getInt("normal_void_height");
        this.BLOCK_SUMO_VOID_HEIGHT = plugin.getConfig().getInt("block_sumo_void_height");
        this.MAX_BLOCK_HEIGHT = plugin.getConfig().getInt("max_place_height");

        this.CHAT_FORMAT = plugin.getConfig().getString("game_chat_format");

        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onVoid(PlayerMoveEvent e) {
        Player player = e.getPlayer();
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;

        if (game.getGameType() == GameType.BLOCK_SUMO) {
            if (dabPlayer.getLocation().getY() <= game.getSpawnPoints().get(0).getY() - BLOCK_SUMO_VOID_HEIGHT && game.getState() != GameState.ENDED) {
                if (game.getState() == GameState.PLAYING && !player.getAllowFlight()) {
                    game.deathLogic(dabPlayer, true);
                } else {
                    player.teleport(game.getWaitingLobby());
                }
            }
        } else if (game.getGameType() == GameType.NORMAL) {
            if (dabPlayer.getLocation().getY() <= game.getSpawnPoints().get(0).getY() - NORMAL_VOID_HEIGHT && game.getState() != GameState.ENDED) {
                if (game.getState() == GameState.PLAYING && !player.getAllowFlight()) {
                    game.deathLogic(dabPlayer, true);
                } else {
                    player.teleport(game.getWaitingLobby());
                }
            }
        }

        if (game.getGameType() != GameType.BLOCK_SUMO) return;
        if (game.getState() != GameState.PLAYING) return;
        if (dabPlayer.getPlayer().getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.GOLD_BLOCK) return;
        if (game.getClinchWinPlayer() != null && (game.getClinchWinPlayer().toString().equals(player.getUniqueId().toString()))) return;
        if (player.getAllowFlight()) return;

        if (game.getClinchWinPlayer() == null || game.getClinchWinPlayer() == Debug.DUMMY_UUID.getUuid()) {
            game.setClinchWinPlayer(player.getUniqueId());
            new GoldBlockWin(plugin, dabPlayer, game);
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;
        if (e.getCause() == EntityDamageEvent.DamageCause.ENTITY_ATTACK) return;

        Player player = (Player) e.getEntity();
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;

        if (player.getAllowFlight() || game.getState() == GameState.WAITING || game.getState() == GameState.STARTING) {
            e.setCancelled(true);
            return;
        }

        e.setDamage(0);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        e.blockList().clear();
    }

    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        Entity entity = e.getEntity();
        if (!(entity instanceof Player)) return;

        Player player = (Player) e.getEntity();

        if (!(e.getDamager() instanceof Player)) {
            if (e.getDamager().getType() == EntityType.FIREBALL || e.getDamager().getType() == EntityType.SNOWBALL) {
                Projectile projectile = (Projectile) e.getDamager();
                if (!(projectile.getShooter() instanceof Player)) return;
                Player shooter = (Player) projectile.getShooter();

                DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());
                DabPlayer dDamager = plugin.getPlayersManager().getPlayer(shooter.getUniqueId());

                Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
                if (game == null) return;

                if (shooter.getAllowFlight() || game.getState() == GameState.WAITING || game.getState() == GameState.STARTING || player.getAllowFlight()) {
                    e.setCancelled(true);
                    return;
                }

                if (game.getGameType() == GameType.BLOCK_SUMO) plugin.getPlayersManager().getDamageManager().handleDamage(dabPlayer, dDamager);
                e.setDamage(0);
            }
            return;
        }

        Player damager = (Player) e.getDamager();

        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());
        DabPlayer dDamager = plugin.getPlayersManager().getPlayer(damager.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;

        if (damager.getAllowFlight() || game.getState() == GameState.WAITING || game.getState() == GameState.STARTING || player.getAllowFlight()) {
            e.setCancelled(true);
            return;
        }

        if (game.getGameType() == GameType.BLOCK_SUMO) plugin.getPlayersManager().getDamageManager().handleDamage(dabPlayer, dDamager);
        e.setDamage(0);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Player player = e.getPlayer();
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;
        game.quitLogic(dabPlayer);
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;
        if (game.getGameType() != GameType.BLOCK_SUMO) return;
        if (game.getState() != GameState.PLAYING) {
            e.setCancelled(true);
            return;
        }

        Block block = e.getBlock();
        if (block.getLocation().getY() >= game.getSpawnPoints().get(0).getY() + MAX_BLOCK_HEIGHT) {
            e.setCancelled(true);
            return;
        }

        for (Location spawnPoint : game.getSpawnPoints()) {
            if (spawnPoint.getX() == block.getLocation().getX() && spawnPoint.getZ() == block.getZ() &&
                    (spawnPoint.getY() == block.getY() || spawnPoint.getY() == (block.getY() + 1))) {
                e.setCancelled(true);
                return;
            }
        }

        if (block.getType() == Material.TNT) {
            e.setCancelled(true);
            block.getWorld().spawnEntity(block.getLocation(), EntityType.PRIMED_TNT);

            ItemStack itemStack = player.getItemInHand().clone();
            itemStack.setAmount(itemStack.getAmount() - 1);

            player.setItemInHand(itemStack);
            return;
        }

        if (block.getType() != Material.WOOL) return;

        BlockState blockState = block.getState();
        blockState.setData(new Wool(getRandomWoolColor()));
        blockState.update();

        player.getInventory().getItemInHand().setAmount(64);
        player.updateInventory();

        game.getBlocks().add(block);

        if (isNearGoldBlock((BlockSumo) game, block.getLocation())) displayBlockBreakAnimation(block, game);
    }

    @EventHandler
    public void onBreak(BlockBreakEvent e) {
        Player player = e.getPlayer();
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;
        if (game.getGameType() != GameType.BLOCK_SUMO || player.getAllowFlight()) {
            e.setCancelled(true);
            return;
        }

        Block block = e.getBlock();
        if (game.getBlocks().contains(block)) {
            block.setType(Material.AIR);
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onHungerLoss(FoodLevelChangeEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) {
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        game.announce(ChatUtils.placeholders(player, CHAT_FORMAT
                .replace("%message%", e.getMessage())));
    }

    @EventHandler
    public void onInteraction(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;
        if (game.getGameType() != GameType.BLOCK_SUMO) return;
        if (game.getState() != GameState.PLAYING) {
            e.setCancelled(true);
            return;
        }

        if (e.getAction() == Action.RIGHT_CLICK_AIR ||  e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() == null || e.getItem().getType() != Material.FIREBALL) return;

            Location eye = player.getEyeLocation();
            Location loc = eye.add(eye.getDirection().multiply(1.2));
            Fireball fireball = (Fireball) loc.getWorld().spawnEntity(loc, EntityType.FIREBALL);
            fireball.setVelocity(loc.getDirection().normalize().multiply(2));
            fireball.setShooter(player);

            ItemStack itemStack = e.getItem().clone();
            itemStack.setAmount(itemStack.getAmount() - 1);

            player.setItemInHand(itemStack);
        }
    }

    private DyeColor getRandomWoolColor() {
        DyeColor[] colors = DyeColor.values();

        return colors[new Random().nextInt(colors.length)];
    }

    private boolean isNearGoldBlock(BlockSumo game, Location location) {
        Location goldBlock = game.getGoldBlock();

        if ((goldBlock.getZ() + 1 == location.getZ() || goldBlock.getZ() - 1 == location.getZ()) && goldBlock.getX() == location.getX()) return true;
        if ((goldBlock.getX() + 1 == location.getX() || goldBlock.getX() - 1 == location.getX()) && goldBlock.getZ() == location.getZ()) return true;
        if ((goldBlock.getZ() + 1 == location.getZ() || goldBlock.getZ() - 1 == location.getZ()) && goldBlock.getX() == location.getX() &&
        goldBlock.getY() + 1 == location.getY()) return true;
        if ((goldBlock.getX() + 1 == location.getX() || goldBlock.getX() - 1 == location.getX()) && goldBlock.getZ() == location.getZ() &&
                goldBlock.getY() + 1 == location.getY()) return true;

        return goldBlock.getX() == location.getX() && goldBlock.getZ() == location.getZ() && (goldBlock.getY() + 1 == location.getY()
    || goldBlock.getY() + 2 == location.getY() || goldBlock.getY() + 3 == location.getY());
    }

    private void displayBlockBreakAnimation(Block placedBlock, Game game) {
        int entityId = getBlockEntityId(placedBlock);

        new BukkitRunnable() {
            int i = 0;
            int breakAmount = 0;

            @Override
            public void run() {
                i +=2;
                breakAmount+=2;

                PacketPlayOutBlockBreakAnimation packet = new PacketPlayOutBlockBreakAnimation(entityId, new BlockPosition(placedBlock.getX(), placedBlock.getY(), placedBlock.getZ()), breakAmount);

                for (Player p : placedBlock.getLocation().getWorld().getPlayers()) {
                    if (p.getLocation().distance(placedBlock.getLocation()) < 64) {
                        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
                    }
                }

                if (i >= 10) {
                    PacketPlayOutBlockBreakAnimation clearPacket = new PacketPlayOutBlockBreakAnimation(entityId, new BlockPosition(placedBlock.getX(), placedBlock.getY(), placedBlock.getZ()), 10);
                    for (Player p : placedBlock.getLocation().getWorld().getPlayers()) {
                        if (p.getLocation().distance(placedBlock.getLocation()) < 64) {
                            ((CraftPlayer) p).getHandle().playerConnection.sendPacket(clearPacket);
                        }
                    }

                    this.cancel();
                    placedBlock.setType(Material.AIR);
                    game.getBlocks().remove(placedBlock);
                }
            }
        }.runTaskTimer(plugin, 0, 15);
    }

    private static int getBlockEntityId(Block block) {
        return   ((block.getX() & 0xFFF) << 20)
                | ((block.getZ() & 0xFFF) << 8)
                | (block.getY() & 0xFF);
    }
}

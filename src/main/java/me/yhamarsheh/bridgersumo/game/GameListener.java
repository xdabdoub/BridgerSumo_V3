package me.yhamarsheh.bridgersumo.game;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.enums.Debug;
import me.yhamarsheh.bridgersumo.enums.GameState;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.runnables.GoldBlockWin;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import me.yhamarsheh.bridgersumo.utilities.Logger;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.*;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftFireball;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftTNTPrimed;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.Wool;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.Objects;
import java.util.Random;
import java.util.UUID;

public class GameListener implements Listener {

    private final BridgerSumo plugin;
    private final int NORMAL_VOID_HEIGHT;
    private final int BLOCK_SUMO_VOID_HEIGHT;
    private final int MAX_BLOCK_HEIGHT;

    private final String CHAT_FORMAT;

    // Fireball Attributes
    private final double FB_EXPLOSION_SIZE;
    private final boolean FB_MAKE_FIRE;
    private final double FB_HORIZONTAL;
    private final double FB_VERTICAL;
    private final double FB_SPEED_MULTIPLIER;

    // TNT Attributes
    private final double TNT_BARY_CENTER_ALTERNATION_IN_Y;
    private final double TNT_STRENGTH_REDUCTION_CONSTANT;
    private final double TNT_Y_AXIS_REDUCTION_CONSTANT;

    // Border
    private final int BORDER_SIZE;

    public GameListener(BridgerSumo plugin) {
        this.plugin = plugin;
        this.NORMAL_VOID_HEIGHT = plugin.getConfig().getInt("normal_void_height");
        this.BLOCK_SUMO_VOID_HEIGHT = plugin.getConfig().getInt("block_sumo_void_height");
        this.MAX_BLOCK_HEIGHT = plugin.getConfig().getInt("max_place_height");

        this.CHAT_FORMAT = plugin.getConfig().getString("game_chat_format");

        this.FB_EXPLOSION_SIZE = plugin.getConfig().getDouble("fireball.explosion_size");
        this.FB_MAKE_FIRE = plugin.getConfig().getBoolean("fireball.make_fire");
        this.FB_HORIZONTAL = plugin.getConfig().getDouble("fireball.horizontal");
        this.FB_VERTICAL = plugin.getConfig().getDouble("fireball.vertical");
        this.FB_SPEED_MULTIPLIER = plugin.getConfig().getDouble("fireball.speed_multiplier");

        this.TNT_BARY_CENTER_ALTERNATION_IN_Y = plugin.getConfig().getDouble("tnt.bary_center_alternation_in_y");
        this.TNT_STRENGTH_REDUCTION_CONSTANT = plugin.getConfig().getDouble("tnt.strength_reduction_constant");
        this.TNT_Y_AXIS_REDUCTION_CONSTANT = plugin.getConfig().getDouble("tnt.y_axis_reduction_constant");

        this.BORDER_SIZE = plugin.getConfig().getInt("block_sumo_max_distance_from_center");

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

            if (dabPlayer.getLocation().distance(((BlockSumo)game).getGoldBlock()) > BORDER_SIZE + 5) {
                dabPlayer.sendMessage(Messages.WHY_YOU_FLYING.toString());
                Location loc = player.getLocation();
                Vector vector = loc.getDirection().multiply(-1);
                vector.normalize();
                vector.multiply(5);
                loc.add(vector);
                player.teleport(loc);
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
    public void onPickup(PlayerPickupItemEvent e) {
        Player player = e.getPlayer();

        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());
        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;

        if (!game.isSpectating(dabPlayer)) return;
        e.setCancelled(true);
        /// sssss
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        Player player = e.getPlayer();

        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());
        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;
        e.setCancelled(true);
    }

    @EventHandler
    public void onEntityExplode(EntityExplodeEvent e) {
        if (e.getEntity().getType() == EntityType.PRIMED_TNT) {

            TNTPrimed tnt = (TNTPrimed) e.getEntity();
            Player shooter = (Player) tnt.getSource();

            if (shooter == null) return;
            DabPlayer dDamager = plugin.getPlayersManager().getPlayer(shooter.getUniqueId());

            Game game = plugin.getGamesManager().getPlayerGame(dDamager);
            if (game == null) return;

            e.blockList().removeIf(block -> !game.getBlocks().contains(block));
        } else if (e.getEntity().getType() == EntityType.FIREBALL) {

            Fireball fireball = (Fireball) e.getEntity();
            Player shooter = (Player) fireball.getShooter();

            if (shooter == null) return;
            DabPlayer dDamager = plugin.getPlayersManager().getPlayer(shooter.getUniqueId());

            Game game = plugin.getGamesManager().getPlayerGame(dDamager);
            if (game == null) return;
            e.blockList().removeIf(block -> !game.getBlocks().contains(block));
        }
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
            } else if (e.getDamager().getType() == EntityType.PRIMED_TNT) {
                TNTPrimed tntPrimed = (TNTPrimed) e.getDamager();
                Player shooter = (Player) tntPrimed.getSource();

                DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());
                DabPlayer dDamager = plugin.getPlayersManager().getPlayer(shooter.getUniqueId());

                Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
                if (game == null) return;

                if (shooter.getAllowFlight() || game.getState() == GameState.WAITING || game.getState() == GameState.STARTING || player.getAllowFlight()) {
                    e.setCancelled(true);
                    return;
                }

                if (game.getGameType() == GameType.BLOCK_SUMO) plugin.getPlayersManager().getDamageManager().handleDamage(dabPlayer, dDamager);

                Vector distance = player.getLocation().subtract(0, TNT_BARY_CENTER_ALTERNATION_IN_Y, 0).toVector().subtract(tntPrimed.getLocation().toVector());
                Vector direction = distance.clone().normalize();
                double force = ((tntPrimed.getYield() * tntPrimed.getYield()) / (TNT_STRENGTH_REDUCTION_CONSTANT + distance.length()));
                Vector resultingForce = direction.clone().multiply(force);
                resultingForce.setY(resultingForce.getY() / (distance.length() + TNT_Y_AXIS_REDUCTION_CONSTANT));
                player.setVelocity(resultingForce);

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

        if (game.getGameType() == GameType.BLOCK_SUMO) {
            if (block.getLocation().distance(((BlockSumo)game).getGoldBlock()) > BORDER_SIZE) {
                e.setCancelled(true);
                return;
            }
        }

        for (Location spawnPoint : game.getSpawnPoints()) {
            if (spawnPoint.equals(block.getLocation())) {
                e.setCancelled(true);
                return;
            }
        }

        if (block.getType() == Material.TNT) {
            block.setType(Material.AIR);

            TNTPrimed tnt = Objects.requireNonNull(e.getBlock().getLocation().getWorld()).spawn(e.getBlock().getLocation().add(0.5, 0, 0.5), TNTPrimed.class);
            tnt.setFuseTicks(45);
            setSource(tnt, player);
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
    public void onFireballHit(ProjectileHitEvent e) {
        if(!(e.getEntity() instanceof Fireball)) return;
        Location location = e.getEntity().getLocation();

        ProjectileSource projectileSource = e.getEntity().getShooter();
        if(!(projectileSource instanceof Player)) return;
        Player source = (Player) projectileSource;
        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(source.getUniqueId());

        Game game = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (game == null) return;

        Vector vector = location.toVector();

        World world = location.getWorld();

        assert world != null;
        Collection<Entity> nearbyEntities = world
                .getNearbyEntities(location, FB_EXPLOSION_SIZE, FB_EXPLOSION_SIZE, FB_EXPLOSION_SIZE);
        for(Entity entity : nearbyEntities) {
            if(!(entity instanceof Player)) continue;
            Player player = (Player) entity;
            DabPlayer dabPlayer1 = plugin.getPlayersManager().getPlayer(player.getUniqueId());
            if(plugin.getGamesManager().getPlayerGame(dabPlayer1) == null) continue;

            Vector playerVector = player.getLocation().toVector();
            Vector normalizedVector = vector.subtract(playerVector).normalize();
            Vector horizontalVector = normalizedVector.multiply(-FB_HORIZONTAL);
            double y = normalizedVector.getY();
            /*if(y < 0 ) y += 1.5;*/
            if(true) {
                y = FB_VERTICAL*1.5; // kb for not jumping
            } else {
                y = y*FB_VERTICAL*1.5; // kb for jumping
            }
            player.setVelocity(horizontalVector.setY(y));
        }
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

        BlockSumo blockSumo = (BlockSumo) game;

        if (e.getAction() == Action.RIGHT_CLICK_AIR ||  e.getAction() == Action.RIGHT_CLICK_BLOCK) {
            if (e.getItem() == null || e.getItem().getType() == Material.AIR) return;

            switch (e.getItem().getType()) {
                case FIREBALL:
                    e.setCancelled(true);

                    Fireball fb = player.launchProjectile(Fireball.class);
                    Vector direction = player.getEyeLocation().getDirection();
                    fb = setFireballDirection(fb, direction);
                    fb.setVelocity(fb.getDirection().multiply(FB_SPEED_MULTIPLIER));
                    fb.setIsIncendiary(FB_MAKE_FIRE);
                    fb.setYield((float) FB_EXPLOSION_SIZE);

                    ItemStack itemStack = e.getItem().clone();
                    itemStack.setAmount(itemStack.getAmount() - 1);

                    player.setItemInHand(itemStack);
                    break;
                case NETHER_STAR:
                    e.setCancelled(true);
                    blockSumo.getLives().replace(dabPlayer, blockSumo.getPlayerLives(dabPlayer) + 1);
                    dabPlayer.sendMessage(Messages.LIFE_ADDED_USING_NETHER_STAR.toString());

                    ItemStack itemStack2 = e.getItem().clone();
                    itemStack2.setAmount(itemStack2.getAmount() - 1);

                    player.setItemInHand(itemStack2);
                    break;
            }
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

    private void setSource(TNTPrimed tnt, Player owner) {
        EntityLiving nmsEntityLiving = (((CraftLivingEntity) owner).getHandle());
        EntityTNTPrimed nmsTNT = (((CraftTNTPrimed) tnt).getHandle());
        try {
            Field sourceField = EntityTNTPrimed.class.getDeclaredField("source");
            sourceField.setAccessible(true);
            sourceField.set(nmsTNT, nmsEntityLiving);
        } catch (Exception ex) {
            BridgerSumo.LOGGER.error(Logger.Reason.GENERIC, "An exception occurred while attempting to the set the source of a TNT block. More Info: " + ex.toString());
        }
    }

    public Fireball setFireballDirection(Fireball fireball, Vector vector) {
        EntityFireball fb = ((CraftFireball) fireball).getHandle();
        fb.dirX = vector.getX() * 0.1D;
        fb.dirY = vector.getY() * 0.1D;
        fb.dirZ = vector.getZ() * 0.1D;
        return (Fireball) fb.getBukkitEntity();
    }
}

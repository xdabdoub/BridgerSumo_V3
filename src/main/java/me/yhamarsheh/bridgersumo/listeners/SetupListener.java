package me.yhamarsheh.bridgersumo.listeners;

import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.commands.MainCMD;
import me.yhamarsheh.bridgersumo.game.Game;
import me.yhamarsheh.bridgersumo.game.GameType;
import me.yhamarsheh.bridgersumo.game.modes.BlockSumo;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import me.yhamarsheh.bridgersumo.utilities.ChatUtils;
import me.yhamarsheh.bridgersumo.utilities.LocationUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class SetupListener implements Listener {

    private final BridgerSumo plugin;
    public SetupListener(BridgerSumo plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlace(PlayerInteractEvent e) {
        Player player = e.getPlayer();

        if (!player.hasPermission("bridgersumo.admin")) return;
        if (e.getAction() != Action.RIGHT_CLICK_AIR) return;

        Location location = player.getLocation();

        DabPlayer dabPlayer = plugin.getPlayersManager().getPlayer(player.getUniqueId());

        ItemStack item = e.getItem();
        Game g = plugin.getGamesManager().getPlayerGame(dabPlayer);
        if (g != null) return;

        if (!MainCMD.getSetups().containsKey(player)) return;
        Game game = MainCMD.getSetups().get(player);
        if (item.getType() == Material.BED) {
            if (game.getSpawnPoints().size() != game.getMaxPlayers()) {
                e.setCancelled(true);
                player.sendMessage(ChatUtils.color("&cYou didn't complete setting up the spawn-points. ("
                + game.getSpawnPoints().size() + "/" + game.getMaxPlayers() + ")"));
                return;
            }

            if (game.getWaitingLobby() == null) {
                e.setCancelled(true);
                player.sendMessage(ChatUtils.color("&cYou didn't set the waiting lobby. Use the redstone in your 2nd slot"));
                return;
            }

            if (game.getGameType() == GameType.BLOCK_SUMO) {
                BlockSumo blockSumo = (BlockSumo) game;
                if (blockSumo.getGoldBlock() == null) {
                    e.setCancelled(true);
                    player.sendMessage(ChatUtils.color("&cYou didn't set the gold block location. Use the gold ingot in your 3rd slot"));
                    return;
                }
            }

            MainCMD.getSetups().remove(player);
            player.sendMessage(String.format(Messages.SETUP_COMPLETE.toString(), game.getDisplayName()));
            plugin.getGamesManager().saveGame(game);
            player.getInventory().clear();
        } else if (item.getType() == Material.ARMOR_STAND) {
            if (game.getSpawnPoints().size() >= game.getMaxPlayers()) {
                player.sendMessage(ChatUtils.color("&cYou reached the maximum spawn points. Finish the setup using the bed in your 9th slot!"));
                e.setCancelled(true);
                return;
            }

            game.getSpawnPoints().add(location);
            player.sendMessage(String.format(Messages.SPAWN_POINT_ADDED.toString(),
                    LocationUtils.encodeLocation(location),
                    game.getSpawnPoints().size(), game.getMaxPlayers()));

            ArmorStand armorStand = (ArmorStand) location.getWorld().spawnEntity(location,
                    EntityType.ARMOR_STAND);

            armorStand.setCustomName("Spawn point");
            armorStand.setCustomNameVisible(true);
            Bukkit.getScheduler().runTaskLater(plugin, armorStand::remove, 20 * 10);
        } else if (item.getType() == Material.REDSTONE) {
            game.setWaitingLobby(location);
            player.sendMessage(String.format(Messages.WAITING_LOBBY_SET.toString(),
                    LocationUtils.encodeLocation(location)));
        } else if (item.getType() == Material.GOLD_INGOT) {
            if (game.getGameType() == GameType.BLOCK_SUMO) {
                BlockSumo blockSumo = (BlockSumo) game;
                blockSumo.setGoldBlock(location.getBlock().getRelative(BlockFace.DOWN).getLocation());
                player.sendMessage(String.format(Messages.GOLD_BLOCK_SET.toString(),
                        LocationUtils.encodeLocation(location.getBlock().getRelative(BlockFace.DOWN).getLocation())));
            }
        }
        e.setCancelled(true);
    }
}

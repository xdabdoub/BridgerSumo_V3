package me.yhamarsheh.bridgersumo.objects;

import dev.triumphteam.gui.builder.item.ItemBuilder;
import me.yhamarsheh.bridgersumo.enums.DColor;
import me.yhamarsheh.bridgersumo.storage.objects.DabPlayer;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;

public class TeamColor {

    private final DColor dColor;
    private ItemStack[] armor;

    private final ArrayList<DabPlayer> players;

    public TeamColor(DColor dColor) {
        this.dColor = dColor;
        this.players = new ArrayList<>();

        this.armor = new ItemStack[4];
        setupArmor();
    }

    public DColor getColor() {
        return dColor;
    }

    public ItemStack[] getArmor() {
        return armor;
    }

    public ArrayList<DabPlayer> getPlayers() {
        return players;
    }

    public void setupArmor() {
        armor[0] = ItemBuilder.from(Material.LEATHER_HELMET).color(dColor.getColor()).unbreakable().build();
        armor[1] = ItemBuilder.from(Material.LEATHER_CHESTPLATE).color(dColor.getColor()).unbreakable().build();
        armor[2] = ItemBuilder.from(Material.LEATHER_LEGGINGS).color(dColor.getColor()).unbreakable().build();
        armor[3] = ItemBuilder.from(Material.LEATHER_BOOTS).color(dColor.getColor()).unbreakable().build();
    }
}

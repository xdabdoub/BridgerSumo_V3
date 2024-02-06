package me.yhamarsheh.bridgersumo.objects;

import org.bukkit.inventory.ItemStack;

public class EventItem {

    private final ItemStack itemStack;
    private final String displayName;
    public EventItem(ItemStack itemStack, String displayName) {
        this.itemStack = itemStack;
        this.displayName = displayName;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getDisplayName() {
        return displayName;
    }
}

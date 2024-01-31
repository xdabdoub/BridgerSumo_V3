package me.yhamarsheh.bridgersumo.utilities;

import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ChatUtils {
    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static String placeholders(Player player, String s) {
        return color(PlaceholderAPI.setPlaceholders(player, s));
    }

    public static Component component(String s) {
        return Component.text(color(s));
    }

    public static void sendTitle(String mTitle, String mSub, Player p, int fadeIn, int time, int fadeOut){
        PacketPlayOutTitle title = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.TITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatUtils.color(mTitle) + "\"}"), fadeIn, time, fadeOut);
        PacketPlayOutTitle sub = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.SUBTITLE, IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + ChatUtils.color(mSub) + "\"}"), fadeIn, time, fadeOut);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(sub);
    }
}

package me.yhamarsheh.bridgersumo.objects;

import org.bukkit.ChatColor;

public class TeamColor {

    private ChatColor color;
    public TeamColor(ChatColor color) {
        this.color = color;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getLetter() {
        return color + "" + ChatColor.BOLD + color.name().toCharArray()[0];
    }
}

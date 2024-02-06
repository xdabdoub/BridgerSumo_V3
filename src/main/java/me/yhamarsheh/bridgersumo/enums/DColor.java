package me.yhamarsheh.bridgersumo.enums;

import org.bukkit.ChatColor;
import org.bukkit.Color;

public enum DColor {

    WHITE(Color.WHITE, ChatColor.WHITE, "W"),
    RED(Color.RED, ChatColor.RED, "R"),
    BLUE(Color.BLUE, ChatColor.BLUE, "B"),
    GREEN(Color.GREEN, ChatColor.GREEN, "G"),
    YELLOW(Color.YELLOW, ChatColor.YELLOW, "Y"),
    AQUA(Color.AQUA, ChatColor.AQUA, "A"),
    MAGENTA(Color.MAROON, ChatColor.LIGHT_PURPLE, "M"),
    GRAY(Color.GRAY, ChatColor.GRAY, "G"),
    GOLD(Color.ORANGE, ChatColor.GOLD, "O"),
    PURPLE(Color.PURPLE, ChatColor.DARK_PURPLE, "P"),
    DARK_BLUE(Color.BLUE, ChatColor.DARK_BLUE, "DB"),
    DARK_GREEN(Color.GREEN, ChatColor.DARK_GREEN, "DG");

    Color color;
    ChatColor chatColor;
    String letter;

    DColor(Color color, ChatColor chatColor, String letter) {
        this.color = color;
        this.chatColor = chatColor;
        this.letter = letter;
    }

    public Color getColor() {
        return color;
    }

    public ChatColor getChatColor() {
        return chatColor;
    }

    public String getLetter() {
        return chatColor + "" + ChatColor.BOLD + letter + chatColor;
    }
}

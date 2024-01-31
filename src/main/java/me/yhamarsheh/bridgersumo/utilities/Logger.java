package me.yhamarsheh.bridgersumo.utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import java.util.logging.Level;

public class Logger {

    private java.util.logging.Logger logger;

    public Logger() {

        init();

    }

    public void init() {

        if (this.logger == null) {

            this.logger = new java.util.logging.Logger("BridgerSumo", null) {
            };

            this.logger.setParent(Bukkit.getLogger());
            this.logger.setLevel(Level.ALL);

        }

    }

    public void log(Reason reason, String message) {

        this.logger.info(reason.getColour() + reason.getPrefix() + ChatUtils.color(" " + message));

    }

    public void error(Reason reason, String message) {

        this.logger.info("[BRIDGERSUMO] " + Reason.ERROR.getColour() + Reason.ERROR.getPrefix() + reason.getPrefix() + ChatUtils.color(" " + message));

    }

    public void startUp() {

        log(Reason.GENERIC, "");
        log(Reason.GENERIC, "&b&lBy xDabDoub");
        log(Reason.GENERIC, "&b&lVersion 1.0");

    }

    public enum Reason {

        GENERIC("", ChatColor.WHITE),
        ERROR("[Error] ", ChatColor.RED),
        CONFIG("[Config] ", ChatColor.BLUE),
        SQL("[SQL] ", ChatColor.GOLD),
        KEY("[Key]", ChatColor.DARK_PURPLE);

        private String prefix;
        private ChatColor colour;

        Reason(String prefix, ChatColor colour) {

            this.prefix = prefix;
            this.colour = colour;

        }

        public String getPrefix() {

            return this.prefix;

        }

        public ChatColor getColour() {

            return this.colour;

        }

    }

}
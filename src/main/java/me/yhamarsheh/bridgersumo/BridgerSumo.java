package me.yhamarsheh.bridgersumo;

import me.yhamarsheh.bridgersumo.commands.MainCMD;
import me.yhamarsheh.bridgersumo.commands.handler.CommandRegistry;
import me.yhamarsheh.bridgersumo.game.GameListener;
import me.yhamarsheh.bridgersumo.handlers.RedisManager;
import me.yhamarsheh.bridgersumo.hooks.PAPI;
import me.yhamarsheh.bridgersumo.listeners.DataHandlingListener;
import me.yhamarsheh.bridgersumo.listeners.SetupListener;
import me.yhamarsheh.bridgersumo.locale.Messages;
import me.yhamarsheh.bridgersumo.managers.EventsManager;
import me.yhamarsheh.bridgersumo.managers.GamesManager;
import me.yhamarsheh.bridgersumo.managers.PlayersManager;
import me.yhamarsheh.bridgersumo.managers.ScoreboardManager;
import me.yhamarsheh.bridgersumo.storage.SQLDatabase;
import me.yhamarsheh.bridgersumo.utilities.LocationUtils;
import me.yhamarsheh.bridgersumo.utilities.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public class BridgerSumo extends JavaPlugin {

    public static Logger LOGGER;
    private final double PLUGIN_VERSION = getConfig().getDouble("version");

    private SQLDatabase sqlDatabase;
    private PlayersManager playersManager;
    private GamesManager gamesManager;
    private EventsManager eventsManager;
    private ScoreboardManager scoreboardManager;

    public static Location LOBBY_LOCATION;

    private CommandRegistry registry;

    private RedisManager redisManager;

    public static boolean LOCKDOWN = false;

    private final boolean bungeeEnabled = getConfig().getBoolean("bungee");

    @Override
    public void onLoad() {
        loadLogic();
    }

    @Override
    public void onEnable() {
        initialise();
        LOGGER.startUp();
    }

    @Override
    public void onDisable() {
        playersManager.disable();
        gamesManager.disable();
        eventsManager.disable();

        redisManager.closeConnection();
        scoreboardManager.disable();
    }

    private void loadLogic() {
        LOGGER = new Logger();
    }

    private void initialise() {
        saveDefaultConfig();

        sqlDatabase = new SQLDatabase(this);
        playersManager = new PlayersManager(this);
        gamesManager = new GamesManager(this);
        eventsManager = new EventsManager(this);
        scoreboardManager = new ScoreboardManager(this);

        registry = new CommandRegistry(this);

        redisManager = new RedisManager(this);

        registerCommands();
        registerListeners();

        setupMessages();

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) new PAPI(this);
        if (getConfig().getString("lobby") == null || getConfig().getString("lobby").isEmpty()) return;
        LOBBY_LOCATION = LocationUtils.decodeLocation(getConfig().getString("lobby"));
    }

    private void registerCommands() {
        registry.registerCommands(new MainCMD(this));
    }

    private void registerListeners() {
        new GameListener(this);
        new SetupListener(this);
        new DataHandlingListener(this);
    }

    public void setupMessages() {
        File file = new File(getDataFolder(), "messages.yml");
        YamlConfiguration configuration = YamlConfiguration.loadConfiguration(file);

        if (!file.exists()) {
            for (Messages message : Messages.values()) {
                configuration.set(message.getPath(), message.toString());
            }

            try {
                configuration.save(file);
            } catch (IOException ex) {
                LOGGER.error(Logger.Reason.GENERIC, "[L@95] Couldn't save the messages.yml file. More Info: " + ex.getMessage());
            }

            return;
        }

        for (Messages message : Messages.values()) {
            if (configuration.getString(message.getPath()) == null || configuration.getString(message.getPath()).isEmpty()) {
                configuration.set(message.getPath(), message.toString());

                try {
                    configuration.save(file);
                } catch (IOException ex) {
                    LOGGER.error(Logger.Reason.GENERIC, "[L@107] Couldn't save the messages.yml file. More Info: " + ex.getMessage());
                }
                continue;
            }

            message.setMessage(configuration.getString(message.getPath()));
        }
    }

    private void update() {
        if (!requiresUpdate()) return;
        getConfig().set("lobby_command", "lobby");
        saveConfig();
    }

    private boolean requiresUpdate() {
        double LATEST_VERSION = 1.1D;
        return (this.PLUGIN_VERSION < LATEST_VERSION);
    }

    public boolean isLobbySet() {
        return LOBBY_LOCATION != null;
    }
    public boolean isBungeeEnabled() {
        return bungeeEnabled;
    }

    public GamesManager getGamesManager() {
        return gamesManager;
    }

    public EventsManager getEventsManager() {
        return eventsManager;
    }

    public PlayersManager getPlayersManager() {
        return playersManager;
    }

    public ScoreboardManager getScoreboardManager() {
        return scoreboardManager;
    }

    public SQLDatabase getSqlDatabase() {
        return sqlDatabase;
    }

    public RedisManager getRedisManager() {
        return redisManager;
    }
}

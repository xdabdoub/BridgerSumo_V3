package me.yhamarsheh.bridgersumo.storage;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import me.yhamarsheh.bridgersumo.BridgerSumo;
import me.yhamarsheh.bridgersumo.utilities.Logger;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.logging.Level;

public class SQLDatabase {

    private BridgerSumo plugin;

    private HikariDataSource hikari;

    private String hostname;
    private String port;
    private String database;
    private String username;
    private String password;

    private int minimumConnections;
    private int maximumConnections;
    private long connectionTimeout;

    private final String SUMO_TABLE_NAME = "sumo_data";
    private final String BLOCK_SUMO_TABLE_NAME = "blocksumo_data";

    public SQLDatabase(BridgerSumo plugin) {
        this.plugin = plugin;

        init();
        setupPool();
        Bukkit.getScheduler().runTaskAsynchronously(plugin, this::createTables);
    }

    private void init() {
        FileConfiguration fileConfig = plugin.getConfig();

        this.username = fileConfig.getString("MySQL.user");
        this.password = fileConfig.getString("MySQL.password");
        this.database = fileConfig.getString("MySQL.database");
        this.hostname = fileConfig.getString("MySQL.host");
        this.port = fileConfig.getString("MySQL.port");

        this.minimumConnections = 5;
        this.maximumConnections = 100;
        this.connectionTimeout = 30000;
    }

    private void setupPool() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(
                "jdbc:mysql://" +
                        hostname +
                        ":" +
                        port +
                        "/" +
                        database
        );
        config.setDriverClassName("com.mysql.jdbc.Driver");
        config.setUsername(username);
        config.setPassword(password);
        config.setMinimumIdle(minimumConnections);
        config.setMaximumPoolSize(maximumConnections);
        config.setConnectionTimeout(connectionTimeout);
        config.setLeakDetectionThreshold(3000);
        config.setIdleTimeout(600000);
        config.setMaxLifetime(1800000);

        this.hikari = new HikariDataSource(config);
    }

    public void close(Connection conn, PreparedStatement ps, ResultSet res) {
        if (conn != null) try { conn.close(); } catch (SQLException ignored) {}
        if (ps != null) try { ps.close(); } catch (SQLException ignored) {}
        if (res != null) try { res.close(); } catch (SQLException ignored) {}
    }


    public Connection getConnection() throws SQLException {
        return hikari.getConnection();
    }

    public void closePool() {
        if (hikari != null && !hikari.isClosed()) {
            hikari.close();
        }
    }

    public void createSumoTable() {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = "CREATE TABLE IF NOT EXISTS " + SUMO_TABLE_NAME + " (" +
                "UUID VARCHAR(100),KILLS INT(64),DEATHS INT(64),WIN_STREAK INT(64),GAMES_PLAYED INT(64)," +
                "PRIMARY KEY (UUID))";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.executeUpdate(query);
        } catch (SQLException e) {
            plugin.getLogger().info(Level.SEVERE + "SQL Fail");
            BridgerSumo.LOGGER.error(Logger.Reason.SQL, "Failed to create the 'sumo_data' table.. More info: " + e.getMessage());
        } finally {
            close(connection, statement, null);
        }
    }

    public void createBlockSumoTable() {
        Connection connection = null;
        PreparedStatement statement = null;
        String query = "CREATE TABLE IF NOT EXISTS " + BLOCK_SUMO_TABLE_NAME + " (" +
                "UUID VARCHAR(100),KILLS INT(64),DEATHS INT(64),WIN_STREAK INT(64),GAMES_PLAYED INT(64)," +
                "PRIMARY KEY (UUID))";
        try {
            connection = getConnection();
            statement = connection.prepareStatement(query);
            statement.executeUpdate(query);
        } catch (SQLException e) {
            plugin.getLogger().info(Level.SEVERE + "SQL Fail");
            BridgerSumo.LOGGER.error(Logger.Reason.SQL, "Failed to create the 'blocksumo_data' table.. More info: " + e.getMessage());
        } finally {
            close(connection, statement, null);
        }
    }

    public boolean exists(UUID uuid, String table) {
        PreparedStatement ps = null;
        Connection connection = null;
        ResultSet results = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement("SELECT * FROM " + table + " WHERE UUID=?");
            ps.setString(1, uuid.toString());

            results = ps.executeQuery();
            return results.next();
        } catch (SQLException ex) {
            BridgerSumo.LOGGER.error(Logger.Reason.SQL, "An error has occurred while attempting to view whether the UUID@" +
                    uuid.toString() + " exits in the table@" + table + ".. More info: " + ex.getMessage());
        } finally {
            close(connection, ps, results);
        }
        return false;
    }

    public void createTables() {
        createSumoTable();
        createBlockSumoTable();
    }

    public void removeTable(String x) {
        PreparedStatement ps = null;
        Connection connection = null;
        try {
            connection = getConnection();
            ps = connection.prepareStatement("DROP TABLE " + x);
            ps.executeUpdate();
        } catch(SQLException ex) {
            BridgerSumo.LOGGER.error(Logger.Reason.SQL, "Failed to remove the 'blocksumo_data' table.. More info: " + ex.getMessage());
        } finally {
            close(connection, ps, null);
        }
    }

}
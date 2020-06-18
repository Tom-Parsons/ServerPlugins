package com.xiis.infernocosmetics.cosmetics.api;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import com.xiis.infernocosmetics.cosmetics.Hat;
import com.xiis.infernocosmetics.cosmetics.Trail;
import com.xiis.infernocosmetics.cosmetics.crates.Crate;
import com.xiis.infernocosmetics.player.PlayerInformation;
import com.xiis.infernocosmetics.util.Dev;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.sql.*;

public class Database {

    private static Connection connection = null;

    /**
     * This must be called before any other methods are run - it creates the database connection
     */
    public static void Init(){
        try {
            Connect();
            Dev.consoleMessage("Successfully connected to the cosmetic database.");
            LoadHats();
            LoadTrails();
        }catch(Exception ex){
            ex.printStackTrace();
        }
    }

    private static void Connect() throws SQLException{
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setUser("root");
        dataSource.setPassword("InfernoNetwork1803");
        dataSource.setServerName("127.0.0.1");
        dataSource.setPort(3306);
        dataSource.setDatabaseName("Development");
        dataSource.setLoginTimeout(5);
        Dev.consoleMessage("Attempting to connect to the cosmetic database.");
        connection = dataSource.getConnection();
    }

    public static void Close() throws SQLException {
        if(connection != null) connection.close();
    }

    private static void LoadHats() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("SELECT * FROM Hats ORDER BY rarity ASC, name ASC;");
        while(results.next()){
            new Hat(results.getString("id"), results.getString("name"), results.getInt("rarity"), results.getString("uuid"));
        }
        results.close();
        statement.close();
        Dev.consoleMessage("Successfully loaded hats.");
    }
    private static void LoadTrails() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet results = statement.executeQuery("SELECT * FROM Trails ORDER BY rarity ASC, name ASC;");
        while(results.next()){
            new Trail(results.getString("id"), results.getString("name"), results.getInt("rarity"), Particle.valueOf(results.getString("particle")), Material.valueOf(results.getString("material")));
        }
        results.close();
        statement.close();
        Dev.consoleMessage("Successfully loaded trails.");
    }

    /**
     * Adds a player to the database with default values
     * @param player The player to add to the database
     * @throws Exception, if there is a problem with the database connection
     */
    public static void AddPlayerToDatabase(Player player) throws Exception {
        if(connection != null) {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO Users VALUES('" + player.getUniqueId().toString() + "', 0, 0, 0, 0, 100, 'None', 'None');");
            statement.close();
            PlayerInformation.connected.put(player, new PlayerInformation(player, 0, 0, 0, 0, "None", "None"));
        }else{
            throw new Exception("The connection to the database has not been setup! Please call Init()");
        }
    }

    /**
     * Loads a player into the plugin
     * @param player The player to load
     * @return Returns true if the player exists in the database
     * @throws Exception
     */
    public static boolean LoadPlayer(Player player) throws Exception {
        if(connection != null) {
            Statement statement = connection.createStatement();
            ResultSet results = statement.executeQuery("SELECT * FROM Users WHERE uuid = '" + player.getUniqueId().toString() + "';");
            if (results.next()) {
                PlayerInformation.connected.put(player,
                        new PlayerInformation(player,
                                results.getInt("commonKeys"),
                                results.getInt("uncommonKeys"),
                                results.getInt("rareKeys"),
                                results.getInt("epicKeys"),
                                results.getString("activeHat"),
                                results.getString("activeTrail")));
            }else{
                return false;
            }
            results.close();
            results = statement.executeQuery("SELECT * FROM UserHats WHERE uuid = '" + player.getUniqueId().toString() + "';");
            while(results.next()){
                PlayerInformation.connected.get(player).addHat(results.getString("hatID"));
            }
            results.close();
            results = statement.executeQuery("SELECT * FROM UserTrails WHERE uuid = '" + player.getUniqueId().toString() + "';");
            while(results.next()){
                PlayerInformation.connected.get(player).addTrail(results.getString("trailID"));
            }
            results.close();
            statement.close();
        }else{
            throw new Exception("The connection to the database has not been setup! Please call Init()");
        }
        return true;
    }

    /**
     * Adds a hat to the player hat database, indicating that the player owns the specified hat
     * @param UUID The UUID of the player
     * @param hatID The ID of the hat the player now owns
     * @throws Exception, if there is a problem with the database connection
     */
    public static void AddPlayerHat(String UUID, String hatID) throws Exception {
        if(connection != null){
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO UserHats (uuid, hatID) VALUES('" + UUID + "', '" + hatID + "');");
            statement.close();
        }else{
            throw new Exception("The connection to the database has not been setup! Please call Init()");
        }
    }

    /**
     * Removes a hat from the player hat database, the player will no longer own this hat
     * @param UUID The UUID of the player
     * @param hatID The ID of the hat the player no longer owns
     * @throws Exception, if there is a problem with the database connection
     */
    public static void RemovePlayerHat(String UUID, String hatID) throws Exception {
        if(connection != null){
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM UserHats WHERE uuid='" + UUID + "' AND hatID='" + hatID + "';");
            statement.close();
        }else{
            throw new Exception("The connection to the database has not been setup! Please call Init()");
        }
    }

    /**
     * Adds a hat to the player hat database, indicating that the player owns the specified hat
     * @param UUID The UUID of the player
     * @param trailID The ID of the hat the player now owns
     * @throws Exception, if there is a problem with the database connection
     */
    public static void AddPlayerTrail(String UUID, String trailID) throws Exception {
        if(connection != null){
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO UserTrails (uuid, trailID) VALUES('" + UUID + "', '" + trailID + "');");
            statement.close();
        }else{
            throw new Exception("The connection to the database has not been setup! Please call Init()");
        }
    }

    /**
     * Removes a hat from the player hat database, the player will no longer own this hat
     * @param UUID The UUID of the player
     * @param trailID The ID of the hat the player no longer owns
     * @throws Exception, if there is a problem with the database connection
     */
    public static void RemovePlayerTrail(String UUID, String trailID) throws Exception {
        if(connection != null){
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM UserTrails WHERE uuid='" + UUID + "' AND trailID='" + trailID + "';");
            statement.close();
        }else{
            throw new Exception("The connection to the database has not been setup! Please call Init()");
        }
    }

    public static void SavePlayerData(PlayerInformation pi) throws Exception {
        if(connection != null){
            Statement statement = connection.createStatement();
            statement.execute("UPDATE Users SET " +
                    "commonKeys=" + pi.getKeys(Crate.COMMON) +
                    ", uncommonKeys=" + pi.getKeys(Crate.UNCOMMON) +
                    ", rareKeys=" + pi.getKeys(Crate.RARE) +
                    ", epicKeys=" + pi.getKeys(Crate.EPIC) +
                    ", activeHat='" + pi.getActiveHat() +
                    "', activeTrail='" + pi.getActiveTrail() +
                    "' WHERE uuid='" + pi.getPlayer().getUniqueId().toString() + "';");
            statement.close();
        }else{
            throw new Exception("The connection to the database has not been setup! Please call Init()");
        }
    }

}

package network.inferno.lobby.parkour;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class ParkourManager {

    private static network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);

    public static void setup(){
        LoadParkours();
    }

    public static void LoadParkours(){
        try{
            String exe = "SELECT * FROM inferno_parkour";
            ResultSet rs = core.dm.connection.createStatement().executeQuery(exe);
            while(rs.next()){
                String id = rs.getString("id");
                Location start = new Location(Bukkit.getWorld("world"), rs.getInt("startx"), rs.getInt("starty"), rs.getInt("startz"));
                Location end = new Location(Bukkit.getWorld("world"), rs.getInt("endx"), rs.getInt("endy"), rs.getInt("endz"));
                Location leaderboard = new Location(Bukkit.getWorld("world"), rs.getInt("leaderboardx"), rs.getInt("leaderboardy"), rs.getInt("leaderboardz"));
                HashMap<Integer, Location> checkpoints = new HashMap<Integer, Location>();
                String exe2 = "SELECT * FROM inferno_parkour_checkpoints WHERE parkour_id='" + id + "'";
                ResultSet resultSet = core.dm.connection.createStatement().executeQuery(exe2);
                while(resultSet.next()){
                    checkpoints.put(resultSet.getInt("checkpoint_number"), new Location(Bukkit.getWorld("world"), resultSet.getInt("x"), resultSet.getInt("y"), resultSet.getInt("z")));
                }
                Parkour.parkours.add(new Parkour(id, start, end, leaderboard, checkpoints));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public static void SaveParkour(Parkour parkour){
        try{
            String exe = "SELECT * FROM inferno_parkour WHERE id='" + parkour.getID() + "'";
            ResultSet rs = core.dm.connection.createStatement().executeQuery(exe);
            if(rs.next()){
                PreparedStatement statement = core.dm.connection.prepareStatement("UPDATE inferno_parkour SET " +
                        "startx='" + parkour.getStart().getBlockX() + "', " +
                        "starty='" + parkour.getStart().getBlockY() + "', " +
                        "startz='" + parkour.getStart().getBlockZ() + "', " +
                        "endx='" + parkour.getEnd().getBlockX() + "', " +
                        "endy='" + parkour.getEnd().getBlockY() + "', " +
                        "endz='" + parkour.getEnd().getBlockZ() + "'," +
                        "leaderboardx='" + parkour.getLeaderboardLocation().getBlockZ() + "'," +
                        "leaderboardy='" + parkour.getLeaderboardLocation().getBlockZ() + "'," +
                        "leaderboardz='" + parkour.getLeaderboardLocation().getBlockZ() + "'," +
                        " WHERE id='" + parkour.getID() + "'");
                statement.executeUpdate();
                statement.close();
            }else{
                core.dm.connection.createStatement().executeUpdate("INSERT INTO inferno_parkour (id,startx,starty,startz,endx,endy,endz,leaderboardx,leaderboardy,leaderboardz) VALUES('" + parkour.getID() +"'," +
                        parkour.getStart().getBlockX() + "," +
                        parkour.getStart().getBlockY() + "," +
                        parkour.getStart().getBlockZ() + "," +
                        parkour.getEnd().getBlockX() + "," +
                        parkour.getEnd().getBlockY() + "," +
                        parkour.getEnd().getBlockZ() + "," +
                        parkour.getLeaderboardLocation().getBlockY() + "," +
                        parkour.getLeaderboardLocation().getBlockY() + "," +
                        parkour.getLeaderboardLocation().getBlockY() +
                        ")");
            }
            PreparedStatement statement2 = core.dm.connection.prepareStatement("DELETE FROM inferno_parkour_checkpoints WHERE parkour_id='" + parkour.getID() + "'");
            statement2.executeUpdate();
            statement2.close();
            for(int i : parkour.getCheckpoints().keySet()){
                core.dm.connection.createStatement().executeUpdate("INSERT INTO inferno_parkour_checkpoints (parkour_id,checkpoint_number,x,y,z) VALUES('" + parkour.getID() + "', " +
                        i + "," +
                        parkour.getCheckpoints().get(i).getBlockX() + "," +
                        parkour.getCheckpoints().get(i).getBlockY() + "," +
                        parkour.getCheckpoints().get(i).getBlockZ() + ")");
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public static void DeleteParkour(Parkour parkour){
        try{
            PreparedStatement statement = core.dm.connection.prepareStatement("DELETE FROM inferno_parkour WHERE id='" + parkour.getID() + "'");
            statement.executeUpdate();
            statement.close();
            PreparedStatement statement2 = core.dm.connection.prepareStatement("DELETE FROM inferno_parkour_checkpoints WHERE parkour_id='" + parkour.getID() + "'");
            statement2.executeUpdate();
            statement2.close();
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public static void SaveHighscore(Parkour parkour, Player p, float time){
        try{
            String exe = "SELECT * FROM inferno_parkour_highscores WHERE parkour_id='" + parkour.getID() + "' AND uuid='" + p.getUniqueId() + "'";
            ResultSet rs = core.dm.connection.createStatement().executeQuery(exe);
            if(rs.next()){
                float prevTime = rs.getFloat("time");
                if(time < prevTime){
                    PreparedStatement statement = core.dm.connection.prepareStatement("UPDATE inferno_parkour_highscores SET " +
                            "time=" + time + " WHERE parkour_id='" + parkour.getID() + "' AND uuid='" + p.getUniqueId() + "'");
                    statement.executeUpdate();
                    statement.close();
                    p.sendMessage(core.format.label() + core.format.colorize("&7You got a new highscore of &6" + time + " &7seconds!."));
                }
            }else{
                core.dm.connection.createStatement().executeUpdate("INSERT INTO inferno_parkour_highscores (parkour_id,uuid,time) VALUES('" + parkour.getID() +"', '" + p.getUniqueId() + "', " + time + ")");
                p.sendMessage(core.format.label() + core.format.colorize("&7You got a new highscore of &6" + time + " &7seconds!."));
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public static HashMap<UUID, Float> LoadHighscores(Parkour parkour){
        HashMap<UUID, Float> highscores = new HashMap<UUID, Float>();
        try{
            String exe = "SELECT * FROM inferno_parkour_highscores WHERE parkour_id='" + parkour.getID() + "'";
            ResultSet rs = core.dm.connection.createStatement().executeQuery(exe);
            while(rs.next()){
                String uuid = rs.getString("uuid");
                float highscore = rs.getFloat("time");
                highscores.put(UUID.fromString(uuid), highscore);
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return highscores;
    }

    public static LinkedHashMap<UUID, Float> LoadBestHighscores(Parkour parkour){
        LinkedHashMap<UUID, Float> highscores = new LinkedHashMap<UUID, Float>();
        try{
            String exe = "SELECT * FROM inferno_parkour_highscores WHERE parkour_id='" + parkour.getID() + "' ORDER BY time ASC";
            ResultSet rs = core.dm.connection.createStatement().executeQuery(exe);
            int i = 0;
            while(rs.next() && i < 3){
                String uuid = rs.getString("uuid");
                float highscore = rs.getFloat("time");
                highscores.put(UUID.fromString(uuid), highscore);
                i++;
            }
        }catch(SQLException ex){
            ex.printStackTrace();
        }
        return highscores;
    }

}

package network.inferno.lobby.parkour;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.UUID;

public class ParkourLeaderboard {

    private LinkedHashMap<UUID, Float> _highscores = new LinkedHashMap<UUID, Float>();

    private Location location;

    private ArmorStand title;
    private ArmorStand player1;
    private ArmorStand player2;
    private ArmorStand player3;

    public ParkourLeaderboard(Location loc, LinkedHashMap<UUID, Float> highscores){
        _highscores = highscores;
        location = loc;
        CreateStands();
    }

    private void CreateStands(){
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        title = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 1.5, location.getBlockZ() + 0.5), EntityType.ARMOR_STAND);
        SetArmorstandData(title, ChatColor.GOLD + ChatColor.BOLD.toString() + "Leaderboard");
        int i = 1;
        if(_highscores.size() == 0){
            player1 = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 1.2, location.getBlockZ() + 0.5), EntityType.ARMOR_STAND);
            SetArmorstandData(player1, "No highscores yet!");
        }else {
            for (UUID p : _highscores.keySet()) {
                switch (i) {
                    case 1:
                        player1 = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 1.2, location.getBlockZ() + 0.5), EntityType.ARMOR_STAND);
                        SetArmorstandData(player1, ChatColor.AQUA + "#1 " + ChatColor.WHITE + core.format.colorize(core.prefix.getPrefix(p)) + core.nm.getPlayerName(p) + ChatColor.WHITE + " - " + ChatColor.YELLOW + _highscores.get(p));
                        break;
                    case 2:
                        player2 = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 0.9, location.getBlockZ() + 0.5), EntityType.ARMOR_STAND);
                        SetArmorstandData(player2, ChatColor.AQUA + "#2 " + ChatColor.WHITE + core.format.colorize(core.prefix.getPrefix(p)) + core.nm.getPlayerName(p) + ChatColor.WHITE + " - " + ChatColor.YELLOW + _highscores.get(p));
                        break;
                    case 3:
                        player3 = (ArmorStand) location.getWorld().spawnEntity(new Location(location.getWorld(), location.getBlockX() + 0.5, location.getBlockY() + 0.6, location.getBlockZ() + 0.5), EntityType.ARMOR_STAND);
                        SetArmorstandData(player3, ChatColor.AQUA + "#3 " + ChatColor.WHITE + core.format.colorize(core.prefix.getPrefix(p)) + core.nm.getPlayerName(p) + ChatColor.WHITE + " - " + ChatColor.YELLOW + _highscores.get(p));
                        break;
                }
                i++;
            }
        }
    }

    public void Disable(){
        title.remove();
        if(player1 != null)
            player1.remove();
        if(player2 != null)
            player2.remove();
        if(player3 != null)
            player3.remove();
    }

    public void Reset(){
        Disable();
        CreateStands();;
    }

    private void SetArmorstandData(ArmorStand stand, String name){
        stand.setCustomName(name);
        stand.setCustomNameVisible(true);
        stand.setInvulnerable(true);
        stand.setVisible(false);
        stand.setGravity(false);
        stand.setCollidable(false);
        stand.setSmall(true);
    }

}

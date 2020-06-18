package network.inferno.npcs.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class LocationUtil {

    public static String Serialize(Location loc) {
        return loc.getWorld().getName() + ":" + loc.getX() + ":" + loc.getY() + ":" + loc.getZ() + ":" + loc.getYaw() + ":" + loc.getPitch();
    }

    public static Location Deserialize(String s) {
        String[] split = s.split(":");
        return new Location(Bukkit.getWorld(split[0]), Double.valueOf(split[1]), Double.valueOf(split[2]), Double.valueOf(split[3]), Float.valueOf(split[4]), Float.valueOf(split[5]));
    }

}


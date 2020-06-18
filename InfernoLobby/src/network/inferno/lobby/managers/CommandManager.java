package network.inferno.lobby.managers;

import network.inferno.lobby.commands.ParkourCommand;
import network.inferno.lobby.parkour.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;

public class CommandManager {

    public static void registerCommands() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register("parkour", new ParkourCommand("parkour"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}

package network.inferno.npcs.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import java.lang.reflect.Field;

public class CommandManager {

    public static void RegisterCommands() {
        try {
            final Field bukkitCommandMap = Bukkit.getServer().getClass().getDeclaredField("commandMap");

            bukkitCommandMap.setAccessible(true);
            CommandMap commandMap = (CommandMap) bukkitCommandMap.get(Bukkit.getServer());

            commandMap.register("npc", new NPCCommand("npc"));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}

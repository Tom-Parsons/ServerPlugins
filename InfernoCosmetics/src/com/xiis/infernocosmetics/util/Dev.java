package com.xiis.infernocosmetics.util;

import com.xiis.infernocosmetics.main.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Dev {
    public static boolean ENABLE_DEV = true;

    public Dev() {
    }

    public static void message(Object m) {
        if (!ENABLE_DEV)
            return;
        for (Player p : Bukkit.getOnlinePlayers()) {
            if ((p.getName().equals("Xiis")) || (p.getName().equals("Disarranged"))) {
                p.sendMessage(Main.PREFIX + m.toString());
            }
        }
    }

    public static boolean isDev(Player p) {
        if ((p.getName().equals("Xiis")) || (p.getName().equals("Disarranged"))) {
            return true;
        }
        return false;
    }

    public static void consoleMessage(Object m) {
        Bukkit.getConsoleSender().sendMessage(Main.PREFIX + m);
    }

    public static void consoleWarn(Object m) {
        Bukkit.getConsoleSender().sendMessage(Main.PREFIX + "[" + ChatColor.RED + "WARN" + ChatColor.WHITE + "] " + m);
    }

    public static void consoleBlank() {
        Bukkit.getConsoleSender().sendMessage("");
    }

    public static void consoleBlank(int amount) {
        for (int i = 0; i < amount; i++) {
            consoleBlank();
        }
    }

    public static void consoleSeperator() {
        Bukkit.getConsoleSender().sendMessage("--------------------------------------------------------------------");
    }
}

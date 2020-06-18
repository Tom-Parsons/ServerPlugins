package network.inferno.npcs.events;

import network.inferno.npcs.Main;

public class EventManager {

    public static void RegisterEvents() {
        Main.pl.getServer().getPluginManager().registerEvents(new OnPlayerCommand(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnPlayerChat(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnInventoryClick(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnInventoryClose(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnNPCInteract(), Main.pl);
    }


}

package network.inferno.lobby.managers;

import network.inferno.lobby.Main;
import network.inferno.lobby.events.*;

public class EventManager {

    public static void RegisterEvents() {
        Main.pl.getServer().getPluginManager().registerEvents(new OnPlayerJoin(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnPlayerLeave(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnPlayerLogin(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnInventoryClick(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnInteractEvent(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnPlayerToggleFlight(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnPlayerMove(), Main.pl);
        Main.pl.getServer().getPluginManager().registerEvents(new OnPlayerDamage(), Main.pl);
    }

}

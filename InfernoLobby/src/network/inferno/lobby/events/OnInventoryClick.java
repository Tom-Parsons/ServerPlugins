package network.inferno.lobby.events;

import network.inferno.lobby.managers.LobbyManager;
import network.inferno.lobby.parkour.Parkour;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OnInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        LobbyManager.onInventoryClick(e);
    }

}

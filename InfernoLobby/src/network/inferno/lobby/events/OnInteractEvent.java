package network.inferno.lobby.events;

import network.inferno.lobby.managers.LobbyManager;
import network.inferno.lobby.parkour.Parkour;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class OnInteractEvent implements Listener {

    @EventHandler
    public void onInteractEvent(PlayerInteractEvent e){
        LobbyManager.onPlayerInteract(e);
        for(Parkour parkour : Parkour.parkours){
            parkour.onPlayerInteract(e);
        }
    }

}

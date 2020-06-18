package network.inferno.lobby.events;

import network.inferno.lobby.features.DoubleJump;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerToggleFlightEvent;

public class OnPlayerToggleFlight implements Listener {

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent e){
        DoubleJump.onPlayerToggleFlight(e);
    }

}

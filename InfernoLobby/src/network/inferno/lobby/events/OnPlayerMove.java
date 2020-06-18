package network.inferno.lobby.events;

import network.inferno.lobby.features.DoubleJump;
import network.inferno.lobby.parkour.Parkour;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class OnPlayerMove implements Listener {

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        DoubleJump.onPlayerMove(e);
        for(Parkour parkour : Parkour.parkours){
            parkour.onPlayerMove(e);
        }
        if(!Parkour.isDoingAnyParkour(e.getPlayer())){
            if(e.getPlayer().getLocation().getY() < 2){
                e.getPlayer().teleport(e.getPlayer().getWorld().getSpawnLocation());
            }
        }
    }

}

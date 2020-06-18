package network.inferno.lobby.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class OnPlayerDamage implements Listener {

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent e){
        e.setCancelled(true);
    }

}

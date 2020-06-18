package network.inferno.lobby.events;

import net.ME1312.SubServers.Client.Bukkit.Network.API.SubServer;
import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import network.inferno.lobby.Main;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.util.HashMap;

public class OnPlayerLogin implements Listener {

    public static HashMap<Player, String> playersToLeave = new HashMap<Player, String>();

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e){
        Player p = e.getPlayer();

    }

}

package network.inferno.lobby.events;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import network.inferno.lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class OnPlayerLeave implements Listener {

    @EventHandler
    public void onPlayerLeave(PlayerQuitEvent e){
        Player p = e.getPlayer();
        SubAPI.getInstance().getSubServer(SubAPI.getInstance().getName(), subServer -> {
            subServer.setMotd(Bukkit.getOnlinePlayers().size() + "");
        });
    }

}

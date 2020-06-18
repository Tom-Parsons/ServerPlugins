package network.inferno.lobby.events;

import net.ME1312.SubServers.Client.Bukkit.Network.API.SubServer;
import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import network.inferno.lobby.Main;
import network.inferno.lobby.managers.LobbyManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.HashMap;

public class OnPlayerJoin implements Listener {

    private void JoinServer(Player p, String server){
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        core.bmm.Connect(p, server);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Player p = e.getPlayer();
        /*
        if(SubAPI.getInstance().getName().equals("LOBBY-1")){
            network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
            SubAPI.getInstance().getSubServers(subServers ->{
                HashMap<String, Integer> lobbyPlayerCount = new HashMap<String, Integer>();
                for(SubServer ss : subServers.values()){
                    if(ss.getName().contains("LOBBY")) {
                        String motd = ss.getMotd();
                        lobbyPlayerCount.put(ss.getName(), Integer.valueOf(motd));
                    }
                }
                String smallest = "LOBBY-1";
                for(String s : lobbyPlayerCount.keySet()){
                    if(lobbyPlayerCount.get(s) < lobbyPlayerCount.get(smallest)){
                        smallest = s;
                    }
                }
                if(!smallest.equals("LOBBY-1")){
                    JoinServer(p, smallest);
                    return;
                }
            });
        }*/

        SubAPI.getInstance().getSubServer(SubAPI.getInstance().getName(), subServer -> {
            subServer.setMotd(Bukkit.getOnlinePlayers().size() + "");
        });

        LobbyManager.PlayerJoin(p);
    }

}

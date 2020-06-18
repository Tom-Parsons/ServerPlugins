package network.inferno.lobby;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import network.inferno.lobby.managers.CommandManager;
import network.inferno.lobby.managers.EventManager;
import network.inferno.lobby.managers.LobbyManager;
import network.inferno.lobby.managers.ScoreboardManager;
import network.inferno.lobby.parkour.Parkour;
import network.inferno.lobby.parkour.ParkourManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main pl;
    public static String SERVER_NAME;

    @Override
    public void onEnable(){
        pl = this;
        EventManager.RegisterEvents();
        CommandManager.registerCommands();

        for(Player p : Bukkit.getOnlinePlayers()){
            LobbyManager.PlayerJoin(p);
        }
        ScoreboardManager.ReloadSoreboard();
        ParkourManager.setup();
    }

    @Override
    public void onDisable(){
        for(Parkour parkour : Parkour.parkours){
            parkour.Disable();
        }
    }

    public void LoadSubAPI(){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(pl,
                new Runnable() {
                    @Override
                    public void run() {
                        SubAPI api = SubAPI.getInstance();
                        if(api != null) {
                            try {
                                SERVER_NAME = api.getName();
                                api.getSubServer(SERVER_NAME,
                                        subServer -> {
                                            subServer.setMotd(Bukkit.getOnlinePlayers().size() + "");
                                        });
                            }catch (Exception ex){
                                LoadSubAPI();
                            }
                        }else {
                            LoadSubAPI();
                        }
                    }
                }, 10);
    }

}

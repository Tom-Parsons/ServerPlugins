package network.inferno.gladiators.game;

import network.inferno.gladiators.Main;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Events implements Listener {


    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        Main.game.Join(e.getPlayer());
        e.setJoinMessage(null);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        Main.game.Leave(e.getPlayer());
        e.setQuitMessage(null);
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e){
        if(Main.game.getState().equals("INGAME")){
            e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "THIS GAME IS ALREADY IN PROGRESS!");
        }else{
            if(Main.game.isFull()){
                e.disallow(PlayerLoginEvent.Result.KICK_OTHER, ChatColor.RED + "THIS GAME IS FULL!");
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        Main.game.onPlayerDamage(e);
    }


}

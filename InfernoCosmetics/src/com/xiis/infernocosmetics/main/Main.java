package com.xiis.infernocosmetics.main;

import com.xiis.infernocosmetics.cosmetics.Hats;
import com.xiis.infernocosmetics.cosmetics.Trails;
import com.xiis.infernocosmetics.cosmetics.api.Database;
import com.xiis.infernocosmetics.cosmetics.crates.Crate;
import com.xiis.infernocosmetics.player.PlayerInformation;
import com.xiis.infernocosmetics.util.Dev;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin implements Listener {

    public static Main pl;
    public static String PREFIX = ChatColor.WHITE + "[" + ChatColor.GOLD + "InfernoCosmetics" + ChatColor.WHITE + "] ";

    @Override
    public void onEnable(){
        pl = this;
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new Hats(), this);
        getServer().getPluginManager().registerEvents(new Trails(), this);
        Database.Init();
        Crate.LoadCrates();
        for(Player p : Bukkit.getOnlinePlayers()){
            try {
                if (!Database.LoadPlayer(p)) {
                    Database.AddPlayerToDatabase(p);
                }
            }catch(Exception ex){
                ex.printStackTrace();
                Dev.consoleWarn("There was an error checking if '" + p.getName() + "' was in the database. UUID is: '" + p.getUniqueId() + "'");
            }
        }
        Trails.play();
    }

    @Override
    public void onDisable(){
        try {
            Database.Close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        for(Crate c : Crate.crates) c.disable();
        PlayerInformation.connected.clear();
        PlayerInformation.connected = null;
    }

    @EventHandler
    public void onCommandPreprocessEvent(PlayerCommandPreprocessEvent e){
        Player p = e.getPlayer();
        String msg = e.getMessage();
        if(msg.equalsIgnoreCase("/hats")){
            Hats.chooseHats(p);
            e.setCancelled(true);
        }
        if(msg.equalsIgnoreCase("/trails")){
            Trails.chooseTrails(p);
            e.setCancelled(true);
        }
        if(msg.toLowerCase().startsWith("/addhat ")){
            String[] split = msg.split(" ");
            try {
                if(!PlayerInformation.connected.get(p).hasHat(split[1])) {
                    PlayerInformation.connected.get(p).addNewHat(split[1]);
                    p.sendMessage("You've now got the " + split[1] + " hat");
                }else{
                    p.sendMessage("You already have this hat!");
                }
            }catch(Exception ex){
                p.sendMessage("Something went wrong when trying to add that hat, you probably typed the ID wrong");
            }
            e.setCancelled(true);
        }
        if(msg.toLowerCase().startsWith("/addkey ")){
            String[] split = msg.split(" ");
            try{
                PlayerInformation.connected.get(p).addKey(Integer.valueOf(split[1]));
                p.sendMessage("You've received a " + Crate.RARITY_STRING(Integer.valueOf(split[1])) + ChatColor.WHITE + " key!");

            }catch(Exception ex){
                p.sendMessage("Something went wrong when trying to add that key, you probably didn't type a number");
            }
            e.setCancelled(true);
        }
        if(msg.toLowerCase().equals("/addcrate")){
            Crate.AddCrate(p.getLocation().getBlock().getLocation());
            p.sendMessage("Added a new crate location!");
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerLogin(PlayerLoginEvent e){
        Player p = e.getPlayer();
        try {
            if (!Database.LoadPlayer(p)) {
                Database.AddPlayerToDatabase(p);
            }
        }catch(Exception ex){
            ex.printStackTrace();
            Dev.consoleWarn("There was an error checking if '" + p.getName() + "' was in the database. UUID is: '" + p.getUniqueId() + "'");
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){

    }

    @EventHandler
    void onPlayerQuit(PlayerQuitEvent e){
        Player p = e.getPlayer();
        PlayerInformation.connected.remove(p);
    }

}

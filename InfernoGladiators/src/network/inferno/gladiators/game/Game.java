package network.inferno.gladiators.game;

import com.xiis.infernocosmetics.player.PlayerInformation;
import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import network.inferno.gladiators.Main;
import network.inferno.minigames.API.Minigame;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class Game extends Minigame {

    private HashMap<Player, Player> assignedPlayers = new HashMap<>();

    private int round = 0;
    private int roundStartTimer = 10;
    private int roundTimer = 60;

    public Game(String name, int min_players, int max_players, int lobby_time, int game_time) {
        super(name, min_players, max_players, lobby_time, game_time);
    }

    @Override
    public void Start() {
        AssignPlayers();
        GiveItems();
        RoundTimer();
    }

    @Override
    public void Leave(Player p){
        super.Leave(p);
    }

    private void RoundTimer(){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                new Runnable() {
                    @Override
                    public void run() {
                        if(roundStartTimer > 0){
                            roundStartTimer--;
                            for(Player p : Bukkit.getOnlinePlayers()){
                                if(p.getGameMode() == GameMode.ADVENTURE) {
                                    SetPlayerActionbar(p, ChatColor.GREEN + "Round " + round + ChatColor.GOLD + " starting in " + ChatColor.RED + roundStartTimer);
                                }
                            }
                        }else{
                            if(roundTimer > 0){
                                if(roundTimer == 60){
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        if (p.getGameMode() == GameMode.ADVENTURE) {
                                            SetPlayerActionbar(p, ChatColor.RED + "Round " + round + ChatColor.GOLD + " FIGHT");
                                            p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                                        }
                                    }
                                }
                                roundTimer--;
                            }else {
                                if(getState().equals("INGAME")) {
                                    for (Player p : assignedPlayers.keySet()) {
                                        if (p.getGameMode() == GameMode.ADVENTURE) {
                                            if (p.getHealth() > 2) {
                                                p.damage(2);
                                            } else {
                                                Kill(p);
                                            }
                                            p.sendMessage(Main.core.format.label() + Main.core.format.colorize("&7Time is running out!"));
                                        }
                                    }
                                    for (Player p : assignedPlayers.values()) {
                                        if (p.getGameMode() == GameMode.ADVENTURE) {
                                            if (p.getHealth() > 2) {
                                                p.damage(2);
                                            } else {
                                                Kill(p);
                                            }
                                            p.sendMessage(Main.core.format.label() + Main.core.format.colorize("&7Time is running out!"));
                                        }
                                    }
                                }
                            }
                        }
                        RoundTimer();
                    }
                }, 20);
    }

    private void AssignPlayers(){
        ArrayList<Player> unassigned = new ArrayList<>();
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.getGameMode() == GameMode.ADVENTURE){
                unassigned.add(p);
            }
        }
        round++;
        roundStartTimer = 10;
        for(Player p : Bukkit.getOnlinePlayers()){
            if(unassigned.contains(p)){
                unassigned.remove(p);
                Random rnd = new Random();
                Player assignee = (Player) unassigned.toArray()[rnd.nextInt(unassigned.size())];
                unassigned.remove(assignee);
                assignedPlayers.put(p, assignee);
            }
            SetPlayerActionbar(p, ChatColor.GREEN + "Round " + round +  ChatColor.GOLD + " starting in " + ChatColor.RED + roundStartTimer);
        }
        HidePlayers();
    }

    private void GiveItems(){
        if(round == 1){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getGameMode() == GameMode.ADVENTURE){
                    p.getInventory().addItem(new ItemStack(Material.WOODEN_SWORD));
                    p.getInventory().addItem(new ItemStack(Material.BOW));
                    p.getInventory().setItem(8, new ItemStack(Material.ARROW, 32));

                    PlayerInformation.connected.get(p).setHelmet();
                    p.getInventory().setChestplate(new ItemStack(Material.LEATHER_CHESTPLATE));
                    p.getInventory().setLeggings(new ItemStack(Material.LEATHER_LEGGINGS));
                    p.getInventory().setBoots(new ItemStack(Material.LEATHER_BOOTS));
                }
            }
        }else if(round == 2){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getGameMode() == GameMode.ADVENTURE){
                    p.getInventory().addItem(new ItemStack(Material.STONE_SWORD));
                    p.getInventory().addItem(new ItemStack(Material.BOW));
                    p.getInventory().setItem(8, new ItemStack(Material.ARROW, 32));

                    PlayerInformation.connected.get(p).setHelmet();
                    p.getInventory().setChestplate(new ItemStack(Material.CHAINMAIL_CHESTPLATE));
                    p.getInventory().setLeggings(new ItemStack(Material.CHAINMAIL_LEGGINGS));
                    p.getInventory().setBoots(new ItemStack(Material.CHAINMAIL_BOOTS));
                }
            }
        }else if(round == 3){
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getGameMode() == GameMode.ADVENTURE){
                    p.getInventory().addItem(new ItemStack(Material.IRON_SWORD));
                    p.getInventory().addItem(new ItemStack(Material.BOW));
                    p.getInventory().setItem(8, new ItemStack(Material.ARROW, 32));

                    PlayerInformation.connected.get(p).setHelmet();
                    p.getInventory().setChestplate(new ItemStack(Material.IRON_CHESTPLATE));
                    p.getInventory().setLeggings(new ItemStack(Material.IRON_LEGGINGS));
                    p.getInventory().setBoots(new ItemStack(Material.IRON_BOOTS));
                }
            }
        }else {
            for(Player p : Bukkit.getOnlinePlayers()){
                if(p.getGameMode() == GameMode.ADVENTURE){
                    p.getInventory().addItem(new ItemStack(Material.DIAMOND_SWORD));
                    p.getInventory().addItem(new ItemStack(Material.BOW));
                    p.getInventory().setItem(8, new ItemStack(Material.ARROW, 32));

                    PlayerInformation.connected.get(p).setHelmet();
                    p.getInventory().setChestplate(new ItemStack(Material.DIAMOND_CHESTPLATE));
                    p.getInventory().setLeggings(new ItemStack(Material.DIAMOND_LEGGINGS));
                    p.getInventory().setBoots(new ItemStack(Material.DIAMOND_BOOTS));
                }
            }
        }
    }

    private void HidePlayers(){
        for(Player p : assignedPlayers.keySet()){
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(assignedPlayers.get(p) != pl){
                    p.hidePlayer(Main.pl, pl);
                }else{
                    p.showPlayer(Main.pl, pl);
                }
            }
        }
        for(Player p : assignedPlayers.values()){
            for(Player pl : Bukkit.getOnlinePlayers()){
                if(GetAssigned(p) != pl){
                    p.hidePlayer(Main.pl, pl);
                }else{
                    p.showPlayer(Main.pl, pl);
                }
            }
        }
    }

    private void ShowPlayers(){
        for(Player p : assignedPlayers.keySet()){
            for(Player pl : Bukkit.getOnlinePlayers()){
                p.showPlayer(Main.pl, pl);
            }
        }
    }

    private void ShowAllPlayers(Player p){
        for(Player pl : Bukkit.getOnlinePlayers()){
            p.showPlayer(Main.pl, pl);
        }
    }

    private void ShowAlivePlayers(Player p){
        for(Player pl : Bukkit.getOnlinePlayers()){
            if(pl.getGameMode() == GameMode.ADVENTURE){
                p.showPlayer(Main.pl, pl);
            }
        }
    }

    private int AliveSize(){
        int alive = 0;
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.getGameMode() == GameMode.ADVENTURE) {
                alive++;
            }
        }
        return alive;
    }

    private Player GetAssigned(Player p){
        if(assignedPlayers.containsKey(p)){
            return assignedPlayers.get(p);
        }else{
            for(Player pl : assignedPlayers.keySet()){
                if(assignedPlayers.get(pl) == p){
                    return pl;
                }
            }
        }
        return null;
    }

    @Override
    public void End() {
        Player winner = null;
        for(Player p : Bukkit.getOnlinePlayers()){
            if(p.getGameMode() == GameMode.ADVENTURE){
                winner = p;
            }
        }
        Broadcast("The winner is &6" + winner.getName());
    }

    @Override
    public void UpdateGameScoreboard() {
        for(Player p : Bukkit.getOnlinePlayers()) {
            Scoreboard scoreboard;
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
            Objective obj = scoreboard.registerNewObjective(p.getName(), "dummy", ChatColor.GOLD + getName());
            obj.setDisplaySlot(DisplaySlot.SIDEBAR);
            Score roundScore = obj.getScore(ChatColor.GRAY + "Round: " + ChatColor.GOLD + round);
            roundScore.setScore(15);
            Score playerScore = obj.getScore(ChatColor.GRAY + "Players Alive: " + ChatColor.GOLD + (assignedPlayers.size() * 2));
            playerScore.setScore(14);

            boolean allPlayersTeams = false;

            if(p.getGameMode() == GameMode.ADVENTURE) {
                if(!assignedPlayers.containsKey(p)) {
                    allPlayersTeams = true;
                }else{
                    Player p2 = assignedPlayers.get(p);
                    Team teamP = scoreboard.registerNewTeam(p.getName());
                    teamP.setColor(ChatColor.GREEN);
                    Team teamP2 = scoreboard.registerNewTeam(p2.getName());
                    teamP2.setColor(ChatColor.RED);
                    teamP.addEntry(p.getName());
                    teamP2.addEntry(p2.getName());
                }
            }else{
                allPlayersTeams = true;
            }

            if(allPlayersTeams){
                int teamColour = 0;
                for (Player pl : Bukkit.getOnlinePlayers()) {
                    if (pl.getGameMode() == GameMode.ADVENTURE) {
                        if (assignedPlayers.containsKey(pl)) {
                            Player p2 = assignedPlayers.get(pl);
                            Team team = scoreboard.registerNewTeam(pl.getName());
                            team.setColor(getColourFromNumber(teamColour));
                            team.addEntry(pl.getName());
                            team.addEntry(p2.getName());
                        }
                    }
                }
            }

            p.setScoreboard(scoreboard);
        }
    }

    private ChatColor getColourFromNumber(int number){
        switch(number){
            case 0:
                return ChatColor.GREEN;
            case 1:
                return ChatColor.AQUA;
            case 2:
                return ChatColor.RED;
            case 3:
                return ChatColor.LIGHT_PURPLE;
            case 4:
                return ChatColor.YELLOW;
            case 5:
                return ChatColor.BLUE;
            case 6:
                return ChatColor.GOLD;
            default:
                return ChatColor.DARK_PURPLE;
        }
    }

    private void Kill(Player p){
        Player pl = GetAssigned(p);
        p.setHealth(20);
        p.getInventory().clear();
        p.setGameMode(GameMode.SPECTATOR);
        p.sendMessage(Main.core.format.label() + Main.core.format.colorize("&7You have been killed by &6" + pl.getName()));
        ShowAllPlayers(p);
        SetPlayerActionbar(p, ChatColor.RED + "You are dead!");

        pl.setHealth(20);
        pl.getInventory().clear();
        pl.sendMessage(Main.core.format.label() + Main.core.format.colorize("&7You killed &6" + p.getName()));
        ShowAlivePlayers(pl);
        SetPlayerActionbar(pl, ChatColor.GOLD + "Waiting for end of round...");

        if (AliveSize() > 1) {
            if (assignedPlayers.containsKey(p)) {
                assignedPlayers.remove(p);
            } else {
                assignedPlayers.remove(pl);
            }
            if (assignedPlayers.size() == 0) {
                roundStartTimer = 10;
                roundTimer = 60;
                AssignPlayers();
                GiveItems();
                Broadcast("Next round is starting...");
            }
        } else {
            ForceEnd();
        }
    }

    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if(getState().equals("INGAME")){
            if(roundStartTimer <= 0) {
                if (e.getEntity() instanceof Player && e.getDamager() instanceof Player) {
                    Player damager = (Player) e.getDamager();
                    Player p = (Player) e.getEntity();
                    if(GetAssigned(damager) == p) {
                        if (p.getHealth() - e.getDamage() <= 0) {
                            e.setCancelled(true);
                            Kill(p);
                        }
                    }else{
                        e.setCancelled(true);
                    }
                }
            }else{
                e.setCancelled(true);
            }
        }else{
            e.setCancelled(true);
        }
    }
}

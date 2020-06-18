package network.inferno.minigames.API;

import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import network.inferno.core.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import java.util.HashMap;

public abstract class Minigame {

    protected int MIN_PLAYERS;
    protected int MAX_PLAYERS;
    protected int LOBBY_TIME;
    protected int GAME_TIME;

    private String _name;
    private String _state = "ONLINE";
    private int _timer;
    private HashMap<Player, String> _actionbarMessage = new HashMap<>();

    public Minigame(String name, int min_players, int max_players, int lobby_time, int game_time){
        _name = name;
        MIN_PLAYERS = min_players;
        MAX_PLAYERS = max_players;
        LOBBY_TIME = lobby_time;
        GAME_TIME = game_time;
        _timer = lobby_time;
    }

    public int getSize(){
        return Bukkit.getOnlinePlayers().size();
    }

    public boolean isFull(){
        return getSize() == MAX_PLAYERS;
    }

    public String getState(){
        return _state;
    }

    public String getName(){
        return _name;
    }

    /**
     * Broadcast a message to all players using the [Inferno] prefix
     * @param message
     */
    public void Broadcast(String message){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(Main.getPlugin(Main.class).format.label() + Main.getPlugin(Main.class).format.colorize("&7" + message));
        }
    }

    /**
     * Broadcast a message to all players using ONLY what is defined in message
     * @param message
     */
    public void BroadcastPlain(String message){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.sendMessage(message);
        }
    }

    /**
     * Must be called when a player joins the game
     * @param p
     */
    public void Join(Player p){
        Broadcast(p.getName() + " joined the game. " + ChatColor.GREEN + ChatColor.ITALIC.toString() + (MIN_PLAYERS - Bukkit.getOnlinePlayers().size()) + " more players needed to start.");
        p.teleport(p.getWorld().getSpawnLocation());
        p.setGameMode(GameMode.ADVENTURE);
        p.getInventory().clear();
        SetPlayerActionbar(p, ChatColor.GOLD + "Waiting for players... " + ChatColor.GRAY + ChatColor.ITALIC.toString() + getSize() + "/" + MAX_PLAYERS);

        UpdateScoreboard();

        if(getSize() >= MIN_PLAYERS){
            SetPlayerActionbar(p, ChatColor.GREEN.toString() + _timer);
            Countdown();
        }
    }

    /**
     * Must be called when a player leaves the game
     * @param p
     */
    public void Leave(Player p){
        Broadcast(p.getName() + " left the game");
        UpdateScoreboard();
        _actionbarMessage.remove(p);
    }

    private void Countdown(){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class),
                new Runnable() {
                    @Override
                    public void run() {
                        if(getSize() >= MIN_PLAYERS){
                            for(Player p : Bukkit.getOnlinePlayers()) {
                                SetPlayerActionbar(p, ChatColor.GREEN.toString() + _timer);
                            }
                            UpdateScoreboard();
                            if(_timer == 10){
                                Broadcast("10 seconds until the game starts!");
                            }else if(_timer == 3){
                                Broadcast("3 seconds until the game starts!");
                            }else if(_timer == 2){
                                Broadcast("2 seconds until the game starts!");
                            }else if(_timer == 1){
                                Broadcast("1 second until the game starts!");
                            }else if(_timer == 0){
                                Broadcast("Go!");
                                InitStart();
                            }
                            _timer--;
                            if(_state.equals("ONLINE"))Countdown();
                        }else{
                            for(Player p : Bukkit.getOnlinePlayers()) {
                                SetPlayerActionbar(p, ChatColor.GOLD + "Waiting for players... " + ChatColor.GRAY + ChatColor.ITALIC.toString() + getSize() + "/" + MAX_PLAYERS);
                            }
                            Broadcast("Someone left so waiting for more players before starting!");
                            _timer = LOBBY_TIME;
                            UpdateScoreboard();
                        }
                    }
                }, 20);
    }

    private void Timer(){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class),
                new Runnable() {
                    @Override
                    public void run() {
                        if(getSize() > 1){
                            UpdateScoreboard();
                            if(_timer == 30){
                                Broadcast("30 seconds until the game ends!");
                            }else if(_timer == 3){
                                Broadcast("3 seconds until the game ends!");
                            }else if(_timer == 2){
                                Broadcast("2 seconds until the game ends!");
                            }else if(_timer == 1){
                                Broadcast("1 second until the game ends!");
                            }else if(_timer == 0){
                                InitEnd();
                            }
                            _timer--;
                            if(_state.equals("INGAME"))Timer();
                        }else{
                            Broadcast("Everyone left the game so you won!");
                            _timer = 10;
                            UpdateScoreboard();
                            Resetting();
                        }
                    }
                }, 20);
    }

    private void Reset(){
        for(Player p : Bukkit.getOnlinePlayers()){
            p.kickPlayer(ChatColor.RED + "THIS GAME IS RESETTING!");
        }
        _timer = LOBBY_TIME;

        Bukkit.shutdown();
    }

    private void Resetting(){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.getPlugin(Main.class),
                new Runnable() {
                    @Override
                    public void run() {
                        for(Player p : Bukkit.getOnlinePlayers()) {
                            SetPlayerActionbar(p, ChatColor.RED + "RESETTING...");
                        }
                        UpdateScoreboard();
                        if(_timer == 0){
                            Reset();
                            return;
                        }
                        _timer--;
                        Resetting();
                    }
                }, 20);
    }

    private void InitStart(){
        Main.getPlugin(Main.class).stateHandler.setServerState(Main.getPlugin(Main.class).playerHandler.getPlayerServer(Bukkit.getOnlinePlayers().iterator().next().getName()), "INGAME");
        _timer = GAME_TIME;
        Start();
        _state = "INGAME";
        Timer();
    }
    private void InitEnd(){
        End();
        _timer = 10;
        Resetting();
    }

    private void UpdateScoreboard(){
        if(getState().equals("ONLINE")) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                Objective obj = scoreboard.registerNewObjective(_name, "dummy", ChatColor.GOLD + _name);
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                Score playerScore = obj.getScore(ChatColor.GRAY + "Players: " + ChatColor.GOLD + getSize());
                playerScore.setScore(15);
                Score timeScore = obj.getScore(ChatColor.GRAY + "Time Left: " + ChatColor.GOLD + _timer);
                timeScore.setScore(14);
                p.setScoreboard(scoreboard);
            }
        }else if(getState().equals("INGAME")) {
            UpdateGameScoreboard();
        }else{
            for (Player p : Bukkit.getOnlinePlayers()) {
                Scoreboard scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
                p.setScoreboard(scoreboard);
            }
        }
        UpdateActionBar();
    }

    private void UpdateActionBar(){
        for(Player p : Bukkit.getOnlinePlayers()){
            if(_actionbarMessage.containsKey(p)){
                IChatBaseComponent chat = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + _actionbarMessage.get(p) + "\"}");
                PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chat, ChatMessageType.GAME_INFO);
                CraftPlayer craft = (CraftPlayer) p;
                craft.getHandle().playerConnection.sendPacket(packetPlayOutChat);
            }
        }
    }

    /**
     * Set the message that will be displayed on the player's action bar
     * @param p
     * @param message
     */
    public void SetPlayerActionbar(Player p, String message){
        _actionbarMessage.put(p, message);
    }

    /**
     * Force the game to stop before the timer ends
     */
    public void ForceEnd(){
        InitEnd();
    }

    /**
     * Teleport players to where needed
     * Give players whatever items then need
     * Call any timer functions needed
     * etc.
     */
    public abstract void Start();

    /**
     * Calculate who the winner is
     * Give them whatever tokens they earned
     */
    public abstract void End();

    /**
     * Update the player's scoreboards here
     */
    public abstract void UpdateGameScoreboard();

}
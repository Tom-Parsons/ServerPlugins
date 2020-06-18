package network.inferno.lobby.parkour;

import network.inferno.lobby.Main;
import network.inferno.lobby.managers.LobbyManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Parkour {

    public static ArrayList<Parkour> parkours = new ArrayList<Parkour>();

    private network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);

    public HashMap<UUID, Float> highscores = new HashMap<UUID, Float>();

    private String _id;
    private Location _start;
    private ArmorStand startStand;
    private Location _end;
    private ArmorStand endStand;
    private Location _leaderboardLocation;
    private ParkourLeaderboard pkl;

    private boolean editCooldown = false;
    private boolean editing = false;
    private Player editor = null;

    private HashMap<Player, Integer> playerCheckpoint = new HashMap<Player, Integer>();
    public HashMap<Integer, ArmorStand> _checkpointStands = new HashMap<Integer, ArmorStand>();
    public HashMap<Integer, Location> _checkpoints = new HashMap<Integer, Location>();

    private HashMap<Player, ParkourTimer> timers = new HashMap<Player, ParkourTimer>();

    public Parkour(String id, Location start, Location end, Location leaderboardLocation, HashMap<Integer, Location> checkpoints){
        _id = id;
        _start = start;
        _end = end;
        _leaderboardLocation = leaderboardLocation;
        _checkpoints = checkpoints;
        highscores = ParkourManager.LoadHighscores(this);
        Create();
    }

    public void Reload(){
        Disable();
        Create();
    }

    public void Create(){
        startStand = (ArmorStand) _start.getWorld().spawnEntity(new Location(_start.getWorld(), _start.getX() + 0.5, _start.getY(), _start.getZ() + 0.5), EntityType.ARMOR_STAND);
        startStand.setCustomNameVisible(true);
        startStand.setCustomName(ChatColor.GREEN + "Parkour Start");
        startStand.setVisible(false);
        startStand.setSmall(true);
        startStand.setGravity(false);
        startStand.setInvulnerable(true);
        _start.getBlock().setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        endStand = (ArmorStand) _end.getWorld().spawnEntity(new Location(_end.getWorld(), _end.getX() + 0.5, _end.getY(), _end.getZ() + 0.5), EntityType.ARMOR_STAND);
        endStand.setCustomNameVisible(true);
        endStand.setCustomName(ChatColor.RED + "Parkour End");
        endStand.setVisible(false);
        endStand.setSmall(true);
        endStand.setGravity(false);
        endStand.setInvulnerable(true);
        _end.getBlock().setType(Material.LIGHT_WEIGHTED_PRESSURE_PLATE);
        for(int i : _checkpoints.keySet()){
            Location loc = _checkpoints.get(i);
            ArmorStand stand = (ArmorStand) loc.getWorld().spawnEntity(new Location(loc.getWorld(), loc.getX() + 0.5, loc.getY(), loc.getZ() + 0.5), EntityType.ARMOR_STAND);
            stand.setCustomNameVisible(true);
            stand.setCustomName(ChatColor.AQUA + "Checkpoint #" + i);
            stand.setVisible(false);
            stand.setSmall(true);
            stand.setGravity(false);
            stand.setInvulnerable(true);
            loc.getBlock().setType(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
            _checkpointStands.put(i, stand);
        }
        pkl = new ParkourLeaderboard(_leaderboardLocation, ParkourManager.LoadBestHighscores(this));
    }

    public void Start(Player p){
        ParkourTimer timer = new ParkourTimer(p);
        timers.put(p, timer);
        playerCheckpoint.put(p, 0);
        GiveItems(p);
        Thread thread = new Thread(timer);
        thread.start();
    }

    private void GiveItems(Player p){
        p.getInventory().clear();

        ItemStack checkpoint = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        ItemMeta checkpointMeta = checkpoint.getItemMeta();
        checkpointMeta.setDisplayName(ChatColor.AQUA + "Reset Checkpoint");
        checkpoint.setItemMeta(checkpointMeta);
        ItemStack restart = new ItemStack(Material.CLOCK);
        ItemMeta restartMeta = restart.getItemMeta();
        restartMeta.setDisplayName(ChatColor.GREEN + "Restart");
        restart.setItemMeta(restartMeta);
        ItemStack cancel = new ItemStack(Material.BARRIER);
        ItemMeta cancelMeta = restart.getItemMeta();
        cancelMeta.setDisplayName(ChatColor.RED + "Cancel");
        cancel.setItemMeta(cancelMeta);

        p.getInventory().addItem(checkpoint);
        p.getInventory().addItem(restart);
        p.getInventory().addItem(cancel);
    }

    public void onPlayerMove(PlayerMoveEvent e){
        if(editing)
            return;
        Player p = e.getPlayer();
        Location loc = new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ());
        if(loc.getBlockX() == _start.getBlockX() && loc.getBlockY() == _start.getBlockY() && loc.getBlockZ() == _start.getBlockZ() && !timers.containsKey(p)){
            p.sendMessage(core.format.label() + core.format.colorize("&7You started the parkour."));
            Start(p);
        }else if(loc.getBlockX() == _end.getBlockX() && loc.getBlockY() == _end.getBlockY() && loc.getBlockZ() == _end.getBlockZ()){
            if(timers.containsKey(p)){
                if(playerCheckpoint.get(p) == _checkpoints.size()) {
                    DecimalFormat dec = new DecimalFormat("0.000");
                    timers.get(p).Stop();
                    p.sendMessage(core.format.label() + core.format.colorize("&7You completed the parkour in &6" + dec.format(timers.get(p).getMilliseconds() / 1000d) + " &7seconds"));
                    ParkourManager.SaveHighscore(this, p, Float.valueOf(dec.format(timers.get(p).getMilliseconds() / 1000d)));
                    timers.remove(p);
                    playerCheckpoint.remove(p);
                    pkl.Disable();
                    pkl = new ParkourLeaderboard(_leaderboardLocation, ParkourManager.LoadBestHighscores(this));
                    LobbyManager.GiveItems(p);
                }else{
                    p.sendMessage(core.format.label() + core.format.colorize("&7You've missed a checkpoint."));
                }
            }
        }else if(timers.containsKey(p)){
            if(_checkpoints.get(playerCheckpoint.get(p) + 1) != null) {
                if (loc.getBlockX() == _checkpoints.get(playerCheckpoint.get(p) + 1).getBlockX() && loc.getBlockY() == _checkpoints.get(playerCheckpoint.get(p) + 1).getBlockY() && loc.getBlockZ() == _checkpoints.get(playerCheckpoint.get(p) + 1).getBlockZ()) {
                    DecimalFormat dec = new DecimalFormat("0.000");
                    p.sendMessage(core.format.label() + core.format.colorize("&7You reached checkpoint &b#" + (playerCheckpoint.get(p) + 1) + " &7in &6" + dec.format(timers.get(p).getMilliseconds() / 1000d) + " &7seconds"));
                    playerCheckpoint.put(p, (playerCheckpoint.get(p) + 1));
                }
            }
            if(p.getLocation().getY() < 2){
                if(playerCheckpoint.get(p) == 0){
                    p.teleport(_start);
                }else {
                    p.teleport(_checkpoints.get(playerCheckpoint.get(p)));
                }
                p.sendMessage(core.format.label() + core.format.colorize("&7You fell off the parkour."));
            }
        }
    }

    public boolean isDoingParkour(Player p){
        for(Player pl : timers.keySet()){
            if(p == pl)
                return true;
        }
        return false;
    }
    public static boolean isDoingAnyParkour(Player p){
        for(Parkour parkour : parkours){
            if(parkour.isDoingParkour(p))
                return true;
        }
        return false;
    }

    public void Delete(){
        Disable();
        ParkourManager.DeleteParkour(this);
        parkours.remove(this);
    }

    public void Save(){
        ParkourManager.SaveParkour(this);
    }

    public void Disable(){
        if(startStand != null)
            startStand.remove();
        if(endStand != null)
            endStand.remove();
        for(int i : _checkpointStands.keySet()){
            _checkpointStands.get(i).remove();
            _checkpoints.get(i).getBlock().setType(Material.AIR);
        }
        _start.getBlock().setType(Material.AIR);
        _end.getBlock().setType(Material.AIR);
        pkl.Disable();
    }

    public void onPlayerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack item = p.getInventory().getItemInMainHand();
        if (editor == p) {
            e.setCancelled(true);
        }
        if(e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (item != null) {
                if (item.hasItemMeta()) {
                    String name = core.format.ClearFormatting(item.getItemMeta().getDisplayName());
                    if (editor == p) {
                        if (!editCooldown) {
                            boolean edited = false;
                            if (name.equalsIgnoreCase("Set Start")) {
                                setStart(new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
                                edited = true;
                            } else if (name.equalsIgnoreCase("Set End")) {
                                setEnd(new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
                                edited = true;
                            } else if (name.equalsIgnoreCase("Set Leaderboard Location")) {
                                setLeaderboardLocation(new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
                                edited = true;
                            } else if (name.equalsIgnoreCase("Add Checkpoint")) {
                                addCheckpoint(new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
                                edited = true;
                            } else if (name.equalsIgnoreCase("Remove Checkpoint")) {
                                removeCheckpoint(new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()));
                                edited = true;
                            } else if (name.equalsIgnoreCase("Done")) {
                                StopEditing();
                                edited = true;
                            } else if (name.equalsIgnoreCase("Delete Parkour")) {
                                Delete();
                                StopEditing();
                            }
                            if(edited == true) {
                                editCooldown = true;
                                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                                        new Runnable() {
                                            @Override
                                            public void run() {
                                                editCooldown = false;
                                            }
                                        }, 20);
                            }
                        }
                    } else if (timers.keySet().contains(p)) {
                        e.setCancelled(true);
                        if (name.equalsIgnoreCase("Reset Checkpoint")) {
                            if(playerCheckpoint.get(p) == 0) {
                                p.teleport(_start);
                            }else{
                                p.teleport(_checkpoints.get(playerCheckpoint.get(p)));
                            }
                            p.sendMessage(core.format.label() + core.format.colorize("&7You teleported to your last checkpoint."));
                        } else if (name.equalsIgnoreCase("Restart")) {
                            timers.get(p).Stop();
                            p.sendMessage(core.format.label() + core.format.colorize("&7You restarted the parkour."));
                            p.teleport(_start);
                            timers.remove(p);
                            playerCheckpoint.remove(p);
                            Start(p);
                        } else if (name.equalsIgnoreCase("Cancel")) {
                            timers.get(p).Stop();
                            p.sendMessage(core.format.label() + core.format.colorize("&7You cancelled the parkour."));
                            timers.remove(p);
                            playerCheckpoint.remove(p);
                            Location loc1 = new Location(_start.getWorld(), _start.getBlockX() + 1.5, _start.getBlockY(), _start.getBlockZ()+0.5);
                            Location loc2 = new Location(_start.getWorld(), _start.getBlockX() - 0.5, _start.getBlockY(), _start.getBlockZ());
                            Location loc3 = new Location(_start.getWorld(), _start.getBlockX()+0.5, _start.getBlockY(), _start.getBlockZ() + 1.5);
                            Location loc4 = new Location(_start.getWorld(), _start.getBlockX()+0.5, _start.getBlockY(), _start.getBlockZ() - 0.5);
                            if(loc1.getBlock().getType() == Material.AIR){
                                p.teleport(loc1);
                            }else if(loc2.getBlock().getType() == Material.AIR){
                                p.teleport(loc2);
                            }else if(loc3.getBlock().getType() == Material.AIR){
                                p.teleport(loc3);
                            }else if(loc4.getBlock().getType() == Material.AIR){
                                p.teleport(loc4);
                            }else{
                                p.teleport(_start);
                            }
                            LobbyManager.GiveItems(p);
                        }
                    }
                }
            }
        }
    }

    public void BeginEditing(Player p){
        editing = true;
        editor = p;

        ItemStack setStart = new ItemStack(Material.LIME_TERRACOTTA);
        ItemMeta setStartMeta = setStart.getItemMeta();
        setStartMeta.setDisplayName(ChatColor.GREEN + "Set Start");
        setStart.setItemMeta(setStartMeta);

        ItemStack setEnd = new ItemStack(Material.RED_TERRACOTTA);
        ItemMeta setEndMeta = setEnd.getItemMeta();
        setEndMeta.setDisplayName(ChatColor.RED + "Set End");
        setEnd.setItemMeta(setEndMeta);

        ItemStack addCheckpoint = new ItemStack(Material.HEAVY_WEIGHTED_PRESSURE_PLATE);
        ItemMeta addCheckpointMeta = addCheckpoint.getItemMeta();
        addCheckpointMeta.setDisplayName(ChatColor.GREEN + "Add Checkpoint");
        addCheckpoint.setItemMeta(addCheckpointMeta);

        ItemStack removeCheckpoint = new ItemStack(Material.OAK_PRESSURE_PLATE);
        ItemMeta removeCheckpointMeta = removeCheckpoint.getItemMeta();
        removeCheckpointMeta.setDisplayName(ChatColor.RED + "Remove Checkpoint");
        removeCheckpoint.setItemMeta(removeCheckpointMeta);

        ItemStack changeLeaderboard = new ItemStack(Material.ARMOR_STAND);
        ItemMeta changeLeaderboardMeta = changeLeaderboard.getItemMeta();
        changeLeaderboardMeta.setDisplayName(ChatColor.GREEN + "Set Leaderboard Location");
        changeLeaderboard.setItemMeta(changeLeaderboardMeta);

        ItemStack done = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta doneMeta = done.getItemMeta();
        doneMeta.setDisplayName(ChatColor.AQUA + "Done");
        done.setItemMeta(doneMeta);

        ItemStack delete = new ItemStack(Material.BARRIER);
        ItemMeta deleteMeta = delete.getItemMeta();
        deleteMeta.setDisplayName(ChatColor.RED + "Delete Parkour");
        delete.setItemMeta(deleteMeta);

        p.getInventory().clear();
        p.getInventory().setItem(0, setStart);
        p.getInventory().setItem(1, setEnd);
        p.getInventory().setItem(2, changeLeaderboard);
        p.getInventory().setItem(3, addCheckpoint);
        p.getInventory().setItem(4, removeCheckpoint);
        p.getInventory().setItem(7, done);
        p.getInventory().setItem(8, delete);

        for(Player pl : timers.keySet()){
            pl.sendMessage("This parkour is being edited, you have been kicked out of parkour mode!");
            timers.get(pl).Stop();
        }
        timers.clear();
    }
    public void StopEditing(){
        LobbyManager.GiveItems(editor);
        editing = false;
        editor = null;
    }

    public void setID(String id){
        Delete();
        _id = id;
        Save();
    }

    public void setStart(Location start){
        _start = start;
        Reload();
        Save();
    }
    public void setEnd(Location end){
        _end = end;
        Reload();
        Save();
    }

    public void setLeaderboardLocation(Location leaderboardLocation){
        _leaderboardLocation = leaderboardLocation;
        pkl.Disable();
        pkl = new ParkourLeaderboard(_leaderboardLocation, ParkourManager.LoadBestHighscores(this));
        Save();
    }

    public void addCheckpoint(Location checkpointLoc){
        _checkpoints.put(_checkpoints.size() + 1, checkpointLoc);
        Reload();
        Save();
    }

    public void removeCheckpoint(Location loc){
        int checkpointToRemove = 0;
        for(int i : _checkpoints.keySet()){
            if(_checkpoints.get(i).getBlockX() == loc.getBlockX() && _checkpoints.get(i).getBlockY() == loc.getBlockY() && _checkpoints.get(i).getBlockZ() == loc.getBlockZ()){
                checkpointToRemove = i;
                break;
            }
        }
        if(checkpointToRemove == 0){
            editor.sendMessage("Not a valid checkpoint!");
            return;
        }else{
            _checkpoints.get(checkpointToRemove).getBlock().setType(Material.AIR);
            for(int i = checkpointToRemove + 1; i < _checkpoints.size(); i++){
                _checkpoints.put(i - 1, _checkpoints.get(i));
            }
            _checkpointStands.get(_checkpoints.size()).remove();
            _checkpointStands.remove(_checkpoints.size());
            _checkpoints.remove(_checkpoints.size());
            Reload();
            Save();
        }
    }

    public String getID(){
        return _id;
    }
    public Location getStart(){
        return _start;
    }
    public Location getEnd(){
        return _end;
    }
    public Location getLeaderboardLocation(){
        return _leaderboardLocation;
    }
    public HashMap<Integer, Location> getCheckpoints(){
        return _checkpoints;
    }

}

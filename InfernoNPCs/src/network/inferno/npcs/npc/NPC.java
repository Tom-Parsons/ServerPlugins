package network.inferno.npcs.npc;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.ME1312.SubServers.Client.Bukkit.Network.API.SubServer;
import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import net.minecraft.server.v1_15_R1.*;
import network.inferno.npcs.Main;
import network.inferno.npcs.util.Dev;
import network.inferno.npcs.util.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.BlockIterator;
import org.bukkit.util.Vector;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public class NPC implements Listener {

    public static ArrayList<NPC> NPCs = new ArrayList<NPC>();

    public static NPC getNPC(String name){
        for(NPC npc : NPCs){
            if(npc.getName().equals(name)){
                return npc;
            }
        }
        return null;
    }

    private String _name;
    private String _id;
    private Location _location;
    private NPCType _type;
    private String _data;
    private EntityPlayer _npc;
    private ArmorStand _hologram;

    public static void LoadNPCs(){
        File file = new File("plugins/InfernoNetwork/NPCs.yml");
        if(!file.exists()){
            try {
                file.createNewFile();
            }catch (Exception ex){
                ex.printStackTrace();
            }
        }else{
            YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
            if(yml.isConfigurationSection("NPCs")) {
                for(String s : yml.getConfigurationSection("NPCs").getKeys(false)){
                    NPC npc = new NPC(s, yml.getString("NPCs." + s + ".ID"), NPCType.valueOf(yml.getString("NPCs." + s + ".Type")), yml.getString("NPCs." + s + ".Data"), LocationUtil.Deserialize(yml.getString("NPCs." + s + ".Location")));
                    Main.pl.getServer().getPluginManager().registerEvents(npc, Main.pl);
                }
            }
        }
    }

    public NPC(String name, String id, NPCType type, String data, Location location){
        _name = name;
        _id = id;
        _type = type;
        _data = data;
        _location = location;
        Save();
        NPCs.add(this);
        Create();
        UpdateHologram();
    }

    private void Create(){
        try {
            MinecraftServer MServer = ((CraftServer) Bukkit.getServer()).getServer();
            WorldServer world = ((CraftWorld) _location.getWorld()).getHandle();
            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), _name);

            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.mineskin.org/get/id/" + _id).openConnection();
            InputStreamReader response = new InputStreamReader(connection.getInputStream());

            JSONParser jsonParser = new JSONParser();
            JSONObject responseObject = (JSONObject) jsonParser.parse(response);
            JSONObject dataObj = (JSONObject) responseObject.get("data");
            JSONObject texture = (JSONObject) dataObj.get("texture");


            gameProfile.getProperties().put("textures", new Property("textures", texture.get("value").toString(), texture.get("signature").toString()));

            _npc = new EntityPlayer(MServer, world, gameProfile, new PlayerInteractManager(world));

            _npc.setLocation(_location.getX(), _location.getY(), _location.getZ(), _location.getYaw(), _location.getPitch());

            for (Player p : Bukkit.getOnlinePlayers()) {
                Show(p);
            }

            ReloadHologram();
        }catch(Exception ex){
            ex.printStackTrace();
            Dev.consoleWarn("Failed to create NPC '" + _name + "'");
        }
    }

    public void ReloadHologram(){
        RemoveHologram();
        ShowHologram();
    }

    public void ShowHologram(){
        if(_type == NPCType.SERVER) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                    new Runnable() {
                        @Override
                        public void run() {
                            SubAPI api = SubAPI.getInstance();
                            if (api != null) {
                                try {
                                    api.getSubServers(subServers -> {
                                        int players = 0;
                                        for (SubServer ss : subServers.values()) {
                                            if (ss.getName().contains(_data)) {
                                                players += ss.getPlayers().size();
                                            }
                                        }
                                        _hologram = (ArmorStand) _location.getWorld().spawnEntity(new Location(_location.getWorld(), _location.getX(), _location.getY() + 0.1, _location.getZ()), EntityType.ARMOR_STAND);
                                        _hologram.setInvulnerable(true);
                                        _hologram.setVisible(false);
                                        _hologram.setGravity(false);
                                        _hologram.setCollidable(false);
                                        _hologram.setCustomName(ChatColor.GREEN + "Players: " + players);
                                        _hologram.setCustomNameVisible(true);
                                    });
                                } catch (Exception ex) {
                                    ShowHologram();
                                }
                            } else {
                                ShowHologram();
                            }
                        }
                    }, 10);
        }
    }

    public void UpdateHologram(){
        if(_type == NPCType.SERVER) {
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                    new Runnable() {
                        @Override
                        public void run() {
                            SubAPI api = SubAPI.getInstance();
                            if (api != null) {
                                try {
                                    if(_hologram != null) {
                                        api.getSubServers(subServers -> {
                                            int players = 0;
                                            for (SubServer ss : subServers.values()) {
                                                if (ss.getName().contains(_data)) {
                                                    players += ss.getPlayers().size();
                                                }
                                            }
                                            _hologram.setCustomName(ChatColor.GREEN + "Players: " + players);
                                        });
                                    }
                                } catch (Exception ex) {}
                            }
                            UpdateHologram();
                        }
                    }, 60);
        }
    }

    public void RemoveHologram(){
        if(_hologram != null) _hologram.remove();
    }

    public void Show(Player p){
        PlayerConnection playerConnection = ((CraftPlayer)p).getHandle().playerConnection;
        playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, _npc));
        playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(_npc));
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                new Runnable() {
                    @Override
                    public void run() {
                        playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.REMOVE_PLAYER, _npc));
                    }
                }, 10);
    }

    public void Hide(Player p){
        PlayerConnection playerConnection = ((CraftPlayer)p).getHandle().playerConnection;
        playerConnection.sendPacket(new PacketPlayOutEntityDestroy(_npc.getId()));
        RemoveHologram();
    }

    public void LookAtPlayers(){
        for(Player p : Bukkit.getOnlinePlayers()){
            try {
                if (p.getLocation().distance(new Location(p.getWorld(), _npc.locX(), _npc.locY(), _npc.locZ())) < 5) {
                    PlayerConnection connection = ((CraftPlayer) p).getHandle().playerConnection;
                    Location npcLocation = _npc.getBukkitEntity().getLocation();
                    Location newNpcLocation = npcLocation.setDirection(p.getLocation().subtract(npcLocation).toVector());
                    float yaw = newNpcLocation.getYaw();
                    float pitch = newNpcLocation.getPitch();
                    connection.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(_npc.getId(), (byte) ((yaw % 360.) * 256 / 360), (byte) ((pitch % 360.) * 256 / 360), false));
                    connection.sendPacket(new PacketPlayOutEntityHeadRotation(_npc, (byte) ((yaw % 360.) * 256 / 360)));
                }
            }catch(Exception ex){}
        }
    }

    public void Reload(){
        for(Player p : Bukkit.getOnlinePlayers()){
            Hide(p);
        }
        Create();
    }

    public void Save(){
        File file = new File("plugins/InfernoNetwork/NPCs.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("NPCs." + _name + ".ID", _id);
        yml.set("NPCs." + _name + ".Type", _type.toString());
        yml.set("NPCs." + _name + ".Data", _data);
        yml.set("NPCs." + _name + ".Location", LocationUtil.Serialize(_location));
        try {
            yml.save(file);
        }catch (Exception ex){}
    }

    public void Delete(){
        File file = new File("plugins/InfernoNetwork/NPCs.yml");
        YamlConfiguration yml = YamlConfiguration.loadConfiguration(file);
        yml.set("NPCs." + _name, null);
        try {
            yml.save(file);
        }catch (Exception ex){}
        NPC.NPCs.remove(this);

        for(Player p : Bukkit.getOnlinePlayers()){
            PlayerConnection playerConnection = ((CraftPlayer)p).getHandle().playerConnection;
            playerConnection.sendPacket(new PacketPlayOutEntityDestroy(_npc.getId()));
        }
        RemoveHologram();
    }

    public void Clicked(Player p, int entityID){
        if(entityID == _npc.getId()){
            NPCInteractedEvent event = new NPCInteractedEvent(p, this);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                    new Runnable() {
                        @Override
                        public void run() {
                            Bukkit.getPluginManager().callEvent(event);
                        }
                    }, 1);
        }
    }

    public String getName(){
        return _name;
    }
    public String getData() {
        return _data;
    }
    public String getID() {
        return _id;
    }
    public NPCType getType() {
        return _type;
    }
    public Location getLocation() {
        return _location;
    }
    public void setName(String name){
        Delete();
        _name = name;
        Save();
        Create();
    }
    public void setData(String data) {
        _data = data;
        Save();
    }
    public void setID(String id) {
        _id = id;
        Save();
        Reload();
    }
    public void setType(NPCType type) {
        _type = type;
        Save();
    }
    public void setLocation(Location location) {
        _location = location;
        Save();
        Reload();
    }

    public EntityPlayer getNPC(){
        return _npc;
    }

}

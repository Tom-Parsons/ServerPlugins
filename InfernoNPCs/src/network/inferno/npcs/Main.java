package network.inferno.npcs;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers;
import io.netty.channel.Channel;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelPromise;
import net.minecraft.server.v1_15_R1.PacketPlayInUseEntity;
import network.inferno.npcs.commands.CommandManager;
import network.inferno.npcs.events.EventManager;
import network.inferno.npcs.npc.NPC;
import network.inferno.npcs.npc.NPCInteractedEvent;
import network.inferno.npcs.util.Dev;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;

public class Main extends JavaPlugin implements Listener {

    public static Main pl;
    public static String PREFIX = ChatColor.WHITE + "[" + ChatColor.GOLD + "InfernoNPCs" + ChatColor.WHITE + "] ";

    @Override
    public void onEnable() {
        pl = this;
        getServer().getPluginManager().registerEvents(this, this);
        CommandManager.RegisterCommands();
        EventManager.RegisterEvents();
        NPC.LoadNPCs();
        UpdateNPCs();
        ListenForPackets();
    }

    @Override
    public void onDisable() {
        for(Player p : Bukkit.getOnlinePlayers()){
            for(NPC npc : NPC.NPCs){
                npc.Hide(p);
            }
        }
        NPC.NPCs.clear();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e) {
        final Player player = e.getPlayer();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (player != null && player.isOnline()) {
                    for (NPC npc : NPC.NPCs) {
                        npc.Show(player);
                    }
                }
            }
        }.runTaskLater(Main.pl, 20);
    }

    private void UpdateNPCs(){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                new Runnable() {
                    @Override
                    public void run() {
                        for(NPC npc : NPC.NPCs){
                            npc.LookAtPlayers();
                        }
                        UpdateNPCs();
                    }
                }, 1);
    }

    private void ListenForPackets(){
        PluginManager pm = Bukkit.getPluginManager();
        ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        manager.addPacketListener(new PacketAdapter(this.pl, ListenerPriority.NORMAL, PacketType.Play.Client.USE_ENTITY) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                if (event.getPacketType() == PacketType.Play.Client.USE_ENTITY) {
                    boolean runNPCs = false;
                    if(event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.INTERACT_AT) {
                        if (event.getPacket().getHands().read(0) == EnumWrappers.Hand.MAIN_HAND) {
                            for (NPC npc : NPC.NPCs) {
                                npc.Clicked(event.getPlayer(), event.getPacket().getIntegers().read(0));
                            }
                        }
                    } else if (event.getPacket().getEntityUseActions().read(0) == EnumWrappers.EntityUseAction.ATTACK) {
                        for (NPC npc : NPC.NPCs) {
                            npc.Clicked(event.getPlayer(), event.getPacket().getIntegers().read(0));
                        }
                    }
                }
            }
        });
    }

}

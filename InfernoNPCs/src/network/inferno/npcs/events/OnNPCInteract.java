package network.inferno.npcs.events;

import network.inferno.npcs.commands.NPCCommand;
import network.inferno.npcs.npc.NPC;
import network.inferno.npcs.npc.NPCInteractedEvent;
import network.inferno.npcs.npc.NPCType;
import network.inferno.npcs.util.NPCEditor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class OnNPCInteract implements Listener {

    @EventHandler
    public void OnNPCInteract(NPCInteractedEvent e){
        if(e.isCancelled()) return;
        Player p = e.getPlayer();
        NPC npc = e.getNPC();
        if(p.hasPermission("inferno.npc")){
            if(p.isSneaking()){
                NPCEditor.ShowEditor(p, npc.getName());
                e.setCancelled(true);
                return;
            }
        }
        if(npc.getType() == NPCType.COMMAND){
            p.chat(npc.getData());
        }
    }

}

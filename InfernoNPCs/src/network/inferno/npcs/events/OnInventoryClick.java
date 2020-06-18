package network.inferno.npcs.events;

import network.inferno.npcs.npc.NPC;
import network.inferno.npcs.util.NPCEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class OnInventoryClick implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        NPCEditor.onInventoryClick(e);
    }

}

package network.inferno.npcs.events;

import network.inferno.npcs.util.NPCEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class OnInventoryClose implements Listener {

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        NPCEditor.onInventoryClose(e);
    }

}

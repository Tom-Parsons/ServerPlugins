package network.inferno.npcs.events;

import network.inferno.npcs.util.NPCEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

public class OnPlayerCommand implements Listener {

    @EventHandler
    public void onPlayerCommand(PlayerCommandPreprocessEvent e){
        NPCEditor.onPlayerCommand(e);
    }

}

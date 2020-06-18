package network.inferno.npcs.events;

import network.inferno.npcs.util.Dev;
import network.inferno.npcs.util.NPCEditor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class OnPlayerChat implements Listener {

    @EventHandler
    public void onPlayerChat(AsyncPlayerChatEvent e){
        NPCEditor.onPlayerChat(e);
    }

}

package network.inferno.npcs.npc;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class NPCInteractedEvent extends Event implements Cancellable {
    private Player player;
    private NPC npc;

    private static final HandlerList HANDLERS_LIST = new HandlerList();
    private boolean isCancelled;

    public NPCInteractedEvent(Player player, NPC npc){
        this.player = player;
        this.npc = npc;
        this.isCancelled = false;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.isCancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS_LIST;
    }

    public Player getPlayer(){
        return player;
    }
    public NPC getNPC() {
        return npc;
    }
}
package network.inferno.lobby.parkour;

import net.minecraft.server.v1_15_R1.ChatMessageType;
import net.minecraft.server.v1_15_R1.IChatBaseComponent;
import net.minecraft.server.v1_15_R1.Packet;
import net.minecraft.server.v1_15_R1.PacketPlayOutChat;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;

class ParkourTimer implements Runnable {

    private Player player;
    private boolean counting = false;
    private int milliseconds = 0;

    public ParkourTimer(Player p){
        player = p;
    }

    @Override
    public void run() {
        counting = true;
        long taskTime = 0;
        long sleepTime = 1;
        while (counting) {
            taskTime = System.currentTimeMillis();

            milliseconds++;
            SendActionbar(player);

            taskTime = System.currentTimeMillis()-taskTime;
            if (sleepTime-taskTime > 0 ) {
                if(!Thread.currentThread().isInterrupted()) {
                    try {
                        Thread.sleep(sleepTime - taskTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void SendActionbar(Player p) {
        DecimalFormat dec = new DecimalFormat("0.000");
        IChatBaseComponent chat = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.AQUA + dec.format(getMilliseconds() / 1000d) + "\"}");

        PacketPlayOutChat packetPlayOutChat = new PacketPlayOutChat(chat, ChatMessageType.GAME_INFO);

        CraftPlayer craft = (CraftPlayer) player;

        craft.getHandle().playerConnection.sendPacket(packetPlayOutChat);
    }

    public void Stop(){
        counting = false;
        Thread.currentThread().interrupt();
    }

    public int getMilliseconds(){
        return milliseconds;
    }

}
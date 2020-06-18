package network.inferno.lobby.features;

import network.inferno.core.commands.general.FlyCommand;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;

public class DoubleJump {

    private static ArrayList<Player> cannotDoubleJump = new ArrayList<Player>();

    public static void onPlayerToggleFlight(PlayerToggleFlightEvent e){
        Player p = e.getPlayer();
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        if(!cannotDoubleJump.contains(p) && !FlyCommand.flyingPlayers.contains(p) && p.hasPermission("inferno.doublejump")){
            if(p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR)
                return;
            e.setCancelled(true);
            p.setAllowFlight(false);
            p.setFlying(false);
            p.setVelocity(p.getLocation().getDirection().multiply(2).add(new Vector(0, 0.5, 0)));
            p.getWorld().playSound(p.getLocation(), Sound.ENTITY_BAT_TAKEOFF, 1, 1);
            cannotDoubleJump.add(p);
        }else{
            if (p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR && (!p.isFlying()) && !FlyCommand.flyingPlayers.contains(p)) {
                p.setAllowFlight(false);
            }
        }
    }

    public static void onPlayerMove(PlayerMoveEvent e){
        Player p = e.getPlayer();
        if(p.getWorld().getBlockAt(p.getLocation().getBlockX(), p.getLocation().getBlockY() - 1, p.getLocation().getBlockZ()).getType() != Material.AIR) cannotDoubleJump.remove(p);
        if(!cannotDoubleJump.contains(p) && !FlyCommand.flyingPlayers.contains(p)){
            if ((p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR)
                    && (p.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR) && (!p.isFlying())) {
                p.setAllowFlight(true);
            }
            if ((p.getGameMode() != GameMode.CREATIVE && p.getGameMode() != GameMode.SPECTATOR) && (!p.isFlying())) {
                p.setAllowFlight(true);
            }
        }else{
            if ((p.getGameMode() != GameMode.CREATIVE) && p.getGameMode() != GameMode.SPECTATOR && (!p.isFlying()) && !FlyCommand.flyingPlayers.contains(p)) {
                p.setAllowFlight(false);
            }
        }
    }

}

package network.inferno.lobby.commands;

import network.inferno.lobby.parkour.Parkour;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class ParkourCommand extends BukkitCommand {

    public ParkourCommand(String name){
        super(name);
        this.description = "Creates and modifies parkour";
        this.usageMessage = "/parkour <Create/Edit> <id>";
        this.setPermission("inferno.parkour");
        this.setAliases(new ArrayList<String>());
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args) {
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(core.format.permission());
            return true;
        }
        Player p = (Player)sender;
        int length = args.length;
        if (length == 0) {
            p.sendMessage(core.format.unknownPlayer(args[0]));
            return true;
        }else if(length == 1){
            if(args[0].equalsIgnoreCase("list")) {
                for(Parkour parkour : Parkour.parkours){
                    p.sendMessage(parkour.getID());
                }
            }else{
                p.sendMessage(core.format.invalidArguments() + usageMessage);
            }
            return true;
        }
        else if(length > 1) {
            if(args[0].equalsIgnoreCase("create")) {
                String id = args[1];
                Parkour parkour = new Parkour(id, new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()), new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()), new Location(p.getLocation().getWorld(), p.getLocation().getBlockX(), p.getLocation().getBlockY(), p.getLocation().getBlockZ()), new HashMap<Integer, Location>());
                Parkour.parkours.add(parkour);
                parkour.BeginEditing(p);
                return true;
            }else if(args[0].equalsIgnoreCase("edit")){
                for(Parkour parkour : Parkour.parkours){
                    if(parkour.getID().equalsIgnoreCase(args[1])){
                        parkour.BeginEditing(p);
                        break;
                    }
                }
                return true;
            }else{
                p.sendMessage(core.format.invalidArguments() + usageMessage);
                return true;
            }
        }
        else {
            p.sendMessage(core.format.invalidArguments() + usageMessage);
        }
        return false;
    }

}

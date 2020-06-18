package network.inferno.npcs.commands;

import network.inferno.npcs.Main;
import network.inferno.npcs.npc.NPC;
import network.inferno.npcs.npc.NPCType;
import network.inferno.npcs.util.Dev;
import network.inferno.npcs.util.NPCEditor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class NPCCommand extends BukkitCommand {

    public NPCCommand(String name){
        super(name);
        this.description = "Create or delete NPCs";
        this.usageMessage = "/npc <create/delete/edit> <name>";
        this.setPermission("inferno.npc");
        this.setAliases(new ArrayList<String>());
    }

    @Override
    public boolean execute(CommandSender sender, String alias, String[] args){
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        if (!sender.hasPermission(this.getPermission())) {
            sender.sendMessage(core.format.permission());
            return true;
        }
        Player p = (Player)sender;
        int length = args.length;
        if (length > 1) {
            String name = "";
            for(int x = 1; x <= args.length - 1; x++){
                name += args[x] + " ";
            }
            name = name.substring(0, name.length() - 1);
            NPC npc = NPC.getNPC(name);
            if(args[0].equalsIgnoreCase("create")){
                NPC newNPC = new NPC(name, "1217423270", NPCType.SERVER, "LOBBY", p.getLocation());
                Main.pl.getServer().getPluginManager().registerEvents(newNPC, Main.pl);
                NPCEditor.ShowEditor(p, name);
            }else if(args[0].equalsIgnoreCase("edit")){
                if(NPC.getNPC(name) != null){

                }else{

                }
                NPCEditor.ShowEditor(p, name);
            }else if(args[0].equalsIgnoreCase("delete")){
                if(npc != null){
                    npc.Delete();
                    p.sendMessage(core.format.label() + core.format.colorize("&7Successfully deleted &6" + name));
                }else{
                    p.sendMessage(core.format.colorize("&cNo NPC was found with this name!"));
                }
            }else{
                p.sendMessage(core.format.invalidArguments() + usageMessage);
            }
        }else {
            p.sendMessage(core.format.invalidArguments() + usageMessage);
        }
        return false;
    }

}

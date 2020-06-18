package network.inferno.npcs.util;

import network.inferno.npcs.Main;
import network.inferno.npcs.npc.NPC;
import network.inferno.npcs.npc.NPCType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class NPCEditor {

    public static HashMap<Player, String> editing = new HashMap<Player, String>();
    private static ArrayList<Player> changingInventory = new ArrayList<Player>();
    public static ArrayList<Player> changingNPCName = new ArrayList<Player>();
    public static ArrayList<Player> changingNPCID = new ArrayList<Player>();
    public static ArrayList<Player> changingNPCData = new ArrayList<Player>();

    public static void ShowEditor(Player p, String name){
        Inventory editor = Bukkit.createInventory(null, 9, ChatColor.GOLD + "NPC Editor: " + name);

        NPC npc = NPC.getNPC(name);

        ItemStack nameItem = new ItemStack(Material.NAME_TAG);
        ItemMeta nameItemMeta = nameItem.getItemMeta();
        nameItemMeta.setDisplayName(ChatColor.GOLD + "Name: " + ChatColor.WHITE + name);
        nameItemMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to change name"));
        nameItem.setItemMeta(nameItemMeta);

        ItemStack skinItem = new ItemStack(Material.PLAYER_HEAD);
        ItemMeta skinItemMeta = skinItem.getItemMeta();
        skinItemMeta.setDisplayName(ChatColor.GOLD + "Skin ID: " + ChatColor.WHITE + npc.getID());
        skinItemMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to change skin ID (Mineskins.org)"));
        skinItem.setItemMeta(skinItemMeta);

        ItemStack typeItem = new ItemStack(Material.DIRT);
        String typeItemName = "";
        if(npc.getType() == NPCType.COMMAND){
            typeItem = new ItemStack(Material.COMMAND_BLOCK);
            typeItemName = ChatColor.GOLD + "Type: " + ChatColor.WHITE + "Command";
        }else if(npc.getType() == NPCType.SERVER){
            typeItem = new ItemStack(Material.OBSERVER);
            typeItemName = ChatColor.GOLD + "Type: " + ChatColor.WHITE + "Server";
        }
        ItemMeta typeItemMeta = typeItem.getItemMeta();
        typeItemMeta.setDisplayName(typeItemName);
        typeItemMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to change type"));
        typeItem.setItemMeta(typeItemMeta);

        ItemStack dataItem = new ItemStack(Material.PAPER);
        ItemMeta dataItemMeta = dataItem.getItemMeta();
        if(npc.getType() == NPCType.COMMAND){
            dataItemMeta.setDisplayName(ChatColor.GOLD + "Command: " + ChatColor.WHITE + npc.getData());
            dataItemMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to change the command the player will run"));
        }else if(npc.getType() == NPCType.SERVER){
            dataItemMeta.setDisplayName(ChatColor.GOLD + "Server: " + ChatColor.WHITE + npc.getData());
            dataItemMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to change the server the player will queue for"));
        }
        dataItem.setItemMeta(dataItemMeta);

        ItemStack locationItem = new ItemStack(Material.COMPASS);
        ItemMeta locationItemMeta = locationItem.getItemMeta();
        locationItemMeta.setDisplayName(ChatColor.GOLD + "Location");
        List<String> locationItemLore = new ArrayList<String>();
        locationItemLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to set location to your current location");
        locationItemLore.add("");
        locationItemLore.add(ChatColor.GRAY + "World: " +  npc.getLocation().getWorld().getName());
        locationItemLore.add(ChatColor.GRAY + "X: " + npc.getLocation().getX());
        locationItemLore.add(ChatColor.GRAY + "Y: " + npc.getLocation().getY());
        locationItemLore.add(ChatColor.GRAY + "Z: " + npc.getLocation().getZ());
        locationItemMeta.setLore(locationItemLore);
        locationItem.setItemMeta(locationItemMeta);

        ItemStack deleteItem = new ItemStack(Material.BARRIER);
        ItemMeta deleteItemMeta = deleteItem.getItemMeta();
        deleteItemMeta.setDisplayName(ChatColor.RED + "DELETE");
        deleteItemMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to delete this NPC"));
        deleteItem.setItemMeta(deleteItemMeta);

        editor.setItem(0, nameItem);
        editor.setItem(1, skinItem);
        editor.setItem(2, typeItem);
        editor.setItem(3, dataItem);
        editor.setItem(4, locationItem);
        editor.setItem(8, deleteItem);

        if(!editing.containsKey(p)) editing.put(p, name);
        p.openInventory(editor);
        if(changingInventory.contains(p)) changingInventory.remove(p);
    }

    public static void ShowEditor_Type(Player p){
        Inventory editor_type = Bukkit.createInventory(null, 9, ChatColor.GOLD + "NPC Type: " + editing.get(p));

        NPC npc = NPC.getNPC(editing.get(p));

        ItemStack commandItem = new ItemStack(Material.COMMAND_BLOCK);
        ItemMeta commandItemMeta = commandItem.getItemMeta();
        if(npc.getType() == NPCType.COMMAND) {
            commandItemMeta.setDisplayName(ChatColor.GOLD + "Command " + ChatColor.GREEN + ChatColor.ITALIC.toString() + "Current Type");
        }else{
            commandItemMeta.setDisplayName(ChatColor.GOLD + "Command");
        }
        commandItemMeta.setLore(Arrays.asList(ChatColor.GRAY + "The player runs a command when interacting with the NPC", "", ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to change to this type"));
        commandItem.setItemMeta(commandItemMeta);

        ItemStack serverItem = new ItemStack(Material.OBSERVER);
        ItemMeta serverItemMeta = serverItem.getItemMeta();
        if(npc.getType() == NPCType.SERVER) {
            serverItemMeta.setDisplayName(ChatColor.GOLD + "Server " + ChatColor.GREEN + ChatColor.ITALIC.toString() + "Current Type");
        }else{
            serverItemMeta.setDisplayName(ChatColor.GOLD + "Server");
        }
        serverItemMeta.setLore(Arrays.asList(ChatColor.GRAY + "The player joins a server queue when interacting with the NPC", "", ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to change to this type"));
        serverItem.setItemMeta(serverItemMeta);

        editor_type.setItem(0, commandItem);
        editor_type.setItem(1, serverItem);

        p.openInventory(editor_type);
        if(changingInventory.contains(p)) changingInventory.remove(p);
    }

    public static void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        String title = e.getView().getTitle();
        ItemStack item = e.getCurrentItem();
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        if(editing.containsKey(p)){
            NPC npc = NPC.getNPC(editing.get(p));
            if(title.contains("NPC Editor: ")){
                e.setCancelled(true);
                if(item != null){
                    if(item.hasItemMeta()){
                        if(item.getItemMeta().getDisplayName().contains("Name: ")){
                            changingNPCName.add(p);
                            p.sendMessage(core.format.label() + core.format.colorize("&7Please type the new name of the NPC. &oType 'cancel' to cancel"));
                            p.closeInventory();
                        }else if(item.getItemMeta().getDisplayName().contains("Skin ID: ")){
                            changingNPCID.add(p);
                            p.sendMessage(core.format.label() + core.format.colorize("&7Please type the new skin ID of the NPC. &oType 'cancel' to cancel"));
                            p.closeInventory();
                        }else if(item.getItemMeta().getDisplayName().contains("Type: ")){
                            changingInventory.add(p);
                            ShowEditor_Type(p);
                        }else if(item.getItemMeta().getDisplayName().contains("Command: ") || item.getItemMeta().getDisplayName().contains("Server: ")){
                            changingNPCData.add(p);
                            if(npc.getType() == NPCType.COMMAND){
                                p.sendMessage(core.format.label() + core.format.colorize("&7Please type the new command the player should run &oType 'cancel' to cancel"));
                            }else if(npc.getType() == NPCType.SERVER){
                                p.sendMessage(core.format.label() + core.format.colorize("&7Please type the new server the player should join the queue of &oType 'cancel' to cancel"));
                            }
                            p.closeInventory();
                        }else if(item.getItemMeta().getDisplayName().contains("Location")){
                            npc.setLocation(p.getLocation());
                            p.sendMessage(core.format.label() + core.format.colorize("&7You have changed the location of &6" + editing.get(p) + " &7 to your current location"));
                        }else if(item.getItemMeta().getDisplayName().contains("DELETE")){
                            p.chat("/npc delete " + editing.get(p));
                            p.closeInventory();
                            editing.remove(p);
                        }
                    }
                }
            }else if(title.contains("NPC Type: ")){
                e.setCancelled(true);
                if(item.hasItemMeta()) {
                    if (item.getItemMeta().getDisplayName().contains("Command")) {
                        npc.setType(NPCType.COMMAND);
                        changingInventory.add(p);
                        ShowEditor(p, editing.get(p));
                    } else if (item.getItemMeta().getDisplayName().contains("Server")) {
                        npc.setType(NPCType.SERVER);
                        changingInventory.add(p);
                        ShowEditor(p, editing.get(p));
                    }
                }
            }
        }
    }

    public static void onInventoryClose(InventoryCloseEvent e){
        Player p = (Player) e.getPlayer();
        if(editing.containsKey(p)){
            if(!changingNPCName.contains(p) && !changingNPCID.contains(p) && !changingNPCData.contains(p) && !changingInventory.contains(p)) {
                editing.remove(p);
            }
        }
    }

    public static void onPlayerChat(AsyncPlayerChatEvent e){
        Player p = e.getPlayer();
        String msg = e.getMessage();
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        if(changingNPCName.contains(p)){
            e.setCancelled(true);
            if(msg.equalsIgnoreCase("cancel")){
                changingNPCName.remove(p);
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                        new Runnable() {
                            @Override
                            public void run() {
                                changingInventory.add(p);
                                ShowEditor(p, editing.get(p));
                            }
                        }, 1);
                p.sendMessage(core.format.label() + core.format.colorize("&7You have cancelled changing the name of &6" + editing.get(p)));
            }else{
                if(msg.toCharArray().length <= 16){
                    p.sendMessage(core.format.label() + core.format.colorize("&7You have changed the name of &6" + editing.get(p) + " &7to &6" + msg));
                    NPC editedNPC = NPC.getNPC(editing.get(p));
                    NPC.NPCs.remove(editedNPC);
                    editedNPC.setName(msg);
                    NPC.NPCs.add(editedNPC);
                    editing.put(p, msg);
                    changingNPCName.remove(p);
                    Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                            new Runnable() {
                                @Override
                                public void run() {
                                    changingInventory.add(p);
                                    ShowEditor(p, editing.get(p));
                                }
                            }, 1);
                }else{
                    p.sendMessage(ChatColor.RED + "That name is too long! Please enter a different one");
                }
            }
        }else if(changingNPCID.contains(p)){
            e.setCancelled(true);
            if (!msg.equalsIgnoreCase("cancel")) {
                NPC editedNPC = NPC.getNPC(editing.get(p));
                NPC.NPCs.remove(editedNPC);
                editedNPC.setID(msg);
                NPC.NPCs.add(editedNPC);
                p.sendMessage(core.format.label() + core.format.colorize("&7You have changed the skin ID of &6" + editing.get(p) + " &7 to &6" + msg));
            }else{
                p.sendMessage(core.format.label() + core.format.colorize("&7You have cancelled changing the name of &6" + editing.get(p)));
            }
            changingNPCID.remove(p);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                    new Runnable() {
                        @Override
                        public void run() {
                            changingInventory.add(p);
                            ShowEditor(p, editing.get(p));
                        }
                    }, 1);
        }else if(changingNPCData.contains(p)){
            e.setCancelled(true);
            if(!msg.equalsIgnoreCase("cancel")){
                NPC editedNPC = NPC.getNPC(editing.get(p));
                NPC.NPCs.remove(editedNPC);
                editedNPC.setData(msg);
                NPC.NPCs.add(editedNPC);
                if(editedNPC.getType() == NPCType.COMMAND){
                    p.sendMessage(core.format.label() + core.format.colorize("&7You have changed the command of &6" + editing.get(p) + " &7 to &6" + msg));
                }else if(editedNPC.getType() == NPCType.SERVER){
                    p.sendMessage(core.format.label() + core.format.colorize("&7You have changed the server of &6" + editing.get(p) + " &7 to &6" + msg));
                }
            }else{
                if(NPC.getNPC(editing.get(p)).getType() == NPCType.COMMAND){
                    p.sendMessage(core.format.label() + core.format.colorize("&7You have cancelled changing the command of &6" + editing.get(p)));
                }else if(NPC.getNPC(editing.get(p)).getType() == NPCType.SERVER){
                    p.sendMessage(core.format.label() + core.format.colorize("&7You have cancelled changing the server of &6" + editing.get(p)));
                }
            }
            changingNPCData.remove(p);
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                    new Runnable() {
                        @Override
                        public void run() {
                            changingInventory.add(p);
                            ShowEditor(p, editing.get(p));
                        }
                    }, 1);
        }
    }

    public static void onPlayerCommand(PlayerCommandPreprocessEvent e){
        Player p = e.getPlayer();
        String msg = e.getMessage();
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        if(changingNPCData.contains(p)){
            e.setCancelled(true);
            NPC editedNPC = NPC.getNPC(editing.get(p));
            NPC.NPCs.remove(editedNPC);
            editedNPC.setData(msg);
            NPC.NPCs.add(editedNPC);
            if(NPC.getNPC(editing.get(p)).getType() == NPCType.COMMAND){
                p.sendMessage(core.format.label() + core.format.colorize("&7You have changed the command of &6" + editing.get(p) + " &7 to &6" + msg));
            }else if(NPC.getNPC(editing.get(p)).getType() == NPCType.SERVER){
                p.sendMessage(core.format.label() + core.format.colorize("&7You have changed the server of &6" + editing.get(p) + " &7 to &6" + msg));
            }
            changingNPCData.remove(p);
            ShowEditor(p, editing.get(p));
        }
    }

}

package network.inferno.lobby.managers;

import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import network.inferno.pd.Main;
import network.inferno.pd.managers.PlayerDistributor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class LobbyManager {

    public static void PlayerJoin(Player p){
        p.teleport(p.getWorld().getSpawnLocation());
        p.setGameMode(GameMode.ADVENTURE);
        GiveItems(p);
    }

    public static void PlayerLeave(Player p){

    }

    public static void GiveItems(Player p){
        ItemStack serverSelector = new ItemStack(Material.COMPASS);
        ItemMeta serverSelectorMeta = serverSelector.getItemMeta();
        serverSelectorMeta.setDisplayName(ChatColor.GOLD + "Server Selector");
        serverSelector.setItemMeta(serverSelectorMeta);

        ItemStack lobbySelector = new ItemStack(Material.CLOCK);
        ItemMeta lobbySelectorMeta = lobbySelector.getItemMeta();
        lobbySelectorMeta.setDisplayName(ChatColor.BLUE + "Lobby Selector");
        lobbySelector.setItemMeta(lobbySelectorMeta);

        ItemStack cosmeticsItem = new ItemStack(Material.CHEST);
        ItemMeta cosmeticsItemMeta = cosmeticsItem.getItemMeta();
        cosmeticsItemMeta.setDisplayName(ChatColor.GREEN + "Cosmetics");
        cosmeticsItem.setItemMeta(cosmeticsItemMeta);

        ItemStack achievementsItem = new ItemStack(Material.BOOK);
        ItemMeta achievemeentsItemMeta = achievementsItem.getItemMeta();
        achievemeentsItemMeta.setDisplayName(ChatColor.WHITE + "Achievements");
        achievementsItem.setItemMeta(achievemeentsItemMeta);

        ItemStack friendsItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta friendsItemMeta = (SkullMeta) friendsItem.getItemMeta();
        friendsItemMeta.setOwningPlayer(p);
        friendsItemMeta.setDisplayName(ChatColor.AQUA + "Friends");
        friendsItem.setItemMeta(friendsItemMeta);

        p.getInventory().clear();
        p.getInventory().setItem(0, serverSelector);
        p.getInventory().setItem(1, lobbySelector);
        p.getInventory().setItem(4, cosmeticsItem);
        p.getInventory().setItem(7, achievementsItem);
        p.getInventory().setItem(8, friendsItem);
    }

    public static void ShowServerSelector(Player p){
        Inventory serverInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Server Selector");
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        HashMap<String, Integer> servers = new HashMap<String, Integer>();
        HashMap<String, Integer> serversPlayers = new HashMap<String, Integer>();
        for(String s : core.bdm.onlineServers.keySet()){
            if(s.contains("-") && !s.contains("LOBBY")){
                String name = s.split("-")[0];
                if(!servers.containsKey(name)){
                    servers.put(name, 1);
                }else{
                    servers.put(name, servers.get(name) + 1);
                }
                if(!serversPlayers.containsKey(name)){
                    serversPlayers.put(name, core.bdm.onlineServers.get(s));
                }else{
                    serversPlayers.put(name, serversPlayers.get(name) + core.bdm.onlineServers.get(s));
                }
            }
        }

        ItemStack testItem;
        ItemMeta testItemMeta;
        if(!servers.containsKey("TEST")){
            testItem = new ItemStack(Material.RED_TERRACOTTA);
            testItemMeta = testItem.getItemMeta();
            testItemMeta.setDisplayName(ChatColor.RED + "Test");
            testItemMeta.setLore(Arrays.asList(ChatColor.GRAY + "It appears this minigame has no servers available!", ChatColor.GRAY + "Please check again later!"));
        }else{
            testItem = new ItemStack(Material.TNT);
            testItemMeta = testItem.getItemMeta();
            testItemMeta.setDisplayName(ChatColor.GREEN + "Test");
            testItemMeta.setLore(Arrays.asList(ChatColor.GOLD.toString() + servers.get("TEST") + ChatColor.GRAY + " servers available",
                    ChatColor.GOLD.toString() + serversPlayers.get("TEST") + ChatColor.GRAY + " players",
                    "",
                    ChatColor.GREEN + "Click to join a server"));
        }
        testItem.setItemMeta(testItemMeta);

        ItemStack tntWarsItem;
        ItemMeta tntWarsItemMeta;
        if(!servers.containsKey("TNT")){
            tntWarsItem = new ItemStack(Material.RED_TERRACOTTA);
            tntWarsItemMeta = tntWarsItem.getItemMeta();
            tntWarsItemMeta.setDisplayName(ChatColor.RED + "TNT Wars");
            tntWarsItemMeta.setLore(Arrays.asList(ChatColor.GRAY + "It appears this minigame has no servers available!", ChatColor.GRAY + "Please check again later!"));
        }else{
            tntWarsItem = new ItemStack(Material.TNT);
            tntWarsItemMeta = tntWarsItem.getItemMeta();
            tntWarsItemMeta.setDisplayName(ChatColor.GREEN + "TNT Wars");
            tntWarsItemMeta.setLore(Arrays.asList(ChatColor.GOLD.toString() + servers.get("TNT") + ChatColor.GRAY + " servers available",
                    ChatColor.GOLD.toString() + serversPlayers.get("TNT") + ChatColor.GRAY + " players",
                    "",
                    ChatColor.GREEN + "Click to join a server"));
        }
        tntWarsItem.setItemMeta(tntWarsItemMeta);

        ItemStack colourWarsItem;
        ItemMeta colourWarsItemMeta;
        if(!servers.containsKey("CW")){
            colourWarsItem = new ItemStack(Material.RED_TERRACOTTA);
            colourWarsItemMeta = colourWarsItem.getItemMeta();
            colourWarsItemMeta.setDisplayName(ChatColor.RED + "Colour Wars");
            colourWarsItemMeta.setLore(Arrays.asList(ChatColor.GRAY + "It appears this minigame has no servers available!", ChatColor.GRAY + "Please check again later!"));
        }else{
            colourWarsItem = new ItemStack(Material.WHITE_WOOL);
            colourWarsItemMeta = colourWarsItem.getItemMeta();
            colourWarsItemMeta.setDisplayName(ChatColor.GREEN + "Colour Wars");
            colourWarsItemMeta.setLore(Arrays.asList(ChatColor.GOLD.toString() + servers.get("CW") + ChatColor.GRAY + " servers available",
                    ChatColor.GOLD.toString() + serversPlayers.get("CW") + ChatColor.GRAY + " players",
                    "",
                    ChatColor.GREEN + "Click to join a server"));
        }
        colourWarsItem.setItemMeta(colourWarsItemMeta);

        serverInventory.addItem(testItem);
        serverInventory.addItem(tntWarsItem);
        serverInventory.addItem(colourWarsItem);

        p.openInventory(serverInventory);
    }

    public static void ShowLobbySelector(Player p){
        Inventory lobbyInventory = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Lobby Selector");

        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);

        ItemStack lobby1Item;
        ItemMeta lobby1ItemMeta;
        if(SubAPI.getInstance().getName().equalsIgnoreCase("LOBBY-1")){
            lobby1Item = new ItemStack(Material.RED_TERRACOTTA);
            lobby1ItemMeta = lobby1Item.getItemMeta();
            lobby1ItemMeta.setDisplayName(ChatColor.RED + "Lobby 1");
            lobby1ItemMeta.setLore(Arrays.asList(ChatColor.WHITE.toString() + core.bdm.onlineServers.get("LOBBY-1") + " Players", "", ChatColor.RED + "Already Connected to this Lobby!"));
        }else{
            lobby1Item = new ItemStack(Material.QUARTZ_BLOCK);
            lobby1ItemMeta = lobby1Item.getItemMeta();
            lobby1ItemMeta.setDisplayName(ChatColor.GREEN + "Lobby 1");
            lobby1ItemMeta.setLore(Arrays.asList(ChatColor.WHITE.toString() + core.bdm.onlineServers.get("LOBBY-1") + " Players", "", ChatColor.GREEN + "Click to connect to this Lobby!"));
        }
        lobby1Item.setItemMeta(lobby1ItemMeta);
        ItemStack lobby2Item;
        ItemMeta lobby2ItemMeta;
        if(SubAPI.getInstance().getName().equalsIgnoreCase("LOBBY-2")){
            lobby2Item = new ItemStack(Material.LIME_TERRACOTTA);
            lobby2ItemMeta = lobby2Item.getItemMeta();
            lobby2ItemMeta.setDisplayName(ChatColor.RED + "Lobby 2");
            lobby2ItemMeta.setLore(Arrays.asList(ChatColor.WHITE.toString() + core.bdm.onlineServers.get("LOBBY-2") + " Players", "", ChatColor.RED + "Already Connected to this Lobby!"));
        }else{
            lobby2Item = new ItemStack(Material.QUARTZ_BLOCK);
            lobby2ItemMeta = lobby2Item.getItemMeta();
            lobby2ItemMeta.setDisplayName(ChatColor.GREEN + "Lobby 2");
            lobby2ItemMeta.setLore(Arrays.asList(ChatColor.WHITE.toString() + core.bdm.onlineServers.get("LOBBY-2") + " Players", "", ChatColor.GREEN + "Click to connect to this Lobby!"));
        }
        lobby2Item.setItemMeta(lobby2ItemMeta);

        lobbyInventory.setItem(0, lobby1Item);
        lobbyInventory.setItem(1, lobby2Item);
        p.openInventory(lobbyInventory);
    }

    public static void ShowCosmeticSelector(Player p){
        Inventory cosmetics = Bukkit.createInventory(null, 9, ChatColor.GOLD + "Cosmetics");

        ItemStack hatsItem = new ItemStack(Material.PLAYER_HEAD);
        SkullMeta hatsItemMeta = (SkullMeta) hatsItem.getItemMeta();
        hatsItemMeta.setDisplayName(ChatColor.BLUE + "Hats");
        hatsItemMeta.setOwningPlayer(p);
        hatsItemMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() +  "Click to view your hats"));
        hatsItem.setItemMeta(hatsItemMeta);

        ItemStack trailsItem = new ItemStack(Material.MAGMA_CREAM);
        ItemMeta trailsItemMeta = trailsItem.getItemMeta();
        trailsItemMeta.setDisplayName(ChatColor.BLUE + "Trails");
        trailsItemMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() +  "Click to view your trails"));
        trailsItem.setItemMeta(trailsItemMeta);

        cosmetics.setItem(3, hatsItem);
        cosmetics.setItem(5, trailsItem);
        p.openInventory(cosmetics);
    }

    public static void onPlayerInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        ItemStack i = p.getInventory().getItemInMainHand();
        if(i != null){
            if(i.hasItemMeta()){
                if(i.getItemMeta().getDisplayName().contains("Server Selector")){
                    e.setCancelled(true);
                    ShowServerSelector(p);
                }else if(i.getItemMeta().getDisplayName().contains("Lobby Selector")){
                    e.setCancelled(true);
                    ShowLobbySelector(p);
                }else if(i.getItemMeta().getDisplayName().contains("Cosmetics")){
                    e.setCancelled(true);
                    ShowCosmeticSelector(p);
                }else if(i.getItemMeta().getDisplayName().contains("Achievements")){
                    e.setCancelled(true);
                    p.chat("/achievements");
                }else if(i.getItemMeta().getDisplayName().contains("Friends")){
                    e.setCancelled(true);
                    p.chat("/friend list");
                }
            }
        }
    }

    public static void onInventoryClick(InventoryClickEvent e){
        Player p = (Player) e.getWhoClicked();
        network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        String title = e.getView().getTitle();
        ItemStack item = e.getCurrentItem();
        e.setCancelled(true);
        if(title.contains("Server Selector")){
            if(item != null){
                if(item.hasItemMeta()){
                    if(item.getItemMeta().getLore().contains(ChatColor.GREEN + "Click to join a server")) {
                        String serverName = core.format.ClearFormatting(item.getItemMeta().getDisplayName());
                        Main.pl.pd.JoinServer(p, serverName);
                        p.closeInventory();
                    }
                }
            }
        }else if(title.contains("Lobby Selector")){
            if(item != null){
                if(item.hasItemMeta()){
                    if(item.getType() == Material.QUARTZ_BLOCK || item.getType() == Material.RED_TERRACOTTA) {
                        String lobbyName = core.format.ClearFormatting(item.getItemMeta().getDisplayName());
                        if (!lobbyName.equals(SubAPI.getInstance().getName())) {
                            p.sendMessage(core.format.label() + core.format.colorize("&7Connecting to &6" + lobbyName + "&7..."));
                            core.bmm.Connect(p, lobbyName.toUpperCase().replace(" ", "-"));
                            p.closeInventory();
                        }
                    }
                }
            }
        }else if(title.contains("Cosmetics")){
            if(item != null){
                if(item.hasItemMeta()){
                    String cosmeticName = core.format.ClearFormatting(item.getItemMeta().getDisplayName());
                    if(cosmeticName.equalsIgnoreCase("Hats")){
                        com.xiis.infernocosmetics.cosmetics.Hats.chooseHats(p);
                    }else if(cosmeticName.equalsIgnoreCase("Trails")){
                        com.xiis.infernocosmetics.cosmetics.Trails.chooseTrails(p);
                    }
                }
            }
        }
    }

}

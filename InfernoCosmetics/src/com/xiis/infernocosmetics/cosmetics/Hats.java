package com.xiis.infernocosmetics.cosmetics;

import com.xiis.infernocosmetics.cosmetics.crates.Crate;
import com.xiis.infernocosmetics.player.PlayerInformation;
import com.xiis.infernocosmetics.util.StringUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class Hats implements Listener {

	private network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);

	public static ArrayList<Hat> Hats = new ArrayList<Hat>();
	public static ArrayList<Hat> Common = new ArrayList<Hat>();
	public static ArrayList<Hat> Uncommon = new ArrayList<Hat>();
	public static ArrayList<Hat> Rare = new ArrayList<Hat>();
	public static ArrayList<Hat> Epic = new ArrayList<Hat>();
	public static ArrayList<Hat> Legendary = new ArrayList<Hat>();

	public static Hat getHatFromName(String n){
		for(Hat h : Hats){
			if(h.Name().equals(n)) return h;
		}
		if(n.equals("None")) return null;
		return Hats.get(0);
	}

	public static Hat getHatFromID(String id){
		for(Hat h : Hats){
			if(h.ID().equals(id)) return h;
		}
		if(id.equals("None")) return null;
		return Hats.get(0);
	}
	
	public HashMap<Player, Integer> inInvent = new HashMap<Player, Integer>();
	
	public static void chooseHats(Player p){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		Inventory choosing =  Bukkit.getServer().createInventory(null, 9, ChatColor.BLUE + "Hats: " + pi.getOwnedHatSize() + "/" + Hats.size());
		ItemStack rem = new ItemStack(Material.BARRIER);
		ItemMeta remM = rem.getItemMeta();
		remM.setDisplayName(ChatColor.WHITE + "Remove Hat");
		rem.setItemMeta(remM);
		choosing.setItem(0, rem);
		ItemStack all = getHatFromID("candy_box").Skull();
		ItemMeta allM = all.getItemMeta();
		allM.setDisplayName(ChatColor.WHITE + "All Hats");
		all.setItemMeta(allM);
		choosing.setItem(2, all);
		ItemStack owned = getHatFromID("red_sushi").Skull();
		ItemMeta ownedM = owned.getItemMeta();
		ownedM.setDisplayName(ChatColor.WHITE + "Owned Hats");
		owned.setItemMeta(ownedM);
		choosing.setItem(4, owned);
		ItemStack unowned = getHatFromID("fries").Skull();
		ItemMeta unownedM = unowned.getItemMeta();
		unownedM.setDisplayName(ChatColor.WHITE + "Unowned Hats");
		unowned.setItemMeta(unownedM);
		choosing.setItem(6, unowned);
		p.openInventory(choosing);
	}
	
	public void showAllHats(Player p, int pa){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		ArrayList<Inventory> hatsInventory = new ArrayList<Inventory>();
		inInvent.remove(p);
		int size = Hats.size();
		double pagesD = (double) size / 27;
		int pages = (int) Math.ceil(pagesD);
		int totalI = 0;
		for(int i = 0; i < pages; i++){
			Inventory allHats =  Bukkit.getServer().createInventory(null, 36, ChatColor.BLUE + "Hats: Page " + (i + 1));
			for(int x = 0; x < 27; x++){
				if(Hats.size() == totalI) break;
				ItemStack a = Hats.get(totalI).Skull();
				ItemMeta am = a.getItemMeta();
				if(pi.hasHat(Hats.get(totalI))){
					am.setDisplayName(ChatColor.GOLD.toString() + Crate.RARITY_STRING(Hats.get(totalI).Rarity()) + " " + ChatColor.GREEN + Hats.get(totalI).Name());
				}else{
					am.setDisplayName(ChatColor.GOLD.toString() + Crate.RARITY_STRING(Hats.get(totalI).Rarity()) + " " + ChatColor.RED + Hats.get(totalI).Name());
				}
				a.setItemMeta(am);
				if(Hats.get(totalI).Rarity() != Crate.LEGENDARY){
					allHats.setItem(x, a);
				}else{
					if(pi.hasHat(Hats.get(totalI))){
						allHats.setItem(x, a);
					}
				}
				totalI++;
			}
			ItemStack next = getHatFromID("arrow_right").Skull();
			ItemMeta nextM = next.getItemMeta();
			nextM.setDisplayName(ChatColor.WHITE + "Next Page");
			next.setItemMeta(nextM);
			ItemStack prev = getHatFromID("arrow_left").Skull();
			ItemMeta prevM = prev.getItemMeta();
			prevM.setDisplayName(ChatColor.WHITE + "Previous Page");
			prev.setItemMeta(prevM);
			if(i > 0) allHats.setItem(29, prev);
			if(i < pages - 1) allHats.setItem(33, next);

			ItemStack back = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = back.getItemMeta();
			backMeta.setDisplayName(ChatColor.RED + "Back");
			back.setItemMeta(backMeta);
			allHats.setItem(27, back);

			hatsInventory.add(allHats);
		}
		inInvent.put(p, pa);
		p.openInventory(hatsInventory.get(pa));
	}
	
	public void showAllOwnedHats(Player p, int pa){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		ArrayList<Inventory> hatsInventory = new ArrayList<Inventory>();
		inInvent.remove(p);
		ArrayList<String> hatsS = pi.getHats();
		ArrayList<Hat> hats = new ArrayList<Hat>();
		for(String s : hatsS){
			if(!s.equals("")) {
				Hat h = getHatFromID(s);
				hats.add(h);
			}
		}
		int size = hats.size();
		double pagesD = (double) size / 27;
		int pages = (int) Math.ceil(pagesD);
		int totalI = 0;
		if(pages == 0) pages = 1;
		for(int i = 0; i < pages; i++){
			Inventory allHats =  Bukkit.getServer().createInventory(null, 36, ChatColor.BLUE + "Owned Hats: Page " + (i + 1));
			for(int x = 0; x < 27; x++){
				if(hats.size() == totalI) break;
				ItemStack a = hats.get(totalI).Skull();
				ItemMeta am = a.getItemMeta();
				if(pi.hasHat(hats.get(totalI))){
					am.setDisplayName(ChatColor.GOLD.toString() + Crate.RARITY_STRING(hats.get(totalI).Rarity()) + " " + ChatColor.GREEN + hats.get(totalI).Name());
				}else{
					am.setDisplayName(ChatColor.GOLD.toString() + Crate.RARITY_STRING(hats.get(totalI).Rarity()) + " " + ChatColor.RED + hats.get(totalI).Name());
				}
				a.setItemMeta(am);
				if(hats.get(totalI).Rarity() != Crate.LEGENDARY){
					allHats.setItem(x, a);
				}else{
					if(pi.hasHat(hats.get(totalI))){
						allHats.setItem(x, a);
					}
				}
				totalI++;
			}
			ItemStack next = getHatFromID("arrow_right").Skull();
			ItemMeta nextM = next.getItemMeta();
			nextM.setDisplayName(ChatColor.WHITE + "Next Page");
			next.setItemMeta(nextM);
			ItemStack prev =getHatFromID("arrow_left").Skull();
			ItemMeta prevM = prev.getItemMeta();
			prevM.setDisplayName(ChatColor.WHITE + "Previous Page");
			prev.setItemMeta(prevM);
			if(i > 0) allHats.setItem(29, prev);
			if(i < pages - 1) allHats.setItem(33, next);

			ItemStack back = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = back.getItemMeta();
			backMeta.setDisplayName(ChatColor.RED + "Back");
			back.setItemMeta(backMeta);
			allHats.setItem(27, back);

			hatsInventory.add(allHats);
		}
		inInvent.put(p, pa);
		p.openInventory(hatsInventory.get(pa));
	}
	
	public void showAllUnownedHats(Player p, int pa){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		ArrayList<Inventory> hatsInventory = new ArrayList<Inventory>();
		inInvent.remove(p);
		ArrayList<String> hatsS = pi.getUnownedHats();
		ArrayList<Hat> hats = new ArrayList<Hat>();
		for(String s : hatsS){
			Hat h = getHatFromID(s);
			hats.add(h);
		}
		int size = hats.size();
		double pagesD = (double) size / 27;
		int pages = (int) Math.ceil(pagesD);
		int totalI = 0;
		for(int i = 0; i < pages; i++){
			Inventory allHats =  Bukkit.getServer().createInventory(null, 36, ChatColor.BLUE + "Unowned Hats: Page " + (i + 1));
			for(int x = 0; x < 27; x++){
				if(hats.size() == totalI) break;
				ItemStack a = hats.get(totalI).Skull();
				ItemMeta am = a.getItemMeta();
				if(pi.hasHat(hats.get(totalI))){
					am.setDisplayName(ChatColor.GOLD.toString() + Crate.RARITY_STRING(hats.get(totalI).Rarity()) + " " + ChatColor.GREEN + hats.get(totalI).Name());
				}else{
					am.setDisplayName(ChatColor.GOLD.toString() + Crate.RARITY_STRING(hats.get(totalI).Rarity()) + " " + ChatColor.RED + hats.get(totalI).Name());
				}
				a.setItemMeta(am);
				if(hats.get(totalI).Rarity() != Crate.LEGENDARY){
					allHats.setItem(x, a);
				}else{
					if(pi.hasHat(hats.get(totalI))){
						allHats.setItem(x, a);
					}
				}
				totalI++;
			}
			ItemStack next = getHatFromID("arrow_right").Skull();
			ItemMeta nextM = next.getItemMeta();
			nextM.setDisplayName(ChatColor.WHITE + "Next Page");
			next.setItemMeta(nextM);
			ItemStack prev = getHatFromID("arrow_left").Skull();
			ItemMeta prevM = prev.getItemMeta();
			prevM.setDisplayName(ChatColor.WHITE + "Previous Page");
			prev.setItemMeta(prevM);
			if(i > 0) allHats.setItem(29, prev);
			if(i < pages - 1) allHats.setItem(33, next);

			ItemStack back = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = back.getItemMeta();
			backMeta.setDisplayName(ChatColor.RED + "Back");
			back.setItemMeta(backMeta);
			allHats.setItem(27, back);

			hatsInventory.add(allHats);
		}
		inInvent.put(p, pa);
		p.openInventory(hatsInventory.get(pa));
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		PlayerInformation pi = PlayerInformation.connected.get(p);
		if (e.getView().getTitle().contains("Hats")) {
			e.setCancelled(true);
			if(e.getView().getTitle().equals(ChatColor.BLUE + "Hats: " + pi.getOwnedHatSize() + "/" + Hats.size())){
				try{
					if(e.getCurrentItem().getItemMeta().getDisplayName().contains("All")){
						showAllHats(p, 0);
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Owned")){
						showAllOwnedHats(p, 0);
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Unowned")){
						showAllUnownedHats(p, 0);
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Remove")){
						pi.removeHat();
						p.getInventory().setHelmet(null);
						p.closeInventory();
						p.sendMessage(core.format.label() + core.format.colorize("&7You removed your hat!"));
					}
				}catch(Exception ex){
					
				}
			}else{
				try{
					if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Next")) {
						int i = inInvent.get(p);
						i++;
						inInvent.put(p, i);
						if(e.getView().getTitle().contains("Owned")){
							showAllOwnedHats(p, i);
						}else if(e.getView().getTitle().contains("Unowned")){
							showAllUnownedHats(p, i);
						}else if(e.getView().getTitle().contains("Hats: Page")){
							showAllHats(p, i);
						}
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Previous")){
						int i = inInvent.get(p);
						i--;
						inInvent.put(p, i);
						if(e.getView().getTitle().contains("Owned")){
							showAllOwnedHats(p, i);
						}else if(e.getView().getTitle().contains("Unowned")){
							showAllUnownedHats(p, i);
						}else if(e.getView().getTitle().contains("Hats: Page")){
							showAllHats(p, i);
						}
					}else if(!e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Back")){
						String name = e.getCurrentItem().getItemMeta().getDisplayName();
						name = StringUtil.ClearFormatting(name);
						name = name.replace("Common ", "");
						name = name.replace("Uncommon ", "");
						name = name.replace("Rare ", "");
						name = name.replace("Epic ", "");
						name = name.replace("Legendary ", "");
						if(pi.hasHat(getHatFromName(name))){
							pi.setActiveHat(getHatFromName(name));
							p.closeInventory();
							p.sendMessage(core.format.label() + core.format.colorize("&7You activated the &6" + name + "&7 hat!"));
						}else{
							p.closeInventory();
							p.sendMessage(core.format.label() + core.format.colorize("&7You do not have the &6" + name + "&7 hat!"));
						}
					}
				}catch(Exception ex){
					
				}
			}

			if(e.getCurrentItem() != null) {
				if (e.getCurrentItem().hasItemMeta()) {
					if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Back")) {
						chooseHats(p);
					}
				}
			}

		}
	}

}

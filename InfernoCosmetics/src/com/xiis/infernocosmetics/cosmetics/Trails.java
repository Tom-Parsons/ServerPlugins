package com.xiis.infernocosmetics.cosmetics;

import com.xiis.infernocosmetics.cosmetics.crates.Crate;
import com.xiis.infernocosmetics.main.Main;
import com.xiis.infernocosmetics.player.PlayerInformation;
import com.xiis.infernocosmetics.util.StringUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;

public class Trails implements Listener {

	private network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);

	public static ArrayList<Trail> Trails = new ArrayList<Trail>();
	public static ArrayList<Trail> Common = new ArrayList<Trail>();
	public static ArrayList<Trail> Uncommon = new ArrayList<Trail>();
	public static ArrayList<Trail> Rare = new ArrayList<Trail>();
	public static ArrayList<Trail> Epic = new ArrayList<Trail>();
	public static ArrayList<Trail> Legendary = new ArrayList<Trail>();

	public static Trail getTrailFromName(String n){
		for(Trail t : Trails){
			if(t.Name().equals(n)) return t;
		}
		if(n.equals("None")) return null;
		return Trails.get(0);
	}

    public static Trail getTrailFromID(String id){
        for(Trail t : Trails){
            if(t.ID().equals(id)) return t;
        }
        if(id.equals("None")) return null;
        return Trails.get(0);
    }
	
	
	//TODO: Sort inventory for player side only
	public ArrayList<Inventory> trailsInventory = new ArrayList<Inventory>();
	public HashMap<Player, Integer> inInvent = new HashMap<Player, Integer>();
	
	public static void chooseTrails(Player p){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		Inventory choosing =  Bukkit.getServer().createInventory(null, 9, ChatColor.BLUE + ChatColor.BOLD.toString() + "Trails: " + pi.getOwnedTrailSize() + "/" + Trails.size());
		ItemStack rem = new ItemStack(Material.BARRIER);
		ItemMeta remM = rem.getItemMeta();
		remM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Remove Trail");
		rem.setItemMeta(remM);
		choosing.setItem(0, rem);
		ItemStack all = getTrailFromID("devils_fire").Item();
		ItemMeta allM = all.getItemMeta();
		allM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "All Trails");
		all.setItemMeta(allM);
		choosing.setItem(2, all);
		ItemStack owned = getTrailFromID("emerald_magic").Item();
		ItemMeta ownedM = owned.getItemMeta();
		ownedM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Owned Trails");
		owned.setItemMeta(ownedM);
		choosing.setItem(4, owned);
		ItemStack unowned = getTrailFromID("witch_magic").Item();
		ItemMeta unownedM = unowned.getItemMeta();
		unownedM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Unowned Trails");
		unowned.setItemMeta(unownedM);
		choosing.setItem(6, unowned);
		p.openInventory(choosing);
	}
	
	public void showAllTrails(Player p, int pa){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		trailsInventory.clear();
		inInvent.remove(p);
		int size = Trails.size();
		double pagesD = (double) size / 27;
		int pages = (int) Math.ceil(pagesD);
		int totalI = 0;
		for(int i = 0; i < pages; i++){
			Inventory allTrails =  Bukkit.getServer().createInventory(null, 36, ChatColor.BLUE + ChatColor.BOLD.toString() + "Trails: Page " + (i + 1));
			for(int x = 0; x < 27; x++){
				if(Trails.size() == totalI) break;
				ItemStack a = Trails.get(totalI).Item();
				ItemMeta am = a.getItemMeta();
				if(pi.hasTrail(Trails.get(totalI))){
					am.setDisplayName(Crate.RARITY_STRING(Trails.get(totalI).Rarity()) + " " + ChatColor.GREEN + Trails.get(totalI).Name());
				}else{
					am.setDisplayName(Crate.RARITY_STRING(Trails.get(totalI).Rarity()) + " " + ChatColor.RED + Trails.get(totalI).Name());
				}
				a.setItemMeta(am);
				if(Trails.get(totalI).Rarity() != Crate.LEGENDARY){
					allTrails.setItem(x, a);
				}else{
					if(pi.hasTrail(Trails.get(totalI))){
						allTrails.setItem(x, a);
					}
				}
				totalI++;
			}
			ItemStack next = Hats.getHatFromID("arrow_right").Skull();
			ItemMeta nextM = next.getItemMeta();
			nextM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Next Page");
			next.setItemMeta(nextM);
			ItemStack prev = Hats.getHatFromID("arrow_left").Skull();
			ItemMeta prevM = prev.getItemMeta();
			prevM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Previous Page");
			prev.setItemMeta(prevM);
			if(i > 0) allTrails.setItem(29, prev);
			if(i < pages - 1) allTrails.setItem(33, next);

			ItemStack back = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = back.getItemMeta();
			backMeta.setDisplayName(ChatColor.RED + "Back");
			back.setItemMeta(backMeta);
			allTrails.setItem(27, back);

			trailsInventory.add(allTrails);
		}
		inInvent.put(p, pa);
		p.openInventory(trailsInventory.get(pa));
	}
	
	public void showAllOwnedTrails(Player p, int pa){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		trailsInventory.clear();
		inInvent.remove(p);
		ArrayList<String> trailsS = pi.getTrails();
		ArrayList<Trail> trails = new ArrayList<Trail>();
		for(String s : trailsS){
			if(!s.equals("None")) {
				Trail t = getTrailFromID(s);
				trails.add(t);
			}
		}
		int size = trails.size();
		double pagesD = (double) size / 27;
		int pages = (int) Math.ceil(pagesD);
		int totalI = 0;
		for(int i = 0; i < pages; i++){
			Inventory allTrails =  Bukkit.getServer().createInventory(null, 36, ChatColor.BLUE + ChatColor.BOLD.toString() + "Owned Trails: Page " + (i + 1));
			for(int x = 0; x < 27; x++){
				if(trails.size() == totalI) break;
				ItemStack a = trails.get(totalI).Item();
				ItemMeta am = a.getItemMeta();
				if(pi.hasTrail(trails.get(totalI))){
					am.setDisplayName(Crate.RARITY_STRING(trails.get(totalI).Rarity()) + " " + ChatColor.GREEN + trails.get(totalI).Name());
				}else{
					am.setDisplayName(Crate.RARITY_STRING(trails.get(totalI).Rarity()) + " " + ChatColor.RED + trails.get(totalI).Name());
				}
				a.setItemMeta(am);
				if(trails.get(totalI).Rarity() != Crate.LEGENDARY){
					allTrails.setItem(x, a);
				}else{
					if(pi.hasTrail(trails.get(totalI))){
						allTrails.setItem(x, a);
					}
				}
				totalI++;
			}
			ItemStack next = Hats.getHatFromID("arrow_right").Skull();
			ItemMeta nextM = next.getItemMeta();
			nextM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Next Page");
			next.setItemMeta(nextM);
			ItemStack prev = Hats.getHatFromID("arrow_left").Skull();
			ItemMeta prevM = prev.getItemMeta();
			prevM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Previous Page");
			prev.setItemMeta(prevM);
			if(i > 0) allTrails.setItem(29, prev);
			if(i < pages - 1) allTrails.setItem(33, next);

			ItemStack back = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = back.getItemMeta();
			backMeta.setDisplayName(ChatColor.RED + "Back");
			back.setItemMeta(backMeta);
			allTrails.setItem(27, back);

			trailsInventory.add(allTrails);
		}
		inInvent.put(p, pa);
		p.openInventory(trailsInventory.get(pa));
	}
	
	public void showAllUnownedTrails(Player p, int pa){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		trailsInventory.clear();
		inInvent.remove(p);
		ArrayList<String> trailsS = pi.getUnownedTrails();
		ArrayList<Trail> trails = new ArrayList<Trail>();
		for(String s : trailsS){
			Trail t = getTrailFromID(s);
			trails.add(t);
		}
		int size = trails.size();
		double pagesD = (double) size / 27;
		int pages = (int) Math.ceil(pagesD);
		int totalI = 0;
		for(int i = 0; i < pages; i++){
			Inventory allTrails =  Bukkit.getServer().createInventory(null, 36, ChatColor.BLUE + ChatColor.BOLD.toString() + "Unowned Trails: Page " + (i + 1));
			for(int x = 0; x < 27; x++){
				if(trails.size() == totalI) break;
				ItemStack a = trails.get(totalI).Item();
				ItemMeta am = a.getItemMeta();
				if(pi.hasTrail(trails.get(totalI))){
					am.setDisplayName(Crate.RARITY_STRING(trails.get(totalI).Rarity()) + " " + ChatColor.GREEN + trails.get(totalI).Name());
				}else{
					am.setDisplayName(Crate.RARITY_STRING(trails.get(totalI).Rarity()) + " " + ChatColor.RED + trails.get(totalI).Name());
				}
				a.setItemMeta(am);
				if(trails.get(totalI).Rarity() != Crate.LEGENDARY){
					allTrails.setItem(x, a);
				}else{
					if(pi.hasTrail(trails.get(totalI))){
						allTrails.setItem(x, a);
					}
				}
				totalI++;
			}
			ItemStack next = Hats.getHatFromID("arrow_right").Skull();
			ItemMeta nextM = next.getItemMeta();
			nextM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Next Page");
			next.setItemMeta(nextM);
			ItemStack prev = Hats.getHatFromID("arrow_left").Skull();
			ItemMeta prevM = prev.getItemMeta();
			prevM.setDisplayName(ChatColor.WHITE + ChatColor.BOLD.toString() + "Previous Page");
			prev.setItemMeta(prevM);
			if(i > 0) allTrails.setItem(29, prev);
			if(i < pages - 1) allTrails.setItem(33, next);

			ItemStack back = new ItemStack(Material.BARRIER);
			ItemMeta backMeta = back.getItemMeta();
			backMeta.setDisplayName(ChatColor.RED + "Back");
			back.setItemMeta(backMeta);
			allTrails.setItem(27, back);

			trailsInventory.add(allTrails);
		}
		inInvent.put(p, pa);
		p.openInventory(trailsInventory.get(pa));
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		PlayerInformation pi = PlayerInformation.connected.get(p);
		if (e.getView().getTitle().contains("Trails")) {
			e.setCancelled(true);
			if(e.getView().getTitle().equals(ChatColor.BLUE + ChatColor.BOLD.toString() + "Trails: " + pi.getOwnedTrailSize() + "/" + Trails.size())){
				try{
					if(e.getCurrentItem().getItemMeta().getDisplayName().contains("All")){
						showAllTrails(p, 0);
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Owned")){
						showAllOwnedTrails(p, 0);
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Unowned")){
						showAllUnownedTrails(p, 0);
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Remove")){
						pi.setActiveTrail("None");
						p.closeInventory();
					}
				}catch(Exception ex){
					
				}
			}else{
				try{
					if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Next")) {
						int i = inInvent.get(p);
						i++;
						inInvent.put(p, i);
						p.openInventory(trailsInventory.get(i));
					}else if(e.getCurrentItem().getItemMeta().getDisplayName().contains("Previous")){
						int i = inInvent.get(p);
						i--;
						inInvent.put(p, i);
						p.openInventory(trailsInventory.get(i));
					}else{
						String name = e.getCurrentItem().getItemMeta().getDisplayName();
						name = StringUtil.ClearFormatting(name);
						name = name.replace("Common ", "");
						name = name.replace("Uncommon ", "");
						name = name.replace("Rare ", "");
						name = name.replace("Epic ", "");
						name = name.replace("Legendary ", "");
						if(pi.hasTrail(getTrailFromName(name))){
							pi.setActiveTrail(getTrailFromName(name).ID());
							p.closeInventory();
							p.sendMessage(core.format.label() + core.format.colorize("&7You activated the &6" + name + "&7 trail!"));
						}else{
							p.closeInventory();
							p.sendMessage(core.format.label() + core.format.colorize("&7You do not have the &6" + name + "&7 trail!"));
						}
					}
				}catch(Exception ex){
					
				}
			}

			if(e.getCurrentItem() != null) {
				if (e.getCurrentItem().hasItemMeta()) {
					if (e.getCurrentItem().getItemMeta().getDisplayName().equals(ChatColor.RED + "Back")) {
						chooseTrails(p);
					}
				}
			}

		}
	}

	public static void play() {
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
				new Runnable() {
					@Override
					public void run() {
						for(Player p : PlayerInformation.connected.keySet()) {
							PlayerInformation pi = PlayerInformation.connected.get(p);
							if(!pi.getActiveTrail().equals("None")) {
								createParticle(pi);
							}
						}
						play();
					}
				}, 2);
	}

	public static ArrayList<Location> getCircle(Location center, double radius, int amount)
    {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / amount;
        ArrayList<Location> locations = new ArrayList<Location>();
        for(int i = 0;i < amount; i++)
        {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            locations.add(new Location(world, x, center.getY(), z));
        }
        return locations;
    }
	
	public HashMap<Player, Double> skp = new HashMap<Player, Double>();

	public static void createParticle(PlayerInformation p) {
		for(Player pl : Bukkit.getOnlinePlayers()) {
			if(pl.canSee(p.getPlayer()) || pl == p.getPlayer()) {
				for (Location l : getCircle(new Location(p.getPlayer().getWorld(), (float) (p.getPlayer().getLocation().getX()), (float) (p.getPlayer().getLocation().getY()) + 0.75, (float) (p.getPlayer().getLocation().getZ())), 0.5, 15)) {
					pl.spawnParticle(getTrailFromID(p.getActiveTrail()).Particle(), l, 1, 0.001, 0.001, 0.001, 0.000001);
				}
			}
		}
	}

}

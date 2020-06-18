package com.xiis.infernocosmetics.cosmetics.crates;

import com.xiis.infernocosmetics.cosmetics.*;
import com.xiis.infernocosmetics.main.Main;
import com.xiis.infernocosmetics.player.PlayerInformation;
import com.xiis.infernocosmetics.util.StringUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.io.File;
import java.lang.reflect.Array;
import java.util.*;

public class Crate implements Listener {

	private network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);

    public static final int COMMON = 0;
    public static final int UNCOMMON = 1;
    public static final int RARE = 2;
    public static final int EPIC = 3;
    public static final int LEGENDARY = 4;
    public static final int ULTIMATE = 5;

    public static String RARITY_STRING(int rarity){
    	switch(rarity){
			case COMMON:
				return ChatColor.GRAY + "Common";
			case UNCOMMON:
				return ChatColor.YELLOW + "Uncommon";
			case RARE:
				return ChatColor.BLUE + "Rare";
			case EPIC:
				return ChatColor.DARK_PURPLE + "Epic";
			case LEGENDARY:
				return ChatColor.GOLD + "Legendary";
			case ULTIMATE:
				return ChatColor.LIGHT_PURPLE + "Ultimate";
		}
		return ChatColor.GRAY + "Common";
	}

	public static int KEY_COST(int rarity){
    	switch(rarity){
			case COMMON:
				return 10;
			case UNCOMMON:
				return 25;
			case RARE:
				return 60;
			case EPIC:
				return 150;
			case LEGENDARY:
				return 400;
		}
		return 400;
	}

	public static ArrayList<Crate> crates = new ArrayList<Crate>();

    public static void LoadCrates(){
    	File Folder = new File("plugins/InfernoNetwork/Cosmetics");
    	if(!Folder.exists()){
    		Folder.mkdir();
		}
		File Crates = new File("plugins/InfernoNetwork/Cosmetics/Crates.yml");
		if (!Crates.exists()) {
			try {
				Crates.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			YamlConfiguration yml = YamlConfiguration.loadConfiguration(Crates);
			if(yml.get("Crates") != null) {
				for (Location l : ((ArrayList<Location>) yml.get("Crates"))) {
					new Crate(l);
				}
			}
		}
	}
	public static void AddCrate(Location l){
		File Crates = new File("plugins/InfernoNetwork/Cosmetics/Crates.yml");
		YamlConfiguration yml = YamlConfiguration.loadConfiguration(Crates);
		ArrayList<Location> crates = new ArrayList<Location>();
		if(yml.get("Crates") != null){
			crates = (ArrayList<Location>) yml.get("Crates");
		}
		crates.add(l);
		yml.set("Crates", crates);
		try {
			yml.save(Crates);
		}catch(Exception ex){}
		new Crate(l);
	}

	private Location Location;
	
	ArmorStand display = null;
	
	public Crate(Location Location){
		Bukkit.getServer().getPluginManager().registerEvents(this, Main.pl);
		this.Location = Location;
		World w = Location.getWorld();
		w.getBlockAt(Location).setType(Material.END_PORTAL_FRAME);
		display = (ArmorStand) w.spawnEntity(new Location(w, Location.getX() + 0.5, Location.getY(), Location.getZ() + 0.5), EntityType.ARMOR_STAND);
		display.setCustomName(prefix.replaceAll(" ", ""));
		display.setCustomNameVisible(true);
		display.setVisible(false);
		display.setSmall(true);
		Particle();
		crates.add(this);
	}
	
	public Location Location(){
		return this.Location;
	}
	
	boolean opening = false;
	
	@EventHandler
	public void onRightClick(PlayerInteractEvent e){
		if(e.getAction().equals(Action.RIGHT_CLICK_BLOCK) && e.getHand() == EquipmentSlot.HAND) {
			Player p = e.getPlayer();
			Block b = e.getClickedBlock();
			Location l = b.getLocation();
			if(l.getWorld() == Location.getWorld() && l.getBlockX() == Location.getBlockX() && l.getBlockY() == Location.getBlockY() && l.getBlockZ() == Location.getBlockZ()){
				if(opening == false){
					inCrate.put(p, this);
					chooseKey(p);
				}
			}
		}
	}
	
	public void Cooldown(){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
				new Runnable() {
					@Override
					public void run() {
						Location.getWorld().playSound(Location, Sound.ENTITY_ENDERMAN_TELEPORT, 1, 1);
						hat.setVelocity(new Vector(0.0, 2, 0.0));
						//if(morphE != null) morphE.setVelocity(new Vector(0.0, 2, 0.0));
						found.setVelocity(new Vector(0.0, 2, 0.0));
						doParticle = false;
						RemoveStands();
					}
				}, 20 * 5);
	}
	
	public void RemoveStands(){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
				new Runnable() {
					@Override
					public void run() {
						display.setCustomName(prefix.replaceAll(" ", ""));
						display.setCustomNameVisible(true);
						hat.remove();
						hat = null;
						found.remove();
						found = null;
						opening = false;
						rarity = -1;
						trail = null;
						//morph = null;
						/*if(morphE != null){
							morphE.remove();
						}
						morphE = null;*/
					}
				}, 20 * 1);
	}
	
	boolean doParticle = false;
	
	public void standParticle(){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
				new Runnable() {
					@Override
					public void run() {
						if(doParticle == true){
							hat.teleport(new Location(hat.getWorld(), hat.getLocation().getX(), hat.getLocation().getY(), hat.getLocation().getZ(), hat.getLocation().getYaw() + 10, hat.getLocation().getPitch()));
							if(trail == null){
								Location.getWorld().spawnParticle(Particle.SPELL_WITCH, hat.getLocation().getX(), hat.getLocation().getY(), hat.getLocation().getZ(), 3, 0, 0, 0, 1);
								if(rarity == LEGENDARY || rarity == ULTIMATE) Location.getWorld().spawnParticle(Particle.FLAME, hat.getLocation().getX(), hat.getLocation().getY(), hat.getLocation().getZ(), 3, 0, 0, 0, 0.01);
								if(rarity == ULTIMATE) Location.getWorld().spawnParticle(Particle.DRAGON_BREATH, hat.getLocation().getX(), hat.getLocation().getY(), hat.getLocation().getZ(), 3, 0, 0, 0, 0.01);
								if(rarity == ULTIMATE) Location.getWorld().spawnParticle(Particle.VILLAGER_HAPPY, hat.getLocation().getX(), hat.getLocation().getY(), hat.getLocation().getZ(), 3, 0, 0, 0, 0.01);
							}else{
								Location.getWorld().spawnParticle(trail.Particle(), hat.getLocation().getX(), hat.getLocation().getY() + 0.25, hat.getLocation().getZ(), 3, 0, 0, 0, 1);
							}
							standParticle();
						}
					}
				}, 1);
	}
	
	int rarity = -1;
	ArmorStand hat = null;
	//LivingEntity morphE = null;
	ArmorStand found = null;
	
	String prefix = ChatColor.YELLOW + ChatColor.BOLD.toString() + ChatColor.MAGIC + "::" + ChatColor.YELLOW + ChatColor.BOLD.toString() + "Crate" + ChatColor.MAGIC + "::" + ChatColor.WHITE + " ";
	
	public void openCrate(Player p, int keyUsed){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		if(opening == false){
			int keyAmount = 0;
			if(keyUsed == COMMON) keyAmount = pi.getKeys(COMMON);
			if(keyUsed == UNCOMMON) keyAmount = pi.getKeys(UNCOMMON);
			if(keyUsed == RARE) keyAmount = pi.getKeys(RARE);
			if(keyUsed == EPIC) keyAmount = pi.getKeys(EPIC);
			if(keyAmount >= 1){
				Location.getWorld().playSound(Location, Sound.ENTITY_ENDER_DRAGON_AMBIENT, 1, 1);
				opening = true;
				p.sendMessage(prefix + "You are now opening a crate!");
				openParticle(p, keyUsed);
				display.setCustomName(ChatColor.YELLOW + ChatColor.BOLD.toString() + "Opening...");
			}else{
				p.sendMessage(prefix + "You don't have any keys to open this!");
			}
		}else{
			p.sendMessage(prefix + "This crate is already in use!");
		}
	}
	
	int wait = 0;
	
	public void openParticle(Player p, int keyUsed){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
				new Runnable() {
					@Override
					public void run() {
						wait++;
						if(wait == 20 * 2.7){
							open(p, keyUsed);
							wait = 0;
							doParticle = true;
							standParticle();
						}else{
							if(wait <= 10){
								Location loc = new Location(Location.getWorld(), Location.getX() + 0.5, Location.getY(), Location.getZ() + 0.5);
								double radius = 0.5;
								double x = radius * Math.cos(loc.getY() * wait);
								double z = radius * Math.sin(loc.getY() * wait);
								Location.getWorld().spawnParticle(Particle.PORTAL, (float) (loc.getX() + x), (float) ((loc.getY() + wait / 3)), (float) (loc.getZ() + z), 50, 0.1, 0.1, 0.1, 1);
							}
							openParticle(p, keyUsed);
						}
					}
				}, 1);
	}
	
	Trail trail = null;
	//Morph morph = null;
	
	public void open(Player p, int keyUsed){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		display.setCustomNameVisible(false);
		Random rnd = new Random();
		int r = rnd.nextInt(100 - 0 + 1) + 0;
		Trail t = null;
		Hat h = null;
		//Morph m = null;
		String rank = null;
		if(keyUsed == COMMON){
			if(r <= 70){
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(type <= 75){
					int i = rnd.nextInt((Hats.Common.size() - 1) - 0 + 1) + 0;
					h =Hats.Common.get(i);
				}else if(type > 75 && type <= 100){
					int i = rnd.nextInt((Trails.Common.size() - 1) - 0 + 1) + 0;
					t =Trails.Common.get(i);
				}else if(type > 90){
					//int i = rnd.nextInt((Morphs.Common.size() - 1) - 0 + 1) + 0;
					//m =Morphs.Common.get(i);
				}
				Location.getWorld().playSound(Location, Sound.BLOCK_GRAVEL_STEP, 1, 1);
			}else if(r <= 90){
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(type <= 75){
					int i = rnd.nextInt((Hats.Uncommon.size() - 1) - 0 + 1) + 0;
					h =Hats.Uncommon.get(i);
				}else if(type > 75 && type <= 100){
					int i = rnd.nextInt((Trails.Uncommon.size() - 1) - 0 + 1) + 0;
					t =Trails.Uncommon.get(i);
				}else if(type > 90){
					//int i = rnd.nextInt((Morphs.Common.size() - 1) - 0 + 1) + 0;
					//m =Morphs.Common.get(i);
				}
				Location.getWorld().playSound(Location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			}else if(r <= 98){
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(type <= 75){
					int i = rnd.nextInt((Hats.Rare.size() - 1) - 0 + 1) + 0;
					h =Hats.Rare.get(i);
				}else if(type > 75 && type <= 100){
					int i = rnd.nextInt((Trails.Rare.size() - 1) - 0 + 1) + 0;
					t =Trails.Rare.get(i);
				}else if(type > 90){
					//int i = rnd.nextInt((Morphs.Common.size() - 1) - 0 + 1) + 0;
					//m =Morphs.Common.get(i);
				}
				Location.getWorld().playSound(Location, Sound.BLOCK_FIRE_AMBIENT, 1, 1);
			}else{
				r = rnd.nextInt(100 - 0 + 1) + 0;
				if(r <= 75){
					int type = rnd.nextInt(100 - 0 + 1) + 1;
					if(type <= 75){
						int i = rnd.nextInt((Hats.Epic.size() - 1) - 0 + 1) + 0;
						h =Hats.Epic.get(i);
					}else if(type > 75 && type <= 100){
						int i = rnd.nextInt((Trails.Epic.size() - 1) - 0 + 1) + 0;
						t =Trails.Epic.get(i);
					}else if(type > 90){
						//int i = rnd.nextInt((Morphs.Common.size() - 1) - 0 + 1) + 0;
						//m =Morphs.Common.get(i);
					}
					Location.getWorld().playSound(Location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
				}else{
					int type = rnd.nextInt(100 - 0 + 1) + 1;
					if(type <= 75){
						int i = rnd.nextInt((Hats.Legendary.size() - 1) - 0 + 1) + 0;
						h =Hats.Legendary.get(i);
					}else if(type > 75 && type <= 100){
						int i = rnd.nextInt((Trails.Legendary.size() - 1) - 0 + 1) + 0;
						t =Trails.Legendary.get(i);
					}else if(type > 90){
						//int i = rnd.nextInt((Morphs.Common.size() - 1) - 0 + 1) + 0;
						//m =Morphs.Common.get(i);
					}
					Location.getWorld().playSound(Location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 2);
				}
			}
		}else if(keyUsed == UNCOMMON){
			if(r <= 70){
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(type <= 75){
					int i = rnd.nextInt((Hats.Uncommon.size() - 1) - 0 + 1) + 0;
					h =Hats.Uncommon.get(i);
				}else if(type > 75 && type <= 100){
					int i = rnd.nextInt((Trails.Uncommon.size() - 1) - 0 + 1) + 0;
					t =Trails.Uncommon.get(i);
				}else if(type > 90){
					//int i = rnd.nextInt((Morphs.Uncommon.size() - 1) - 0 + 1) + 0;
					//m =Morphs.Uncommon.get(i);
				}
				Location.getWorld().playSound(Location, Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1, 1);
			}else if(r <= 90){
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(type <= 75){
					int i = rnd.nextInt((Hats.Rare.size() - 1) - 0 + 1) + 0;
					h =Hats.Rare.get(i);
				}else if(type > 75 && type <= 100){
					int i = rnd.nextInt((Trails.Rare.size() - 1) - 0 + 1) + 0;
					t =Trails.Rare.get(i);
				}else if(type > 90){
					//int i = rnd.nextInt((Morphs.Rare.size() - 1) - 0 + 1) + 0;
					//m =Morphs.Rare.get(i);
				}
				Location.getWorld().playSound(Location, Sound.BLOCK_FIRE_AMBIENT, 1, 1);
			}else{
				r = rnd.nextInt(100 - 0 + 1) + 0;
				if(r <= 75){
					int type = rnd.nextInt(100 - 0 + 1) + 1;
					if(type <= 75){
						int i = rnd.nextInt((Hats.Epic.size() - 1) - 0 + 1) + 0;
						h =Hats.Epic.get(i);
					}else if(type > 7 && type <= 100){
						int i = rnd.nextInt((Trails.Epic.size() - 1) - 0 + 1) + 0;
						t =Trails.Epic.get(i);
					}else if(type > 90){
						//int i = rnd.nextInt((Morphs.Epic.size() - 1) - 0 + 1) + 0;
						//m =Morphs.Epic.get(i);
					}
					Location.getWorld().playSound(Location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
				}else{
					int type = rnd.nextInt(100 - 0 + 1) + 1;
					if(type <= 75){
						int i = rnd.nextInt((Hats.Legendary.size() - 1) - 0 + 1) + 0;
						h =Hats.Legendary.get(i);
					}else if(type > 75 && type <= 100){
						int i = rnd.nextInt((Trails.Legendary.size() - 1) - 0 + 1) + 0;
						t =Trails.Legendary.get(i);
					}else if(type > 90){
						//int i = rnd.nextInt((Morphs.Legendary.size() - 1) - 0 + 1) + 0;
						//m =Morphs.Legendary.get(i);
					}
					Location.getWorld().playSound(Location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 2);
				}
			}
		}else if(keyUsed == RARE){
			if(r <= 70){
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(type <= 75){
					int i = rnd.nextInt((Hats.Rare.size() - 1) - 0 + 1) + 0;
					h =Hats.Rare.get(i);
				}else if(type > 75 && type <= 100){
					int i = rnd.nextInt((Trails.Rare.size() - 1) - 0 + 1) + 0;
					t =Trails.Rare.get(i);
				}else if(type > 90){
					//int i = rnd.nextInt((Morphs.Rare.size() - 1) - 0 + 1) + 0;
					//m =Morphs.Rare.get(i);
				}
				Location.getWorld().playSound(Location, Sound.BLOCK_FIRE_AMBIENT, 1, 1);
			}else{
				r = rnd.nextInt(100 - 0 + 1) + 0;
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(r <= 75){
					if(type <= 75){
						int i = rnd.nextInt((Hats.Epic.size() - 1) - 0 + 1) + 0;
						h =Hats.Epic.get(i);
					}else if(type > 75 && type <= 100){
						int i = rnd.nextInt((Trails.Epic.size() - 1) - 0 + 1) + 0;
						t =Trails.Epic.get(i);
					}else if(type > 90){
						//int i = rnd.nextInt((Morphs.Epic.size() - 1) - 0 + 1) + 0;
						//m =Morphs.Epic.get(i);
					}
					Location.getWorld().playSound(Location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
				}else{
					if(type <= 75){
						int i = rnd.nextInt((Hats.Legendary.size() - 1) - 0 + 1) + 0;
						h =Hats.Legendary.get(i);
					}else if(type > 75 && type <= 100){
						int i = rnd.nextInt((Trails.Legendary.size() - 1) - 0 + 1) + 0;
						t =Trails.Legendary.get(i);
					}else if(type > 90){
						//int i = rnd.nextInt((Morphs.Legendary.size() - 1) - 0 + 1) + 0;
						//m =Morphs.Legendary.get(i);
					}
					Location.getWorld().playSound(Location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 2);
				}
			}
		}else{
			r = rnd.nextInt(10000 - 0 + 1) + 0;
			if(r <= 7500){
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(type <= 75){
					int i = rnd.nextInt((Hats.Epic.size() - 1) - 0 + 1) + 0;
					h =Hats.Epic.get(i);
				}else if(type > 75 && type <= 100){
					int i = rnd.nextInt((Trails.Epic.size() - 1) - 0 + 1) + 0;
					t =Trails.Epic.get(i);
				}else if(type > 90){
					//int i = rnd.nextInt((Morphs.Epic.size() - 1) - 0 + 1) + 0;
					//m =Morphs.Epic.get(i);
				}
				Location.getWorld().playSound(Location, Sound.ENTITY_PLAYER_LEVELUP, 1, 1);
			}else if(r > 8500 && r <= 9999){
				int type = rnd.nextInt(100 - 0 + 1) + 1;
				if(type <= 75){
					int i = rnd.nextInt((Hats.Legendary.size() - 1) - 0 + 1) + 0;
					h =Hats.Legendary.get(i);
				}else if(type > 75 && type <= 100){
					int i = rnd.nextInt((Trails.Legendary.size() - 1) - 0 + 1) + 0;
					t =Trails.Legendary.get(i);
				}else if(type > 90){
					//int i = rnd.nextInt((Morphs.Legendary.size() - 1) - 0 + 1) + 0;
					//m =Morphs.Legendary.get(i);
				}
				Location.getWorld().playSound(Location, Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 2);
			}else{

				//WIN A RANK?
				rank = "Inferno";
				/*if(pi.getRank() == Rank.MEMBER){
					rank = "Hero";
				}else if(pi.getRank() == Rank.HERO){
					rank = "God";
				}else{
					rank = "Rubies";
				}*/
			}
		}
		if(h != null){
			if(pi.hasHat(h)){
				int rarity = h.Rarity();
				int money = 0;
				if(rarity <= LEGENDARY){
					int max = (int)(KEY_COST(rarity) * 0.75f);
					int min = (int)(KEY_COST(rarity) * 0.5f);
					money = rnd.nextInt(max - min + 1) + min;
				}else{
					money = rnd.nextInt(1000 - 800 + 1) + 800;
				}
				p.sendMessage(prefix + "You already had the " + h.Name() + " hat, so were rewarded " + ChatColor.AQUA + money + ChatColor.WHITE + " cosmetic tokens!");
				core.cosmetictokens.addCosmeticTokens(p, money);
			}else{
				try {
					pi.addNewHat(h);
					int rarity = h.Rarity();
					if(rarity == COMMON){
						core.ah.Award(p, "Cosmetics_Hats_FindCommon");
					}else if(rarity == UNCOMMON){
						core.ah.Award(p, "Cosmetics_Hats_FindUncommon");
					}else if(rarity == RARE){
						core.ah.Award(p, "Cosmetics_Hats_FindRare");
					}else if(rarity == EPIC){
						core.ah.Award(p, "Cosmetics_Hats_FindEpic");
					}else{
						core.ah.Award(p, "Cosmetics_Hats_FindLegendary");
					}
				}catch(Exception ex){}
			}
		}
		if(t != null){
			if(pi.hasTrail(t)){
				int rarity = t.Rarity();
				int money = 0;
				if(rarity <= LEGENDARY){
					int max = (int)(KEY_COST(rarity) * 0.75f);
					int min = (int)(KEY_COST(rarity) * 0.5f);
					money = rnd.nextInt(max - min + 1) + min;
				}else{
					money = rnd.nextInt(1000 - 800 + 1) + 800;
				}
				p.sendMessage(prefix + "You already had the " + t.Name() + " trail, so were rewarded " + ChatColor.AQUA + money + ChatColor.WHITE + " cosmetic tokens!");
				core.cosmetictokens.addCosmeticTokens(p, money);
			}else{
				try {
					pi.addNewTrail(t.ID());
					int rarity = t.Rarity();
					if(rarity == COMMON){
						core.ah.Award(p, "Cosmetics_Trails_FindCommon");
					}else if(rarity == UNCOMMON){
						core.ah.Award(p, "Cosmetics_Trails_FindUncommon");
					}else if(rarity == RARE){
						core.ah.Award(p, "Cosmetics_Trails_FindRare");
					}else if(rarity == EPIC){
						core.ah.Award(p, "Cosmetics_Trails_FindEpic");
					}else{
						core.ah.Award(p, "Cosmetics_Trails_FindLegendary");
					}
				}catch(Exception ex){}
			}
		}
		/*
		if(m != null){
			if(pi.hasMorph(m)){
				Rarity rare = m.Rarity();
				int money = 0;
				if(rare == Rarity.Common){
					money = rnd.nextInt(10 - 5 + 1) + 5;
				}else if(rare == Rarity.Uncommon){
					money = rnd.nextInt(30 - 20 + 1) + 20;
				}else if(rare == Rarity.Rare){
					money = rnd.nextInt(50 - 75 + 1) + 50;
				}else if(rare == Rarity.Epic){
					money = rnd.nextInt(175 - 125 + 1) + 125;
				}else{
					money = rnd.nextInt(400 - 300 + 1) + 300;
				}
				p.sendMessage(prefix + "You already had the " + m.Name() + " morph, so were rewarded " + ChatColor.RED + money + ChatColor.WHITE + " rubies!");
				pi.addRubies(money);
			}else{
				pi.addMorph(m);
			}
		}*/
		World w = Location.getWorld();
		ArmorStand a = (ArmorStand) w.spawnEntity(new Location(Location.getWorld(), Location.getX() + 0.5, Location.getY(), Location.getZ() + 0.5), EntityType.ARMOR_STAND);
		ArmorStand a2 = (ArmorStand) w.spawnEntity(new Location(Location.getWorld(), Location.getX() + 0.5, Location.getY() + 0.25, Location.getZ() + 0.5), EntityType.ARMOR_STAND);
		/*LivingEntity e = null;
		if(m != null){
			e = (LivingEntity) w.spawnEntity(new Location(Location.getWorld(), Location.getX() + 0.5, Location.getY() + 0.5, Location.getZ() + 0.5), m.Type());
			e.setAI(false);
			e.setInvulnerable(true);
			morphE = e;
		}(*/
		hat = a;
		found = a2;
		String name = "";
		if(h != null){
			name = RARITY_STRING(h.Rarity()) + ChatColor.WHITE + " Hat " + ChatColor.GOLD + h.Name();
		}else if(t != null){
			name = RARITY_STRING(t.Rarity()) + ChatColor.WHITE + " Trail " + ChatColor.GOLD + t.Name();
		}
		/*
		if(m != null){
			rarity = m.Rarity();
			if(m.Rarity() == Rarity.Common){
				name = ChatColor.WHITE + ChatColor.BOLD.toString() + m.Rarity() + " Morph " + ChatColor.WHITE + ChatColor.BOLD.toString() + m.Name();
			}else if(m.Rarity() == Rarity.Uncommon){
				name = ChatColor.BLUE + ChatColor.BOLD.toString() + m.Rarity() + " Morph " + ChatColor.WHITE + ChatColor.BOLD.toString() + m.Name();
			}else if(m.Rarity() == Rarity.Rare){
				name = ChatColor.DARK_BLUE + ChatColor.BOLD.toString() + m.Rarity() + " Morph " + ChatColor.WHITE + ChatColor.BOLD.toString() + m.Name();
			}else if(m.Rarity() == Rarity.Epic){
				name = ChatColor.DARK_PURPLE + ChatColor.BOLD.toString() + m.Rarity() + " Morph " + ChatColor.WHITE + ChatColor.BOLD.toString() + m.Name();
			}else{
				name = ChatColor.GOLD + ChatColor.BOLD.toString() + m.Rarity() + " Morph " + ChatColor.WHITE + ChatColor.BOLD.toString() + m.Name();
			}
		}
		if(rank != null){
			rarity = Rarity.Ultimate;
			if(rank.equals("Hero")){
				name = ChatColor.AQUA + ChatColor.BOLD.toString() + rarity + " Rank " + ChatColor.BLUE + ChatColor.BOLD.toString() + "HERO";
				File f = new File("plugins/XMinigames/PlayerData/" + p.getUniqueId() + ".yml");
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
				yml.set("Rank", "Hero");
				try {
					yml.save(f);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				PlayerVisibility.update();
			}else if(rank.equals("God")){
				name = ChatColor.AQUA + ChatColor.BOLD.toString() + rarity + " Rank " + ChatColor.DARK_GREEN + ChatColor.BOLD.toString() + "GOD";
				File f = new File("plugins/XMinigames/PlayerData/" + p.getUniqueId() + ".yml");
				YamlConfiguration yml = YamlConfiguration.loadConfiguration(f);
				yml.set("Rank", "God");
				try {
					yml.save(f);
				} catch (Exception e1) {
					e1.printStackTrace();
				}
				PlayerVisibility.update();
			}else if(rank.equals("Rubies")){
				int rubies = rnd.nextInt(1000 - 500 + 1) + 500;
				name = ChatColor.AQUA + ChatColor.BOLD.toString() + rarity + " Ruby Bounty " + ChatColor.RED + ChatColor.BOLD.toString() + rubies + " Rubies";
				p.sendMessage(prefix + "You already have the highest possible found rank, so were rewarded " + ChatColor.RED + rubies + ChatColor.WHITE + " rubies!");
				pi.addRubies(rubies);
			}
			for(Player online : Bukkit.getOnlinePlayers()){
				online.playSound(online.getLocation(), Sound.ENTITY_ENDERDRAGON_DEATH, 1, 1);
				online.sendMessage(prefix + pi.getDisplayName() + " found " + name);
			}
		}
		pi.updateScoreboard();
		*/
		if(rank != null){
			name = RARITY_STRING(ULTIMATE) + ChatColor.GOLD + " Rank";
			p.sendMessage(prefix + "You found an " + name + ChatColor.WHITE + " in your crate!");
			for(Player online : Bukkit.getOnlinePlayers()){
				online.playSound(online.getLocation(), Sound.ENTITY_ENDER_DRAGON_DEATH, 1, 1);
				online.sendMessage(prefix + core.format.colorize(core.gm.getPrefix(p) + p.getName()) + ChatColor.WHITE + " found " + name);
			}
		}
		a.setCustomName(name);
		a.setCustomNameVisible(true);
		a.setVisible(false);
		a.setSmall(true);
		if(h != null) a.setHelmet(h.Skull());
		a.setVelocity(new Vector(0.0, 0.75, 0.0));
		a2.setCustomName(core.format.colorize(core.gm.getPrefix(pi.getPlayer()) + pi.getPlayer().getName()) + ChatColor.WHITE + " found");
		a2.setCustomNameVisible(true);
		a2.setVisible(false);
		a2.setSmall(false);
		a2.setVelocity(new Vector(0.0, 0.75, 0.0));
		//if(e != null) e.setVelocity(new Vector(0.0, 0.75, 0.0));
		p.sendMessage(prefix + "You found " + name);
		trail = t;
		pi.removeKey(keyUsed);
		inCrate.remove(p);
		Cooldown();
	}
	
	public void chooseKey(Player p){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		Inventory i = Bukkit.getServer().createInventory(null, 36, ChatColor.GOLD + "Crate: Choose a Key");

		ItemStack common = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta commonMeta = common.getItemMeta();
		commonMeta.setDisplayName(ChatColor.WHITE + "Common Key");
		List<String> commonLore = new ArrayList<>();
		commonLore.add(ChatColor.GREEN + "" + pi.getKeys(COMMON));
		commonLore.add("");
		if(pi.getKeys(COMMON) == 0){
			commonLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to buy more keys");
		}else{
			commonLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to open");
		}
		commonMeta.setLore(commonLore);
		common.setItemMeta(commonMeta);
		i.setItem(10, common);

		ItemStack uncommon = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta uncommonMeta = common.getItemMeta();
		uncommonMeta.setDisplayName(ChatColor.BLUE + "Uncommon Key");
		List<String> uncommonLore = new ArrayList<>();
		uncommonLore.add(ChatColor.GREEN + "" + pi.getKeys(UNCOMMON));
		uncommonLore.add("");
		if(pi.getKeys(UNCOMMON) == 0){
			uncommonLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to buy more keys");
		}else{
			uncommonLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to open");
		}
		uncommonMeta.setLore(uncommonLore);
		uncommon.setItemMeta(uncommonMeta);
		i.setItem(12, uncommon);

		ItemStack rare = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta rareMeta = rare.getItemMeta();
		rareMeta.setDisplayName(ChatColor.DARK_BLUE + "Rare Key");
		List<String> rareLore = new ArrayList<>();
		rareLore.add(ChatColor.GREEN + "" + pi.getKeys(RARE));
		rareLore.add("");
		if(pi.getKeys(RARE) == 0){
			rareLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to buy more keys");
		}else{
			rareLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to open");
		}
		rareMeta.setLore(rareLore);
		rare.setItemMeta(rareMeta);
		i.setItem(14, rare);

		ItemStack epic = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta epicMeta = epic.getItemMeta();
		epicMeta.setDisplayName(ChatColor.DARK_PURPLE + "Epic Key");
		List<String> epicLore = new ArrayList<>();
		epicLore.add(ChatColor.GREEN + "" + pi.getKeys(EPIC));
		epicLore.add("");
		if(pi.getKeys(EPIC) == 0){
			epicLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to buy more keys");
		}else{
			epicLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to open");
		}
		epicMeta.setLore(epicLore);
		epic.setItemMeta(epicMeta);
		i.setItem(16, epic);

		ItemStack tokens = new ItemStack(Material.GOLD_INGOT);
		ItemMeta tokensMeta = tokens.getItemMeta();
		tokensMeta.setDisplayName(ChatColor.BLUE + "Cosmetic Tokens: " + ChatColor.GOLD + core.cosmetictokens.getCosmeticTokensCount(pi.getPlayer()));
		tokensMeta.setLore(Arrays.asList(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to convert tokens to cosmetic tokens"));
		tokens.setItemMeta(tokensMeta);
		i.setItem(31, tokens);

		p.openInventory(i);
	}
	
	public static HashMap<Player, Crate> inCrate = new HashMap<Player, Crate>();

	private void ShowCosmeticTokenInventory(Player p){
		PlayerInformation pi = PlayerInformation.connected.get(p);
		Inventory i = Bukkit.getServer().createInventory(null, 36, ChatColor.GOLD + "Cosmetic Tokens");

		ItemStack convert10 = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta convert10Meta = convert10.getItemMeta();
		convert10Meta.setDisplayName(ChatColor.GOLD + "10 Cosmetic Tokens");
		List<String> convert10Lore = new ArrayList<>();
		if(core.tokens.getTokensCount(p) >= 100) {
			convert10Lore.add(ChatColor.GREEN + "100 Tokens");
			convert10Lore.add("");
			convert10Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to convert");
		}else{
			convert10Lore.add(ChatColor.RED + "100 Tokens");
			convert10Lore.add("");
			convert10Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Not enough tokens!");
		}
		convert10Meta.setLore(convert10Lore);
		convert10.setItemMeta(convert10Meta);
		convert10.setAmount(10);
		i.setItem(10, convert10);

		ItemStack convert50 = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta convert50Meta = convert10.getItemMeta();
		convert50Meta.setDisplayName(ChatColor.GOLD + "50 Cosmetic Tokens");
		List<String> convert50Lore = new ArrayList<>();
		if(core.tokens.getTokensCount(p) >= 500) {
			convert50Lore.add(ChatColor.GREEN + "500 Tokens");
			convert50Lore.add("");
			convert50Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to convert");
		}else{
			convert50Lore.add(ChatColor.RED + "500 Tokens");
			convert50Lore.add("");
			convert50Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Not enough tokens!");
		}
		convert50Meta.setLore(convert50Lore);
		convert50.setItemMeta(convert50Meta);
		convert50.setAmount(50);
		i.setItem(12, convert50);

		ItemStack convert200 = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta convert200Meta = convert10.getItemMeta();
		convert200Meta.setDisplayName(ChatColor.GOLD + "200 Cosmetic Tokens");
		List<String> convert200Lore = new ArrayList<>();
		if(core.tokens.getTokensCount(p) >= 2000) {
			convert200Lore.add(ChatColor.GREEN + "2000 Tokens");
			convert200Lore.add("");
			convert200Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to convert");
		}else{
			convert200Lore.add(ChatColor.RED + "2000 Tokens");
			convert200Lore.add("");
			convert200Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Not enough tokens!");
		}
		convert200Meta.setLore(convert200Lore);
		convert200.setItemMeta(convert200Meta);
		convert200.setAmount(64);
		i.setItem(14, convert200);

		ItemStack convertAll = new ItemStack(Material.GOLD_NUGGET);
		ItemMeta convertAllMeta = convert10.getItemMeta();
		List<String> convertAllLore = new ArrayList<>();
		if(core.tokens.getTokensCount(p) >= 10) {
			int all = (int) Math.floor(core.tokens.getTokensCount(p) / 10d);
			convertAllMeta.setDisplayName(ChatColor.GOLD.toString() + all + " Cosmetic Tokens");
			convertAllLore.add(ChatColor.GREEN + "Convert all tokens to cosmetic tokens");
		}else{
			convertAllMeta.setDisplayName(ChatColor.RED.toString() + "No Tokens Available");
			convertAllLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Not enough tokens!");
		}
		convertAllMeta.setLore(convertAllLore);
		convertAll.setItemMeta(convertAllMeta);
		convertAll.setAmount(64);
		i.setItem(16, convertAll);

		ItemStack tokens = new ItemStack(Material.GOLD_INGOT);
		ItemMeta tokensMeta = tokens.getItemMeta();
		tokensMeta.setDisplayName(ChatColor.BLUE + "Cosmetic Tokens: " + ChatColor.GOLD + core.cosmetictokens.getCosmeticTokensCount(p));
		tokensMeta.setLore(Arrays.asList(ChatColor.LIGHT_PURPLE + "Tokens: " + ChatColor.GOLD + core.tokens.getTokensCount(p)));
		tokens.setItemMeta(tokensMeta);
		i.setItem(31, tokens);

		p.openInventory(i);
	}

	private void ShowBuyKeyInventory(Player p, int key){
		String keyName = RARITY_STRING(key);
		int keyCost = KEY_COST(key);
		int tokens = core.cosmetictokens.getCosmeticTokensCount(p);

		PlayerInformation pi = PlayerInformation.connected.get(p);
		Inventory i = Bukkit.getServer().createInventory(null, 36, ChatColor.GOLD + "Buy Keys: " + keyName);

		ItemStack buy1 = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta buy1Meta = buy1.getItemMeta();
		buy1Meta.setDisplayName(ChatColor.GOLD + "1 " + keyName + ChatColor.GOLD + " Key");
		List<String> buy1Lore = new ArrayList<>();
		if(tokens < keyCost){
			buy1Lore.add(ChatColor.RED.toString() + keyCost + " Cosmetic Tokens");
			buy1Lore.add("");
			buy1Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Not enough tokens!");
		}else{
			buy1Lore.add(ChatColor.GREEN.toString() + keyCost + " Cosmetic Tokens");
			buy1Lore.add("");
			buy1Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to buy 1 key!");
		}
		buy1Meta.setLore(buy1Lore);
		buy1.setItemMeta(buy1Meta);
		i.setItem(10, buy1);

		ItemStack buy5 = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta buy5Meta = buy5.getItemMeta();
		buy5Meta.setDisplayName(ChatColor.GOLD + "5 " + keyName + ChatColor.GOLD + " Keys");
		List<String> buy5Lore = new ArrayList<>();
		if(tokens < (keyCost * 5)){
			buy5Lore.add(ChatColor.RED.toString() + (keyCost * 5) + " Cosmetic Tokens");
			buy5Lore.add("");
			buy5Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Not enough tokens!");
		}else{
			buy5Lore.add(ChatColor.GREEN.toString() + (keyCost * 5) + " Cosmetic Tokens");
			buy5Lore.add("");
			buy5Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to buy 5 keys!");
		}
		buy5Meta.setLore(buy5Lore);
		buy5.setItemMeta(buy5Meta);
		buy5.setAmount(5);
		i.setItem(12, buy5);

		ItemStack buy10 = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta buy10Meta = buy10.getItemMeta();
		buy10Meta.setDisplayName(ChatColor.GOLD + "10 " + keyName + ChatColor.GOLD + " Keys");
		List<String> buy10Lore = new ArrayList<>();
		if(tokens < (keyCost * 5)){
			buy10Lore.add(ChatColor.RED.toString() + (keyCost * 10) + " Cosmetic Tokens");
			buy10Lore.add("");
			buy10Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Not enough tokens!");
		}else{
			buy10Lore.add(ChatColor.GREEN.toString() + (keyCost * 10) + " Cosmetic Tokens");
			buy10Lore.add("");
			buy10Lore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to buy 10 keys!");
		}
		buy10Meta.setLore(buy10Lore);
		buy10.setItemMeta(buy10Meta);
		buy10.setAmount(10);
		i.setItem(14, buy10);

		int max = (int) Math.floor(core.cosmetictokens.getCosmeticTokensCount(p) / keyCost);
		ItemStack buyMax = new ItemStack(Material.TRIPWIRE_HOOK);
		ItemMeta buyMaxMeta = buyMax.getItemMeta();
		buyMaxMeta.setDisplayName(ChatColor.GOLD.toString() + max + " " + keyName + ChatColor.GOLD + " Keys");
		List<String> buyMaxLore = new ArrayList<>();
		if(tokens < (keyCost * max)){
			buyMaxLore.add(ChatColor.RED.toString() + (keyCost * max) + " Cosmetic Tokens");
			buyMaxLore.add("");
			buyMaxLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Not enough tokens!");
		}else{
			buyMaxLore.add(ChatColor.GREEN.toString() + (keyCost * max) + " Cosmetic Tokens");
			buyMaxLore.add("");
			buyMaxLore.add(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Click to buy " + max + " keys!");
		}
		buyMaxMeta.setLore(buyMaxLore);
		buyMax.setItemMeta(buyMaxMeta);
		if(max > 64){
			buyMax.setAmount(64);
		}else {
			buyMax.setAmount(max);
		}
		i.setItem(16, buyMax);

		ItemStack tokensItem = new ItemStack(Material.GOLD_INGOT);
		ItemMeta tokensMeta = tokensItem.getItemMeta();
		tokensMeta.setDisplayName(ChatColor.BLUE + "Cosmetic Tokens: " + ChatColor.GOLD + core.cosmetictokens.getCosmeticTokensCount(p));
		tokensItem.setItemMeta(tokensMeta);
		i.setItem(31, tokensItem);

		p.openInventory(i);
	}

	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (e.getView().getTitle().contains("Choose a Key")) {
			e.setCancelled(true);
			try{
				if (e.getCurrentItem().getType() == Material.TRIPWIRE_HOOK) {
					if(inCrate.get(p) == this){
						if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Common")) {
							if(PlayerInformation.connected.get(p).getKeys(COMMON) != 0) {
								p.closeInventory();
								openCrate(p, COMMON);
							}else{
								ShowBuyKeyInventory(p, COMMON);
							}
						}
						if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Uncommon")) {
							if(PlayerInformation.connected.get(p).getKeys(UNCOMMON) != 0) {
								p.closeInventory();
								openCrate(p, UNCOMMON);
							}else{
								ShowBuyKeyInventory(p, UNCOMMON);
							}
						}
						if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Rare")) {
							if(PlayerInformation.connected.get(p).getKeys(RARE) != 0) {
								p.closeInventory();
								openCrate(p, RARE);
							}else{
								ShowBuyKeyInventory(p, RARE);
							}
						}
						if (e.getCurrentItem().getItemMeta().getDisplayName().contains("Epic")) {
							if(PlayerInformation.connected.get(p).getKeys(EPIC) != 0) {
								p.closeInventory();
								openCrate(p, EPIC);
							}else{
								ShowBuyKeyInventory(p, EPIC);
							}
						}
					}
				}else if(e.getCurrentItem().getType() == Material.GOLD_INGOT){
					ShowCosmeticTokenInventory(p);
				}
			}catch(Exception ex){
				
			}
		}else if(e.getView().getTitle().contains("Cosmetic Tokens")){
			if(e.getCurrentItem() != null) {
				if (e.getCurrentItem().hasItemMeta()) {
					String name = e.getCurrentItem().getItemMeta().getDisplayName();
					int tokens = core.tokens.getTokensCount(p);
					if(name.contains("10 Cosmetic Tokens")){
						if(tokens >= 100){
							core.tokens.removeTokens(p, 100);
							core.cosmetictokens.addCosmeticTokens(p, 10);
						}
					}else if(name.contains("50 Cosmetic Tokens")){
						if(tokens >= 500){
							core.tokens.removeTokens(p, 500);
							core.cosmetictokens.addCosmeticTokens(p, 50);
						}
					}else if(name.contains("200 Cosmetic Tokens")){
						if(tokens >= 2000){
							core.tokens.removeTokens(p, 2000);
							core.cosmetictokens.addCosmeticTokens(p, 200);
						}
					}else{
						if(tokens >= 10) {
							int leftoverTokens = tokens % 10;
							int cosmeticTokens = (int) Math.floor(core.tokens.getTokensCount(p) / 10);
							core.tokens.removeTokens(p, tokens - leftoverTokens);
							core.cosmetictokens.addCosmeticTokens(p, cosmeticTokens);
						}
					}
					ShowCosmeticTokenInventory(p);
				}
			}
		}else if(e.getView().getTitle().contains("Buy Keys: ")){
			String name = e.getView().getTitle();
			name = StringUtil.ClearFormatting(name);
			name = name.split(": ")[1].toUpperCase();
			if(e.getCurrentItem() != null){
				if(e.getCurrentItem().hasItemMeta()){
					String itemName = StringUtil.ClearFormatting(e.getCurrentItem().getItemMeta().getDisplayName());
					int cost = KEY_COST(COMMON);
					int key = COMMON;
					if(name.equals("Uncommon")){
						cost = KEY_COST(UNCOMMON);
						key = UNCOMMON;
					}else if(name.equals("Rare")){
						cost = KEY_COST(RARE);
						key = RARE;
					}else if(name.equals("Epic")){
						cost = KEY_COST(EPIC);
						key = EPIC;
					}
					int tokens = core.cosmetictokens.getCosmeticTokensCount(p);
					if(itemName.contains("Key")){
						int amount = Integer.valueOf(itemName.split(" ")[0]);
						if(tokens >= cost * amount){
							for(int i = 0; i < amount; i++) {
								PlayerInformation.connected.get(p).addKey(key);
							}
							core.cosmetictokens.removeCosmeticTokens(p, cost * amount);
						}else{
							p.sendMessage(core.format.label() + core.format.colorize("&7Not enough cosmetic tokens for this!"));
						}
					}
					ShowBuyKeyInventory(p, key);
				}
			}
		}
	}
	
	public void Particle(){
		Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
				new Runnable() {
			@Override
			public void run() {
				Location loc = new Location(Location.getWorld(), Location.getX() + 0.5, Location.getY() - 0.3, Location.getZ() + 0.5);
				double radius = 0.75;
				for(double y = 0; y <= 10; y+=0.05) {
				    double x = radius * Math.cos(y);
				    double z = radius * Math.sin(y);
				    Location.getWorld().spawnParticle(Particle.PORTAL, (float) (loc.getX() + x), (float) ((loc.getY())), (float) (loc.getZ() + z), 0, 0, 0, 0, 1);
				}
				Particle();
			}
		}, 10);
	}
	
	public void disable() {
		if(hat != null) {
			hat.remove();
			hat = null;
		}
		if(found != null) {
			found.remove();
			found = null;
		}
		opening = false;
		rarity = -1;
		trail = null;
		//morph = null;
		/*if(morphE != null){
			morphE.remove();
			morphE = null;
		}*/
		display.remove();
	}

}

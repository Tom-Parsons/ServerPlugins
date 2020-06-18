package com.xiis.infernocosmetics.cosmetics;

import com.xiis.infernocosmetics.cosmetics.crates.Crate;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Trail {

	private String _id;
	private String _name;
	private int _rarity;
	private Particle _particle;
	private Material _item;
	
	public Trail(String id, String name, int rarity, Particle particle, Material item){
		_id = id;
		_name = name;
		_rarity = rarity;
		_particle = particle;
		_item = item;
		Trails.Trails.add(this);
		if(_rarity == Crate.COMMON) Trails.Common.add(this);
		if(_rarity == Crate.UNCOMMON) Trails.Uncommon.add(this);
		if(_rarity == Crate.RARE) Trails.Rare.add(this);
		if(_rarity == Crate.EPIC) Trails.Epic.add(this);
		if(_rarity == Crate.LEGENDARY) Trails.Legendary.add(this);
	}

	public String ID() { return _id; }

	public String Name(){
		return _name;
	}
	
	public int Rarity(){
		return _rarity;
	}
	
	public Particle Particle(){
		return _particle;
	}
	
	public ItemStack Item(){
		ItemStack i = new ItemStack(_item);
		ItemMeta im = i.getItemMeta();
		im.setDisplayName(ChatColor.WHITE + _name);
		i.setItemMeta(im);
		return i;
	}

}

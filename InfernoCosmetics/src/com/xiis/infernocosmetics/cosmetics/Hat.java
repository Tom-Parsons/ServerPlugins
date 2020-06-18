package com.xiis.infernocosmetics.cosmetics;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.lang.reflect.Field;

public class Hat extends ItemStack{

	private String _id;
	private String _name;
	private int _rarity;
	private String _UUID;
	
	public Hat(String id, String name, int rarity, String UUID){
		_id = id;
		_name = name;
		_rarity = rarity;
		_UUID = UUID;
		Hats.Hats.add(this);
		if(_rarity == 0) Hats.Common.add(this);
		if(_rarity == 1) Hats.Uncommon.add(this);
		if(_rarity == 2) Hats.Rare.add(this);
		if(_rarity == 3) Hats.Epic.add(this);
		if(_rarity == 4) Hats.Legendary.add(this);
	}

	public String ID() { return _id; }

	public String Name(){
		return _name;
	}
	
	public int Rarity(){
		return _rarity;
	}
	
	public String UUID(){
		return _UUID;
	}
	
	public ItemStack Skull(){
        ItemStack head = new ItemStack(Material.PLAYER_HEAD);
        if(_UUID.isEmpty())return head;
        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(java.util.UUID.randomUUID(), null);
        byte[] encodedData = _UUID.getBytes();
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);
        return head;
	}
	
}

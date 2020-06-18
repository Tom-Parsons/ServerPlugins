package com.xiis.infernocosmetics.player;

import com.xiis.infernocosmetics.cosmetics.Hat;
import com.xiis.infernocosmetics.cosmetics.Hats;
import com.xiis.infernocosmetics.cosmetics.Trail;
import com.xiis.infernocosmetics.cosmetics.Trails;
import com.xiis.infernocosmetics.cosmetics.api.Database;
import com.xiis.infernocosmetics.cosmetics.crates.Crate;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerInformation {

    private network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);

    public static HashMap<Player, PlayerInformation> connected = new HashMap<Player, PlayerInformation>();

    private Player _player;
    private int _commonKeys;
    private int _uncommonKeys;
    private int _rareKeys;
    private int _epicKeys;

    private ArrayList<String> _hats = new ArrayList<String>();
    private ArrayList<String> _trails = new ArrayList<String>();

    private String _activeHat = "None";
    private String _activeTrail = "None";

    public PlayerInformation(Player player, int commonKeys, int uncommonKeys, int rareKeys, int epicKeys, String activeHat, String activeTrail){
        _player = player;
        _commonKeys = commonKeys;
        _uncommonKeys = uncommonKeys;
        _rareKeys = rareKeys;
        _epicKeys = epicKeys;
        _activeHat = activeHat;
        if(!_activeHat.equals("None")) player.getInventory().setHelmet(Hats.getHatFromID(_activeHat).Skull());
        _activeTrail = activeTrail;
    }

    public Player getPlayer(){
        return _player;
    }
    public int getKeys(int key){
        switch(key){
            case Crate.COMMON:
                return _commonKeys;
            case Crate.UNCOMMON:
                return _uncommonKeys;
            case Crate.RARE:
                return _rareKeys;
            case Crate.EPIC:
                return _epicKeys;
        }
        return 0;
    }
    public void addKey(int key){
        switch(key){
            case Crate.COMMON:
                _commonKeys++;
                break;
            case Crate.UNCOMMON:
                _uncommonKeys++;
                break;
            case Crate.RARE:
                _rareKeys++;
                break;
            case Crate.EPIC:
                _epicKeys++;
                break;
        }
        try {
            Save();
        }catch(Exception ex){ }
    }
    public void removeKey(int key){
        switch(key){
            case Crate.COMMON:
                _commonKeys--;
                break;
            case Crate.UNCOMMON:
                _uncommonKeys--;
                break;
            case Crate.RARE:
                _rareKeys--;
                break;
            case Crate.EPIC:
                _epicKeys--;
                break;
        }
        try {
            Save();
        }catch(Exception ex){ }
    }

    public void addHat(String hatID)  {
        _hats.add(hatID);
    }
    public void addNewHat(String hatID) throws Exception {
        _hats.add(hatID);
        Database.AddPlayerHat(_player.getUniqueId().toString(), hatID);
        if(getOwnedHatSize() >= 5){
            core.ah.Award(_player, "Cosmetics_Hats_Own5");
        }
        if(getOwnedHatSize() >= 10){
            core.ah.Award(_player, "Cosmetics_Hats_Own10");
        }
        if(getOwnedHatSize() >= 20){
            core.ah.Award(_player, "Cosmetics_Hats_Own20");
        }
        if(getOwnedHatSize() == Hats.Hats.size()){
            core.ah.Award(_player, "Cosmetics_Hats_OwnAll");
        }
    }
    public void addNewHat(Hat hat) throws Exception {
        addNewHat(hat.ID());
    }
    public ArrayList<String> getHats(){
        return _hats;
    }
    public int getOwnedHatSize() {
        return _hats.size();
    }
    public boolean hasHat(Hat hat) {
        return _hats.contains(hat.ID());
    }
    public boolean hasHat(String hatID){
        return _hats.contains(hatID);
    }
    public ArrayList<String> getUnownedHats(){
        ArrayList<String> unownedHats = new ArrayList<String>();
        for(Hat h : Hats.Hats){
            if(!hasHat(h)) unownedHats.add(h.ID());
        }
        return unownedHats;
    }
    public void setActiveHat(String hatID){
        _activeHat = hatID;
        if(!_activeHat.equals("None")) _player.getInventory().setHelmet(Hats.getHatFromID(_activeHat).Skull());
        try {
            Save();
        }catch(Exception ex){ }
    }
    public void setActiveHat(Hat hat){
        setActiveHat(hat.ID());
    }
    public void removeHat(){
        setActiveHat("None");
    }
    public String getActiveHat(){
        return _activeHat;
    }
    public void setHelmet(){
        if(!_activeHat.equals("None")) _player.getInventory().setHelmet(Hats.getHatFromID(_activeHat).Skull());
    }

    public void addTrail(String trailID)  {
        _trails.add(trailID);
    }
    public void addNewTrail(String trailID) throws Exception {
        _trails.add(trailID);
        Database.AddPlayerTrail(_player.getUniqueId().toString(), trailID);
        if(getOwnedTrailSize() >= 5){
            core.ah.Award(_player, "Cosmetics_Trails_Own5");
        }
        if(getOwnedTrailSize() >= 10){
            core.ah.Award(_player, "Cosmetics_Trails_Own10");
        }
        if(getOwnedTrailSize() == Trails.Trails.size()){
            core.ah.Award(_player, "Cosmetics_Trails_OwnAll");
        }
    }
    public void addNewTrail(Trail trail) throws Exception {
        addNewTrail(trail.ID());
    }
    public ArrayList<String> getTrails(){
        return _trails;
    }
    public int getOwnedTrailSize() {
        return _trails.size();
    }
    public boolean hasTrail(Trail trail) {
        return _trails.contains(trail.ID());
    }
    public boolean hasTrail(String trailID){
        return _trails.contains(trailID);
    }
    public ArrayList<String> getUnownedTrails(){
        ArrayList<String> unownedTrails = new ArrayList<String>();
        for(Trail t : Trails.Trails){
            if(!hasTrail(t)) unownedTrails.add(t.ID());
        }
        return unownedTrails;
    }
    public void setActiveTrail(String trailID){
        _activeTrail = trailID;
        try {
            Save();
        }catch(Exception ex){ }
    }
    public void setActiveTrail(Trail trail){
        setActiveTrail(trail.ID());
    }
    public void removeTrail(){
        setActiveTrail("None");
    }
    public String getActiveTrail(){
        return _activeTrail;
    }


    public void Save() throws Exception {
        Database.SavePlayerData(this);
    }
}

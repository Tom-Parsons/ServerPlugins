package network.inferno.gladiators;

import network.inferno.gladiators.game.Events;
import network.inferno.gladiators.game.Game;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

    public static Main pl;
    public static network.inferno.core.Main core;

    public static Game game;

    @Override
    public void onEnable(){
        pl = this;
        core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
        getServer().getPluginManager().registerEvents(new Events(), this);
        game = new Game("Gladiators", 4, 16, 10, 60 * 5);
    }

}

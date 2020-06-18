package network.inferno.lobby.managers;

import net.ME1312.SubServers.Client.Bukkit.Network.API.SubServer;
import net.ME1312.SubServers.Client.Bukkit.SubAPI;
import network.inferno.lobby.Main;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

public class ScoreboardManager {

    public static void ReloadSoreboard(){
        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(Main.pl,
                new Runnable() {
                    @Override
                    public void run() {
                        SubAPI api = SubAPI.getInstance();
                        if(api != null) {
                            try {
                                api.getSubServers(subServers -> {
                                    network.inferno.core.Main core = network.inferno.core.Main.getPlugin(network.inferno.core.Main.class);
                                    int players = 0;
                                    for (SubServer ss : subServers.values()) {
                                        players += ss.getPlayers().size();
                                    }
                                    for (Player p : Bukkit.getOnlinePlayers()) {
                                        Scoreboard scoreboard = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
                                        Objective objective = scoreboard.registerNewObjective("Lobby", "dummy", ChatColor.GOLD + "Inferno" + ChatColor.GRAY + "Network");
                                        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
                                        Score playersScore = objective.getScore(ChatColor.GREEN + "Players: " + ChatColor.WHITE + players);
                                        playersScore.setScore(15);
                                        Score blank1 = objective.getScore("");
                                        blank1.setScore(14);
                                        Score rankScore = objective.getScore(ChatColor.BLUE + "Rank: " + core.format.colorize(core.prefix.getPrefix(p)));
                                        rankScore.setScore(13);
                                        Score blank2 = objective.getScore(" ");
                                        blank2.setScore(12);
                                        Score levelScore = objective.getScore(ChatColor.YELLOW + "Level: " + ChatColor.WHITE + core.level.getLevelPrefix(core.level.getLevel(p)));
                                        levelScore.setScore(11);
                                        Score tokensScore = objective.getScore(ChatColor.GOLD + "Tokens: " + ChatColor.WHITE + core.tokens.getTokensCount(p));
                                        tokensScore.setScore(10);
                                        Score cosmeticTokensScore = objective.getScore(ChatColor.LIGHT_PURPLE + "Cosmetic Tokens: " + ChatColor.WHITE + core.cosmetictokens.getCosmeticTokensCount(p));
                                        cosmeticTokensScore.setScore(9);
                                        Score blank3 = objective.getScore("  ");
                                        blank3.setScore(8);
                                        Score websiteScore = objective.getScore(ChatColor.RED + "www.infernonetwork.org");
                                        websiteScore.setScore(7);
                                        p.setScoreboard(scoreboard);
                                    }
                                });
                            }catch (Exception ex){ }
                        }
                        ReloadSoreboard();
                    }
                }, 40);
    }

}

package me.mehboss.pvp;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class Listeners implements Listener {

	private Main plugin;
	private Checks checks;

	public Listeners(Main plugin, Checks checks) {
		this.plugin = plugin;
		this.checks = checks;
	}

	@EventHandler
	public void signChange(SignChangeEvent sign) {
		if (!sign.getLine(0).isEmpty() && !sign.getLine(1).isEmpty() && sign.getLine(0).equals("S1v1")
				&& plugin.arenas.containsKey(sign.getLine(1))) {
			sign.setLine(2, "Queue: 0");
			sign.setLine(3, "Status: EMPTY");

			if (!plugin.signloc.contains(sign.getBlock().getLocation()))
				plugin.signloc.add(sign.getBlock().getLocation());
		}
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {

		if (!plugin.playing.containsKey(e.getPlayer())) {
			return;
		}

		for (Arena arena : plugin.arenas.values()) {
			if (arena.getQueue().contains(e.getPlayer())) {
				arena.getQueue().remove(e.getPlayer());

				if (arena.getQueue().isEmpty()) {
					checks.restartGame(null, arena);
				} else {
					checks.restartGame(arena.getQueue().get(0), arena);
				}

				if (arena.getState() != ArenaState.WAITING && arena.getState() != ArenaState.EMPTY)
					plugin.quit.add(e.getPlayer());

				break;
			}
		}

		plugin.playing.remove(e.getPlayer());
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		if (plugin.quit.contains(e.getPlayer())) {
			Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(), "spawn " + e.getPlayer().getName());
			plugin.quit.remove(e.getPlayer());

			plugin.dataConfig.getStringList("Players").remove(e.getPlayer().getUniqueId().toString());
			plugin.saveCustomYml(plugin.dataConfig, plugin.dataYml);
			plugin.initCustomYml();
		}
	}

	@EventHandler
	public void regenHunger(FoodLevelChangeEvent e) {
		if (e.getEntity() instanceof Player)
			return;

		Player p = (Player) e.getEntity();

		if (!plugin.playing.containsKey(p) || plugin.arenas.get(plugin.playing.get(p)).getState() != ArenaState.RUNNING)
			return;

		if (plugin.getConfig().getBoolean("Hunger") == true)
			return;

		if (p.getFoodLevel() < 20)
			e.setFoodLevel(20);

		e.setCancelled(true);
	}

	@EventHandler
	public void onDeath(PlayerDeathEvent e) {

		if (e.getEntity() != null && plugin.playing.containsKey(e.getEntity())
				&& plugin.arenas.get(plugin.playing.get(e.getEntity())).getState() == ArenaState.RUNNING) {

			Arena arena = plugin.arenas.get(plugin.playing.get(e.getEntity()));

			Player killed = (Player) e.getEntity();
			Player killer = arena.getQueue().get(0);
			killer = arena.getQueue().get(0);

			if (arena.getQueue().get(0).getName().equals(killed.getName()))
				killer = arena.getQueue().get(1);

			if (e.getEntity().getKiller() instanceof Player) {

				if (plugin.getConfig().getString("Defeat-Player") != null)
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("Defeat-Player").replaceAll("%winner%", killer.getName())
									.replaceAll("%loser%", killed.getName())
									.replaceAll("%arena%", plugin.playing.get(killed))));
			} else {

				if (plugin.getConfig().getString("Defeat-Unknown") != null)
					Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("Defeat-Unknown").replaceAll("%winner%", killer.getName())
									.replaceAll("%loser%", killed.getName()).replaceAll("%arena%", arena.getName())));
			}
			checks.endGame(killer, killed, arena);
		}
	}
}

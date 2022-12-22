package me.mehboss.pvp;

import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;

public class Checks {

	private Main plugin;

	public Checks(Main plugin) {
		this.plugin = plugin;
	}

	public void checkQueue(Player p, Arena arena) {

		boolean found = false;

		if (arena != null && arena.getQueue().size() < 2) {
			found = true;
		}

		if (arena != null && arena.getQueue().size() >= 2) {
			sendMessage(p, "Arena-Full");
			return;
		}

		if (arena == null) {
			for (Arena name : plugin.arenas.values()) {
				if (name.getState() == ArenaState.WAITING) {
					arena = name;
					found = true;
					break;
				}
			}

			if (found == false) {
				for (Arena name : plugin.arenas.values()) {
					if (name.getState() == ArenaState.EMPTY) {
						arena = name;
						found = true;
						break;
					}
				}
			}
		}

		if (found == true) {
			plugin.playing.put(p, arena.getName());
			arena.addQueue(p);

			if (arena.getQueue().size() == 2) {
				arena.setState(ArenaState.STARTING);
				updateSigns();

				startGame(p, arena);
			}

			if (arena.getQueue().size() == 1) {
				arena.setState(ArenaState.WAITING);
				p.sendMessage(
						ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Added-To-Queue"))
								.replaceAll("%arena%", arena.getName()));
				updateSigns();
			}
		}

		if (found == false) {
			sendMessage(p, "No-Available-Arenas");
		}
	}

	public void updateSigns() {

		if (plugin.signloc.isEmpty()) {
			return;
		}
		
		for (Location loc : plugin.signloc) {

			if (loc.getBlock() == null || loc.getBlock().getType() == null || !plugin.signMatch(loc.getBlock())) {
				continue;
			}

			Sign sign = (Sign) loc.getBlock().getState();

			if (!sign.isPlaced() || sign == null) {
				continue;
			}

			if (!sign.getLine(1).isEmpty() && plugin.arenas.containsKey(sign.getLine(1))) {
				Arena arena = plugin.arenas.get(sign.getLine(1));
				sign.setLine(3, "Status: " + arena.getState().toString());
				sign.setLine(2, "Queue: " + String.valueOf(arena.getQueue().size()));
				sign.update();
			}
		}
	}

	public void restartGame(Player p, Arena arena) {
		if (arena.getQueue().isEmpty()) {
			arena.setState(ArenaState.EMPTY);
			updateSigns();
			return;
		}

		if (p != null)
			sendMessage(arena.getQueue().get(0), "Player-Left");

		arena.setState(ArenaState.WAITING);
		updateSigns();
	}

	public void endGame(Player winner, Player loser, Arena arena) {

		arena.setState(ArenaState.RESETTING);

		if (plugin.getConfig().getString("Winner-Congrats") != null)
			sendMessage(winner, "Winner-Congrats");

		if (plugin.getConfig().getString("Timed-Pickup") != null)
			sendMessage(winner, "Timed-Pickup");

		updateSigns();

		Countdown countdown = new Countdown(plugin.getConfig().getInt("Pickup-Seconds"), winner);
		countdown.start();

		Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SurvivalPvP"), new Runnable() {
			@Override
			public void run() {
				for (Entity e : winner.getNearbyEntities((double) 2, (double) 2, (double) 2)) {
					if (e instanceof Item) {
						e.remove();
					}
				}

				for (String command : plugin.getConfig().getStringList("End-Commands")) {
					Bukkit.dispatchCommand(Bukkit.getServer().getConsoleSender(),
							command.replaceAll("%loser%", loser.getName()).replaceAll("%winner%", winner.getName()));
				}

				plugin.playing.remove(winner);
				plugin.playing.remove(loser);

				arena.clearQueue();
				arena.setState(ArenaState.EMPTY);
				updateSigns();
			}
		}, 10 * 20);
	}

	public void startGame(Player p, Arena arena) {

		if (arena.getLocation1() == null || arena.getLocation2() == null) {
			arena.setState(ArenaState.WAITING);
			arena.clearQueue();
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					plugin.getConfig().getString("Could-Not-Start").replaceAll("%arena%", arena.getName())));
			plugin.getLogger().log(Level.SEVERE,
					"Could not start arena " + arena.getName() + " because both spawn locations are not set.");
			return;
		}

		if (arena.getQueue().size() != 2) {
			p.sendMessage(ChatColor.RED + "An error occurred. Check console for details.");
			plugin.getLogger().log(Level.SEVERE,
					"Something went wrong while attempting to start the game. Somehow an arena ended up with more than 2 players in queue for a single arena. Please contact the developer for further investigation.");
			return;
		}

		Player player1 = arena.getQueue().get(0);
		Player player2 = arena.getQueue().get(1);

		player1.sendMessage(
				ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Joined-Full-Queue"))
						.replaceAll("%arena%", arena.getName()));
		player2.sendMessage(
				ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Joined-Full-Queue"))
						.replaceAll("%arena%", arena.getName()));
		
		Bukkit.getScheduler().runTaskLater(Bukkit.getPluginManager().getPlugin("SurvivalPvP"), new Runnable() {
			@Override
			public void run() {
				if (arena.getQueue().size() == 2) {

					arena.setState(ArenaState.RUNNING);
					updateSigns();

					player1.teleport(arena.getLocation1());
					player2.teleport(arena.getLocation2());

					player1.sendMessage(ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("Game-Start").replaceAll("%opponent%", player2.getName())));
					player2.sendMessage(ChatColor.translateAlternateColorCodes('&',
							plugin.getConfig().getString("Game-Start").replaceAll("%opponent%", player1.getName())));

				} else {
					restartGame(arena.getQueue().get(0), arena);
				}
			}
		}, 5 * 20);
	}

	public void lobby(String action, String name, Player p) {

		if (action.equals("create")) {

			if (plugin.arenas.containsKey(name)) {
				sendMessage(p, "Arena-Found");

			} else {
				plugin.arenasConfig.createSection("Arenas." + name);
				plugin.saveCustomYml(plugin.arenasConfig, plugin.arenasYml);
				plugin.initCustomYml();

				Arena arena = new Arena(name);

				arena.setState(ArenaState.EMPTY);
				plugin.arenas.put(arena.getName(), arena);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						plugin.getConfig().getString("Arena-Created").replaceAll("%arena%", arena.getName())));
			}
		}

		if (action.equals("delete")) {

			if (!plugin.arenas.containsKey(name)) {
				sendMessage(p, "No-Arena");

			} else {
				plugin.arenasConfig.set("Arenas." + name, "null");
				plugin.saveCustomYml(plugin.arenasConfig, plugin.arenasYml);
				plugin.initCustomYml();

				plugin.arenas.remove(name);
				p.sendMessage(ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString("Arena-Deleted")
						.replaceAll("%arena%", plugin.arenas.get(name).getName())));

			}
		}
	}

	public void sendMessage(Player p, String path) {
		String args = ChatColor.translateAlternateColorCodes('&', plugin.getConfig().getString(path));
		p.sendMessage(args);
	}
}

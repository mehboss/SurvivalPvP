package me.mehboss.pvp;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

	private Checks plugin;
	private Main main;

	public Commands(Checks plugin, Main main) {
		this.plugin = plugin;
		this.main = main;
	}

	public boolean permission(Player p) {
		return p.hasPermission("s1v1.admin");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {

		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You must be a player in order to use this command!");
			return false;
		}

		Player p = (Player) sender;

		if (args.length < 1 || args.length > 3) {
			invalidArgs(p);
			return false;
		}

		if (args[0].equalsIgnoreCase("leave")) {

			if (!p.hasPermission("s1v1.leave")) {
				plugin.sendMessage(p, "No-Perms");
				return false;
			}

			if (!main.playing.containsKey(p) || main.arenas.get(main.playing.get(p)).getState() == ArenaState.RUNNING) {
				plugin.sendMessage(p, "Not-In-Queue");
				return false;
			}

			Arena arena = main.arenas.get(main.playing.get(p));
			arena.removeQueue(p);

			plugin.restartGame(p, arena);
			main.playing.remove(p);
			p.sendMessage(ChatColor.translateAlternateColorCodes('&',
					main.getConfig().getString("Left-Queue").replaceAll("%arena%", arena.getName())));
			return true;

		}

		if (args[0].equalsIgnoreCase("list") && args.length == 1) {

			if (main.arenas.isEmpty()) {
				plugin.sendMessage(p, "No-Arenas");
				return false;
			}

			p.sendMessage(ChatColor.DARK_GRAY + "----------------------------");
			p.sendMessage(ChatColor.RED + "  ARENA LIST");
			p.sendMessage(" ");
			
			for (Arena arena : main.arenas.values()) {

				ChatColor color = ChatColor.GREEN;

				if (arena.getState() == ArenaState.WAITING)
					color = ChatColor.DARK_GREEN;
				if (arena.getState() == ArenaState.STARTING)
					color = ChatColor.RED;
				if (arena.getState() == ArenaState.RUNNING)
					color = ChatColor.DARK_RED;

				p.sendMessage(ChatColor.GRAY + arena.getName() + ChatColor.DARK_GRAY + " [" + ChatColor.YELLOW + "STATUS: " + color
						+ arena.getState().toString() + ChatColor.DARK_GRAY + "]");
			}
			p.sendMessage(ChatColor.DARK_GRAY + "----------------------------");
			return true;
		}

		if (args[0].equalsIgnoreCase("queue") && args.length == 1) {

			if (!p.hasPermission("s1v1.queue")) {
				plugin.sendMessage(p, "No-Perms");
				return false;
			}

			if (main.arenas.isEmpty()) {
				plugin.sendMessage(p, "No-Arenas");
				return false;
			}

			p.sendMessage(ChatColor.DARK_GRAY + "----------------------------");
			p.sendMessage(ChatColor.RED + "  ARENA QUEUE");
			p.sendMessage(" ");
			for (Arena arena : main.arenas.values()) {
				ChatColor color = ChatColor.GREEN;

				String player1 = "Empty";
				String player2 = "Empty";

				if (arena.getQueue().size() >= 1) {
					player1 = arena.getQueue().get(0).getName();
				}

				if (arena.getQueue().size() == 2) {
					player2 = arena.getQueue().get(1).getName();
				}

				if (arena.getState() == ArenaState.WAITING)
					color = ChatColor.DARK_GREEN;
				if (arena.getState() == ArenaState.STARTING)
					color = ChatColor.RED;
				if (arena.getState() == ArenaState.RUNNING)
					color = ChatColor.DARK_RED;

				p.sendMessage("    " + ChatColor.GRAY + arena.getName().toUpperCase());
				p.sendMessage(ChatColor.DARK_GRAY + " [" + ChatColor.YELLOW + "STATUS: " + color
						+ arena.getState().toString() + ChatColor.DARK_GRAY + "]");
				p.sendMessage(ChatColor.DARK_GRAY + " [" + ChatColor.RED + "QUEUE: " + ChatColor.GRAY + player1.toUpperCase()
						+ ChatColor.WHITE + ", " + ChatColor.GRAY + player2.toUpperCase() + ChatColor.DARK_GRAY + "]");
				p.sendMessage(" ");
			}
			p.sendMessage(ChatColor.DARK_GRAY + "----------------------------");
			return true;
		}

		if (args[0].equalsIgnoreCase("arena")) {

			if (!p.hasPermission("s1v1.admin")) {
				plugin.sendMessage(p, "No-Perms");
				return false;
			}

			if (args.length == 1) {
				invalidArgs(p);
				return false;
			}

			if (args[1].equalsIgnoreCase("create")) {

				if (args.length != 3) {
					invalidArgs(p);
					return false;
				}

				plugin.lobby("create", args[2], p);
				return true;

			}

			if (args[1].equalsIgnoreCase("delete")) {

				if (args.length != 3) {
					invalidArgs(p);
					return false;
				}

				plugin.lobby("delete", args[2], p);
				return true;
			}

			if (args[1].equalsIgnoreCase("setspawn1") && args.length == 3) {
				if (!main.arenas.containsKey(args[2])) {
					plugin.sendMessage(p, "No-Arena");
					return false;
				}

				main.arenasConfig.set("Arenas." + args[2] + ".Spawn-1", p.getLocation());
				main.saveCustomYml(main.arenasConfig, main.arenasYml);
				main.initCustomYml();

				main.arenas.get(args[2]).setLocation1(p.getLocation());
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						main.getConfig().getString("Spawnpoint-1").replaceAll("%arena%", args[2])));
				return true;

			}

			if (args[1].equalsIgnoreCase("setspawn2") && args.length == 3) {
				if (!main.arenas.containsKey(args[2])) {
					plugin.sendMessage(p, "No-Arena");
					return false;
				}

				main.arenasConfig.set("Arenas." + args[2] + ".Spawn-2", p.getLocation());
				main.saveCustomYml(main.arenasConfig, main.arenasYml);
				main.initCustomYml();

				main.arenas.get(args[2]).setLocation2(p.getLocation());
				p.sendMessage(ChatColor.translateAlternateColorCodes('&',
						main.getConfig().getString("Spawnpoint-2").replaceAll("%arena%", args[2])));
				return true;

			}
			invalidArgs(p);
			return false;
		}
		invalidArgs(p);
		return false;
	}

	public void invalidArgs(Player p) {
		for (String message : main.getConfig().getStringList("Invalid-Args")) {
			String args = ChatColor.translateAlternateColorCodes('&', message);
			p.sendMessage(args);
		}
	}
}

package me.mehboss.pvp;

import org.bukkit.ChatColor;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class SetupSign implements Listener {

	private Checks plugin;
	private Main main;

	public SetupSign(Checks plugin, Main main) {
		this.plugin = plugin;
		this.main = main;
	}

	@EventHandler
	public void onClick(PlayerInteractEvent e) {
		Action action = e.getAction();

		if (action == Action.RIGHT_CLICK_BLOCK && main.signMatch(e.getClickedBlock())) {
			Sign sign = (Sign) e.getClickedBlock().getState();

			if (sign.getLine(0) == null || !sign.getLine(0).equals("S1v1")) {
				return;
			}

			if (main.playing.containsKey(e.getPlayer())) {
				e.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', main.getConfig()
						.getString("Already-In-Queue").replaceAll("%arena%", main.playing.get(e.getPlayer()))));
				return;
			}

			if (sign.getLine(1).isEmpty()) {
				plugin.checkQueue(e.getPlayer(), null);
				return;
			}

			if (!main.arenas.containsKey(sign.getLine(1))) {
				plugin.sendMessage(e.getPlayer(), "No-Arena");
				return;
			}

			if (!main.signloc.contains(sign.getLocation()))
				main.signloc.add(sign.getLocation());

			Arena arena = main.arenas.get(sign.getLine(1));
			plugin.checkQueue(e.getPlayer(), arena);

			sign.setLine(2, "Queue: " + String.valueOf(arena.getQueue().size()));
			sign.setLine(3, "Status: " + arena.getState().toString());
			sign.update();
		}
	}
}

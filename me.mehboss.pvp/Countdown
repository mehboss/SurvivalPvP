package me.mehboss.pvp;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Countdown extends BukkitRunnable {

	private int seconds;
	private Player winner;
	
	public Countdown(int seconds, Player p) {
		this.seconds = seconds;
		this.winner = p;
	}

	public void start() {
		this.runTaskTimer(Bukkit.getPluginManager().getPlugin("SurvivalPvP"), 0, 20);
	}

	@Override
	public void run() {
		 {
			if (seconds <= 0) {
				cancel();
			} else {
				String message = String.valueOf(seconds);
				winner.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
				seconds--;
			}
			
			if (seconds <= 3 && seconds > 0)
				winner.sendMessage(String.valueOf(seconds));
		};
	}
}

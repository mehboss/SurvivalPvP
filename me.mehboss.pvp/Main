package me.mehboss.pvp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	Map<String, Arena> arenas = new HashMap<>();
	Map<Player, String> playing = new HashMap<>();
	ArrayList<Player> quit = new ArrayList<>();
	ArrayList<Location> signloc = new ArrayList<>();

	File dataYml = new File(getDataFolder() + "/playerdata.yml");
	FileConfiguration dataConfig = null;

	File signsYml = new File(getDataFolder() + "/signs.yml");
	FileConfiguration signsConfig = null;

	File arenasYml = new File(getDataFolder() + "/arenas.yml");
	FileConfiguration arenasConfig = null;

	public void saveCustomYml(FileConfiguration ymlConfig, File ymlFile) {
		if (!dataYml.exists()) {
			saveResource("playerdata.yml", false);
		}
		if (!signsYml.exists()) {
			saveResource("signs.yml", false);
		}
		if (!arenasYml.exists()) {
			saveResource("arenas.yml", false);
		}
		if (ymlFile.exists() && ymlConfig != null) {
			try {
				ymlConfig.save(ymlFile);
			} catch (IOException e) {
				return;

			}
		}
	}

	public void initCustomYml() {
		dataConfig = YamlConfiguration.loadConfiguration(dataYml);
		signsConfig = YamlConfiguration.loadConfiguration(signsYml);
		arenasConfig = YamlConfiguration.loadConfiguration(arenasYml);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {

		getLogger().log(Level.INFO, "---------------------------");
		getLogger().log(Level.INFO, "Successfully enabled SurvivalPvp(v" + getDescription().getVersion() + ")");
		getLogger().log(Level.INFO, "Report any bugs that you find! :)");
		getLogger().log(Level.INFO, " ");
		getLogger().log(Level.INFO, "Made by MehBoss on SpigotMC!");
		getLogger().log(Level.INFO, "---------------------------");

		saveDefaultConfig();
		reloadConfig();

		saveCustomYml(signsConfig, signsYml);
		saveCustomYml(dataConfig, dataYml);
		saveCustomYml(arenasConfig, arenasYml);
		initCustomYml();

		Bukkit.getPluginManager().registerEvents(new SetupSign(new Checks(this), this), this);
		Bukkit.getPluginManager().registerEvents(new Listeners(this, new Checks(this)), this);
		getCommand("s1v1").setExecutor(new Commands(new Checks(this), this));

		loadLobbies();

		if (getConfig().get("Players") != null) {
			for (String p : dataConfig.getStringList("Players")) {
				Player pl = Bukkit.getPlayer(UUID.fromString(p));
				quit.add(pl);
			}
		}

		if (signsConfig.get("Signs") != null) {
			Object signsList = signsConfig.get("Signs");

			if (signsList instanceof ArrayList<?>) {
				signloc = (ArrayList<Location>) signsList;
			}
		}
	}

	@Override
	public void onDisable() {

		getLogger().log(Level.INFO, "---------------------------");
		getLogger().log(Level.INFO, "Saving arenas..");
		getLogger().log(Level.INFO, "Saving playerdata..");
		getLogger().log(Level.INFO, "Saving signdata..");
		getLogger().log(Level.INFO, "FINISHED!");
		getLogger().log(Level.INFO, "---------------------------");

		if (!quit.isEmpty()) {
			List<String> list = new ArrayList<>();

			for (Player pl : quit) {
				list.add(pl.getUniqueId().toString());
			}
			dataConfig.set("Players", list);
		}

		ArrayList<Location> newsign = new ArrayList<>();

		if (!signloc.isEmpty()) {
			signsConfig.set("Signs", signloc);

			for (Location loc : signloc) {
				if (loc == null || loc.getBlock() == null || !signMatch(loc.getBlock())) {
					continue;
				}

				newsign.add(loc);
				Sign sign = (Sign) loc.getBlock().getState();

				sign.setLine(2, "Queue: 0");
				sign.setLine(3, "Status: EMPTY");
				sign.update();
			}
		}
		
		signsConfig.set("Signs", newsign);
		saveCustomYml(dataConfig, dataYml);
		saveCustomYml(signsConfig, signsYml);
		saveCustomYml(arenasConfig, arenasYml);
		initCustomYml();
	}

	public boolean signMatch(Block b) {
		if (b.getType() == XMaterial.matchXMaterial("OAK_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("SPRUCE_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("BIRCH_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("JUNGLE_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("ACACIA_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("DARK_OAK_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("MANGROVE_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("OAK_WALL_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("SPRUCE_WALL_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("BIRCH_WALL_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("JUNGLE_WALL_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("ACACIA_WALL_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("DARK_OAK_WALL_SIGN").get().parseMaterial()
				|| b.getType() == XMaterial.matchXMaterial("MANGROVE_WALL_SIGN").get().parseMaterial())
			return true;

		return false;
	}

	public void loadLobbies() {
		for (String lobby : arenasConfig.getConfigurationSection("Arenas").getKeys(false)) {

			Arena arena = new Arena(lobby);
			arena.setState(ArenaState.EMPTY);

			if (arenasConfig.getString("Arenas." + lobby + ".Spawn-1") != null) {
				Location loc = (Location) arenasConfig.get("Arenas." + lobby + ".Spawn-1");
				arena.setLocation1(loc);
			}

			if (arenasConfig.getString("Arenas." + lobby + ".Spawn-2") != null) {
				Location loc = (Location) arenasConfig.get("Arenas." + lobby + ".Spawn-2");
				arena.setLocation2(loc);
			}

			arenas.put(lobby, arena);
		}
	}
}

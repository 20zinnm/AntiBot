package com.superspytx.ab;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.superspytx.ab.Metrics.Graph;
import com.superspytx.ab.abs.CommandEvent;
import com.superspytx.ab.callunits.CallUnit;
import com.superspytx.ab.handlers.Handlers;
import com.superspytx.ab.settings.Lang;
import com.superspytx.ab.settings.Permissions;
import com.superspytx.ab.settings.Settings;
import com.superspytx.ab.settings.SettingsCore;
import com.superspytx.ab.tils.GeoTils;
import com.superspytx.ab.workflow.Agent;
import com.superspytx.ab.workflow.GD;

public class AntiBot extends JavaPlugin {
	private static AntiBot instance;
	private static SettingsCore settingscore;
	private static String version;
	private static int buildnum = 0;
	private static boolean development;
	private Updates updates;
	
	public static YamlConfiguration LANG;
	public static File LANG_FILE;
	
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(this);
	}
	
	public void onEnable() {
		instance = this;
		loadLang();
		/* Make plugin directory if it doesn't exist.*
		 * This should fix the GeoIP problem */
		if (!getDataFolder().exists()) getDataFolder().mkdir();
		
		/* Initialize GeoIP utilities */
		GeoTils.initialize();
		
		/* Configuration */
		settingscore = new SettingsCore();
		settingscore.loadDefaults();
		settingscore.loadSettings();
		
		/* Events */
		(new CallUnit()).registerUnits();
		
		
		/* Register players on server */
		for (Player pl : this.getServer().getOnlinePlayers()) {
			GD.getPI(pl).ab_alreadyin = true;
		}
		
	
		
		/* Let's get our metric sticks and slap Metrics until it loads */
		Metrics metrics = null;
		try {
			metrics = new Metrics(this);
			
			Graph graph = metrics.createGraph("Bot Blocking Data");
			
			graph.addPlotter(new Metrics.Plotter("Bots Blocked") {
				
				@Override
				public int getValue() {
					return GD.b_blks;
				}
				
			});
			
			graph.addPlotter(new Metrics.Plotter("Chat Spam Blocked") {
				
				@Override
				public int getValue() {
					return GD.cs_spams;
				}
				
			});
			
			graph.addPlotter(new Metrics.Plotter("Chat Overflows") {
				
				@Override
				public int getValue() {
					return GD.cf_ovfl;
				}
				
			});
			
			graph.addPlotter(new Metrics.Plotter("Captcha Triggers") {
				
				@Override
				public int getValue() {
					return GD.cp_caps;
				}
				
			});
			
			graph.addPlotter(new Metrics.Plotter("Country Users Blocked") {
				
				@Override
				public int getValue() {
					return GD.cb_invs;
				}
				
			});
			
			metrics.start();
			
		} catch (IOException e) {
			AB.log("Metrics haz failed.");
		}
		
		/* Delayed Start */
		if (Settings.delayedStart && Settings.enabled) {
			Settings.enabled = false;
			Settings.delayingStart = true;
			
			getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
				
				public void run() {
					AB.log("System has been enabled!");
					for (Player pl : Bukkit.getOnlinePlayers()) {
						if (Permissions.ADMIN_NOTIFY.getPermission(pl)) pl.sendMessage(Lang.PREFIX.toString() + ChatColor.GREEN + "System has been enabled!");
					}
					Settings.enabled = true;
					Settings.delayedStart = false;
					Settings.delayingStart = false;
				}
			}, Settings.startdelay * 20L);
			AB.log("System is now having a delayed start!");
		}
		
		/* Setup GD update task */
		Bukkit.getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
			public void run() {
				GD.updateTask();
			}
		}, 1200L, 1200L);
		
		/* Setup updates task */
		updates = new Updates();
		
		if (Settings.checkupdates) {
			getServer().getScheduler().runTaskTimerAsynchronously(this, new Runnable() {
				
				public void run() {
					if (Settings.checkupdates) {
						updates.run();
					}
				}
			}, 140L, 12000L);
		}
		
		/* Almost finished */
		PluginDescriptionFile pdfFile = getDescription();
		version = pdfFile.getVersion().replace("${BUILD_NUMBER}", "0");
		development = (version.contains("-SNAPSHOT"));
		
		if (development) {
			getLogger().info("This is a development version of AntiBot and not a official release.  Please be careful.  Please report bugs as you find them.");
			buildnum = Integer.parseInt(version.split("-")[2].replace("b", ""));
		} else {
			getLogger().info("If you have any issues with AntiBot.  Or there is a false positive! Don't be afraid to make a ticket!");
		}
		
		/* Now all finished */
		getLogger().info("AntiBot v" + version + " (Notorious UnLeet & deRobert Edition) enabled!");
	}
	
	public static AntiBot getInstance() {
		return instance;
	}
	
	public static String getVersion() {
		return version;
	}
	
	public static String getRealVersion() {
		if (version.contains("-")) { return version.split("-")[0]; }
		return version;
	}
	
	public static boolean isDevelopment() {
		return development;
	}
	
	public static int getBuildNumber() {
		return buildnum;
	}
	
	public static SettingsCore getSettingsCore() {
		return settingscore;
	}
	
	public static void reload() {
		/* Cancel Tasks */
		Bukkit.getScheduler().cancelTasks(AB.getInstance());
		
		/* Reset GD (Global Data) */
		GD.reset();
		
		/* Register players on server */
		for (Player pl : AB.getInstance().getServer().getOnlinePlayers()) {
			GD.getPI(pl).ab_alreadyin = true;
		}
		
		// maybe reload lang?
		
	}
	
	public static void debug(String e) {
		if (Settings.debugmode) {
			getInstance().getLogger().info("Debug: " + e);
			
			for (Player pl : Bukkit.getOnlinePlayers()) {
				if (Permissions.ADMIN_DEBUG.getPermission(pl)) pl.sendMessage("[AntiBot] Debug: " + e);
			}
		}
	}
	
	public static void log(String e) {
		getInstance().getLogger().info(e);
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().startsWith("ab") || cmd.getName().startsWith("antibot")) return Agent.dispatchUnit(new CommandEvent(sender, cmd, label, args), Handlers.COMMAND, true);
		
		return false;
	}
	
	/**
	 * Load the lang.yml file.
	 * @return The lang.yml config.
	 */
	public void loadLang() {
	    File lang = new File(getDataFolder(), "language.yml");
	    if (!lang.exists()) {
	        try {
	            getDataFolder().mkdir();
	            lang.createNewFile();
	            InputStream defConfigStream = this.getResource("language.yml");
	            if (defConfigStream != null) {
	                @SuppressWarnings("deprecation")
					YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
	                defConfig.save(lang);
	                Lang.setFile(defConfig);
	                return;
	            }
	        } catch(IOException e) {
	            e.printStackTrace(); // So they notice
	            getLogger().severe("[PluginName] Couldn't create language file.");
	            getLogger().severe("[PluginName] This is a fatal error. Now disabling");
	            Bukkit.getPluginManager().disablePlugin(this);
	        }
	    }
	    YamlConfiguration conf = YamlConfiguration.loadConfiguration(lang);
	    for(Lang item:Lang.values()) {
	        if (conf.getString(item.getPath()) == null) {
	            conf.set(item.getPath(), item.getDefault());
	        }
	    }
	    Lang.setFile(conf);
	    AntiBot.LANG = conf;
	    AntiBot.LANG_FILE = lang;
	    try {
	        conf.save(getLangFile());
	    } catch(IOException e) {
	        getLogger().log(Level.WARNING, "PluginName: Failed to save lang.yml.");
	        getLogger().log(Level.WARNING, "PluginName: Report this stack trace to <your name>.");
	        e.printStackTrace();
	        Bukkit.getPluginManager().disablePlugin(this);
	    }
	}
	
	/**
	* Gets the lang.yml config.
	* @return The lang.yml config.
	*/
	public YamlConfiguration getLang() {
	    return LANG;
	}
	 
	/**
	* Get the lang.yml file.
	* @return The lang.yml file.
	*/
	public File getLangFile() {
	    return LANG_FILE;
	}
	
}

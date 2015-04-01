package com.superspytx.ab.handlers.chat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.superspytx.ab.AB;
import com.superspytx.ab.abs.EventAction;
import com.superspytx.ab.abs.Handler;
import com.superspytx.ab.settings.Lang;
import com.superspytx.ab.settings.Permissions;
import com.superspytx.ab.settings.Settings;
import com.superspytx.ab.tils.Tils;
import com.superspytx.ab.workflow.GD;

public class CommandHandler implements Handler {
	
	@Override
	public boolean run(EventAction info) {
		CommandSender sender = info.sender;
		Command cmd = info.cmd;
		String label = info.label;
		String[] args = info.args;
		
		if (sender == null || cmd == null || label == null || args == null) return false;
		
		Player player = null;
		try {
			player = (Player) sender;
		} catch (Exception e) {
			// console!
		}
		
		if (args.length < 1) {
			returnMotd(sender);
			return true;
		}
		
		if (player != null) {
			if (!Permissions.ADMIN.getPermission(player, sender)) { return true; }
		}
		
		if (args[0].compareToIgnoreCase("help") == 0) {
			sender.sendMessage(Lang.PREFIX.toString() + "AntiBot Help:");
			sender.sendMessage(Lang.PREFIX.toString() + "");
			sender.sendMessage(Lang.PREFIX.toString() + "/antibot help - Help Menu");
			if (Permissions.ADMIN_RELOAD.getPermission(player)) {
				sender.sendMessage(Lang.PREFIX.toString() + "/antibot reload - Reload configuration");
			}
			if (Permissions.ADMIN_INFO.getPermission(player)) {
				sender.sendMessage(Lang.PREFIX.toString() + "/antibot info - Check current status of AntiBot.");
			}
			if (Permissions.ADMIN_CHATMUTE.getPermission(player)) {
				sender.sendMessage(Lang.PREFIX.toString() + "/antibot chatmute - Toggle chat flow's global chat mute.");
			}
			if (Permissions.ADMIN_FLUSH.getPermission(player)) {
				sender.sendMessage(Lang.PREFIX.toString() + "/antibot flush - Flushes all the data.");
			}
			if (Permissions.ADMIN_TOGGLE.getPermission(player)) {
				sender.sendMessage(Lang.PREFIX.toString() + "/antibot off - Turn off AntiBot.");
				sender.sendMessage(Lang.PREFIX.toString() + "/antibot on - Turn on AntiBot.");
			}
			sender.sendMessage(Lang.PREFIX.toString() + "/antibot version - Check this version of AntiBot.");
			return true;
		}
		if (args[0].compareToIgnoreCase("reload") == 0) {
			if (Permissions.ADMIN_RELOAD.getPermission(player, sender)) {
				if (AB.getSettingsCore().loadSettings()) {
					sender.sendMessage(Lang.PREFIX.toString() + ChatColor.GREEN + "Reloaded configuration successfully!");
				} else {
					sender.sendMessage(Lang.PREFIX.toString() + ChatColor.RED + "Configuration failed to reload.");
				}
			}
			return true;
		}
		
		if (args[0].compareToIgnoreCase("chatmute") == 0) {
			if (Permissions.ADMIN_CHATMUTE.getPermission(player, sender)) {
				boolean b = !GD.cf_gm;
				try {
					b = (args[0].equalsIgnoreCase("on") ? true : (args[0].equalsIgnoreCase("off") ? false : !GD.cf_gm));
				} catch (Exception shutup) {}
				
				GD.cf_gm = b;
				if (Settings.notify && Settings.enabled) {
					if (b) AB.getInstance().getServer().broadcastMessage(Lang.PREFIX.toString() + ChatColor.DARK_AQUA + Lang.OVERFLOWED.toString().replace("%s", "infinity, and beyond"));
					if (!b) AB.getInstance().getServer().broadcastMessage(Lang.PREFIX.toString() + ChatColor.GREEN + "Chat has been unmuted by " + sender.getName() + "!");
					
					GD.cf_cts = 0;
				}
			}
			
			return true;
		}
		
		if (args[0].compareToIgnoreCase("on") == 0) {
			if (Permissions.ADMIN_TOGGLE.getPermission(player, sender)) {
				if (Settings.enabled) {
					sender.sendMessage(Lang.PREFIX.toString() + "The system is already enabled!");
				} else {
					Settings.enabled = true;
					sender.sendMessage(Lang.PREFIX.toString() + ChatColor.GREEN + "System has been enabled!");
				}
				
			}
			return true;
			// Reload here.
		}
		
		if (args[0].compareToIgnoreCase("off") == 0) {
			if (Permissions.ADMIN_TOGGLE.getPermission(player, sender)) {
				if (!Settings.enabled) {
					sender.sendMessage(Lang.PREFIX.toString() + "The system is already disabled!");
				} else {
					Settings.enabled = false;
					sender.sendMessage(Lang.PREFIX.toString() + ChatColor.RED + "System has been disabled!");
				}
				
				if (GD.cf_gm) sender.sendMessage(Lang.PREFIX.toString() + ChatColor.DARK_RED + "ERROR: You have left the server chat muted! Nobody is able to talk.");
			}
			return true;
			// Reload here.
		}
		if (args[0].compareToIgnoreCase("info") == 0) {
			if (Permissions.ADMIN_INFO.getPermission(player, sender)) {
				sender.sendMessage(Lang.PREFIX.toString() + "AntiBot System Info:");
				sender.sendMessage(Lang.PREFIX.toString() + "");
				sender.sendMessage(Lang.PREFIX.toString() + "Secs between last login: " + Tils.getLongDiff(GD.b_lc));
				sender.sendMessage(Lang.PREFIX.toString() + "Current Intervals: " + Settings.interval);
				sender.sendMessage(Lang.PREFIX.toString() + "Logged in: " + GD.b_cp.size());
				sender.sendMessage(Lang.PREFIX.toString() + "# of Accounts: " + Settings.accounts);
			}
			return true;
		}
		
		if (args[0].compareToIgnoreCase("flush") == 0) {
			if (Permissions.ADMIN_FLUSH.getPermission(player, sender)) {
				AB.reload();
				sender.sendMessage(Lang.PREFIX.toString() + ChatColor.GREEN + "Flushed data successfully!");
			}
			return true;
		}
		
		if (args[0].compareToIgnoreCase("version") == 0) {
			returnMotd(sender);
			return true;
		}
		sender.sendMessage(Lang.PREFIX.toString() + ChatColor.RED + "Unknown system command.");
		return false;
	}
	
	@Override
	public void performActions(EventAction info) {
		
	}
	
	public String returnStatus() {
		if (!Settings.enabled) {
			if (Settings.delayedStart) {
				return ChatColor.RED + "Disabled" + ChatColor.WHITE + " (" + ChatColor.YELLOW + "Delayed Start" + ChatColor.WHITE + ")";
			} else {
				return ChatColor.RED + "Disabled";
			}
		} else {
			return ChatColor.GREEN + "Enabled";
		}
	}
	
	public void returnMotd(CommandSender sender) {
		Date localdate = new Date(Settings.installdate);
		SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
		sender.sendMessage(Lang.PREFIX.toString() + "AntiBot " + AB.getVersion() + " - By .SuPaH sPii");
		sender.sendMessage(Lang.PREFIX.toString() + "Inspired by Wolflink289 <3");
		sender.sendMessage(Lang.PREFIX.toString() + "Continued inspiration by Evenprime & Fafaffy <3");
		sender.sendMessage(Lang.PREFIX.toString() + "Recontinuted interest by H31IX & horde of users affected by Chat Spam in #anticheat");
		// No Narcissism but... :3
		sender.sendMessage(Lang.PREFIX.toString() + "Language file implementation and 1.8.3 update by AniSkywalker (20zinnm) <3");
		
		// return status if player has admin permissions.
		Player player = null;
		try {
			player = (Player) sender;
		} catch (Exception e) {
			// console!
		}
		if (Permissions.ADMIN_NOTIFY.getPermission(player)) {
			sender.sendMessage(Lang.PREFIX.toString() + "System Status: " + returnStatus());
		}
		Random random = new Random();
		switch (random.nextInt(20)) {
			case 0:
				sender.sendMessage(Lang.PREFIX.toString() + "System Installed on " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 1:
				sender.sendMessage(Lang.PREFIX.toString() + "Keeping PWN4G3 & Paradigm out of the game since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 2:
				sender.sendMessage(Lang.PREFIX.toString() + "Combatting spam since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 3:
				sender.sendMessage(Lang.PREFIX.toString() + "Supporting PWN4G3 & Paradigm Bots since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 4:
				sender.sendMessage(Lang.PREFIX.toString() + "Running PWN4G3 & Paradigm Bots to the void since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 5:
				sender.sendMessage(Lang.PREFIX.toString() + "Making people mad since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 6:
				sender.sendMessage(Lang.PREFIX.toString() + "Trolling spammers since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 7:
				sender.sendMessage(Lang.PREFIX.toString() + "Supporting Wolflink289's idea since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 8:
				sender.sendMessage(Lang.PREFIX.toString() + "Protecting this server since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 9:
				sender.sendMessage(Lang.PREFIX.toString() + "All lights turned green since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 10:
				sender.sendMessage(Lang.PREFIX.toString() + "Corrupting ability to spam since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 11:
				sender.sendMessage(Lang.PREFIX.toString() + "Minecraft PWN4G3 & Paradigm dun goof'd on " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 12:
				sender.sendMessage(Lang.PREFIX.toString() + "Making .SuPaH sPii proud since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 13:
				sender.sendMessage(Lang.PREFIX.toString() + "Injected the Vaccine on " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 14:
				sender.sendMessage(Lang.PREFIX.toString() + "Giving AIDS to spammers since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 15:
				sender.sendMessage(Lang.PREFIX.toString() + "Turning spammer users to WTF faces since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 16:
				sender.sendMessage(Lang.PREFIX.toString() + "Chinese secret happened on " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 17:
				sender.sendMessage(Lang.PREFIX.toString() + "Been in Slim Shady's world since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 18:
				sender.sendMessage(Lang.PREFIX.toString() + "Making other communities jelly since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 19:
				sender.sendMessage(Lang.PREFIX.toString() + "Didn't have to buy anything since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			case 20:
				sender.sendMessage(Lang.PREFIX.toString() + "Making Jogn shit his pants since " + ChatColor.GREEN + sdf.format(localdate));
				break;
			default:
				sender.sendMessage(Lang.PREFIX.toString() + "System Installed on " + ChatColor.GREEN + sdf.format(localdate));
				break;
		}
	}
	
}

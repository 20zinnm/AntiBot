package me.freebuild.superspytx.ab.settings;

import me.freebuild.superspytx.ab.AntiBot;

public class Settings {
	public static AntiBot core = null;
	public static int interval = 5000; // Bot: Interval
	public static int accounts = 4;
	public static int spamam = 4;
	public static int spamtime = 2500;
	public static int spamdiffct = 3;
	public static int connectFor = 30000;
	public static int timetooverflow = 5000;
	public static int overflows = 5;
	public static int captchaAttempts = 3;
	public static long startdelay = 60L;
	public static long loginDelay = (long) 10000L;
	public static long loginHold = (long) 10000L;
	public static long installdate = 0L;
	public static boolean delayingStart = false;
	public static boolean flowEnabled = true;
	public static boolean notify = true;
	public static boolean useWhiteListPerms = false;
	public static boolean useOpPerms = false;
	public static boolean enabled = true;
	public static boolean debugmode = false; // TODO: set to false!
	public static boolean whiteList = false;
	public static boolean enableAntiSpam = true;
	public static boolean enableMultiAccs = false;
	public static boolean banUsers = false;
	public static boolean geoIP = true;
	public static boolean whiteListCountry = false;
	public static boolean logindelayEnabled = true;
	public static boolean checkupdates = true;
	public static boolean delayedStart = true;
	public static boolean captchaEnabled = true;
	public static boolean forceCaptchaOnJoin = false;
	public static boolean forceCaptchaOnChatSpam = true;
	public static boolean forceCaptchaOnChatFlow = true;
	public static boolean forceCaptchaOnMultiAcc = true;
	public static boolean forceCaptchaOnBotSpam = true;
	public static String kickMsg = "[AntiBot] Tripwired protection!";
	public static String captchafail = "[AntiBot] CAPTCHA Failed!";
	public static String countryBanMsg = "Your country is banned from this server!";
	public static String connectMsg = "You are not on the whitelist!";
	public static String connectInvasion = "The server is currently under attack.";
	public static String overflowedmessage = "SILENCE! Let's cool it down for %sec% seconds.";
	public static String loginDelayMsg = "You logged in too quickly! Please wait &sec& second(s).";
	public static String prefix = "\247f[\247bAntiBot\247f] ";
	private static boolean setup = false;
	
	public static boolean isSet() {
		return setup;
	}
	
	public static void setup() {
		setup = true;
	}
}

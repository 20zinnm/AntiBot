package com.superspytx.ab.settings;
 
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;
 
/**
* An enum for requesting strings from the language file.
* @author gomeow
*/
public enum Lang {
    PREFIX("prefix", "&f[&bAntiBot&f] "),
    KICKMSG("kick-msg", "&cTripwired protection!"),
    ADMINDSNOTIFY("admin-delayedstart-notify", "&cAntiBot is currently under delayed start. Chat Spam is not protected yet."),
    ADMINNVNOTIFY("admin-newversion-notify", "&cAntiBot has a new version available! New version: %nv, your version: %yv"),
    ADMINNBNOTIFY("admin-newdevbuild-notify", "&cAntiBot has a new development build available! New build: %nb, your build: %yb"),
    COUNTRYBAN("country-ban", "&cYour country is banned from this server!"),
    OVERFLOWED("overflowed-message", "&cSILENCE! Let's cool it down for %s seconds."),
    CAPTCHAKICK("captcha-kick", "&cCAPTCHA Failed!"),
    CAPONELEFT("captcha-one-left", "&cOne attempt left!"),
    CAPATTEMPTSLEFT("captcha-attempts-left", "You have %a attempts left!"),
    LOGINDELAY("login-delay", "You logged in too quickly! Please wait %s second(s)."),
    CAPWRONG("captcha-wrong", "&cIncorrect captcha! You have %w wrong."),
    CAPRIGHT("captcha-correct", "&aCorrect! Thanks for not being a bot. You can now speak again."),
    CHATUNMUTED("chat-unmuted", "&aChat has been unmuted."),
    NO_PERMS("no-permissions", "&cYou don't have permission for that!");
 
    private String path;
    private String def;
    private static YamlConfiguration LANG;
 
    /**
    * Lang enum constructor.
    * @param path The string path.
    * @param start The default string.
    */
    Lang(String path, String start) {
        this.path = path;
        this.def = start;
    }
 
    /**
    * Set the {@code YamlConfiguration} to use.
    * @param config The config to set.
    */
    public static void setFile(YamlConfiguration config) {
        LANG = config;
    }
 
    @Override
    public String toString() {
        if (this == PREFIX)
            return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def)) + " ";
        return ChatColor.translateAlternateColorCodes('&', LANG.getString(this.path, def));
    }
 
    /**
    * Get the default value of the path.
    * @return The default value of the path.
    */
    public String getDefault() {
        return this.def;
    }
 
    /**
    * Get the path to the string.
    * @return The path to the string.
    */
    public String getPath() {
        return this.path;
    }
}
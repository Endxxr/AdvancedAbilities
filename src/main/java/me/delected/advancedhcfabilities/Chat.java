package me.delected.advancedhcfabilities;

import org.bukkit.ChatColor;
import org.bukkit.configuration.Configuration;

public class Chat {
    Configuration config = AdvancedHCFAbilities.plugin().getConfig();

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    public static int validateLevel(int i) {
        if (i < 1 || i > 255) return 0;
        return i - 1;
    }
}

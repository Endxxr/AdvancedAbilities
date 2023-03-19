package me.delected.advancedabilities.utils;

import org.bukkit.ChatColor;

public class ChatUtils {

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String parseTime(long seconds) {
        long minutes = seconds / 60;
        long remainder = seconds % 60;
        String finalString;
        if (minutes >= 1) {
            finalString =  minutes+"m "+remainder+"s";
        } else {
            finalString = seconds+"s";
        }

        return finalString;
    }

}

package me.delected.advancedabilities.api;

import net.md_5.bungee.api.ChatColor;

import java.util.List;

public class ChatUtils {

    public static String colorize(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }


    public static String colorize(String string, String... placeholders) {

        if (string == null) return null;

        for (int i = 0; i < placeholders.length; i += 2) {
            string = string.replace(placeholders[i], placeholders[i + 1]);
        }

        return colorize(string);

    }

    public static List<String> colorize(List<String> strings, String... placeholders) {
        strings.replaceAll(s -> colorize(s, placeholders));
        return strings;
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


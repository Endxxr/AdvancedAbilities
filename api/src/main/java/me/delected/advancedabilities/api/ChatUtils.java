package me.delected.advancedabilities.api;

import net.md_5.bungee.api.ChatColor;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&(#[a-fA-F0-9]{6})");

    public static String colorize(String string) {
        string = ChatColor.translateAlternateColorCodes('&', string);
        return translateHexColors(string);
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


    private static String translateHexColors(String string) {
        Matcher matcher = HEX_PATTERN.matcher(string);

        while (matcher.find()) {
            String color = matcher.group(0); // &#FFFFFF
            StringBuilder replacement = new StringBuilder();
            for (char character : color.substring(1).toCharArray()) { //#FFFFFF
                if (character == '#') character = 'x';
                replacement.append(ChatColor.COLOR_CHAR).append(character);
            }
            string = string.replace(color, replacement.toString());
        }
        return string;
    }

}


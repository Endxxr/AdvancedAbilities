package me.delected.advancedabilities.commands;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.AdvancedAbilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

import java.util.Arrays;
import java.util.List;

public class AbilityCommand implements CommandExecutor, TabExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length==1) {

            if (args[0].equalsIgnoreCase("reload")) {
                if (!sender.hasPermission("advancedabilities.reload")) {
                    sender.sendMessage(ChatUtils.colorize("&cYou do not have permission to do this!"));
                    return true;
                }
                sender.sendMessage(ChatUtils.colorize("&eReloading..."));
                AdvancedAbilities.getPlugin().reloadPlugin();
                sender.sendMessage(ChatUtils.colorize("&eReloaded!"));
                return true;

            } else if (args[0].equalsIgnoreCase("clearcooldowns")) {

                if (!sender.hasPermission("advancedabilities.clearcooldowns")) {
                    sender.sendMessage(ChatUtils.colorize("&cYou do not have permission to do this!"));
                    return true;
                }

                AdvancedAbilities.getPlugin().getAbilityManager().clearCooldowns();
                sender.sendMessage(ChatUtils.colorize("&eCleared all cooldowns!"));
                return true;

            }
        }

        sender.sendMessage(ChatUtils.colorize("&6&lAdvancedAbilities &7by Delected_ & Endxxr"));
        if (sender.hasPermission("advancedabilities.admin")) {
            sender.sendMessage(ChatUtils.colorize("&e&lHELP MENU"));
            sender.sendMessage(ChatUtils.colorize(""));
            sender.sendMessage(ChatUtils.colorize("&e/ability &7- &fShows this menu"));
            sender.sendMessage(ChatUtils.colorize("&e/ability &6[reload|clearcooldowns] &7- &fReloads the plugin"));
            sender.sendMessage(ChatUtils.colorize("&e/getability &7- &fGets an ability"));
        }




        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length==1) {
            return Arrays.asList("reload", "clearcooldowns");
        }

        return null;
    }
}

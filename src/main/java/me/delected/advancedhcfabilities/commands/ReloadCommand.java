package me.delected.advancedhcfabilities.commands;

import me.delected.advancedhcfabilities.AdvancedHCFAbilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public class ReloadCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("hcfabils.reload") && !(sender instanceof ConsoleCommandSender)) return true;
        AdvancedHCFAbilities.plugin().saveDefaultConfig();
        AdvancedHCFAbilities.plugin().reloadConfig();
        sender.sendMessage("config reloaded!");
        return true;
    }
}

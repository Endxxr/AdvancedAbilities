package me.delected.advancedhcfabilities.commands;

import me.delected.advancedhcfabilities.AdvancedHCFAbilities;
import me.delected.advancedhcfabilities.ability.abilities.Ability;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemCommand implements CommandExecutor {

    Configuration config = AdvancedHCFAbilities.plugin().getConfig();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender.hasPermission("hcfabils.getitem"))) {
            ((Player) sender).sendMessage(ChatColor.RED + "You do not have permission to use this command!");
            return true;
        }

        if (args.length != 2 && args.length != 3) {
            error(sender);
            return true;
        }


        if (Bukkit.getPlayerExact(args[0]) == null) {
            sender.sendMessage("The player specified is not online, or does not exist!");
            return true;
        }
        int amount = 0;
        try {amount = args.length == 2 ? 1 : Integer.parseInt(args[2]); } 
        catch (NumberFormatException nfe) {
            sender.sendMessage("The ");
        }

        Ability abil = Ability.getFromString(args[1]);

        if (abil == null) {
            sender.sendMessage("This item does not exist!");
            return true;
        }

        giveItem(Bukkit.getPlayerExact(args[0]), abil, amount);


        return true;

    }

    private void error(CommandSender sender) {
        sender.sendMessage("Invalid command structure! Correct usage: /<command> [player's name] [item] (optional: item amount, default to 1)");
    }

    private void giveItem(Player p, Ability ability, int amount) {

        for (int i = 0; i < amount; i++) {
            p.getInventory().addItem(ability.item());
        }
    }
}

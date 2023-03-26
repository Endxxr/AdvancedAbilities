package me.delected.advancedabilities.commands;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ability.Ability;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class GetAbilityCommand implements CommandExecutor, TabExecutor {

    private final AdvancedAbilities instance = AdvancedAbilities.getPlugin();

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!sender.hasPermission("advancedabilities.get")) {
            String message = instance.getConfig().getString("messages.no-permission");
            sender.sendMessage(ChatUtils.colorize(message));
            return true;
        }

        if (!(args.length==3)) {
            sender.sendMessage(ChatUtils.colorize(instance.getConfig().getString("commands.give.usage")));
            return true;
        }

        Player player = Bukkit.getPlayer(args[0]);
        Ability ability = instance.getAbilityManager().getAbilityByName(args[1]);
        int amount;

        try {
            amount = Integer.parseInt(args[2]);
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatUtils.colorize(instance.getConfig().getString("commands.give.invalid")));
            return true;
        }

        if (player == null) {
            sender.sendMessage(ChatUtils.colorize(instance.getConfig().getString("commands.give.offline")));
            return true;
        }

        if (ability == null) {
            sender.sendMessage(ChatUtils.colorize(instance.getConfig().getString("commands.give.invalid")));
            return true;
        }

        if (amount < 0) {
            sender.sendMessage(ChatUtils.colorize(instance.getConfig().getString("commands.give.invalid")));
            return true;
        }

        final ItemStack itemStack = ability.getItem().clone();
        itemStack.setAmount(amount);
        player.getInventory().addItem(itemStack);
        sender.sendMessage(ChatUtils.colorize(instance.getConfig().getString("commands.give.message")
                        .replaceAll("%player%", player.getDisplayName()))
                .replaceAll("%amount%", String.valueOf(amount))
                .replaceAll("%item%", itemStack.getItemMeta().getDisplayName()));
        return true;
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {

        if (args.length==1) {
            List<String> temp = new ArrayList<>();
            Bukkit.getOnlinePlayers().iterator().forEachRemaining(player -> {
                if (player.getName().startsWith(args[0])) {
                    temp.add(player.getName());
                }

            });
            return temp;
        }

        if (args.length==2) {
            List<String> temp = new ArrayList<>();
            AdvancedAbilities.getPlugin().getAbilityManager().getAbilities().iterator().forEachRemaining(ability -> {
                if (ability.getId().startsWith(args[1])) {
                    temp.add(ability.getId());
                }
            });
            return temp;
        }

        if (args.length==3) {
            List<String> temp = new ArrayList<>();
            for (int i = 1; i <= 64; i++) {
                temp.add(String.valueOf(i));
            }
            return temp;
        }

        return null;
    }
}

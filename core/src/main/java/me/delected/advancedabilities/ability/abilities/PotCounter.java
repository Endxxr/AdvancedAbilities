package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.TargetAbility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

public class PotCounter extends TargetAbility {

    @Override
    public String getId() {
        return "pot-counter";
    }

    @Override
    public boolean removeItem() {
        return false;
    }

    @Override
    public void run(Player player, Player target) {


        Inventory inventory = target.getInventory();
        int count = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) != null) {
                if (inventory.getItem(i).getType().toString().contains("POTION")) {
                    count++;
                }
            }
        }

        player.sendMessage(ChatUtils.colorize(getExecuteMessage().replace("%target%", target.getName()).replace("%count%", String.valueOf(count))));
        target.sendMessage(ChatUtils.colorize(getTargetMessage().replace("%player%", player.getName()).replace("%count%", String.valueOf(count))));

        playSound(player);
        playSound(target);

    }
}

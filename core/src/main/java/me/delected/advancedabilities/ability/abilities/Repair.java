package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.ClickableAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Repair extends ClickableAbility {

    @Override
    public String getId() {
        return "repair";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player) {
        for (ItemStack armor : player.getInventory().getArmorContents()) {
            if (armor == null || armor.getType() == Material.AIR) continue;
            if (isRepairable(armor)) armor.setDurability((short) -armor.getType().getMaxDurability());
        }
        player.updateInventory();
        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        addCooldown(player);
    }

    private boolean isRepairable(ItemStack itemStack) {
        return itemStack != null
                && itemStack.getDurability() != 0
                && (!itemStack.getType().isBlock() || !itemStack.getType().isEdible())
                && itemStack.getType() != Material.AIR
                && itemStack.getType().getMaxDurability() != 0;
    }
}

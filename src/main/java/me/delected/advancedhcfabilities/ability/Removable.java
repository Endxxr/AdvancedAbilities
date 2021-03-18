package me.delected.advancedhcfabilities.ability;

import me.delected.advancedhcfabilities.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Removable {
    default void removeFrom(Player p) {
        ItemStack item = getRemovable();

        for (ItemStack invItem : p.getInventory().getContents()) {
            if (invItem == null) continue;
            if (invItem.getItemMeta() == null || invItem.getItemMeta().getDisplayName() == null) continue;
            if (!invItem.getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) continue;
            if (invItem.getAmount() != 1) invItem.setAmount(invItem.getAmount() - 1);
            else p.getInventory().setItem(p.getInventory().getHeldItemSlot(), new ItemStack(Material.AIR));
            break;
        }
    }

    ItemStack getRemovable();
}

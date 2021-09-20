package me.delected.advancedhcfabilities.ability;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public interface Removable {
    default void removeFrom(Player p) {
        ItemStack item = getRemovable();

        ItemStack[] invItem = p.getInventory().getContents();

        // ugly loop brought to you by the saviour ability!
        // also, i don't know what a variable is apparently
        for (int i = 0 ; i < p.getInventory().getSize(); i++) {
            if (invItem[i] == null) continue;
            if (invItem[i].getItemMeta() == null || invItem[i].getItemMeta().getDisplayName() == null) continue;
            if (!invItem[i].getItemMeta().getDisplayName().equalsIgnoreCase(item.getItemMeta().getDisplayName())) continue;
            if (invItem[i].getAmount() != 1) {
                p.sendMessage("set amount to 1 less. itemstack: " + invItem[i]);
                invItem[i].setAmount(invItem[i].getAmount() - 1);
            }

            else {
                p.sendMessage("cleared at i. itemstack: " + invItem[i]);
                p.getInventory().clear(i);
            }
            break;
        }
    }

    ItemStack getRemovable();
}

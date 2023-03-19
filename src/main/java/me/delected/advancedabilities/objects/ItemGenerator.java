package me.delected.advancedabilities.objects;

import me.delected.advancedabilities.objects.ability.Ability;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ItemGenerator {

    ItemStack createItem(Ability ability);
    HashMap<String, Material> getForcedItems();
}

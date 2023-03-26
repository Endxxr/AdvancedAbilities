package me.delected.advancedabilities.api.objects;

import me.delected.advancedabilities.api.ability.Ability;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

public interface ItemGenerator {

    ItemStack createItem(Ability ability);
    HashMap<String, Material> getForcedItems();
}

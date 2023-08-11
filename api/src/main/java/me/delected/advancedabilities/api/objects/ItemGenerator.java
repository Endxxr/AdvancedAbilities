package me.delected.advancedabilities.api.objects;

import me.delected.advancedabilities.api.objects.ability.Ability;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;

/**
 *
 * An interface for creating the item of the abilities.
 *
 */

public abstract class ItemGenerator {

    public abstract ItemStack createItem(Ability ability);
    public abstract HashMap<String, Material> getForcedItems();
}

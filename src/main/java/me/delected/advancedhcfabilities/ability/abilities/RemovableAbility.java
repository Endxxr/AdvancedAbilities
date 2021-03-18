package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.ability.Removable;
import org.bukkit.inventory.ItemStack;

public abstract class RemovableAbility extends Ability implements Removable {

    public RemovableAbility(String... differentAliases) {
        super(differentAliases);
    }

    @Override
    public ItemStack getRemovable() {
        return item();
    }
}

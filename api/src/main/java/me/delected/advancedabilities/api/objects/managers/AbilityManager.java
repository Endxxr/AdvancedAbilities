package me.delected.advancedabilities.api.objects.managers;

import me.delected.advancedabilities.api.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface AbilityManager {

    Ability getAbilityByItem(ItemStack item);
    Ability getAbilityByName(String name);
    List<Ability> getAbilities();
    void registerAbility(Ability ability);
    void addGlobalCooldown(Player player);
    void clearCooldowns();
    boolean inCooldown(Player player, Ability ability);


}

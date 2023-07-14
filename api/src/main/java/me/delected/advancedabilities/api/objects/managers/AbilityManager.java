package me.delected.advancedabilities.api.objects.managers;

import me.delected.advancedabilities.api.objects.ability.Ability;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

/**
 *
 * Handles the abilities, you can get the abilities, register them, and check if a player is in cooldown.
 *
 */


public interface AbilityManager {

    /**
     *
     * Gets the ability by the item
     *
     * @param item the item
     * @return the ability or null if not found
     */
    Ability getAbilityByItem(ItemStack item);

    /**
     *
     * Gets the ability by the name
     *
     * @param name the name
     * @return the ability or null if not found
     */
    Ability getAbilityByName(String name);
    /**
     *
     * Gets the list of all the registered abilities
     *
     * @return the list of abilities
     */

    List<Ability> getAbilities();


    /**
     * 
     * Registers the ability and its listeners, you can use this to register your own abilities.
     * 
     * @param ability the ability
     *               
     */
    
    void registerAbility(Ability ability);



    /**
     *
     * Adds a player to the global cooldown
     *
     * @param player the player
     */
    void addGlobalCooldown(Player player);

    /**
     *
     * Clears the global cooldowns and ability cooldowns
     *
     */
    void clearCooldowns();
    /**
     *
     * Checks if a player is in cooldown, either global or ability cooldown
     *
     * @param player the player
     * @param ability the ability
     * @return true if the player is in cooldown, false if not
     */
    boolean inCooldown(Player player, Ability ability);

    /**
     * 
     * Check if a player is in spawn
     * 
     * @param player the player
     * @param location the location of the player
     * @return true if the player is in spawn, false if not
     */
    
    boolean inSpawn(Player player, Location location);

    /**
     * 
     * Loads the spawn from the config
     * 
     */
    void setSpawn();
    
}

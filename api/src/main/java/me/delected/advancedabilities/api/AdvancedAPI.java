package me.delected.advancedabilities.api;

import me.delected.advancedabilities.api.objects.ItemGenerator;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import me.delected.advancedabilities.api.objects.managers.RegionChecker;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.logging.Logger;

public interface AdvancedAPI {


    /**
     *
     * Gets the item generator
     *
     * @return the item generator
     */
    ItemGenerator getItemGenerator();

    FileConfiguration getConfig();

    /**
     *
     * Gets the abilities' config (ability.yml)
     *
     * @return the abilities config
     */
    FileConfiguration getAbilitiesConfig();

    /**
     *
     * Get the ability manager
     *
     * @return the ability manager
     */
    AbilityManager getAbilityManager();

    /**
     *
     * Get the region checker
     *
     * @return the region checker
     */

    RegionChecker getRegionChecker();
    boolean isWorldGuardEnabled();

    Logger getLogger();
    Plugin getPlugin();
    String getLatestVersion();
    boolean isUpdateAvailable();
    void runTaskAsync(Runnable runnable);
    void runTaskLater(Runnable runnable, long delay);

}

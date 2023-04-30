package me.delected.advancedabilities.api;

import me.delected.advancedabilities.api.objects.ItemGenerator;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

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
     * Gets the ability manager
     *
     * @return the ability manager
     */
    AbilityManager getAbilityManager();
    Logger getLogger();
    BukkitTask runTaskAsync(Runnable runnable);
    Plugin getPlugin();


}

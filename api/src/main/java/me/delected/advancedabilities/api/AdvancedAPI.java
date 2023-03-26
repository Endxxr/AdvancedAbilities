package me.delected.advancedabilities.api;

import me.delected.advancedabilities.api.objects.ItemGenerator;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitTask;

import java.util.logging.Logger;

public interface AdvancedAPI {


    ItemGenerator getItemGenerator();
    FileConfiguration getAbilitiesConfig();
    AbilityManager getAbilityManager();
    Logger getLogger();
    BukkitTask runTaskAsync(Runnable runnable);


    class Provider {
        private static AdvancedAPI api;
        public static void setAPI(AdvancedAPI api) {
            Provider.api = api;
        }
        public static AdvancedAPI getAPI() {
            return api;
        }
    }

}

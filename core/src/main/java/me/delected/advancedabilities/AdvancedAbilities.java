package me.delected.advancedabilities;

import lombok.Getter;
import me.delected.advancedabilities.ability.AbilityListener;
import me.delected.advancedabilities.managers.AbilityManagerImpl;
import me.delected.advancedabilities.api.AdvancedAPI;
import me.delected.advancedabilities.api.objects.ItemGenerator;
import me.delected.advancedabilities.commands.AbilityCommand;
import me.delected.advancedabilities.commands.GetAbilityCommand;
import me.delected.advancedabilities.legacy.LegacyItemGenerator;
import me.delected.advancedabilities.modern.ModernItemGenerator;
import me.delected.advancedabilities.api.enums.NMSVersion;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class AdvancedAbilities extends JavaPlugin implements AdvancedAPI {
    @Getter
    private static AdvancedAbilities plugin;
    @Getter
    private FileConfiguration abilitiesConfig;
    @Getter
    private AbilityManagerImpl abilityManager;

    @Getter
    private ItemGenerator itemGenerator;


    @Override
    public void onEnable() {

        plugin = this;
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("§6§lAdvancedAbilities §8§l» §e§l4.0");
        getLogger().info("§e§lby Delected_ & Endxxr");
        getLogger().info("");
        getLogger().info("Loading...");
        getLogger().info("");
        checkVersion();
        saveConfigs();
        setInstances();
        registerCommands();
        registerListeners();
        getLogger().info("Done ✓");
        getLogger().info("§8§l§m------------------");


    }

    @Override
    public void onDisable() {
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("§6§lAdvancedAbilities §8§l» §e§l4.0");
        getLogger().info("§e§lby Delected_ & Endxxr");
        getLogger().info("");
        getLogger().info("Disabling...");
        getLogger().info("§8§l§m------------------");
        getAbilityManager().stopCleanup();
    }

    private void registerCommands() {

        getCommand("getability").setExecutor(new GetAbilityCommand());
        getCommand("getability").setTabCompleter(new GetAbilityCommand());
        getCommand("ability").setExecutor(new AbilityCommand());

    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AbilityListener(), this);
    }

    private void checkVersion() {

        NMSVersion nmsVersion = NMSVersion.getSeverVersion();
        if (nmsVersion.getInt() < 80 || nmsVersion.getInt() > 192) {
            getLogger().severe("Your version ("+nmsVersion.name()+") isn't supported!");
            getLogger().severe("Disabling the plugin...");
            getLogger().info("§8§l§m------------------");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void saveConfigs() {

        saveDefaultConfig();
        saveResource("abilities.yml", false);
        abilitiesConfig = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "abilities.yml"));
        getLogger().info("Configurations loaded!");
    }

    private void setInstances() {

        AdvancedAPI.Provider.setAPI(this);

        if (NMSVersion.isLegacy()) {
            itemGenerator = new LegacyItemGenerator();
        } else {
            itemGenerator = new ModernItemGenerator();
        }
        abilityManager = new AbilityManagerImpl(this);

    }


    public void reloadPlugin() {
        super.reloadConfig();
        saveConfigs();
        reloadAbilities();
        setInstances();
    }

    private void reloadAbilities() {
        HandlerList.unregisterAll(this);
        registerListeners();
        abilityManager.stopCleanup();
        abilityManager = new AbilityManagerImpl(this);

    }


    @Override
    public BukkitTask runTaskAsync(Runnable runnable) {
        return Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }
}

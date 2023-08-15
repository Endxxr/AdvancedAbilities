package me.delected.advancedabilities;

import lombok.Getter;
import lombok.SneakyThrows;
import me.delected.advancedabilities.ability.AbilityListener;
import me.delected.advancedabilities.ability.abilities.*;
import me.delected.advancedabilities.api.AdvancedAPI;
import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.enums.NMSVersion;
import me.delected.advancedabilities.api.objects.ItemGenerator;
import me.delected.advancedabilities.api.objects.managers.RegionChecker;
import me.delected.advancedabilities.commands.AbilityCommand;
import me.delected.advancedabilities.commands.GetAbilityCommand;
import me.delected.advancedabilities.legacy.LegacyItemGenerator;
import me.delected.advancedabilities.legacy.WG6RegionChecker;
import me.delected.advancedabilities.legacy.abilities.LegacyGrapplingHook;
import me.delected.advancedabilities.listeners.JoinListener;
import me.delected.advancedabilities.managers.AbilityManagerImpl;
import me.delected.advancedabilities.modern.ModernItemGenerator;
import me.delected.advancedabilities.modern.WG7RegionChecker;
import me.delected.advancedabilities.modern.abilities.ModernGrapplingHook;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

//TODO add worldguard region support, timewarp pearl death

public final class AdvancedAbilities extends JavaPlugin implements AdvancedAPI {
    @Getter
    private FileConfiguration abilitiesConfig;
    @Getter
    private AbilityManagerImpl abilityManager;
    @Getter
    private ItemGenerator itemGenerator;
    private RegionChecker regionChecker;
    @Getter
    private String latestVersion;
    @Getter
    private boolean updateAvailable = false;
    private boolean worldGuardEnabled = false;
    private final String CURRENT_VERSION = getPlugin().getDescription().getVersion();

    @Override
    public void onLoad() {
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("§6§lAdvancedAbilities §8§l» §e§l${project.version}");
        getLogger().info("§e§lby Endxxr");
        getLogger().info("§e§l& Delected_ §7(Original Author)");
        getLogger().info("");
        getLogger().info("§8§l§m------------------");
        AdvancedProvider.setApi(this);
        setUpWorldGuard();
        checkVersion();
        checkUpdate();
    }

    @Override
    public void onEnable() {
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("Loading...");
        getLogger().info("");
        saveConfigs();
        setInstances();
        registerCommands();
        registerAbilities();
        registerListeners();
        getLogger().info("Done ✓");
        getLogger().info("§8§l§m------------------");

        new Metrics(this, 18224);

    }

    @Override
    public void onDisable() {
        getLogger().info("§8§l§m------------------");
        getLogger().info("");
        getLogger().info("§6§lAdvancedAbilities §8§l» §e§l4.1.2");
        getLogger().info("");
        getLogger().info("Disabling...");
        getLogger().info("§8§l§m------------------");
        getAbilityManager().stopCleanup();
    }

    private void registerCommands() {

        getCommand("getability").setExecutor(new GetAbilityCommand(this));
        getCommand("getability").setTabCompleter(new GetAbilityCommand(this));
        getCommand("ability").setExecutor(new AbilityCommand(this));

    }

    private void registerAbilities() {

        //Register abilities
        abilityManager.registerAbility(new AntiBlockUp());
        abilityManager.registerAbility(new Bamboozle());
        abilityManager.registerAbility(new FakePearl());
        abilityManager.registerAbility(new InstantCrapple());
        abilityManager.registerAbility(new InstantGapple());
        abilityManager.registerAbility(new Invulnerability());
        abilityManager.registerAbility(new Leap());
        abilityManager.registerAbility(new PortableBard());
        abilityManager.registerAbility(new PotCounter());
        abilityManager.registerAbility(new RabbitSoul());
        abilityManager.registerAbility(new Repair());
        abilityManager.registerAbility(new RepairAll());
        abilityManager.registerAbility(new RottenEgg());
        abilityManager.registerAbility(new Saviour());
        abilityManager.registerAbility(new Stun());
        abilityManager.registerAbility(new SwitcherSnowBall());
        abilityManager.registerAbility(new TimeWarpPearl());


        if (NMSVersion.isLegacy()) {
            abilityManager.registerAbility(new LegacyGrapplingHook());
        } else {
            abilityManager.registerAbility(new ModernGrapplingHook());
        }

    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new AbilityListener(this), this);
        getServer().getPluginManager().registerEvents(new JoinListener(this), this);
    }

    private void checkVersion() {

        NMSVersion nmsVersion = NMSVersion.getSeverVersion();
        if (nmsVersion.getInt() < 80 || nmsVersion.getInt() > 201) {
            getLogger().severe("Your version ("+nmsVersion.name()+") isn't supported!");
            getLogger().severe("Disabling the plugin...");
            getLogger().info("§8§l§m------------------");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    @SneakyThrows
    private void saveConfigs() {

        saveDefaultConfig();
        saveResource("abilities.yml", false);

        FileInputStream is = new FileInputStream(new File(getDataFolder(), "abilities.yml"));
        InputStreamReader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
        abilitiesConfig = YamlConfiguration.loadConfiguration(reader);

        getLogger().info("Configurations loaded!");
    }

    private void setInstances() {

        if (NMSVersion.isLegacy()) {
            itemGenerator = new LegacyItemGenerator();
        } else {
            itemGenerator = new ModernItemGenerator();
        }

        abilityManager = new AbilityManagerImpl(this);

    }

    private void setUpWorldGuard() {

        if (Bukkit.getPluginManager().getPlugin("WorldGuard") == null) return;

        worldGuardEnabled = true;
        boolean oldWGVersion = false;
        try {
            Class.forName("com.sk89q.worldguard.WorldGuard"); //Checks for WorldGuard class only in version 7.0.0
        } catch (ClassNotFoundException e) {
            oldWGVersion = true;
            getLogger().info("Detected WorldGuard v6.x.x!");
        }

        if (oldWGVersion) {
            regionChecker = new WG6RegionChecker();
        } else {
            regionChecker = new WG7RegionChecker();
        }
        regionChecker.registerFlags();
    }


    public RegionChecker getRegionChecker() {
        if (Bukkit.getPluginManager().getPlugin("WorldGuard") != null) return regionChecker;
        getLogger().severe("WorldGuard not found! You won't be able to use the region checker!");
        return null;
    }

    @Override
    public boolean isWorldGuardEnabled() {
        return worldGuardEnabled;
    }


    public void reloadPlugin() {
        abilityManager.stopCleanup();
        super.reloadConfig();
        saveConfigs();
        setInstances();
        reloadAbilities();
    }

    private void reloadAbilities() {
        HandlerList.unregisterAll(this);
        registerAbilities();
        registerListeners();
    }


    @Override
    public void runTaskAsync(Runnable runnable) {
        Bukkit.getScheduler().runTaskAsynchronously(this, runnable);
    }

    @Override
    public void runTaskLater(Runnable runnable, long delay) {
        Bukkit.getScheduler().runTaskLater(this, runnable, delay);
    }

    @Override
    public Plugin getPlugin() {
        return this;
    }


    private void checkUpdate() {
        latestVersion = getSpigotVersion();
        if (!CURRENT_VERSION.equals(latestVersion)) {
            updateAvailable = true;
            getLogger().warning("There is a new version available!");
            getLogger().warning("Your version: " + CURRENT_VERSION);
            getLogger().warning("Latest version: " + latestVersion);
            getLogger().warning("Download it here: https://www.spigotmc.org/resources/73132/");
        }
    }

    @SneakyThrows
    private String getSpigotVersion() {
        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            try (InputStream inputStream = new URL("https://api.spigotmc.org/legacy/update.php?resource=73132").openStream(); Scanner scanner = new Scanner(inputStream)) {
                if (scanner.hasNext()) {
                    return scanner.next();
                }
            } catch (IOException exception) {
                getLogger().warning("Cannot look for updates: " + exception.getMessage());
            }
            return CURRENT_VERSION;
        });

        return future.get(5000, TimeUnit.MILLISECONDS);
    }
}

package me.delected.advancedhcfabilities;

import me.delected.advancedhcfabilities.ability.abilities.Ability;
import me.delected.advancedhcfabilities.ability.abilities.*;
import me.delected.advancedhcfabilities.commands.ItemCommand;
import me.delected.advancedhcfabilities.commands.ReloadCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class AdvancedHCFAbilities extends JavaPlugin {
    private static JavaPlugin plugin;
    public static JavaPlugin plugin() { return plugin; }

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();

        for (Ability abil : AbilityManager.abilities) {
            getServer().getPluginManager().registerEvents(abil, this);
        }

        getCommand("getitem").setExecutor(new ItemCommand());
        getCommand("ahareload").setExecutor(new ReloadCommand());

    }
}

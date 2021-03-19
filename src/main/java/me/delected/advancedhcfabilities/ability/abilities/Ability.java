package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.AdvancedHCFAbilities;
import me.delected.advancedhcfabilities.Chat;
import me.delected.advancedhcfabilities.CooldownManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public abstract class Ability implements Listener {
    private final Set<String> validStrings = new HashSet<>();

    /* Thanks Frido#2279 for helping me with this */
    protected Ability(String... differentAliases) {
        List<String[]> split = Arrays.stream(differentAliases).map(s -> s.toLowerCase().split(";")).collect(Collectors.toList());
        for(String[] strings : split) {
            for (int i = 0, max = strings.length; i <= max; i++) {
                for (int j = 0; j < max - i + 1; j++) {
                    String alias = String.join("", Arrays.copyOfRange(strings, j, j + i));
                    validStrings.add(alias);
                }
            }
        }
    }
    public static Ability getFromString(String input) {
        String str = input.toLowerCase().replaceAll("^[ \t]+|[ \t]+$", "")
                .replaceAll("-", "").replaceAll("_", "");
        for (Ability abil : AbilityManager.abilities) {
            if (abil.validStrings.contains(str)) return abil;
        }
        return null;
    }

    public Configuration config = AdvancedHCFAbilities.plugin().getConfig();
    public CooldownManager cm = new CooldownManager();

    protected String getName() {
        return Chat.color(config.getString(getShortName() + "_displayname"));
    }

    protected List<String> getLore() {
        List<String> lore = new ArrayList<>();
        for (String s : config.getStringList(getShortName() + "_lore")) {
            lore.add(Chat.color(s));
        }
        return lore;
    }

    protected long getCooldownConfig() {
        return config.getLong(getShortName() + "_cooldown_time");
    }


    public abstract String getShortName();

    public abstract long getTimeLeft(Player p);

    public abstract void setCooldown(Player p);

    public boolean isOnCooldown(Player p) {
        return !(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) >= getCooldownConfig());
    }

    public abstract Material getMaterial();

    public ItemStack item() {
        ItemStack item = new ItemStack(getMaterial());
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getName());
        List<String> lore = new ArrayList<>();
        for (String str : getLore()) {
            lore.add(Chat.color(str));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    public boolean isInBlacklistedArea(Player p) {
        Location loc = p.getLocation();
        Location l1 = new Location(p.getWorld(), config.getDouble("ability_blacklisted_area.x1"), 0, config.getDouble("ability_blacklisted_area.z1"));
        Location l2 = new Location(p.getWorld(), config.getDouble("ability_blacklisted_area.x2"), 256, config.getDouble("ability_blacklisted_area.z2"));
        int x1 = Math.min(l1.getBlockX(), l2.getBlockX());
        int y1 = Math.min(l1.getBlockY(), l2.getBlockY());
        int z1 = Math.min(l1.getBlockZ(), l2.getBlockZ());
        int x2 = Math.max(l1.getBlockX(), l2.getBlockX());
        int y2 = Math.max(l1.getBlockY(), l2.getBlockY());
        int z2 = Math.max(l1.getBlockZ(), l2.getBlockZ());

        return loc.getX() >= x1 && loc.getX() <= x2 && loc.getY() >= y1 && loc.getY() <= y2 && loc.getZ() >= z1 && loc.getZ() <= z2;
    }
}

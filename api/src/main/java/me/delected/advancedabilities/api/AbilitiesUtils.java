package me.delected.advancedabilities.api;

import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AbilitiesUtils {

    private static Location coord1;
    private static Location coord2;

    static {
        setSpawn();
    }


    public static void addPotionEffect(Player player, PotionEffectType type, int duration, int amplifier) {
        PotionEffect effect = getPotionEffect(player, type);
        if (effect!=null) {
            if (effect.getDuration() < duration && effect.getAmplifier() < amplifier) {
                player.removePotionEffect(type);
                player.addPotionEffect(new PotionEffect(type, duration, amplifier));
            } else if (effect.getDuration() < duration) {
                player.removePotionEffect(type);
                player.addPotionEffect(new PotionEffect(type, duration, effect.getAmplifier()));
            } else if (effect.getAmplifier() < amplifier) {
                player.removePotionEffect(type);
                player.addPotionEffect(new PotionEffect(type, effect.getDuration(), amplifier));
            }
        } else {
            player.addPotionEffect(new PotionEffect(type, duration, amplifier));
        }
    }

    public static List<PotionEffect> getPotionEffects(Ability ability) {
        final List<PotionEffect> temp = new ArrayList<>();
        final int duration = ability.getConfigSection().getInt("duration");
        for (String effects : ability.getConfigSection().getStringList("effects")) {
            final String[] split = effects.split(":");
            try {
                temp.add(new PotionEffect(PotionEffectType.getByName(split[0]), duration*20, Integer.parseInt(split[1]), false, false));
            } catch (IllegalArgumentException e) {
                AdvancedProvider.getAPI().getLogger().info("Invalid potion effect type: " + split[0]);
            }
        }
        return temp;
    }

    public static boolean inSpawn(Player player, Location location) {

        //Sort the two coordinates in min and max coordinates
        int minX = Math.min(coord1.getBlockX(), coord2.getBlockX());
        int minY = Math.min(coord1.getBlockY(), coord2.getBlockY());
        int minZ = Math.min(coord1.getBlockZ(), coord2.getBlockZ());
        int maxX = Math.max(coord1.getBlockX(), coord2.getBlockX());
        int maxY = Math.max(coord1.getBlockY(), coord2.getBlockY());
        int maxZ = Math.max(coord1.getBlockZ(), coord2.getBlockZ());

        Location min = new Location(location.getWorld(), minX, minY, minZ);
        Location max = new Location(location.getWorld(), maxX, maxY, maxZ);

        if  (location.getBlockX() >= min.getBlockX() && location.getBlockX() <= max.getBlockX()
                && location.getBlockY() >= min.getBlockY() && location.getBlockY() <= max.getBlockY()
                && location.getBlockZ() >= min.getBlockZ() && location.getBlockZ() <= max.getBlockZ()) {
            player.sendMessage(ChatUtils.colorize(AdvancedProvider.getAPI().getConfig().getString("spawn-region.deny")));
            return true;
        }

        return false;
    }


    private static PotionEffect getPotionEffect(Player player, PotionEffectType type) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(type)) {
                return effect;
            }
        }
        return null;
    }


    public static void setSpawn() {
        FileConfiguration configuration = AdvancedProvider.getAPI().getConfig();
        coord1 =new Location(
                Bukkit.getWorld(configuration.getString("spawn-region.world")),
                configuration.getDouble("spawn-region.x1"),
                configuration.getDouble("spawn-region.y1"),
                configuration.getDouble("spawn-region.z1"));

        coord2 =new Location(
                Bukkit.getWorld(configuration.getString("spawn-region.world")),
                configuration.getDouble("spawn-region.x2"),
                configuration.getDouble("spawn-region.y2"),
                configuration.getDouble("spawn-region.z2"));

    }






}

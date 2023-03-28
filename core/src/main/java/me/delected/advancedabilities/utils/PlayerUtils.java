package me.delected.advancedabilities.utils;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PlayerUtils {

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

    private static PotionEffect getPotionEffect(Player player, PotionEffectType type) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getType().equals(type)) {
                return effect;
            }
        }
        return null;
    }

}

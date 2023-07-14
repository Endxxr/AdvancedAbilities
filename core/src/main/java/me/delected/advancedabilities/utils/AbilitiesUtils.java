package me.delected.advancedabilities.utils;

import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.objects.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class AbilitiesUtils {


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
        final List<PotionEffect> effectsList = new ArrayList<>();
        final int duration = ability.getConfig().getInt("duration");
        for (String effects : ability.getConfig().getStringList("effects")) {
            final String[] split = effects.split(":");
            try {
                effectsList.add(new PotionEffect(PotionEffectType.getByName(split[0]), duration*20, Integer.parseInt(split[1]), false, false));
            } catch (IllegalArgumentException e) {
                AdvancedProvider.getAPI().getLogger().info("Invalid potion effect type: " + split[0]);
            }
        }
        return effectsList;
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


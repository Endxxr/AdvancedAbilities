package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.ClickableAbility;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class InstantGapple extends ClickableAbility {
    @Override
    public String getId() {
        return "instant-gapple";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player) {
        player.sendMessage(ChatUtils.colorize(getConfigSection().getString("message.done")));

        player.setFoodLevel(player.getFoodLevel()+4);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 1));
        player.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));

        addCooldown(player);


    }
}

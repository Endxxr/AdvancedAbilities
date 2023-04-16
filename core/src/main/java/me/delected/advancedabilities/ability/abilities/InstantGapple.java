package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.ClickableAbility;
import me.delected.advancedabilities.utils.AbilitiesUtils;
import org.bukkit.entity.Player;
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
        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        player.setFoodLevel(player.getFoodLevel()+4);
        AbilitiesUtils.addPotionEffect(player, PotionEffectType.ABSORPTION, 2400, 3);
        AbilitiesUtils.addPotionEffect(player, PotionEffectType.REGENERATION, 400, 1);
        AbilitiesUtils.addPotionEffect(player, PotionEffectType.FIRE_RESISTANCE, 6000, 0);
        AbilitiesUtils.addPotionEffect(player, PotionEffectType.DAMAGE_RESISTANCE, 6000, 0);
        addCooldown(player);
    }
}

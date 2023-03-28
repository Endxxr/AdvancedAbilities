package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.ClickableAbility;
import me.delected.advancedabilities.utils.PlayerUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class InstantCrapple extends ClickableAbility {
    @Override
    public String getId() {
        return "instant-crapple";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player) {


        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        player.setFoodLevel(player.getFoodLevel()+4);
        PlayerUtils.addPotionEffect(player, PotionEffectType.ABSORPTION, 2400, 0);
        PlayerUtils.addPotionEffect(player, PotionEffectType.REGENERATION, 100, 1);

        addCooldown(player);

    }
}

package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.objects.ability.ClickableAbility;
import me.delected.advancedabilities.utils.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
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


        player.sendMessage(ChatUtils.colorize(getConfigSection().getString("message.done")));
        player.setFoodLevel(player.getFoodLevel()+4);
        player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
        player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));;

        addCooldown(player);

    }
}

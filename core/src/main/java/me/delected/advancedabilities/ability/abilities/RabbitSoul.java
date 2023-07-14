package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.ClickableAbility;
import me.delected.advancedabilities.utils.AbilitiesUtils;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffectType;

public class RabbitSoul extends ClickableAbility {

    @Override
    public String getId() {
        return "rabbit-soul";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player) {
        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        AbilitiesUtils.addPotionEffect(player, PotionEffectType.SPEED, getConfig().getInt("speed.duration")*20, getConfig().getInt("speed.level"));
        AbilitiesUtils.addPotionEffect(player, PotionEffectType.JUMP, getConfig().getInt("jump-boost.duration")*20, getConfig().getInt("jump-boost.level"));
        addCooldown(player);

        playSound(player);

    }

}

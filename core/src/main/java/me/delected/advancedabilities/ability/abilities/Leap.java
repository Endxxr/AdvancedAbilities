package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.ClickableAbility;
import org.bukkit.entity.Player;

public class Leap extends ClickableAbility {
    @Override
    public String getId() {
        return "leap";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player) {

        double multiplierX = getConfigSection().getDouble("multiply-x");
        double multiplierY = getConfigSection().getDouble("multiply-y");

        player.setVelocity(player.getLocation().getDirection().multiply(multiplierX).setY(multiplierY));
        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        player.playSound(player.getLocation(), getSound(), 1, 0);
        addCooldown(player);

    }
}

package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.TargetAbility;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Bamboozle extends TargetAbility {

    @Override
    public String getId() {
        return "bamboozle";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player, Player target) {

        int seconds = getConfig().getInt("seconds");
        List<ItemStack> hotbar = new ArrayList<>();

        for (int i = 0 ; i <= 8 ; i++) {
            hotbar.add(target.getInventory().getItem(i)); //Mette la hotbar nella lista
        }

        Collections.shuffle(hotbar, new Random());
        int i = 0;
        for (ItemStack item : hotbar) {
            target.getInventory().setItem(i ,item); //setta la hotbar
            i++;
        }

        addCooldown(player);
        target.playSound(target.getLocation(), getSound(), 1, 0);
        player.sendMessage(ChatUtils.colorize(getExecuteMessage()
                .replaceAll("%seconds%", String.valueOf(seconds))
                .replaceAll("%player%", target.getDisplayName())));
        target.sendMessage(ChatUtils.colorize(getTargetMessage()
                .replaceAll("%seconds%", String.valueOf(seconds))
                .replaceAll("%player%", player.getDisplayName())));

        playSound(target);

    }

}

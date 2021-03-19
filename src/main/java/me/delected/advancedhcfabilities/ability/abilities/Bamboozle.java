package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Bamboozle extends RemovableAbility {

    public Bamboozle() {
        super("bamboozle", "mixup");
    }

    @Override
    public Material getMaterial() { return Material.NAME_TAG; }

    @Override
    public String getShortName() { return "bamboozle"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getBamboozleCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setBamboozleCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @EventHandler
    public void onPlayerUseBamboozle(PlayerInteractEntityEvent e) {
        if (!(e.getPlayer().getItemInHand().getType() == Material.NAME_TAG)) return;

        if (e.getRightClicked().getType() != EntityType.PLAYER) return;

        ItemMeta handItemMeta = e.getPlayer().getItemInHand().getItemMeta();
        if (handItemMeta == null) return;
        if (handItemMeta.getDisplayName() == null) return;
        if (!handItemMeta.getDisplayName().equalsIgnoreCase(getName())) return;

        Player rc = (Player) e.getRightClicked();

        if (isOnCooldown(e.getPlayer())) {
            e.getPlayer().sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(e.getPlayer())) - getCooldownConfig())))));
            return;
        }

//        if (checkGlobalCooldown(e.getPlayer())) return;

        rc.sendMessage(Chat.color(config.getString("message_to_bamboozle_hit"))
                .replace("{hitter}", e.getPlayer().getDisplayName()).replace("{hit}", rc.getDisplayName()));
        e.getPlayer().sendMessage(Chat.color(config.getString("message_to_bamboozle_hitter"))
                .replace("{hitter}", e.getPlayer().getDisplayName()).replace("{hit}", rc.getDisplayName()));
        // stunned Player stuff

        rc.playSound(rc.getLocation(), Sound.BLAZE_HIT, 1F, 0F);
        e.getPlayer().playSound(e.getPlayer().getLocation(), Sound.BLAZE_HIT, 1F, 0F);

        removeFrom(e.getPlayer());

        // bamboozle inventory here
        List<ItemStack> hotbar = new ArrayList<>();

        for (int i = 0 ; i <= 8 ; i++) {
            hotbar.add(rc.getInventory().getItem(i));
        }
        Collections.shuffle(hotbar, new Random());

        int i = 0;
        for (ItemStack item : hotbar) {
            rc.getInventory().setItem(i ,item);
            i++;
        }

        setCooldown(e.getPlayer());
    }
}

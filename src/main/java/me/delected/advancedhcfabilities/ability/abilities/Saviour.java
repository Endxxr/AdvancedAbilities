package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import me.delected.advancedhcfabilities.ability.Effect;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Saviour extends RemovableAbility implements Effect {

    public Saviour() {
        super("saviour", "save");
    }

    @Override
    public String getShortName() { return "saviour"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getSaviourCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setSaviourCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @Override
    public Material getMaterial() { return Material.BOOK; }

    @Override
    public void giveEffect(Player p) {
        List<PotionEffect> effects = Arrays.asList(
                new PotionEffect(PotionEffectType.ABSORPTION, 200, 4),
                new PotionEffect(PotionEffectType.REGENERATION, config.getInt("regeneration_seconds", 10) * 20, 2));
        p.addPotionEffects(effects);
    }
    @EventHandler
    public void onEntityDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        Player p = (Player) e.getEntity();
        if (!(p.getHealth() - e.getFinalDamage() <= 0)) return;
        if (!p.getInventory().containsAtLeast(item(), 1)) return;
        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            return;
        }

//        if (checkGlobalCooldown(p)) return;

        p.sendMessage(Chat.color(config.getString("message_to_saviour_user")));
        removeFrom(p);
        Location loc = p.getLocation();
        e.setCancelled(true);
        p.teleport(loc);
        p.setHealth(20);
        p.setFoodLevel(20);
        giveEffect(p);
        setCooldown(p);
    }
}

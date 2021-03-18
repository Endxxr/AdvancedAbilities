package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import me.delected.advancedhcfabilities.ability.Effect;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class RottenEgg extends Ability implements Effect {
    List<Player> eggPlayers = new ArrayList<>();

    public RottenEgg() {
        super("rotten;egg", "poison;egg");
    }

    @Override
    public Material getMaterial() { return Material.EGG; }


    @Override
    public String getShortName() {
        return "egg";
    }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getEggCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setEggCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @Override
    public void giveEffect(Player p) {
        List<PotionEffect> effects = Arrays.asList(
                new PotionEffect(PotionEffectType.BLINDNESS, config.getInt("blindness_duration", 10) * 20, Chat.validateLevel(config.getInt("blindness_level", 2))),
                new PotionEffect(PotionEffectType.POISON, config.getInt("poison_duration", 5) * 20, Chat.validateLevel(config.getInt("poison_level", 1))),
                new PotionEffect(PotionEffectType.CONFUSION, config.getInt("nausea_duration", 10) * 20, Chat.validateLevel(config.getInt("nausea_level", 1))));
        p.addPotionEffects(effects);
    }

    @EventHandler
    public void onPlayerLaunchEgg(ProjectileLaunchEvent e) {
        if (!( e.getEntity() instanceof Egg)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) e.getEntity().getShooter();

        if (shooter.getItemInHand().getItemMeta() == null) return;
        String handName = shooter.getItemInHand().getItemMeta().getDisplayName();
        if (handName == null) return;
        if (!handName.equalsIgnoreCase(getName())) return;

        if (isOnCooldown(shooter)) {
            shooter.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(shooter)) - getCooldownConfig())))));
            shooter.getInventory().addItem(item());
            e.setCancelled(true);
            return;
        }
        eggPlayers.add(shooter);
    }

    @EventHandler
    public void onPlayerHitByEgg(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Egg)) return;

        Player hit = (Player) e.getEntity();
        Entity damager = e.getDamager();

        Egg egg = (Egg) damager;

        if (!(egg.getShooter() instanceof Player)) return;
        if (egg.getShooter() == hit) return;
        Player shooter = (Player) egg.getShooter();
        if (!eggPlayers.contains(shooter)) return;

        eggPlayers.remove(shooter);


        // send messages

        hit.sendMessage(Chat.color(config.getString("message_to_egg_hit"))
                .replace("{hit}", hit.getDisplayName())
                .replace("{thrower}", shooter.getDisplayName()));

        shooter.sendMessage(Chat.color(config.getString("message_to_egg_thrower"))
                .replace("{hit}", hit.getDisplayName())
                .replace("{thrower}", shooter.getDisplayName()));


        // add effects
        giveEffect(hit);

        // add to cooldown
        setCooldown(shooter);
    }

    @EventHandler
    public void onChickenSpawn(PlayerEggThrowEvent e) {
        e.setHatching(false);
    }
}

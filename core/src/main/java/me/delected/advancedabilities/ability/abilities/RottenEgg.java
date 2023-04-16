package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.utils.AbilitiesUtils;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.*;

import static org.bukkit.Bukkit.getName;

public class RottenEgg extends Ability implements Listener {

    private final Set<UUID> eggPlayers = new HashSet<>();

    @Override
    public String getId() {
        return "rotten-egg";
    }

    @Override
    public boolean removeItem() {
        return false;
    }

    @EventHandler
    public void onEggThrow(ProjectileLaunchEvent event) {

        if (!( event.getEntity() instanceof Egg)) return;
        if (!( event.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) event.getEntity().getShooter();

        if (AbilitiesUtils.inSpawn(shooter, shooter.getLocation())) return;
        if (AdvancedAbilities.getPlugin().getAbilityManager().inCooldown(shooter, this)) return;
        if (shooter.getItemInHand().isSimilar(getItem())) return;

        eggPlayers.add(shooter.getUniqueId());
        addCooldown(shooter);
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
        if (!eggPlayers.contains(shooter.getUniqueId())) return;

        eggPlayers.remove(shooter.getUniqueId());


        // send messages

        shooter.sendMessage(ChatUtils.colorize(getExecuteMessage())
                .replace("%target%", hit.getDisplayName()));

        hit.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.hit"))
                .replace("%target%", hit.getDisplayName())
                .replace("%player%", shooter.getDisplayName()));


        // add effects
        hit.addPotionEffects(AbilitiesUtils.getPotionEffects(this));

        // add to cooldown
        addCooldown(shooter);
    }

    @EventHandler
    public void onChickenSpawn(PlayerEggThrowEvent e) {
        e.setHatching(false);
    }






}

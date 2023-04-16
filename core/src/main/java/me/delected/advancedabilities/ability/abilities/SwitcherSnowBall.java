package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.utils.AbilitiesUtils;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class SwitcherSnowBall extends Ability implements Listener {

    private final Set<UUID> snowPlayers = new HashSet<>();

    @Override
    public String getId() {
        return "switcher-snowball";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @EventHandler
    public void onSnowBallThrow(ProjectileLaunchEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity().getType() != EntityType.SNOWBALL) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();

        if (AbilitiesUtils.inSpawn(player, player.getLocation())) return;
        if (snowPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.wait")));
            event.setCancelled(true);
            return;
        }
        if (!player.getItemInHand().isSimilar(getItem())) return;
        if (AdvancedAbilities.getPlugin().getAbilityManager().inCooldown(player, this)) {
            event.setCancelled(true);
            return;
        };
        snowPlayers.add(player.getUniqueId());
        addCooldown(player);
    }

    @EventHandler
    public void onPlayerHitBySnowball(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Snowball)) return;

        Player hit = (Player) e.getEntity();
        Entity damager = e.getDamager();

        Snowball snowball = (Snowball) damager;

        if (!(snowball.getShooter() instanceof Player)) return;
        if (snowball.getShooter() == hit) return;
        Player shooter = (Player) snowball.getShooter();
        if (!snowPlayers.contains(shooter.getUniqueId())) return;

        snowPlayers.remove(shooter.getUniqueId());


        // send messages

        shooter.sendMessage(ChatUtils.colorize(getExecuteMessage())
                .replace("%target%", hit.getDisplayName()));

        hit.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.hit"))
                .replace("%target%", hit.getDisplayName())
                .replace("%player%", shooter.getDisplayName()));

        Location hitLocation = hit.getLocation().clone();
        Location shooterLocation = shooter.getLocation().clone();

        hit.teleport(shooterLocation);
        shooter.teleport(hitLocation);

        // add to cooldown
        addCooldown(shooter);
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.SNOWBALL) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        if (!snowPlayers.contains(player.getUniqueId())) return;
        snowPlayers.remove(player.getUniqueId());
    }


}

package me.delected.advancedabilities.legacy.abilities;

import me.delected.advancedabilities.api.AbilitiesUtils;
import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class LegacySwitcherSnowBall extends Ability implements Listener {

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
    public void onPlayerHitBySnowball(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Snowball)) return;

        Player hit = (Player) e.getEntity();
        Entity damager = e.getDamager();

        Snowball snowball = (Snowball) damager;

        if (AbilitiesUtils.isNPC(hit)) return;
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

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == PlayerInteractEvent.Result.DENY) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null) return;
        if (!event.getItem().getType().equals(Material.SNOW_BALL)) return;

        Player player = event.getPlayer();

        if (snowPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.wait")));
        }

        if (AdvancedProvider.getAPI().getAbilityManager().getAbilityByItem(player.getItemInHand()) != this) return;

        if (!AbilitiesUtils.canExecute(player, this)) {
            event.setCancelled(true);
            return;
        }

        if (snowPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.wait")));
            event.setCancelled(true);
            return;
        }

        snowPlayers.add(player.getUniqueId());
        addCooldown(player);
    }
}

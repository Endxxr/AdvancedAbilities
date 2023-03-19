package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.objects.ability.Ability;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class GrapplingHook extends Ability implements Listener {

    private final HashMap<UUID, Vector> grapple = new HashMap<>();
    private final Set<UUID> fallList = new HashSet<>();

    @Override
    public String getId() {
        return "grappling-hook";
    }

    @Override
    public boolean removeItem() {
        return false;
    }

    @EventHandler
    public void onGrappleLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) event.getEntity().getShooter();
        if (event.getEntity().getType() != EntityType.FISHING_HOOK) return;

        ItemStack item = shooter.getItemInHand();
        if (item==null) return;

        Ability ability = AdvancedAbilities.getPlugin().getAbilityManager().getAbilityByItem(item);
        if (ability==null) return;

        if (cooldownPlayers.containsKey(shooter.getUniqueId())) return;
        grapple.put(shooter.getUniqueId(), event.getEntity().getVelocity());
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (item==null) return;

        Ability ability = AdvancedAbilities.getPlugin().getAbilityManager().getAbilityByItem(item);
        if (ability==null) return;

        if (!event.getState().equals(PlayerFishEvent.State.FAILED_ATTEMPT)) return;

        Player player = event.getPlayer();
        double distance = player.getLocation().distance(event.getHook().getLocation());

        if (!grapple.containsKey(player.getUniqueId())) return;

        Location loc = player.getLocation();
        Location hookLoc = event.getHook().getLocation();

        double dis = loc.distance(hookLoc);

        double X = (1.0 + 0.24 * dis) * (hookLoc.getX() - loc.getX()) / dis;
        double Y = (1.0 + 0.12 * dis) * (hookLoc.getY() - loc.getY()) / dis - 0.5 * -0.08 * dis;
        double Z = (1.0 + 0.24 * dis) * (hookLoc.getZ() - loc.getZ()) / dis;

        Vector v = player.getVelocity();
        v.setX(X);
        v.setY(Y);
        v.setZ(Z);
        player.setVelocity(v);

        player.setVelocity(grapple.get(player.getUniqueId()).multiply(distance * .3).setY((distance * 0.1) > 1 ? 1 : player.getLocation().getPitch() < -70 ? 1.25 : player.getLocation().getPitch() < -50 ? 1.125 : 1));
        grapple.remove(player.getUniqueId());

        item.setDurability((short) 0);

        // add to fall dmg list if fall dmg is disabled
        if (!getConfigSection().getBoolean("fall-damage")) fallList.add(player.getUniqueId());

        // add to cooldown
        addCooldown(player);
    }


    @EventHandler
    public void onPlayerFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        if (!(event.getCause() == EntityDamageEvent.DamageCause.FALL)) return;
        if (fallList.contains(event.getEntity().getUniqueId())) {
            fallList.remove(event.getEntity().getUniqueId());
            event.setCancelled(true);
        }
    }


}

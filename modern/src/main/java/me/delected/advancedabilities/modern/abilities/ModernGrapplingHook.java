package me.delected.advancedabilities.modern.abilities;

import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.ability.Ability;
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

public class ModernGrapplingHook extends Ability implements Listener {
    private final HashMap<UUID, Vector> grapple = new HashMap<>();
    private final Set<UUID> fallList = new HashSet<>();

    public String getId() {
        return "grappling-hook";
    }

    public boolean removeItem() {
        return false;
    }

    @EventHandler
    public void onGrappleLaunch(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player))
            return;
        Player shooter = (Player)event.getEntity().getShooter();
        if (event.getEntity().getType() != EntityType.FISHING_HOOK)
            return;
        ItemStack item = shooter.getItemInHand();
        if (item == null)
            return;
        Ability ability = AdvancedProvider.getAPI().getAbilityManager().getAbilityByItem(item);
        if (ability == null)
            return;
        if (AdvancedProvider.getAPI().getAbilityManager().inCooldown(shooter, this))
            return;
        this.grapple.putIfAbsent(shooter.getUniqueId(), event.getEntity().getVelocity());
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (item == null)
            return;
        Ability ability = AdvancedProvider.getAPI().getAbilityManager().getAbilityByItem(item);
        if (ability == null)
            return;
        Player player = event.getPlayer();

        Location loc = player.getLocation();
        Location hookLoc = event.getHook().getLocation();
        Vector v = this.grapple.get(player.getUniqueId());
        if (!this.grapple.containsKey(player.getUniqueId())) return;

        this.grapple.remove(player.getUniqueId());
        double dis = loc.distance(hookLoc);
        item.setDurability((short)0);
        if (!getConfigSection().getBoolean("fall-damage"))
            this.fallList.add(player.getUniqueId());
        addCooldown(player);


        double X = (1.0D + 0.24D * dis) * (hookLoc.getX() - loc.getX()) / dis;
        double Y = (1.0D + 0.12D * dis) * (hookLoc.getY() - loc.getY()) / dis - -0.04D * dis;
        double Z = (1.0D + 0.24D * dis) * (hookLoc.getZ() - loc.getZ()) / dis;
        Vector playerVector = player.getVelocity();
        playerVector.setX(X);
        playerVector.setY(Y);
        playerVector.setZ(Z);
        player.setVelocity(playerVector);
        player.setVelocity(v.multiply(dis * 0.3D).setY((dis * 0.1D > 1.0D) ? 1.0D : ((player.getLocation().getPitch() < -70.0F) ? 1.25D : ((player.getLocation().getPitch() < -50.0F) ? 1.125D : 1.0D))));
    }

    @EventHandler
    public void onPlayerFallDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player))
            return;
        if (event.getCause() != EntityDamageEvent.DamageCause.FALL)
            return;
        if (this.fallList.contains(event.getEntity().getUniqueId())) {
            this.fallList.remove(event.getEntity().getUniqueId());
            event.setCancelled(true);
        }
    }
}
package me.delected.advancedabilities.ability.abilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.enums.NMSVersion;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fish;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

public class GrapplingHook extends Ability implements Listener {
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
        Ability ability = AdvancedAbilities.getPlugin().getAbilityManager().getAbilityByItem(item);
        if (ability == null)
            return;
        if (AdvancedAbilities.getPlugin().getAbilityManager().inCooldown(shooter, this))
            return;
        if (this.cooldownPlayers.containsKey(shooter.getUniqueId()))
            return;
        this.grapple.put(shooter.getUniqueId(), event.getEntity().getVelocity());
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        ItemStack item = event.getPlayer().getItemInHand();
        if (item == null)
            return;
        Ability ability = AdvancedAbilities.getPlugin().getAbilityManager().getAbilityByItem(item);
        if (ability == null)
            return;
        if (!event.getState().equals(PlayerFishEvent.State.FAILED_ATTEMPT))
            return;
        Player player = event.getPlayer();

        if (!this.grapple.containsKey(player.getUniqueId()))
            return;
        Location loc = player.getLocation();

        Location hookLoc;
        if (NMSVersion.isLegacy()) {
            hookLoc = ((Fish)event.getHook()).getLocation();
        } else {
            hookLoc = event.getHook().getLocation();
        }



        double dis = loc.distance(hookLoc);
        double X = (1.0D + 0.24D * dis) * (hookLoc.getX() - loc.getX()) / dis;
        double Y = (1.0D + 0.12D * dis) * (hookLoc.getY() - loc.getY()) / dis - -0.04D * dis;
        double Z = (1.0D + 0.24D * dis) * (hookLoc.getZ() - loc.getZ()) / dis;
        Vector v = player.getVelocity();
        v.setX(X);
        v.setY(Y);
        v.setZ(Z);
        player.setVelocity(v);
        player.setVelocity(((Vector)this.grapple.get(player.getUniqueId())).multiply(dis * 0.3D).setY((dis * 0.1D > 1.0D) ? 1.0D : ((player.getLocation().getPitch() < -70.0F) ? 1.25D : ((player.getLocation().getPitch() < -50.0F) ? 1.125D : 1.0D))));
        this.grapple.remove(player.getUniqueId());
        item.setDurability((short)0);
        if (!getConfigSection().getBoolean("fall-damage"))
            this.fallList.add(player.getUniqueId());
        addCooldown(player);
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

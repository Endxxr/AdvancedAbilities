package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class GrapplingHook extends Ability {

    private final HashMap<Player, Vector> grapple = new HashMap<>();
    private final List<Player> fallList = new ArrayList<>();

    public GrapplingHook() {
        super("grappling;hook", "grapple");
    }

    @Override
    public Material getMaterial() { return Material.FISHING_ROD; }


    @Override
    public String getShortName() {
        return "grapple";
    }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getGrappleCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) {
        cm.setGrappleCooldown(p.getUniqueId(), System.currentTimeMillis());
    }

    @EventHandler
    public void onGrappleLaunch(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) e.getEntity().getShooter();

        if (e.getEntity().getType() != EntityType.FISHING_HOOK) return;
        if (shooter.getItemInHand() == null || shooter.getItemInHand().getItemMeta() == null || shooter.getItemInHand().getItemMeta().getDisplayName() == null)
            return;
        if (!shooter.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getName())) return;
        grapple.put(shooter, e.getEntity().getVelocity());
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent e) {
        ItemStack item = e.getPlayer().getItemInHand();
        if (item == null || item.getItemMeta() == null || item.getItemMeta().getDisplayName() == null) return;

        if (!item.getItemMeta().getDisplayName().equalsIgnoreCase(getName())) return;

        if (!e.getState().equals(PlayerFishEvent.State.FAILED_ATTEMPT)) return;

        Player p = e.getPlayer();

        double distance = p.getLocation().distance(e.getHook().getLocation());

        if (!grapple.containsKey(p)) return;

        if (isOnCooldown(p)) {
            e.getPlayer().sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            return;
        }


        Location loc = p.getLocation();
        Location hookLoc = e.getHook().getLocation();

        double dis = loc.distance(hookLoc);

        double X = (1.0 + 0.24 * dis) * (hookLoc.getX() - loc.getX()) / dis;
        double Y = (1.0 + 0.12 * dis) * (hookLoc.getY() - loc.getY()) / dis - 0.5 * -0.08 * dis;
        double Z = (1.0 + 0.24 * dis) * (hookLoc.getZ() - loc.getZ()) / dis;

        Vector v = p.getVelocity();
        v.setX(X);
        v.setY(Y);
        v.setZ(Z);
        p.setVelocity(v);

        p.setVelocity(grapple.get(p).multiply(distance * .3).setY((distance * 0.1) > 1 ? 1 : p.getLocation().getPitch() < -70 ? 1.25 : p.getLocation().getPitch() < -50 ? 1.125 : 1));
        grapple.remove(p);

        item.setDurability((short) 0);

        // add to fall dmg list if fall dmg is disabled
        if (!config.getBoolean("grapple_fall_damage_enabled")) fallList.add(p);

        // add to cooldown
        setCooldown(p);
    }

    @EventHandler
    public void onPlayerFallDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getCause() == EntityDamageEvent.DamageCause.FALL)) return;
        if (fallList.contains(((Player) e.getEntity()).getPlayer())) {
            fallList.remove(((Player) e.getEntity()).getPlayer());
            e.setCancelled(true);
        }
    }
}
package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SwitcherSnowball extends Ability {
    List<Player> snowballPlayers = new ArrayList<>();

    public SwitcherSnowball() {
        super("switcher;snowball");
    }

    @Override
    public Material getMaterial() { return Material.SNOW_BALL; }


    @Override
    public String getShortName() {
        return "snowball";
    }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getSnowballCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setSnowballCooldown(p.getUniqueId(), System.currentTimeMillis()); }



    @EventHandler
    public void onPlayerLaunchEgg(ProjectileLaunchEvent e) {
        if (!(e.getEntity() instanceof Snowball)) return;
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
        snowballPlayers.add(shooter);
    }

    @EventHandler
    public void onPlayerHitBySnowball(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Snowball)) return;

        Player hit = (Player) e.getEntity();
        Entity damager = e.getDamager();

        Player shooter;

        Snowball snowball = (Snowball) damager;

        if (!(snowball.getShooter() instanceof Player)) return;
        shooter = (Player) snowball.getShooter();
        if (!snowballPlayers.contains(shooter)) return;
        if (shooter == hit) return;

        snowballPlayers.remove(shooter);


        // send messages

        hit.sendMessage(Chat.color(config.getString("message_to_snowball_hit"))
                .replace("{hit}", hit.getDisplayName())
                .replace("{thrower}", shooter.getDisplayName()));

        shooter.sendMessage(Chat.color(config.getString("message_to_snowball_thrower"))
                .replace("{hit}", hit.getDisplayName())
                .replace("{thrower}", shooter.getDisplayName()));


        // switch
        Location loc = shooter.getLocation();
        shooter.teleport(hit);
        hit.teleport(loc);

        // add to cooldown
        setCooldown(shooter);
    }
}

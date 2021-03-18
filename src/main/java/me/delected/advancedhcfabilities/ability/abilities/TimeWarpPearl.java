package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.AdvancedHCFAbilities;
import me.delected.advancedhcfabilities.Chat;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class TimeWarpPearl extends Ability {
    HashMap<Player, Location> warpList = new HashMap<>();

    public TimeWarpPearl() {
        super("time;warp");
    }

    @Override
    public Material getMaterial() { return Material.ENDER_PEARL; }


    @Override
    public String getShortName() { return "time_warp"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getWarpCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setWarpCooldown(p.getUniqueId(), System.currentTimeMillis()); }



    @EventHandler
    public void onPlayerThrowPearl(ProjectileLaunchEvent e) {
        if (e.isCancelled()) return;
        if (e.getEntity().getType() != EntityType.ENDER_PEARL) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player p = (Player) e.getEntity().getShooter();

        if (p.getItemInHand() == null | p.getItemInHand().getItemMeta() == null || p.getItemInHand().getItemMeta().getDisplayName() == null) return;
        String name = p.getItemInHand().getItemMeta().getDisplayName();
        if (!name.equalsIgnoreCase(getName())) return;


        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            p.getInventory().addItem(item());
            e.setCancelled(true);
            return;
        }
        if (warpList.containsKey(p)) {
            p.sendMessage(ChatColor.RED + "You already have an out-going warp pearl!");
            return;
        }




        // add to list
        warpList.put(p, p.getLocation());

        p.sendMessage(Chat.color(config.getString("message_to_warp_pearler",
                "&6&lAbilities &8» &7You used a &5&lTime-Warp Pearl&7. You will be teleported back in &65 seconds.")));


        // add to cooldown
        setCooldown(p);
    }

    @EventHandler
    public void onPearlLand(ProjectileHitEvent e) {
        if (!(e.getEntity().getType() == EntityType.ENDER_PEARL)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player p = (Player) e.getEntity().getShooter();


        if (!warpList.containsKey(p)) return;



        Bukkit.getScheduler().runTaskLater(AdvancedHCFAbilities.plugin(), () -> {
            p.teleport(warpList.get(p));
            warpList.remove(p);
        }, config.getLong("time_before_warp_tp", 5) * 20);

    }
}

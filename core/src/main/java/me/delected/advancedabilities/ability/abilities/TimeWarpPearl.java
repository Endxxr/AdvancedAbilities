package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ability.Ability;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashMap;
import java.util.UUID;

public class TimeWarpPearl extends Ability implements Listener {

    private final HashMap<UUID, Location> waitingPlayers = new HashMap<>();

    @Override
    public String getId() {
        return "time-warp-pearl";
    }

    @Override
    public boolean removeItem() {
        return true;
    }


    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        if (!player.getItemInHand().isSimilar(getItem())) return;
        if (AdvancedAbilities.getPlugin().getAbilityManager().inCooldown(player, this)){
            event.setCancelled(true);
            return;
        }
        if (waitingPlayers.containsKey(player.getUniqueId())) {
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.wait")));
            event.setCancelled(true);
            return;
        }

        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        waitingPlayers.put(player.getUniqueId(), player.getLocation());
        addCooldown(player);
    }

    @EventHandler
    public void onLand(ProjectileHitEvent event){
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        final Player player = (Player) event.getEntity().getShooter();
        if (!waitingPlayers.containsKey(player.getUniqueId())) return;
        Bukkit.getScheduler().runTaskLater(AdvancedAbilities.getPlugin(), () -> {
            player.teleport(waitingPlayers.get(player.getUniqueId()), PlayerTeleportEvent.TeleportCause.PLUGIN);
            waitingPlayers.remove(player.getUniqueId());
        }, getConfigSection().getInt("seconds")* 20L);


    }



}

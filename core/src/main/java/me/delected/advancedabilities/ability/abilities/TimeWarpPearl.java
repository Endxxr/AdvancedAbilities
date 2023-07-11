package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.ThrowableAbility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.UUID;

public class TimeWarpPearl extends ThrowableAbility {

    private final HashMap<UUID, Location> teleportedPlayers = new HashMap<>();

    @Override
    public String getId() {
        return "time-warp-pearl";
    }

    @Override
    public boolean removeItem() {
        return true;
    }


    @Override
    public EntityType getEntityType() {
        return EntityType.ENDER_PEARL;
    }

    @Override
    public boolean allowMultipleProjectiles() {
        return false;
    }

    @Override
    public void run(Player player, ItemStack item) {
        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        addCooldown(player);
        teleportedPlayers.put(player.getUniqueId(), player.getLocation());
    }

    @EventHandler
    public void onLand(ProjectileHitEvent event){
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        final Player player = (Player) event.getEntity().getShooter();
        if (!teleportedPlayers.containsKey(player.getUniqueId())) return;
        removeThrow(player);
        Bukkit.getScheduler().runTaskLater(AdvancedAbilities.getInstance(), () -> {
            if (!teleportedPlayers.containsKey(player.getUniqueId())) return; // check if they died or something
            if (api.getAbilityManager().inSpawn(player, player.getLocation())) return;
            player.teleport(teleportedPlayers.get(player.getUniqueId()), PlayerTeleportEvent.TeleportCause.PLUGIN);
            teleportedPlayers.remove(player.getUniqueId());
            player.playSound(player.getLocation(), getSound(), 1, 1);
        }, getConfig().getInt("seconds")* 20L);
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        if (!teleportedPlayers.containsKey(event.getEntity().getUniqueId())) return;
        teleportedPlayers.remove(event.getEntity().getKiller().getUniqueId());
    }


}

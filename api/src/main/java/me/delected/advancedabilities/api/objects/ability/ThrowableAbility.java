package me.delected.advancedabilities.api.objects.ability;

import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public abstract class ThrowableAbility extends Ability implements Listener {
    protected final Set<UUID> throwList = new HashSet<>(); //Projectiles List, used to check if landed projectile is one of ours
    protected final Set<UUID> playerList = new HashSet<>(); //Players List, used to check if player has already thrown a projectile
    private final Set<UUID> waitingPlayers = new HashSet<>(); //Players waiting for projectile to be thrown


    public abstract EntityType getEntityType();
    public abstract boolean allowMultipleProjectiles();

    @EventHandler
    public void onLaunch(ProjectileLaunchEvent event) {

        if (event.isCancelled()) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) event.getEntity().getShooter();
        if (shooter == null) return;
        if (event.getEntity().getType() != getEntityType()) return;
        if (!waitingPlayers.contains(shooter.getUniqueId())) return;

        waitingPlayers.remove(shooter.getUniqueId());
        event.getEntity().setMetadata("ability_id", new FixedMetadataValue(AdvancedProvider.getAPI().getPlugin(), getId()));
        throwList.add(event.getEntity().getUniqueId());
    }

    public abstract void run(Player player, ItemStack item);
    public void onHit(Player player, Player hit, ItemStack item) {}
    public boolean isRunnable(Player player, ItemStack item) { // True = cant run, false = can run
        if (item == null || !item.getType().equals(getItem().getType())) return false;
        if (AdvancedProvider.getAPI().getAbilityManager().getAbilityByItem(item) != this) return false; //Avoid bugs
        AbilityManager abilityManager = AdvancedProvider.getAPI().getAbilityManager();
        if (AdvancedProvider.getAPI().getAbilityManager().inSpawn(player, player.getLocation())) {
            return true;
        }
        if (abilityManager.inCooldown(player, this)) return false;
        if (getConfig().getBoolean("multipleProjectiles") && playerList.contains(player.getUniqueId()) && !allowMultipleProjectiles()) {
            player.sendMessage(ChatUtils.colorize(getConfig().getString("messages.wait")));
            return true;
        }
        return false;
    }

    public boolean isHittable(Player shooter, Player hit, Projectile projectile) {

        if (projectile.getType() != getEntityType()) return true;
        if (hit == null) return true;
        if (hit == shooter) return true;
        if (!(projectile.getShooter() instanceof Player)) return true;
        if (getConfig().getBoolean("multipleProjectiles") && playerList.contains(shooter.getUniqueId()) && !allowMultipleProjectiles()) {
            shooter.sendMessage(ChatUtils.colorize(getConfig().getString("messages.wait")));
            return true;
        }

        return false;
    }

    public void addThrow(Player player) {
        waitingPlayers.add(player.getUniqueId());
        if (getConfig().getBoolean("wait")) {
            playerList.add(player.getUniqueId());
        }
    }

    protected void removeThrow(Player player) {
        throwList.remove(player.getUniqueId());
        if (getConfig().getBoolean("wait")) {
            playerList.remove(player.getUniqueId());
        }
    }

}

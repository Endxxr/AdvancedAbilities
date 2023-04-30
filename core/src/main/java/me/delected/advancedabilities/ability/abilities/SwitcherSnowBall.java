package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.ThrowableAbility;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.inventory.ItemStack;

public class SwitcherSnowBall extends ThrowableAbility {


    @Override
    public String getId() {
        return "switcher-snowball";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @EventHandler
    public void onSnowballHit(ProjectileHitEvent event) {
        if (event.getEntity().getType() != EntityType.SNOWBALL) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();
        if (!playerList.contains(player.getUniqueId())) return;
        playerList.remove(player.getUniqueId());
        removeThrow(player);
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.SNOWBALL;
    }

    @Override
    public boolean allowMultipleProjectiles() {
        return true;
    }

    @Override
    public void run(Player player, ItemStack item) {
        playerList.add(player.getUniqueId());
        addCooldown(player);
    }

    @Override
    public void onHit(Player player, Player hit, ItemStack item) {

        // send messages
        player.sendMessage(ChatUtils.colorize(getExecuteMessage())
                .replace("%target%", hit.getDisplayName()));

        hit.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.hit"))
                .replace("%target%", hit.getDisplayName())
                .replace("%player%", player.getDisplayName()));

        Location hitLocation = hit.getLocation().clone();
        Location shooterLocation = player.getLocation().clone();

        hit.teleport(shooterLocation);
        player.teleport(hitLocation);
    }
}

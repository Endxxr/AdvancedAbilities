package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.utils.AbilitiesUtils;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.ThrowableAbility;
import org.bukkit.entity.Egg;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class RottenEgg extends ThrowableAbility {

    @Override
    public String getId() {
        return "rotten-egg";
    }

    @Override
    public boolean removeItem() {
        return false;
    }

    @Override
    public EntityType getEntityType() {
        return EntityType.EGG;
    }

    @Override
    public boolean allowMultipleProjectiles() {
        return true;
    }

    @Override
    public void run(Player player, ItemStack item) {
        addCooldown(player);
    }

    @EventHandler
    public void onEggGroundHit(ProjectileHitEvent event) {

        if (!(event.getEntity() instanceof Egg)) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;

        UUID projectileUUID = event.getEntity().getUniqueId();
        if (!throwList.contains(projectileUUID)) return;

        throwList.remove(projectileUUID);

    }

    @Override
    public void onHit(Player player, Player hit, ItemStack item) {
        player.sendMessage(ChatUtils.colorize(getExecuteMessage())
                .replace("%target%", hit.getDisplayName()));

        hit.sendMessage(ChatUtils.colorize(getConfig().getString("messages.hit"))
                .replace("%target%", hit.getDisplayName())
                .replace("%player%", player.getDisplayName()));


        // add effects
        hit.addPotionEffects(AbilitiesUtils.getPotionEffects(this));
    }

    @EventHandler
    public void onChickenSpawn(PlayerEggThrowEvent e) {
        e.setHatching(false);
    }


}

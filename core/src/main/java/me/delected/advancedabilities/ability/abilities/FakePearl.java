package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.ThrowableAbility;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;

public class FakePearl extends ThrowableAbility {


    @Override
    public String getId() {
        return "fake-pearl";
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
        return true;
    }

    @EventHandler
    public void onPlayerTeleportPearl(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if (api.getAbilityManager().inSpawn(event.getPlayer(), event.getPlayer().getLocation())) return;
        if (playerList.contains(event.getPlayer().getUniqueId())) {
            playerList.remove(event.getPlayer().getUniqueId());
            removeThrow(event.getPlayer());
            event.setCancelled(true);
        }
    }

    @Override
    public void run(Player player, ItemStack item) {
        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        playerList.add(player.getUniqueId());
        addCooldown(player);
        playSound(player);

    }

}

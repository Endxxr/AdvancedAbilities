package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.objects.ability.Ability;
import me.delected.advancedabilities.utils.ChatUtils;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class FakePearl extends Ability implements Listener {

    private final Set<UUID> fakePearls = new HashSet<>();

    @Override
    public String getId() {
        return "fake-pearl";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @EventHandler
    public void onThrow(ProjectileLaunchEvent event) {
        if (event.isCancelled()) return;
        if (event.getEntity().getType() != EntityType.ENDER_PEARL) return;
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        Player player = (Player) event.getEntity().getShooter();


        if (fakePearls.contains(player.getUniqueId())) {
            player.sendMessage("messages.wait-before-pearl");
            return;
        }
        if (!player.getItemInHand().isSimilar(getItem())) return;
        if (AdvancedAbilities.getPlugin().getAbilityManager().inCooldown(player, this)) {
            event.setCancelled(true);
            return;
        };
        fakePearls.add(player.getUniqueId());
        addCooldown(player);
        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));

    }

    @EventHandler
    public void onPlayerTeleportPearl(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if (fakePearls.contains(event.getPlayer().getUniqueId())) {
            fakePearls.remove(event.getPlayer().getUniqueId());
            event.setCancelled(true);
        }
    }




}

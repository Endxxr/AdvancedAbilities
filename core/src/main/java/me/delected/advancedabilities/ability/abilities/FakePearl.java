package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.AbilitiesUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
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
    public void onInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == PlayerInteractEvent.Result.DENY) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null || !event.getItem().getType().equals(Material.ENDER_PEARL)) return;

        Player player = event.getPlayer();

        if (fakePearls.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.wait")));
        }

        if (AdvancedAbilities.getPlugin().getAbilityManager().getAbilityByItem(player.getItemInHand()) != this) return;

        if (!AbilitiesUtils.canExecute(player, this)) {
            event.setCancelled(true);
            return;
        }

        if (fakePearls.contains(player.getUniqueId())) {
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.wait")));
            event.setCancelled(true);
            return;
        }

        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        fakePearls.add(player.getUniqueId());
        addCooldown(player);

    }

    @EventHandler
    public void onPlayerTeleportPearl(PlayerTeleportEvent event) {
        if (event.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if (AbilitiesUtils.inSpawn(event.getPlayer(), event.getPlayer().getLocation())) return;
        if (fakePearls.contains(event.getPlayer().getUniqueId())) {
            fakePearls.remove(event.getPlayer().getUniqueId());
            event.setCancelled(true);
        }
    }




}

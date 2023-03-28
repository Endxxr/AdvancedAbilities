package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ability.TargetAbility;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AntiBlockUp extends TargetAbility {


    private final Set<UUID> blockedPlayers = new HashSet<>();

    @Override
    public String getId() {
        return "anti-blockup";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player, Player target) {

        blockedPlayers.add(target.getUniqueId());

        int seconds = getConfigSection().getInt("seconds");

        player.sendMessage(ChatUtils.colorize(getExecuteMessage()
                        .replaceAll("%player%", target.getDisplayName()))
                        .replaceAll("%seconds%", String.valueOf(seconds)));
        target.sendMessage(ChatUtils.colorize(getTargetMessage()
                .replaceAll("%seconds%", String.valueOf(seconds))
                .replaceAll("%player%", player.getDisplayName())));

        target.playSound(target.getLocation(), getSound(), 0, 1);

        addCooldown(player);
        Bukkit.getScheduler().runTaskLater(AdvancedAbilities.getPlugin(), () -> blockedPlayers.remove(target.getUniqueId()), seconds*20L);

    }

    @EventHandler
    public void onPlayerPlaceEvent(BlockPlaceEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakEvent(BlockBreakEvent event) {
        if (blockedPlayers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }





}

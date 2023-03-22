package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.objects.ability.TargetAbility;
import me.delected.advancedabilities.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Stun extends TargetAbility implements Listener {

    private final Set<UUID> stunnedPlayers = new HashSet<>();

    @Override
    public String getId() {
        return "stun";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player, Player target) {
        if (stunnedPlayers.contains(target.getUniqueId())) {
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.already-stun")));
            return;
        }
        stunnedPlayers.add(target.getUniqueId());
        addCooldown(player);

        int seconds = getConfigSection().getInt("seconds");

        player.sendMessage(ChatUtils.colorize(getExecuteMessage()
                        .replace("%seconds", String.valueOf(seconds)))
                .replace("%player%", target.getDisplayName()));
        target.sendMessage(ChatUtils.colorize(getTargetMessage()
                .replace("%player%", player.getDisplayName())
                .replace("%seconds%", String.valueOf(seconds))));
        target.playSound(target.getLocation(), getSound(), 1, 0 );
        target.setWalkSpeed(0);
        Bukkit.getScheduler().runTaskLater(AdvancedAbilities.getPlugin(), () -> {
            stunnedPlayers.remove(target.getUniqueId());
            target.setWalkSpeed(0.2F);
        }, seconds*20L);
    }


    public void onPlayerEnderpearl(ProjectileLaunchEvent event) {
        if (!(event.getEntity().getShooter() instanceof Player)) return;
        final Player shooter = (Player) event.getEntity().getShooter();
        if (!stunnedPlayers.contains(shooter.getUniqueId())) {return;}
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerBreakEvent(BlockBreakEvent event) {
        if (stunnedPlayers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }
    @EventHandler
    public void onPlayerPlaceEvent(BlockPlaceEvent event) {
        if (stunnedPlayers.contains(event.getPlayer().getUniqueId())) event.setCancelled(true);
    }


    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event) {
        if (stunnedPlayers.isEmpty()) return;
        if (!stunnedPlayers.contains(event.getPlayer().getUniqueId())) return;
        if (event.getTo().getY() > event.getFrom().getY()) {
            event.getPlayer().teleport(event.getFrom());
        }
    }


}

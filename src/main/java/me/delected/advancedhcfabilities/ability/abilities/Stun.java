package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.AdvancedHCFAbilities;
import me.delected.advancedhcfabilities.Chat;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Stun extends RemovableAbility {
    public static ArrayList<Player> stunnedPlayers = new ArrayList<>();

    public Stun() {
        super("stun", "stunner");
    }

    @Override
    public Material getMaterial() { return Material.COAL; }


    @Override
    public String getShortName() { return "stun"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getStunCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setStunCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @EventHandler
    public void onPlayerThrowPearl(ProjectileLaunchEvent e) {
        if (!(e.getEntity().getType() == EntityType.ENDER_PEARL)) return;

        if (!stunnedPlayers.contains((Player) e.getEntity().getShooter())) return;

        ((Player) e.getEntity().getShooter()).getInventory().addItem(new ItemStack(Material.ENDER_PEARL));
        e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerMoveEvent(PlayerMoveEvent e) {
        if (!stunnedPlayers.contains(e.getPlayer())) return;

        if(e.getTo().getBlockX() > e.getFrom().getBlockX() || e.getTo().getBlockX() < e.getFrom().getBlockX() || e.getTo().getBlockZ() > e.getFrom().getBlockZ() || e.getTo().getBlockZ() < e.getFrom().getBlockZ()) {
            e.getPlayer().teleport(e.getFrom());
        }
    }
    @EventHandler
    public void onPlayerBreakEvent(BlockBreakEvent e) {
        if (stunnedPlayers.contains(e.getPlayer())) e.setCancelled(true);
    }
    @EventHandler
    public void onPlayerPlaceEvent(BlockPlaceEvent e) {
        if (stunnedPlayers.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerUseStun(PlayerInteractEntityEvent e) {
        if (!(e.getPlayer().getItemInHand().getType() == Material.COAL)) return;

        if (e.getRightClicked().getType() != EntityType.PLAYER) return;

        ItemMeta handItemMeta = e.getPlayer().getItemInHand().getItemMeta();
        if (handItemMeta == null) return;
        if (handItemMeta.getDisplayName() == null) return;
        if (!handItemMeta.getDisplayName().equalsIgnoreCase(getName())) return;
        Player rc = (Player) e.getRightClicked();
        Player p = e.getPlayer();

        long timeLeft = System.currentTimeMillis() - cm.getStunCooldown(p.getUniqueId());

        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(timeLeft) - getCooldownConfig())))));
            return;
        }

        rc.sendMessage(Chat.color(config.getString("message_to_stun_hit"))
                .replace("{hitter}", p.getDisplayName()).replace("{hit}", rc.getDisplayName()));
        p.sendMessage(Chat.color(config.getString("message_to_stun_hitter", "&8You hit &9{hit} &8with a stunner!"))
                .replace("{hitter}", p.getDisplayName()).replace("{hit}", rc.getDisplayName()));
        // stunned Player stuff

        rc.playSound(rc.getLocation(), Sound.ANVIL_LAND, 1F, 0F);
        p.playSound(p.getLocation(), Sound.ANVIL_LAND, 1F, 0F);

        // remove coal
        removeFrom(p);

        stunnedPlayers.add(rc);

        Bukkit.getScheduler().runTaskLater(AdvancedHCFAbilities.plugin(), () ->
                stunnedPlayers.remove(rc), config.getLong("stun_duration", 5) * 20);
        setCooldown(p);
    }
}

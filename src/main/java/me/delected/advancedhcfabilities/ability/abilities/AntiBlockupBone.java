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
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class AntiBlockupBone extends RemovableAbility {

    public AntiBlockupBone() {
        super("anti;blockup;bone", "antibone");
    }

    public static ArrayList<Player> bonedPlayers = new ArrayList<>();
    public static HashMap<Player, Integer> boneCount = new HashMap<>();



    @Override
    public String getShortName() {
        return "anti_bone";
    }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getBoneCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setBoneCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @Override
    public Material getMaterial() { return Material.BONE; }

    @EventHandler
    public void onPlayerBreakEvent(BlockBreakEvent e) {
        if (bonedPlayers.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerPlaceEvent(BlockPlaceEvent e) {
        if (bonedPlayers.contains(e.getPlayer())) e.setCancelled(true);
    }

    @EventHandler
    public void onPlayerUseBone(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (!(p.getItemInHand().getType() == Material.BONE)) return;

        if (e.getRightClicked().getType() != EntityType.PLAYER) return;

        ItemMeta handItemMeta = p.getItemInHand().getItemMeta();
        if (handItemMeta == null) return;
        if (handItemMeta.getDisplayName() == null) return;
        if (!handItemMeta.getDisplayName().equalsIgnoreCase(getName())) return;
        Player rc = (Player) e.getRightClicked();

        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            return;
        }

//        if (checkGlobalCooldown(p)) return;

        if (!boneCount.containsKey(rc)) {
            boneCount.put(rc, 1);
        } else {
            boneCount.put(rc, boneCount.get(rc) + 1);
        }


        if (!(boneCount.get(rc) >= config.getInt("amount_of_anti_bone_hits", 3))) {
            // not enough to stun player
            rc.playSound(rc.getLocation(), Sound.NOTE_PLING, 1F, -1F);
            p.playSound(rc.getLocation(), Sound.NOTE_PLING, 1F, -1F);
            return;
        }

        boneCount.put(rc, 0);

        rc.sendMessage(Chat.color(config.getString("message_to_anti_bone_hit"))
                .replace("{hitter}", p.getDisplayName()).replace("{hit}", rc.getDisplayName()));
        p.sendMessage(Chat.color(config.getString("message_to_anti_bone_hitter"))
                .replace("{hitter}", p.getDisplayName()).replace("{hit}", rc.getDisplayName()));

        rc.playSound(rc.getLocation(), Sound.ITEM_BREAK, 1F, 0F);
        p.playSound(rc.getLocation(), Sound.ITEM_BREAK, 1F, 0F);

        removeFrom(p);

        bonedPlayers.add(rc);

        Bukkit.getScheduler().runTaskLater(AdvancedHCFAbilities.plugin(), () ->
                bonedPlayers.remove(rc), config.getLong("anti_bone_duration", 45) * 20);

        setCooldown(p);
    }
}

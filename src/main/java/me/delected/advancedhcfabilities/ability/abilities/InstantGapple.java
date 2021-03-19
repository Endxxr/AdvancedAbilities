package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import me.delected.advancedhcfabilities.ability.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class InstantGapple extends RemovableAbility implements Effect {
    List<Player> gappleCooldown = new ArrayList<>();

    public InstantGapple() {
        super("gapple", "enchantedgoldenapple");
    }

    @Override
    public Material getMaterial() { return Material.GOLDEN_APPLE; }

    @Override
    public ItemStack item() {
        ItemStack item = new ItemStack(Material.GOLDEN_APPLE, 1, (short) 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(getName());
        List<String> lore = new ArrayList<>();
        for (String str : getLore()) {
            lore.add(Chat.color(str));
        }
        meta.setLore(lore);
        item.setItemMeta(meta);

        return item;
    }

    @Override
    public String getShortName() { return "gapple"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getGappleCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setGappleCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @Override
    public void giveEffect(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 3));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 400, 1));
        p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 6000, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 6000, 0));
    }

    @EventHandler
    public void onPlayerEatApple(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getItemMeta() == null || e.getItem().getItemMeta().getDisplayName() == null) return;
        String name = e.getItem().getItemMeta().getDisplayName();
        if (!name.equalsIgnoreCase(getName())) return;
        if (e.getClickedBlock() instanceof DirectionalContainer) return;
        if (!(e.getItem().getType() == Material.GOLDEN_APPLE && e.getItem().getDurability() == (short) 1)) return;

        Player p = e.getPlayer();

        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            gappleCooldown.add(p);
            e.setCancelled(true);
            return;
        }

//        if (checkGlobalCooldown(p)) {
//            gappleCooldown.add(p);
//            e.setCancelled(true);
//            return;
//        }/

        if (isInBlacklistedArea(p)) {
            p.sendMessage(Chat.color(config.getString("ability_blacklisted_message")));
            gappleCooldown.add(p);
            e.setCancelled(true);
            return;
        }

        e.setCancelled(true);
        p.sendMessage(Chat.color(config.getString("message_to_gapple_eater")));
        removeFrom(p);

        p.setFoodLevel(20);
        giveEffect(p);

        setCooldown(p);
    }

    @EventHandler
    public void onPlayerEatGapple(PlayerItemConsumeEvent e) {
        if (gappleCooldown.contains(e.getPlayer())) {
            e.setCancelled(true);
            gappleCooldown.remove(e.getPlayer());
            e.getPlayer().getInventory().addItem(item());
        }
    }
}

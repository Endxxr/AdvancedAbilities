package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import me.delected.advancedhcfabilities.ability.Effect;
import me.delected.advancedhcfabilities.ability.GUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class PortableBard extends RemovableAbility implements GUI {

    public PortableBard() { super("portable;bard"); }

    @Override
    public String getShortName() { return "bard"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getPortableBardCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setPortableBardCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @Override
    public Material getMaterial() { return Material.GOLD_NUGGET; }

    @Override
    public void open(Player p) { p.openInventory(getInv()); }

    @EventHandler
    public void onPlayerUseSoul(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getItemMeta() == null || e.getItem().getItemMeta().getDisplayName() == null) return;
        String name = e.getItem().getItemMeta().getDisplayName();
        if (!name.equalsIgnoreCase(getName())) return;
        if (e.getClickedBlock() instanceof DirectionalContainer) return;
        if (!(e.getItem().getType() == Material.GOLD_NUGGET)) return;

        Player p = e.getPlayer();

        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            return;
        }

        removeFrom(p);
        open(p);
        setCooldown(p);
        e.setCancelled(true);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent e) {
        if (!e.getClickedInventory().getName().equals(Chat.color(config.getString("bard_gui_name")))) return;

        e.setCancelled(true);
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player p = (Player) e.getWhoClicked();
        if (e.getSlot() == 1) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, config.getInt("bard_strength_duration") * 20,
                    Chat.validateLevel(config.getInt("bard_strength_level"))));
            p.playSound(p.getLocation(), Sound.ARROW_HIT, 1F, 0F);
            p.closeInventory();
        }
        else if (e.getSlot() == 7) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, config.getInt("bard_res_duration") * 20,
                    Chat.validateLevel(config.getInt("bard_res_level"))));
            p.playSound(p.getLocation(), Sound.ARROW_HIT, 1F, 0F);
            p.closeInventory();
        }
    }

    private Inventory getInv() {
        Inventory i = Bukkit.createInventory(null, 9, Chat.color(config.getString("bard_gui_name")));

        ItemStack strength = new ItemStack(Material.BLAZE_POWDER);
        ItemMeta strengthMeta = strength.getItemMeta();
        strengthMeta.setDisplayName(ChatColor.GOLD + "Strength");
        strengthMeta.setLore(Arrays.asList(" ", ChatColor.GRAY + "Click me to receive strength!"));
        strength.setItemMeta(strengthMeta);

        ItemStack res = new ItemStack(Material.IRON_INGOT);
        ItemMeta resMeta = res.getItemMeta();
        resMeta.setDisplayName(ChatColor.WHITE + "Resistance");
        resMeta.setLore(Arrays.asList(" ", ChatColor.GRAY + "Click me to receive resistance!"));
        res.setItemMeta(resMeta);

        i.setItem(1, strength);
        i.setItem(7, res);


        return i;
    }
}

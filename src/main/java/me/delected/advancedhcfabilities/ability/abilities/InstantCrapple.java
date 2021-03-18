package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import me.delected.advancedhcfabilities.ability.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class InstantCrapple extends RemovableAbility implements Effect {

    public InstantCrapple() {
        super("crapple", "goldenapple");
    }

    @Override
    public Material getMaterial() { return Material.GOLDEN_APPLE; }

    @Override
    public String getShortName() { return "crapple"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getCrappleCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setCrappleCooldown(p.getUniqueId(), System.currentTimeMillis()); }


    @Override
    public void giveEffect(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, 2400, 0));
        p.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1));
    }

    @EventHandler
    public void onPlayerEatApple(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getItemMeta() == null || e.getItem().getItemMeta().getDisplayName() == null) return;
        String name = e.getItem().getItemMeta().getDisplayName();
        if (!name.equalsIgnoreCase(getName())) return;
        if (e.getClickedBlock() instanceof DirectionalContainer) return;
        if (!(e.getItem().getType() == Material.GOLDEN_APPLE)) return;

        Player p = e.getPlayer();

        e.setCancelled(true);
        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
                p.getInventory().addItem(item());
            }
            return;
        }

        p.sendMessage(Chat.color(config.getString("message_to_crapple_eater")));
        removeFrom(p);

        p.setFoodLevel(20);
        giveEffect(p);

        setCooldown(p);
    }
}

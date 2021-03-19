package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import me.delected.advancedhcfabilities.ability.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.material.DirectionalContainer;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.concurrent.TimeUnit;

public class RabbitsSoul extends RemovableAbility implements Effect {

    public RabbitsSoul() { super("rabbit;soul", "rabbits;soul"); }

    @Override
    public Material getMaterial() { return Material.FEATHER; }

    @Override
    public String getShortName() { return "rabbit"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getRabbitsSoulCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setRabbitsSouldCooldown(p.getUniqueId(), System.currentTimeMillis()); }


    @Override
    public void giveEffect(Player p) {
        p.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, config.getInt("jump_boost_duration") * 20, Chat.validateLevel(config.getInt("jump_boost_level"))));
        p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, config.getInt("speed_duration") * 20, Chat.validateLevel(config.getInt("speed_level"))));
    }

    @EventHandler
    public void onPlayerUseSoul(PlayerInteractEvent e) {
        if (e.getItem() == null || e.getItem().getItemMeta() == null || e.getItem().getItemMeta().getDisplayName() == null) return;
        String name = e.getItem().getItemMeta().getDisplayName();
        if (!name.equalsIgnoreCase(getName())) return;
        if (e.getClickedBlock() instanceof DirectionalContainer) return;
        if (!(e.getItem().getType() == Material.FEATHER)) return;

        Player p = e.getPlayer();

        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            return;
        }

//        if (checkGlobalCooldown(p)) return;

        p.sendMessage(Chat.color(config.getString("message_to_rabbit_user")));
        removeFrom(p);

        giveEffect(p);

        setCooldown(p);

        e.setCancelled(true);


    }
}

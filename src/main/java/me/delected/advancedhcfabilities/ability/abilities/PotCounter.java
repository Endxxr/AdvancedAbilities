package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffectType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

public class PotCounter extends RemovableAbility {

    HashSet<Short> shorts = new HashSet<Short>(){{
        add((short) 8229);
        add((short) 8261);
        add((short) 16421);
        add((short) 16453);
    }};

    public PotCounter() { super("pot;counter", "potion;counter"); }

    @Override
    public String getShortName() { return "pot_counter"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getPotCounterCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setPotCounterCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @Override
    public Material getMaterial() { return Material.STICK; }

    @EventHandler
    public void onPlayerClickWithStick(PlayerInteractEntityEvent e) {
        Player p = e.getPlayer();
        if (!(e.getRightClicked() instanceof Player)) return;
        Player rc = (Player) e.getRightClicked();

        if (p.getItemInHand() == null || p.getItemInHand().getItemMeta() == null || p.getItemInHand().getItemMeta().getDisplayName() == null) return;

        if (!p.getItemInHand().getItemMeta().getDisplayName().equalsIgnoreCase(getName())) return;

        // check cooldown
        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            return;
        }

        removeFrom(p);

        long amount = Arrays.stream(rc.getInventory().getContents())
                .filter(this::isHealthPotion)
                .count();

        p.sendMessage(Chat.color(config.getString("message_sent_to_user").replace("{count}", String.valueOf(amount)).replace("{user}", rc.getDisplayName())));

        setCooldown(p);
    }

    private boolean isHealthPotion(ItemStack item) {
        if (item == null) return false;
        return item.getType() == Material.POTION && shorts.contains(item.getDurability());
    }
}

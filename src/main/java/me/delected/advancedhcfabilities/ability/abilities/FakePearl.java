package me.delected.advancedhcfabilities.ability.abilities;

import me.delected.advancedhcfabilities.Chat;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class FakePearl extends Ability {

    public FakePearl() {
        super("fake");
    }
    List<Player> fakeList = new ArrayList<>();

    @Override
    public Material getMaterial() { return Material.ENDER_PEARL; }

    @Override
    public String getShortName() { return "fake_pearl"; }

    @Override
    public long getTimeLeft(Player p) { return System.currentTimeMillis() - cm.getFakePearlCooldown(p.getUniqueId()); }

    @Override
    public void setCooldown(Player p) { cm.setFakePearlCooldown(p.getUniqueId(), System.currentTimeMillis()); }

    @EventHandler
    public void onPlayerThrowPearl(ProjectileLaunchEvent e) {
        if (e.isCancelled()) return;
        if (e.getEntity().getType() != EntityType.ENDER_PEARL) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player p = (Player) e.getEntity().getShooter();

        if (p.getItemInHand() == null | p.getItemInHand().getItemMeta() == null || p.getItemInHand().getItemMeta().getDisplayName() == null) return;
        String name = p.getItemInHand().getItemMeta().getDisplayName();

        if (!name.equalsIgnoreCase(getName())) return;

        if (isOnCooldown(p)) {
            p.sendMessage(Chat.color(config.getString("cooldown_message")
                    .replace("{time}", String.valueOf(Math.abs(TimeUnit.MILLISECONDS.toSeconds(getTimeLeft(p)) - getCooldownConfig())))));
            p.getInventory().addItem(item());
            e.setCancelled(true);
            return;
        }

//        if (checkGlobalCooldown(p)) {
//            p.getInventory().addItem(item());
//            e.setCancelled(true);
//            return;
//        }

        // add to list
        fakeList.add(p);

        p.sendMessage(Chat.color(config.getString("message_to_fake_pearler", "&6&lAbilities &8» &7You used a &dFake Pearl&7!")));


        // add to cooldown
        setCooldown(p);
    }

    @EventHandler
    public void onPlayerTeleportPearl(PlayerTeleportEvent e) {
        if (e.getCause() != PlayerTeleportEvent.TeleportCause.ENDER_PEARL) return;
        if (fakeList.contains(e.getPlayer())) {
            fakeList.remove(e.getPlayer());
            e.setCancelled(true);
        }
    }
}

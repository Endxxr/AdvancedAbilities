package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.AbilitiesUtils;
import org.bukkit.Material;
import org.bukkit.entity.Egg;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class RottenEgg extends Ability implements Listener {

    private final Set<UUID> eggPlayers = new HashSet<>();

    @Override
    public String getId() {
        return "rotten-egg";
    }

    @Override
    public boolean removeItem() {
        return false;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.useItemInHand() == PlayerInteractEvent.Result.DENY) return;
        if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return;
        if (event.getItem() == null || !event.getItem().getType().equals(Material.EGG)) return;

        Player player = event.getPlayer();

        if (eggPlayers.contains(player.getUniqueId())) {
            event.setCancelled(true);
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.wait")));
        }

        if (AdvancedAbilities.getPlugin().getAbilityManager().getAbilityByItem(player.getItemInHand()) != this) return;

        if (!AbilitiesUtils.canExecute(player, this)) {
            event.setCancelled(true);
            return;
        }

        if (eggPlayers.contains(player.getUniqueId())) {
            player.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.wait")));
            event.setCancelled(true);
            return;
        }

        player.sendMessage(ChatUtils.colorize(getExecuteMessage()));
        eggPlayers.add(player.getUniqueId());
        addCooldown(player);

    }

    @EventHandler
    public void onPlayerHitByEgg(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player)) return;
        if (!(e.getDamager() instanceof Egg)) return;

        Player hit = (Player) e.getEntity();
        Entity damager = e.getDamager();

        Egg egg = (Egg) damager;

        if (AbilitiesUtils.isNPC(hit)) return;
        if (!(egg.getShooter() instanceof Player)) return;
        if (egg.getShooter() == hit) return;
        Player shooter = (Player) egg.getShooter();
        if (!eggPlayers.contains(shooter.getUniqueId())) return;

        eggPlayers.remove(shooter.getUniqueId());


        // send messages

        shooter.sendMessage(ChatUtils.colorize(getExecuteMessage())
                .replace("%target%", hit.getDisplayName()));

        hit.sendMessage(ChatUtils.colorize(getConfigSection().getString("messages.hit"))
                .replace("%target%", hit.getDisplayName())
                .replace("%player%", shooter.getDisplayName()));


        // add effects
        hit.addPotionEffects(AbilitiesUtils.getPotionEffects(this));

        // add to cooldown
        addCooldown(shooter);
    }

    @EventHandler
    public void onEggGroundHit(ProjectileHitEvent e) {

        if (!(e.getEntity() instanceof Egg)) return;
        if (!(e.getEntity().getShooter() instanceof Player)) return;

        Player shooter = (Player) e.getEntity().getShooter();
        if (!eggPlayers.contains(shooter.getUniqueId())) return;

        eggPlayers.remove(shooter.getUniqueId());

    }


    @EventHandler
    public void onChickenSpawn(PlayerEggThrowEvent e) {
        e.setHatching(false);
    }






}

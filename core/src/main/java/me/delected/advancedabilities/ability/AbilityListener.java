package me.delected.advancedabilities.ability;

import me.delected.advancedabilities.api.AdvancedAPI;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.enums.NMSVersion;
import me.delected.advancedabilities.api.objects.ability.Ability;
import me.delected.advancedabilities.api.objects.ability.ClickableAbility;
import me.delected.advancedabilities.api.objects.ability.TargetAbility;
import me.delected.advancedabilities.api.objects.ability.ThrowableAbility;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

public class AbilityListener implements Listener {

    private final AdvancedAPI api;

    public AbilityListener (AdvancedAPI api) {
        this.api = api;
    }


    @EventHandler
    public void onClickableAbility(PlayerInteractEvent event) {
        if (event.useItemInHand() == Event.Result.DENY) return;
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item==null) return; //Interacts with AIR
        Ability ability = api.getAbilityManager().getAbilityByItem(item);
        if (ability==null) return;
        if (checkItem(item)) {
            event.setCancelled(true);
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
            player.updateInventory();
        }
        if (ability instanceof ClickableAbility) {
            event.setCancelled(true);
            if (isNotRunnable(player, ability)) return;
            if (ability.removeItem()) {
                if (item.getAmount() == 1) {
                    player.setItemInHand(new ItemStack(Material.AIR));
                } else {
                    item.setAmount(item.getAmount() - 1);
                }
            }
            player.updateInventory();
            api.getAbilityManager().addGlobalCooldown(player);
            ((ClickableAbility) ability).run(player);

        } else if (ability instanceof ThrowableAbility) {

            if (!(event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR)) return; // It's punching something

            ThrowableAbility throwableAbility = (ThrowableAbility) ability;
            if (throwableAbility.isNotRunnable(player, event.getItem())) {
                event.setCancelled(true);
                return;
            }

            throwableAbility.run(player, event.getItem());
            throwableAbility.addThrow(player);
        }
    }

    @EventHandler
    public void onTargetAbility(EntityDamageByEntityEvent event) {

        if (event.isCancelled()) return;
        if (!(event.getEntity() instanceof Player)) return;
        Player target = (Player) event.getEntity();
        if (target.hasMetadata("NPC")) return;

        Player player;
        if (event.getDamager() instanceof Player) {
            player = (Player) event.getDamager();
        } else if (event.getDamager() instanceof org.bukkit.entity.Projectile && ((org.bukkit.entity.Projectile) event.getDamager()).getShooter() instanceof Player) {
            player = (Player) ((org.bukkit.entity.Projectile) event.getDamager()).getShooter();
        } else {
            return;
        }


        Ability ability;
        ItemStack item = player.getItemInHand();

        /*
        Quick explanation: If the damager is a projectile, it will get the ability from the metadata of the projectile,
         set in the ThrowableAbility class. If it's not a projectile, it will get the ability from the item in the player's hand.
         */


        if (event.getDamager() instanceof org.bukkit.entity.Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (target == projectile.getShooter()) return; //Damage from EnderPearl TP
            if (projectile.hasMetadata("ability_id")) {
                ability = api.getAbilityManager().getAbilityByName(projectile.getMetadata("ability_id").get(0).asString());
            } else {
                return;
            }
        } else {
            if (item == null || item.getType() == Material.AIR || item.getAmount() == 0) return;
            ability = api.getAbilityManager().getAbilityByItem(item);
        }


        if (ability==null) return;
        if (ability instanceof TargetAbility) {

            if (isNotRunnable(player, ability)) return;

            if (item.getAmount() == 1) {
                player.setItemInHand(new ItemStack(Material.AIR));
            } else {
                item.setAmount(item.getAmount() - 1);
            }

            player.updateInventory();
            api.getAbilityManager().addGlobalCooldown(player);
            ((TargetAbility) ability).processHit(player, target);

        } else if (ability instanceof ThrowableAbility) {
            ThrowableAbility throwableAbility = (ThrowableAbility) ability;
            Projectile projectile = (Projectile) event.getDamager();
            if (throwableAbility.isHittable(player, target, projectile)) return;
            throwableAbility.onHit(player, target, item);
        }
    }

    private boolean isNotRunnable(Player player, Ability ability) {

        if (!player.hasPermission("advancedabilities.ability."+ability.getId())) {
            player.sendMessage(ChatUtils.colorize(api.getConfig().getString("messages.no-permission")));
            return true;

        }
        if (api.isWorldGuardEnabled() && api.getRegionChecker().isInForbiddenRegion(player)) return true;

        if (api.getAbilityManager().inCooldown(player, ability)) return true;

        return api.getAbilityManager().inSpawn(player, player.getLocation());
    }


    //Check if the item is something that do something when right-clicked
    private boolean checkItem(ItemStack item) {

        Material material = item.getType();

        if (material.isBlock()) {
            return true;
        }

        if (NMSVersion.isLegacy()) {
            if (material.getId()==351 && item.getDurability()==15) return true; //Bone Meal
        } else {
            if (material==Material.BONE_MEAL) return true;
        }

        return material.toString().contains("SEEDS");
    }


    @EventHandler
    public void antiEat(PlayerItemConsumeEvent event) {

        if (event.isCancelled()) return;
        if (api.getAbilityManager().getAbilityByItem(event.getItem())!=null) event.setCancelled(true);

    }



}

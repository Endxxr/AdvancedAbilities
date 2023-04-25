package me.delected.advancedabilities.ability;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.ability.ClickableAbility;
import me.delected.advancedabilities.api.ability.TargetAbility;
import me.delected.advancedabilities.api.AbilitiesUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AbilityListener implements Listener {

    private final AdvancedAbilities instance = AdvancedAbilities.getPlugin();
    private final Set<String> ignoredItems = new HashSet<>();
    public AbilityListener() {
        ignoredItems.addAll(Arrays.asList("SNOWBALL", "ENDER-PEARL", "EGG"));
    }


    @EventHandler
    public void onClickableAbility(PlayerInteractEvent event) {

        if (event.useItemInHand() == Event.Result.DENY) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item==null) return; //Interacts with AIR
        if (ignoredItems.contains(item.getType().name())) return;

        Ability ability = AdvancedAbilities.getPlugin().getAbilityManager().getAbilityByItem(item);
        if (ability==null) return;

        if (item.getType().isBlock() && !item.getType().isInteractable()) {
            event.setUseInteractedBlock(Event.Result.DENY);
            event.setUseItemInHand(Event.Result.DENY);
        }

        if (!(ability instanceof ClickableAbility)) return;

        event.setCancelled(true);

        if (runAbility(player, ability)) return;

        if (ability.removeItem()) {
            if (item.getAmount()==1) {
                player.setItemInHand(new ItemStack(Material.AIR));
            } else {
                item.setAmount(item.getAmount()-1);
            }
        }

        player.updateInventory();
        instance.getAbilityManager().addGlobalCooldown(player);
        ((ClickableAbility) ability).run(player);
    }

    @EventHandler
    public void onTargetAbility(EntityDamageByEntityEvent event) {

        if (event.isCancelled()) return;
        if (!(event.getDamager() instanceof Player)) return;
        if (!(event.getEntity() instanceof Player)) return;

        Player player = (Player) event.getDamager();
        Player target = (Player) event.getEntity();

        if (target.hasMetadata("NPC")) return;

        ItemStack item = player.getItemInHand();

        if (item == null || item.getType() == Material.AIR || item.getAmount() == 0) return;

        Ability ability = instance.getAbilityManager().getAbilityByItem(item);
        if (ability==null) return;
        if (!(ability instanceof TargetAbility)) return;

        if (item.getAmount()==1) {
            player.setItemInHand(new ItemStack(Material.AIR));
        } else {
            item.setAmount(item.getAmount()-1);
        }

        if (runAbility(player, ability)) return;

        player.updateInventory();
        instance.getAbilityManager().addGlobalCooldown(player);
        ((TargetAbility) ability).processHit(player, target);
    }

    private boolean runAbility(Player player, Ability ability) {
        if (!player.hasPermission("advancedabilties.ability."+ability.getId())) {
            player.sendMessage(ChatUtils.colorize(instance.getConfig().getString("messages.no-permission")));
            return true;
        }

        if (instance.getAbilityManager().inCooldown(player, ability)) return true;

        return AbilitiesUtils.inSpawn(player, player.getLocation());
    }



    @EventHandler
    public void antiEat(PlayerItemConsumeEvent event) {

        if (event.isCancelled()) return;
        if (instance.getAbilityManager().getAbilityByItem(event.getItem())!=null) event.setCancelled(true);

    }



}

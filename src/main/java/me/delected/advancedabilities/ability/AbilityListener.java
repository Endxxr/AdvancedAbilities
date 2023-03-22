package me.delected.advancedabilities.ability;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.objects.ability.Ability;
import me.delected.advancedabilities.objects.ability.ClickableAbility;
import me.delected.advancedabilities.objects.ability.TargetAbility;
import me.delected.advancedabilities.utils.ChatUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class AbilityListener implements Listener {

    private final AdvancedAbilities instance = AdvancedAbilities.getPlugin();
    private final Set<String> ignoredItems = new HashSet<>();
    private final Location coord1;
    private final Location coord2;

    public AbilityListener() {
        ignoredItems.addAll(Arrays.asList("SNOWBALL", "ENDER-PEARL", "EGG"));


        Configuration configuration = instance.getConfig();
        coord1=new Location(
                instance.getServer().getWorld(configuration.getString("spawn-region.world")),
                configuration.getDouble("spawn-location.x1"),
                configuration.getDouble("spawn-location.y1"),
                configuration.getDouble("spawn-location.z1"));
        coord2=new Location(
                instance.getServer().getWorld(configuration.getString("spawn-region.world")),
                configuration.getDouble("spawn-location.x2"),
                configuration.getDouble("spawn-location.y2"),
                configuration.getDouble("spawn-location.z2"));


    }


    @EventHandler
    public void onClickableAbility(PlayerInteractEvent event) {

        if (event.isCancelled()) return;

        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item==null) return; //Interacts with AIR
        if (ignoredItems.contains(item.getType().name())) return;

        Ability ability = AdvancedAbilities.getPlugin().getAbilityManager().getAbilityByItem(item);
        if (ability==null) return;
        if (!(ability instanceof ClickableAbility)) return;

        event.setCancelled(true);
        event.setUseInteractedBlock(Event.Result.DENY);
        event.setUseItemInHand(Event.Result.DENY);
        
        if (!player.hasPermission("advancedabilties.ability."+ability.getId())) {
            player.sendMessage(ChatUtils.colorize(instance.getConfig().getString("messages.no-permission")));
            return;
        }

        if (instance.getAbilityManager().inCooldown(player, ability)) return;

        if (inSpawn(player.getLocation())) return;

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
        ItemStack item = player.getItemInHand();

        if (item==null) {
            player.sendMessage(ChatUtils.colorize(instance.getConfig().getString("hit-enemy")));
            return;
        }

        Ability ability = instance.getAbilityManager().getAbilityByItem(item);
        if (ability==null) return;
        if (!(ability instanceof TargetAbility)) return;

        if (item.getAmount()==1) {
            item.setType(Material.AIR);
            return;
        }
        item.setAmount(item.getAmount()-1);

        if (!player.hasPermission("advancedabilties.ability."+ability.getId())) {
            player.sendMessage(ChatUtils.colorize(instance.getConfig().getString("messages.no-permission")));
            return;
        }

        if (instance.getAbilityManager().inCooldown(player, ability)) return;

        if (inSpawn(player.getLocation())) return;

        player.updateInventory();
        instance.getAbilityManager().addGlobalCooldown(player);
        ((TargetAbility) ability).processHit(player, target);

    }



    private boolean inSpawn(Location location) {

        if ((location.getBlockX() > coord1.getBlockX()) && (location.getBlockX() < coord2.getBlockX())) {
            if ((location.getBlockY() > coord1.getBlockY()) && (location.getBlockY() < coord2.getBlockY())) {
                return (location.getBlockZ() > coord1.getBlockZ()) && (location.getBlockZ() < coord2.getBlockZ());
            }
        }
        return false;
    }

}

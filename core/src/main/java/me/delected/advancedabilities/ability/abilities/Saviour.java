package me.delected.advancedabilities.ability.abilities;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.objects.ability.Ability;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;

public class Saviour extends Ability implements Listener {
    @Override
    public String getId() {
        return "saviour";
    }

    @Override
    public boolean removeItem() {
        return true;
    }


    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player) || event.isCancelled()) return;

        Player player = (Player) event.getEntity();

        if (!(player.getHealth() - event.getFinalDamage() <= 0)) return;
        if (AdvancedAbilities.getInstance().getAbilityManager().inCooldown(player, this)) return;

        int position = getSaviourPosition(player.getInventory());
        if (position < 0) return;

        ItemStack savior = player.getInventory().getItem(position);

        if (savior.getAmount()==1) {
            player.getInventory().setItem(position, null);
        } else {
            savior.setAmount(savior.getAmount()-1);
        }

        event.setCancelled(true);
        player.setHealth(4);
        player.addPotionEffects(getEffects());

        addCooldown(player);
    }



    public List<PotionEffect> getEffects() {
        final List<PotionEffect> temp = new ArrayList<>();
        final int duration = getConfig().getInt("duration");
        for (String effects : getConfig().getStringList("effects")) {
            final String[] split = effects.split(":");
            temp.add(new PotionEffect(PotionEffectType.getByName(split[0]), duration*20, Integer.parseInt(split[1]), false, false));
        }
        return temp;
    }

    public int getSaviourPosition(Inventory inventory) {

        ItemStack[] contents = inventory.getContents();

        for (int i = 0; i < contents.length; i++) {
            if (getItem().isSimilar(contents[i])) return i;
        }
        return -1;

    }


}

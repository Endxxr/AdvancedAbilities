package me.delected.advancedabilities.legacy;

import me.delected.advancedabilities.api.AdvancedAPI;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.objects.ItemGenerator;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import javax.swing.text.html.parser.Entity;
import java.util.HashMap;
import java.util.List;

public class LegacyItemGenerator implements ItemGenerator {

    private final HashMap<String, Material> FORCED_ITEMS = new HashMap<String, Material>() {{
        put("fake-pearl", Material.ENDER_PEARL);
        put("timewarp-pearl", Material.ENDER_PEARL);
        put("instant-crapple", Material.GOLDEN_APPLE);
        put("instant-gapple", Material.GOLDEN_APPLE);
        put("grappling-hook", Material.FISHING_ROD);
        put("rotten-egg", Material.EGG);
        put("switcher-snowball", Material.SNOW_BALL);
    }};


    @Override
    public ItemStack createItem(Ability ability) {
        Material material = FORCED_ITEMS.get(ability.getId());
        if (material==null) {
            material=Material.getMaterial(ability.getConfigSection().getString("item.material").toUpperCase());
            if (material==null) {
                material = Material.STICK;
                AdvancedAPI.Provider.getAPI().getLogger().warning("Material " + ability.getConfigSection().getString("item.material") + " for ability " + ability.getId() + " is invalid! Using STICK instead.");
            }
        }

        short data = (short) ability.getConfigSection().getInt("item.data");

        if (ability.getId().equalsIgnoreCase("instant-gapple")) data = 1;
        ItemStack item = new ItemStack(material, 1, data);

        if (ability.getConfigSection().getBoolean("item.glow")) item.addUnsafeEnchantment(Enchantment.LUCK, 1);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatUtils.colorize(ability.getConfigSection().getString("item.name")));

        List<String> lore = ability.getConfigSection().getStringList("item.lore");
        lore.replaceAll(s -> colorizeLore(s, ability));
        meta.setLore(lore);

        meta.spigot().setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);


        return item;
    }


    @Override
    public HashMap<String, Material> getForcedItems() {
        return FORCED_ITEMS;
    }

    private String colorizeLore(String lore, Ability ability) {
        String seconds = ability.getConfigSection().getString("seconds");
        if (seconds == null) seconds = "0";
        return ChatUtils.colorize(lore.replaceAll("%cooldown%", ChatUtils.parseTime(ability.getCooldownTime())).replaceAll("%seconds%", seconds));
    }
}

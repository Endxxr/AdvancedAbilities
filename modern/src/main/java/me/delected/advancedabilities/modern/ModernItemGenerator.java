package me.delected.advancedabilities.modern;

import de.tr7zw.nbtapi.NBTItem;
import me.delected.advancedabilities.api.AdvancedAPI;
import me.delected.advancedabilities.api.AdvancedProvider;
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

public class ModernItemGenerator implements ItemGenerator {

    private final HashMap<String, Material> FORCED_ITEMS = new HashMap<String, Material>() {{
        put("fake-pearl", Material.ENDER_PEARL);
        put("timewarp-pearl", Material.ENDER_PEARL);
        put("instant-crapple", Material.ENCHANTED_GOLDEN_APPLE);
        put("instant-gapple", Material.ENCHANTED_BOOK);
        put("grappling-hook", Material.FISHING_ROD);
        put("rotten-egg", Material.EGG);
        put("switcher-snowball", Material.SNOWBALL);
    }};


    //TODO add custom model data
    @Override
    public ItemStack createItem(Ability ability) {
        Material material = FORCED_ITEMS.get(ability.getId());
        if (material==null) {
            material=Material.getMaterial(ability.getConfigSection().getString("item.material").toUpperCase());
            if (material==null) {
                material = Material.STICK;
                AdvancedProvider.getAPI().getLogger().warning("Material " + ability.getConfigSection().getString("item.material") + " for ability " + ability.getId() + " is invalid! Using STICK instead.");
            }
        }

        ItemStack item = new ItemStack(material);
        if (ability.getConfigSection().getBoolean("item.glow")) item.addUnsafeEnchantment(Enchantment.LUCK, 1);

        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatUtils.colorize(ability.getConfigSection().getString("item.name")));

        List<String> lore = ability.getConfigSection().getStringList("item.lore");
        lore.forEach(ChatUtils::colorize);
        meta.setLore(lore);

        meta.setUnbreakable(true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_UNBREAKABLE);
        item.setItemMeta(meta);

        NBTItem nbtItem = new NBTItem(item);
        nbtItem.setString("ability", ability.getId());
        nbtItem.setBoolean("ability-item", true);
        item = nbtItem.getItem();

        return item;
    }

    @Override
    public HashMap<String, Material> getForcedItems() {
        return FORCED_ITEMS;
    }
}

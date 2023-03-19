package me.delected.advancedabilities.reflections.non_legacy;

import me.delected.advancedabilities.objects.ItemGenerator;
import me.delected.advancedabilities.objects.ability.Ability;
import me.delected.advancedabilities.utils.ChatUtils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

public class NonLegacyItemGenerator implements ItemGenerator {

    private final HashMap<String, Material> FORCED_ITEMS = new HashMap<String, Material>() {{
        put("fake-pearl", Material.ENDER_PEARL);
        put("timewarp-pearl", Material.ENDER_PEARL);
        put("instant-crapple", Material.ENCHANTED_GOLDEN_APPLE);
        put("instant-gapple", Material.ENCHANTED_BOOK);
        put("grappling-hook", Material.FISHING_ROD);
    }};


    //TODO add custom model data
    @Override
    public ItemStack createItem(Ability ability) {
        Material material = FORCED_ITEMS.get(ability.getId());
        if (material==null) {
            material=Material.getMaterial(ability.getConfigSection().getString("item.material"));
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


        return item;
    }

    @Override
    public HashMap<String, Material> getForcedItems() {
        return FORCED_ITEMS;
    }
}

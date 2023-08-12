package me.delected.advancedabilities.legacy;

import de.tr7zw.changeme.nbtapi.NBT;
import de.tr7zw.changeme.nbtapi.NBTItem;
import de.tr7zw.changeme.nbtapi.iface.ReadWriteNBT;
import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ItemGenerator;
import me.delected.advancedabilities.api.objects.ability.Ability;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class LegacyItemGenerator extends ItemGenerator {

    private final HashMap<String, Material> FORCED_ITEMS;

    public LegacyItemGenerator() {
        FORCED_ITEMS  = new HashMap<String, Material>() {
            {
                put("fake-pearl", Material.ENDER_PEARL);
                put("timewarp-pearl", Material.ENDER_PEARL);
                put("instant-crapple", Material.GOLDEN_APPLE);
                put("instant-gapple", Material.GOLDEN_APPLE);
                put("grappling-hook", Material.FISHING_ROD);
                put("rotten-egg", Material.EGG);
                put("switcher-snowball", Material.SNOW_BALL);
            }};
    }


    @Override
    public ItemStack createItem(Ability ability) {
        Material material = FORCED_ITEMS.get(ability.getId());
        if (material==null) {
            material=Material.getMaterial(ability.getConfig().getString("item.material").toUpperCase());
            if (material==null) {
                material = Material.STICK;
                AdvancedProvider.getAPI().getLogger().warning("Material " + ability.getConfig().getString("item.material") + " for ability " + ability.getId() + " is invalid! Using STICK instead.");
            }
        }

        short data = (short) ability.getConfig().getInt("item.data");

        if (ability.getId().equalsIgnoreCase("instant-gapple")) data = 1;
        ItemStack item = new ItemStack(material, 1, data);

        if (material == Material.SKULL_ITEM ) {
            if (data == 3) {
                String headTexture = ability.getConfig().getString("item.texture");
                if (headTexture!=null) {
                    NBT.modify(item, nbt -> {
                        ReadWriteNBT skullOwner = nbt.getOrCreateCompound("SkullOwner");
                        skullOwner.setUUID("Id", UUID.randomUUID());
                        skullOwner.getOrCreateCompound("Properties").getCompoundList("textures").addCompound().setString("Value", headTexture);
                    });
                }
            } else {
                AdvancedProvider.getAPI().getLogger().warning("You're using a skull item with a data value other than 3! You won't be able to apply a texture to it!");
            }
        }


        if (ability.getConfig().getBoolean("item.glow")) item.addUnsafeEnchantment(Enchantment.LUCK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatUtils.colorize(ability.getConfig().getString("item.name")));

        List<String> lore = ability.getConfig().getStringList("item.lore");
        lore.replaceAll(s -> colorizeLore(s, ability));
        meta.setLore(lore);

        meta.spigot().setUnbreakable(true);
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

    private String colorizeLore(String lore, Ability ability) {
        String seconds = ability.getConfig().getString("seconds");
        if (seconds == null) seconds = "0";
        return ChatUtils.colorize(lore.replaceAll("%cooldown%", ChatUtils.parseTime(ability.getCooldownTime())).replaceAll("%seconds%", seconds));
    }
}

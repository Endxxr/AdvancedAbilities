package me.delected.advancedabilities.ability.abilities;

import de.tr7zw.nbtapi.NBTItem;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.ClickableAbility;
import me.delected.advancedabilities.utils.AbilitiesUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;
import java.util.Set;

public class PortableBard extends ClickableAbility implements Listener {

    private Inventory inv;

    @Override
    public String getId() {
        return "portable-bard";
    }

    @Override
    public boolean removeItem() {
        return true;
    }

    @Override
    public void run(Player player) {
        player.openInventory(getInventory());
    }


    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getClickedInventory() == null) return;
        if (event.getClickedInventory().getName() == null) return;
        if (!event.getClickedInventory().getName().equals(inv.getName())) return;
        if (!(event.getWhoClicked() instanceof Player)) return;

        event.setCancelled(true);

        Player player = (Player) event.getWhoClicked();
        ItemStack item = event.getCurrentItem();
        NBTItem nbt = new NBTItem(item);

        if (!nbt.hasNBTData()) return;
        String effect = nbt.getString("effect");

        PotionEffect potionEffect = deserializePotionEffect(effect);
        AbilitiesUtils.addPotionEffect(player, potionEffect.getType(), potionEffect.getDuration(), player.getLevel());

        addCooldown(player);
        player.closeInventory();

    }

    private Inventory getInventory() {

        if (inv != null) return inv;

        String title = getConfig().getString("inventory.title");
        int size = getConfig().getInt("inventory.size");
        Inventory inv = Bukkit.createInventory(null, size, ChatUtils.colorize(title));

        setItems(inv);
        setFill(inv);

        this.inv = inv;
        return inv;

    }

    private void setItems(Inventory emptyInv) {

        ConfigurationSection section = getConfig().getConfigurationSection("inventory.items");
        Set<String> keys = section.getKeys(false);

        for (String key : keys) {
            if (key.equalsIgnoreCase("fill")) continue;


            String materialName = section.getString(key + ".material");
            String serializedEffect = section.getString(key + ".effect");
            PotionEffect effect = deserializePotionEffect(section.getString(key + ".effect"));

            Material material = Material.getMaterial(materialName) == null ? Material.STONE : Material.getMaterial(materialName);
            int amount = section.getInt(key + ".amount");
            int slot = section.getInt(key + ".slot");

            ItemStack stack = new ItemStack(material, amount);
            ItemMeta meta = stack.getItemMeta();
            List<String> lore = ChatUtils.colorize(section.getStringList(key + ".lore"), "%seconds%", String.valueOf(effect.getDuration()/20), "%cooldown%", String.valueOf(getCooldownTime()));
            meta.setDisplayName(ChatUtils.colorize(section.getString(key + ".name")));
            meta.setLore(lore);
            stack.setItemMeta(meta);

            NBTItem nbt = new NBTItem(stack);
            nbt.setString("effect", serializedEffect);
            stack = nbt.getItem();

            emptyInv.setItem(slot, stack);

        }

    }

    private void setFill(Inventory fillInv) {

        ConfigurationSection section = getConfig().getConfigurationSection("inventory.items.fill");

        String materialName = section.getString("material");
        Material material = Material.getMaterial(materialName) == null ? Material.STONE : Material.getMaterial(materialName);
        int amount = section.getInt("amount");
        List<String> lore = ChatUtils.colorize(section.getStringList("lore"));

        ItemStack stack = new ItemStack(material, amount);
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(ChatUtils.colorize(section.getString("name")));
        meta.setLore(lore);
        stack.setItemMeta(meta);

        for (int i = 0; i < fillInv.getSize(); i++) {
            if (fillInv.getItem(i) == null || fillInv.getItem(i).getType() == Material.AIR) {
                fillInv.setItem(i, stack);
            }
        }

    }


    private PotionEffect deserializePotionEffect(String effect) {
        String[] split = effect.split(":");
        PotionEffect potionEffect;
        try {
            potionEffect = new PotionEffect(PotionEffectType.getByName(split[0]), Integer.parseInt(split[1])*20, Integer.parseInt(split[2]));
        } catch (Exception e) {
            api.getLogger().severe("Error deserializing potion effect: " + effect);
            api.getLogger().severe("The effect doesn't exists.");
            api.getLogger().severe("Using WATER_BREATHING:1:0 instead.");
            return new PotionEffect(PotionEffectType.WATER_BREATHING, 1, 0);
        }

        return potionEffect;
    }

}

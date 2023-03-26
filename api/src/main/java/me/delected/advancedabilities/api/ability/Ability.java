package me.delected.advancedabilities.api.ability;

import lombok.Getter;
import me.delected.advancedabilities.api.AdvancedAPI;
import me.delected.advancedabilities.api.enums.NMSVersion;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Ability {

    @Getter public final ItemStack item;
    @Getter public final ConcurrentHashMap<UUID, Long> cooldownPlayers;
    public abstract String getId();
    public abstract boolean removeItem();
    public Ability() {
        cooldownPlayers = new ConcurrentHashMap<>();
        item = AdvancedAPI.Provider.getAPI().getItemGenerator().createItem(this);
    }

    public ConfigurationSection getConfigSection() {
        return AdvancedAPI.Provider.getAPI().getAbilitiesConfig().getConfigurationSection(getId());
    }

    public String getExecuteMessage() {
        return getConfigSection().getString("messages.done");
    }
    public int getCooldownTime() {
        return getConfigSection().getInt("cooldown");
    }

    public Sound getSound() {

        Sound sound;
        try {
            sound = Sound.valueOf(getConfigSection().getString("sound"));

        } catch (Exception exception) {
            sound = NMSVersion.isLegacy() ? Sound.valueOf("NOTE_PLING") : Sound.valueOf("BLOCK_NOTE_BLOCK_PLING");
            AdvancedAPI.Provider.getAPI().getLogger().warning("Invalid sound name for ability " + getId() + "!");
        }

        return sound;
    }

    public long getRemainingTime(Player player) {
        final Long cooldown = cooldownPlayers.get(player.getUniqueId());
        return (cooldown == null ? 0 : cooldown-System.currentTimeMillis());
    }

    public void addCooldown(Player player) {
        AdvancedAPI.Provider.getAPI().runTaskAsync(() -> {
            if (cooldownPlayers.containsKey(player.getUniqueId())) {
                cooldownPlayers.replace(player.getUniqueId(), getCooldownTime() * 1000L + System.currentTimeMillis());
            } else {
                cooldownPlayers.put(player.getUniqueId(), getCooldownTime() * 1000L + System.currentTimeMillis());
            }
        });
    }
}



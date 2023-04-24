package me.delected.advancedabilities.api.ability;

import lombok.Getter;
import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.enums.NMSVersion;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class Ability {

    @Getter private final ItemStack item;
    @Getter protected final ConcurrentHashMap<UUID, Long> cooldownPlayers;
    public abstract String getId();
    public abstract boolean removeItem();
    public Ability() {
        cooldownPlayers = new ConcurrentHashMap<>();
        item = AdvancedProvider.getAPI().getItemGenerator().createItem(this);
    }

    public ConfigurationSection getConfigSection() {
        return AdvancedProvider.getAPI().getAbilitiesConfig().getConfigurationSection(getId());
    }

    public String getExecuteMessage() {
        return getConfigSection().getString("messages.done");
    }
    public int getCooldownTime() {
        return getConfigSection().getInt("cooldown");
    }

    /**
     *
     * Returns the sound to play when the ability is used.
     * Only for certain abilities.
     *
     * @return the sound or NOTE_PLING (or BLOCK_NOTE_BLOCK_PLING) if invalid
     */
    public Sound getSound() {
        Sound sound;
        try {
            sound = Sound.valueOf(getConfigSection().getString("sound"));
        } catch (Exception exception) {
            sound = NMSVersion.isLegacy() ? Sound.valueOf("NOTE_PLING") : Sound.valueOf("BLOCK_NOTE_BLOCK_PLING");
            AdvancedProvider.getAPI().getLogger().warning("Invalid sound name for ability " + getId() + "!");
        }
        return sound;
    }

    /**
     *
     * Returns the remaining time in seconds.
     *
     * @param player player to check
     * @return remaining time in seconds
     */
    public long getRemainingTime(Player player) {
        final Long cooldown = cooldownPlayers.get(player.getUniqueId());
        return (cooldown == null ? 0 : cooldown-System.currentTimeMillis());
    }

    protected void addCooldown(Player player) {
        AdvancedProvider.getAPI().runTaskAsync(() -> {
            if (cooldownPlayers.containsKey(player.getUniqueId())) {
                cooldownPlayers.replace(player.getUniqueId(), getCooldownTime() * 1000L + System.currentTimeMillis());
            } else {
                cooldownPlayers.put(player.getUniqueId(), getCooldownTime() * 1000L + System.currentTimeMillis());
            }
        });
    }
}



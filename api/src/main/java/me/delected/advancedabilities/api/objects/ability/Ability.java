package me.delected.advancedabilities.api.objects.ability;

import lombok.Getter;
import me.delected.advancedabilities.api.AdvancedAPI;
import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.enums.NMSVersion;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;


/**
 *
 * Represents an ability, you can use this to create your own abilities.
 *
 */



public abstract class Ability {

    protected final AdvancedAPI api = AdvancedProvider.getAPI();
    private final ConfigurationSection config;
    @Getter
    private final ItemStack item;
    @Getter
    private final ConcurrentHashMap<UUID, Long> cooldownPlayers;

    public abstract String getId();
    public abstract boolean removeItem();


    /**
     *
     * Initializes the ability, creating its item and cooldown map.
     * Using this constructor will give you the plugin's config.
     *
     */

    public Ability() {
        this.config = api.getAbilitiesConfig().getConfigurationSection(getId());
        cooldownPlayers = new ConcurrentHashMap<>();
        item = AdvancedProvider.getAPI().getItemGenerator().createItem(this);
    }

    /**
     *
     * Initializes the ability, creating its item and cooldown map.
     * You can specify your own config section.
     *
     */

    public Ability(ConfigurationSection config) {
        this.config = config;
        cooldownPlayers = new ConcurrentHashMap<>();
        item = AdvancedProvider.getAPI().getItemGenerator().createItem(this);
    }

    public ConfigurationSection getConfig() {
        return config;
    }

    public String getExecuteMessage() {
        return getConfig().getString("messages.done");
    }
    public int getCooldownTime() {
        return getConfig().getInt("cooldown");
    }

    /**
     *
     * Returns the sound to play when the ability is used.
     * Only for certain abilities.
     *
     * @return the sound or NOTE_PLING (or BLOCK_NOTE_BLOCK_PLING) if invalid.
     * Null if not specified in the config
     */
    public Sound getSound() {
        String soundName = getConfig().getString("sound");
        if (soundName == null || soundName.equalsIgnoreCase("none")) return null;

        Sound sound;
        try {
            sound = Sound.valueOf(soundName);
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
        Long cooldown = cooldownPlayers.get(player.getUniqueId());
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

    protected void playSound(Player player) {
        Sound sound = getSound();
        if (sound == null) return;
        player.playSound(player.getLocation(), sound, 0, 1);
    }
}



package me.delected.advancedabilities.objects.ability;

import lombok.Getter;
import me.delected.advancedabilities.AdvancedAbilities;
import org.bukkit.Bukkit;
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
        item = AdvancedAbilities.getPlugin().getItemGenerator().createItem(this);
    }

    public ConfigurationSection getConfigSection() {
        return AdvancedAbilities.getPlugin().getAbilitiesConfig().getConfigurationSection(getId());
    }

    public String getExecuteMessage() {
        return getConfigSection().getString("message.done");
    }
    public int getCooldownTime() {
        return getConfigSection().getInt("cooldown");
    }

    public Sound getSound() {

        Sound sound;
        try {
            sound = Sound.valueOf(getConfigSection().getString("sound"));
        } catch (Exception exception) {
            sound = Sound.BLOCK_NOTE_BLOCK_PLING;
            AdvancedAbilities.getPlugin().getLogger().warning("Invalid sound name for ability " + getId() + "!");
        }

        return sound;
    }

    public long getRemainingTime(Player player) {
        final Long cooldown = cooldownPlayers.get(player.getUniqueId());
        return (cooldown == null ? 0 : cooldown-System.currentTimeMillis());
    }

    protected void addCooldown(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(AdvancedAbilities.getPlugin(), () -> {
            if (cooldownPlayers.containsKey(player.getUniqueId())) {
                cooldownPlayers.replace(player.getUniqueId(), getCooldownTime()*1000L+System.currentTimeMillis());
            } else {
                cooldownPlayers.put(player.getUniqueId(), getCooldownTime()*1000L+System.currentTimeMillis());
            }
        });
    }



}



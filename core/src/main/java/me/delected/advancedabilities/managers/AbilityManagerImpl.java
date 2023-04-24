package me.delected.advancedabilities.managers;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.ability.abilities.*;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.ability.TargetAbility;
import me.delected.advancedabilities.api.enums.NMSVersion;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import me.delected.advancedabilities.legacy.abilities.LegacyGrapplingHook;
import me.delected.advancedabilities.legacy.abilities.LegacySwitcherSnowBall;
import me.delected.advancedabilities.modern.abilities.ModernGrapplingHook;
import me.delected.advancedabilities.modern.abilities.ModernSwitcherSnowBall;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityManagerImpl implements AbilityManager {

    private final AdvancedAbilities instance;
    private final HashMap<String, Ability> abilities;
    @Getter
    private ConcurrentHashMap<UUID, Long> globalCooldown;
    private BukkitTask cleanupTask;

    public AbilityManagerImpl(AdvancedAbilities instance) {
        this.instance = instance;
        this.abilities = new HashMap<>();


        //Register abilities
        registerAbility(new AntiBlockUp());
        registerAbility(new Bamboozle());
        registerAbility(new FakePearl());
        registerAbility(new InstantCrapple());
        registerAbility(new InstantGapple());
        registerAbility(new Invulnerability());
        registerAbility(new Leap());
        registerAbility(new RabbitSoul());
        registerAbility(new Repair());
        registerAbility(new RepairAll());
        registerAbility(new RottenEgg());
        registerAbility(new Saviour());
        registerAbility(new Stun());
        registerAbility(new TimeWarpPearl());

        if (NMSVersion.isLegacy()) {
            registerAbility(new LegacyGrapplingHook());
            registerAbility(new LegacySwitcherSnowBall());
        } else {
            registerAbility(new ModernGrapplingHook());
            registerAbility(new ModernSwitcherSnowBall());
        }


        boolean globalCooldownEnabled = instance.getConfig().getBoolean("global-cooldown.enabled");
        if (globalCooldownEnabled) globalCooldown = new ConcurrentHashMap<>();

        instance.getLogger().info("Registered " + abilities.size() + " abilities");

        startCleanup(globalCooldownEnabled);
    }

    @Override
    public Ability getAbilityByItem(ItemStack item) {

        if (item == null || item.getType() == Material.AIR || item.getAmount() == 0) {
            AdvancedAbilities.getPlugin().getLogger().warning("Attempted to get ability from null item");
            return null;
        }

        NBTItem nbtItem = new NBTItem(item);
        if (!nbtItem.getBoolean("ability-item")) return null;

        String abilityId = nbtItem.getString("ability");
        return getAbilityByName(abilityId);
    }

    @Override
    public Ability getAbilityByName(String name) {
        return abilities.get(name);
    }

    @Override
    public List<Ability> getAbilities() {
        return new ArrayList<>(abilities.values());
    }

    //Checks and removes expired cooldowns
    public void startCleanup(boolean globalCooldownEnabled) {
        cleanupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {

            if (globalCooldownEnabled) {
                globalCooldown.forEach((uuid, aLong) -> {
                    if (aLong < System.currentTimeMillis()) globalCooldown.remove(uuid);
                });
            }

            for (Ability ability : abilities.values()) {
                ability.getCooldownPlayers().entrySet().removeIf(entry -> entry.getValue() < System.currentTimeMillis());
                if (ability instanceof TargetAbility) {
                    TargetAbility targetAbility = (TargetAbility) ability;
                    targetAbility.getHitPlayers().entrySet().removeIf(entry -> Bukkit.getPlayer(entry.getKey())==null);
                }
            }
        }, 0, 20);
    }

    public void stopCleanup() {
        cleanupTask.cancel();
    }

    @Override
    public void addGlobalCooldown(Player player) {
        if (globalCooldown==null) return;
        globalCooldown.put(player.getUniqueId(), System.currentTimeMillis() + instance.getConfig().getLong("global-cooldown.cooldown")*1000);
    }

    //Clears all cooldowns
    @Override
    public void clearCooldowns() {
        globalCooldown.clear();
        for (Ability ability : abilities.values()) {
            ability.getCooldownPlayers().clear();
            if (ability instanceof TargetAbility) {
                TargetAbility targetAbility = (TargetAbility) ability;
                targetAbility.getHitPlayers().clear();
            }
        }

    }


    @Override
    public boolean inCooldown(Player player, Ability ability) {

        if (player.hasPermission("advancedabilities.bypass.cooldown")) return false;

        //Global Cooldown
        if (globalCooldown!=null) {
            long globalWait = instance.getAbilityManager().getGlobalCooldown().get(player.getUniqueId()) / 1000;
            if (globalWait > 0) {
                player.sendMessage(ChatUtils.colorize(instance.getConfig().getString("messages.cooldown")
                        .replaceAll("%cooldown%", ChatUtils.parseTime(globalWait))));
                return true;
            }
        }

        //Ability Cooldown
        long wait = ability.getRemainingTime(player)/1000;
        if (wait>0) {
            player.sendMessage(ChatUtils.colorize(instance.getConfig().getString("messages.cooldown")
                    .replaceAll("%cooldown%", ChatUtils.parseTime(wait))));
            return true;
        }

        return false;

    }

    @Override
    public void registerAbility(Ability ability) {
        abilities.put(ability.getId(), ability);
        if (ability instanceof Listener) Bukkit.getPluginManager().registerEvents((Listener) ability, instance);
        instance.getLogger().info("Registered ability " + ability.getId());
    }


}

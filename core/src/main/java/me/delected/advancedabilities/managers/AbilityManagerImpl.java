package me.delected.advancedabilities.managers;

import lombok.Getter;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.ability.abilities.*;
import me.delected.advancedabilities.ability.abilities.AntiBlockUp;
import me.delected.advancedabilities.ability.abilities.Bamboozle;
import me.delected.advancedabilities.ability.abilities.FakePearl;
import me.delected.advancedabilities.api.ability.Ability;
import me.delected.advancedabilities.api.ability.TargetAbility;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import org.bukkit.Bukkit;
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
    private final HashMap<ItemStack, Ability> abilities;
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
        registerAbility(new GrapplingHook());


        boolean globalCooldownEnabled = instance.getConfig().getBoolean("global-cooldown.enabled");
        if (globalCooldownEnabled) globalCooldown = new ConcurrentHashMap<>();


        startCleanup(globalCooldownEnabled);
    }

    @Override
    public Ability getAbilityByItem(ItemStack item) {
        ItemStack cloned = item.clone();
        cloned.setAmount(1);
        return abilities.get(cloned);
    }

    @Override
    public Ability getAbilityByName(String name) {
        for (Ability ability : abilities.values()) {
            if (ability.getId().equalsIgnoreCase(name)) {
                return ability;
            }
        }
        return null;
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
        abilities.put(ability.getItem(), ability);

        boolean hasListener = false;
        for (Class<?> clazz : ability.getClass().getInterfaces()) {
            if (clazz.getSimpleName().equalsIgnoreCase("Listener")) {
                hasListener = true;
                break;
            }
        }

        if (hasListener) Bukkit.getPluginManager().registerEvents((Listener) ability, instance);

    }


}

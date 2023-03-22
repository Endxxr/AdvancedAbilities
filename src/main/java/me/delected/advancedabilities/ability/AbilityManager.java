package me.delected.advancedabilities.ability;

import lombok.Getter;
import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.ability.abilities.*;
import me.delected.advancedabilities.objects.ability.Ability;
import me.delected.advancedabilities.objects.ability.TargetAbility;
import me.delected.advancedabilities.utils.ChatUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityManager {

    private final AdvancedAbilities instance;
    private final HashMap<ItemStack, Ability> abilities;
    @Getter
    private ConcurrentHashMap<UUID, Long> globalCooldown;
    private BukkitTask cleanupTask;

    public AbilityManager(AdvancedAbilities instance) {
        this.instance = instance;
        this.abilities = new HashMap<>();

        PluginManager pluginManager = Bukkit.getPluginManager();

        AntiBlockUp antiBlockUp = new AntiBlockUp();
        Bamboozle bamboozle = new Bamboozle();
        FakePearl fakePearl = new FakePearl();
        GrapplingHook grapplingHook = new GrapplingHook();
        InstantCrapple instantCrapple = new InstantCrapple();
        InstantGapple instantGapple = new InstantGapple();
        Invulnerability invulnerability = new Invulnerability();
        Leap leap = new Leap();
        Repair repair = new Repair();
        RepairAll repairAll = new RepairAll();
        Saviour saviour = new Saviour();
        Stun stun = new Stun();
        TimeWarpPearl timeWarpPearl = new TimeWarpPearl();

        abilities.put(antiBlockUp.getItem(), antiBlockUp);
        abilities.put(bamboozle.getItem(), bamboozle);
        abilities.put(fakePearl.getItem(), fakePearl);
        abilities.put(grapplingHook.getItem(), grapplingHook);
        abilities.put(instantCrapple.getItem(), instantCrapple);
        abilities.put(instantGapple.getItem(), instantGapple);
        abilities.put(invulnerability.getItem(), invulnerability);
        abilities.put(leap.getItem(), leap);
        abilities.put(repair.getItem(), repair);
        abilities.put(repairAll.getItem(), repairAll);
        abilities.put(saviour.getItem(), saviour);
        abilities.put(stun.getItem(), stun);
        abilities.put(timeWarpPearl.getItem(), timeWarpPearl);

        pluginManager.registerEvents(antiBlockUp, instance);
        pluginManager.registerEvents(fakePearl, instance);
        pluginManager.registerEvents(grapplingHook, instance);
        pluginManager.registerEvents(invulnerability, instance);
        pluginManager.registerEvents(saviour, instance);
        pluginManager.registerEvents(stun, instance);
        pluginManager.registerEvents(timeWarpPearl, instance);

        if (instance.getConfig().getBoolean("global-cooldown.enabled")) globalCooldown = new ConcurrentHashMap<>();

        startCleanup();
    }


    public Ability getAbilityByItem(ItemStack item) {
        ItemStack cloned = item.clone();
        cloned.setAmount(1);
        return abilities.get(cloned);
    }

    public Ability getAbilityByName(String name) {
        for (Ability ability : abilities.values()) {
            if (ability.getId().equalsIgnoreCase(name)) {
                return ability;
            }
        }
        return null;
    }

    public List<Ability> getAbilities() {
        return new ArrayList<>(abilities.values());
    }

    //Checks and removes expired cooldowns
    public void startCleanup() {
        cleanupTask = Bukkit.getScheduler().runTaskTimerAsynchronously(instance, () -> {
            globalCooldown.forEach((uuid, aLong) -> {
                if (aLong < System.currentTimeMillis()) globalCooldown.remove(uuid);
            });
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

    public void addGlobalCooldown(Player player) {
        if (globalCooldown==null) return;
        globalCooldown.put(player.getUniqueId(), System.currentTimeMillis() + instance.getConfig().getLong("global-cooldown.cooldown")*1000);
    }

    //Clears all cooldowns
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


    public boolean inCooldown(Player player, Ability ability) {

        //Global Cooldown
        long globalWait = instance.getAbilityManager().getGlobalCooldown().get(player.getUniqueId())/1000;
        if (globalWait>0) {
            player.sendMessage(ChatUtils.colorize(instance.getConfig().getString("messages.cooldown")
                    .replaceAll("%cooldown%", ChatUtils.parseTime(globalWait))));
            return true;
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


}

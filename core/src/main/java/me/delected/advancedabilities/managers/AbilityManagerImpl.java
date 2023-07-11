package me.delected.advancedabilities.managers;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.ChatUtils;
import me.delected.advancedabilities.api.objects.ability.Ability;
import me.delected.advancedabilities.api.objects.ability.TargetAbility;
import me.delected.advancedabilities.api.objects.managers.AbilityManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class AbilityManagerImpl implements AbilityManager {

    private final AdvancedAbilities instance;
    private final HashMap<String, Ability> abilities;
    @Getter
    private final Set<UUID> projectiles;
    @Getter
    private ConcurrentHashMap<UUID, Long> globalCoolDownPlayers;
    private BukkitTask cleanupTask;
    private Location location1;
    private Location location2;


    public AbilityManagerImpl(AdvancedAbilities instance) {
        this.instance = instance;
        this.abilities = new HashMap<>();
        projectiles = new HashSet<>();

        boolean globalCooldownEnabled = instance.getConfig().getBoolean("global-cooldown.enabled");
        if (globalCooldownEnabled) globalCoolDownPlayers = new ConcurrentHashMap<>();

        setSpawn();
        startCleanup(globalCooldownEnabled);
    }

    @Override
    public Ability getAbilityByItem(ItemStack item) {

        if (item == null || item.getType() == Material.AIR || item.getAmount() == 0) {
            AdvancedAbilities.getInstance().getLogger().warning("Attempted to get ability from null item");
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
                globalCoolDownPlayers.forEach((uuid, aLong) -> {
                    if (aLong < System.currentTimeMillis()) globalCoolDownPlayers.remove(uuid);
                });
            }

            for (Ability ability : abilities.values()) {
                ability.getCooldownPlayers().entrySet().removeIf(entry -> entry.getValue() < System.currentTimeMillis());
                if (ability instanceof TargetAbility) {
                    TargetAbility targetAbility = (TargetAbility) ability;
                    targetAbility.getHitPlayers().entrySet().removeIf(entry -> Bukkit.getPlayer(entry.getKey())==null);
                }
            }
        }, 0, 20*10L); //Every 10 seconds the cooldown will be checked and removed if expired
    }

    public void stopCleanup() {
        cleanupTask.cancel();
    }

    @Override
    public void addGlobalCooldown(Player player) {
        if (globalCoolDownPlayers ==null) return;
        globalCoolDownPlayers.put(player.getUniqueId(), System.currentTimeMillis() + instance.getConfig().getLong("global-cooldown.cooldown")*1000);
    }

    //Clears all cooldowns
    @Override
    public void clearCooldowns() {
        if (globalCoolDownPlayers ==null) return;
        globalCoolDownPlayers.clear();
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
        if (globalCoolDownPlayers !=null) {
            long globalWait = globalCoolDownPlayers.get(player.getUniqueId()) / 1000;
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

        ability.getCooldownPlayers().remove(player.getUniqueId()); //Remove player from cooldown map if they are not in cooldown anymore
        return false;

    }

    @Override
    public boolean inSpawn(Player player, Location location) {

        //Sort the two coordinates in min and max coordinates
        int minX = Math.min(location1.getBlockX(), location2.getBlockX());
        int minY = Math.min(location1.getBlockY(), location2.getBlockY());
        int minZ = Math.min(location1.getBlockZ(), location2.getBlockZ());
        int maxX = Math.max(location1.getBlockX(), location2.getBlockX());
        int maxY = Math.max(location1.getBlockY(), location2.getBlockY());
        int maxZ = Math.max(location1.getBlockZ(), location2.getBlockZ());

        Location min = new Location(location.getWorld(), minX, minY, minZ);
        Location max = new Location(location.getWorld(), maxX, maxY, maxZ);

        if  (location.getBlockX() >= min.getBlockX() && location.getBlockX() <= max.getBlockX()
                && location.getBlockY() >= min.getBlockY() && location.getBlockY() <= max.getBlockY()
                && location.getBlockZ() >= min.getBlockZ() && location.getBlockZ() <= max.getBlockZ()) {
            player.sendMessage(ChatUtils.colorize(AdvancedProvider.getAPI().getConfig().getString("spawn-region.deny")));
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

    public void setSpawn() {
        FileConfiguration configuration = AdvancedProvider.getAPI().getConfig();
        location1 = new Location(
                Bukkit.getWorld(configuration.getString("spawn-region.world")),
                configuration.getDouble("spawn-region.x1"),
                configuration.getDouble("spawn-region.y1"),
                configuration.getDouble("spawn-region.z1"));

        location2 = new Location(
                Bukkit.getWorld(configuration.getString("spawn-region.world")),
                configuration.getDouble("spawn-region.x2"),
                configuration.getDouble("spawn-region.y2"),
                configuration.getDouble("spawn-region.z2"));

    }
}

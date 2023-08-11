package me.delected.advancedabilities.modern;

import com.sk89q.minecraft.util.commands.CommandException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extension.platform.Actor;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.LocalPlayer;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.RegionResultSet;
import com.sk89q.worldguard.protection.association.RegionAssociable;
import com.sk89q.worldguard.protection.flags.Flag;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.objects.managers.RegionChecker;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class WG7RegionChecker implements RegionChecker {

    private static StateFlag NO_ABILITIES_FLAG;

    @Override
    public void registerFlags() {

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {
            StateFlag flag = new StateFlag("no-abilities", false);
            registry.register(flag);
            NO_ABILITIES_FLAG = flag;
        } catch (FlagConflictException e) {
            Flag<?> existingFlag = registry.get("no-abilities");
            if (existingFlag instanceof StateFlag) {
                AdvancedProvider.getAPI().getLogger().warning("The WorldGuard flag was already registered? Have you done a reload?");
                NO_ABILITIES_FLAG = (StateFlag) existingFlag;
            }
        }

    }


    @Override
    public boolean isInForbiddenRegion(Player player) {

        CompletableFuture<Boolean> future = CompletableFuture.supplyAsync(() -> {

            LocalPlayer wgPlayer = WorldGuardPlugin.inst().wrapPlayer(player);
            ApplicableRegionSet regions = getPlayerRegions(player);
            StateFlag.State flagState = regions.queryState(wgPlayer, NO_ABILITIES_FLAG);

            return flagState == StateFlag.State.ALLOW;
        });

        boolean isInForbiddenRegion = false;
        try {
            isInForbiddenRegion = future.get();
        } catch (InterruptedException | ExecutionException e) {
            AdvancedProvider.getAPI().getLogger().severe("Error while retrieving player's WorldGuard Region!");
        }

        return isInForbiddenRegion;

    }

    private ApplicableRegionSet getPlayerRegions(Player player) {
        Location location = player.getLocation();
        BlockVector3 wgVector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        World wgWorld = BukkitAdapter.adapt(location.getWorld());
        RegionManager wgRegionManager = WorldGuard.getInstance().getPlatform().getRegionContainer().get(wgWorld);
        if (wgRegionManager == null) return new RegionResultSet(Collections.emptySet(), null);
        return wgRegionManager.getApplicableRegions(wgVector);
    }

}

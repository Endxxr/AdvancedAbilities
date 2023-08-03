package me.delected.advancedabilities.legacy;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.delected.advancedabilities.api.AdvancedProvider;
import me.delected.advancedabilities.api.objects.managers.RegionChecker;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.logging.Logger;

public class WG6RegionChecker implements RegionChecker {

    private static final String REGION_PREFIX = "no_abilities";


    @Override
    public void registerFlags() {
        Logger logger = AdvancedProvider.getAPI().getLogger();
        logger.warning("This version of WorldGuard doesn't support custom flags!");
        logger.warning("Using regions' name instead");
    }

    @Override
    public boolean isInForbiddenRegion(Player player) {

        ApplicableRegionSet regions = getPlayerRegions(player);
        for (ProtectedRegion region : regions) {
            String lowerCaseID = region.getId().toLowerCase();
            if (lowerCaseID.startsWith(REGION_PREFIX)) return true;
        }

        return false;
    }

    private ApplicableRegionSet getPlayerRegions(Player player) {
        Location location = player.getLocation();
        World world = player.getWorld();
        return WorldGuardPlugin.inst().getRegionManager(world).getApplicableRegions(location);
    }
}

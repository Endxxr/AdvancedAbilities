package me.delected.advancedabilities.api.objects.managers;

import org.bukkit.entity.Player;

public interface RegionChecker {

    void registerFlags();

    boolean isInForbiddenRegion(Player player);


}

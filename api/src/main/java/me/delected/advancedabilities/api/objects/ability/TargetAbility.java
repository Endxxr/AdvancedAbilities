package me.delected.advancedabilities.api.objects.ability;

import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public abstract class TargetAbility extends Ability implements Listener {


    @Getter
    public final ConcurrentHashMap<UUID, Integer> hitPlayers = new ConcurrentHashMap<>();

    public String getTargetMessage() {
        return getConfig().getString("messages.target");
    }
    public abstract void run(Player player, Player target);
    public int getNeededHits() {
        return getConfig().getInt("needed-hits");
    }

    public void processHit(Player player, Player target) {
        hitPlayers.putIfAbsent(player.getUniqueId(), getNeededHits());
        hitPlayers.replace(player.getUniqueId(), hitPlayers.get(player.getUniqueId())-1);
        if (hitPlayers.get(player.getUniqueId())<=0) {
            hitPlayers.remove(player.getUniqueId());
            run(player, target);
        }
    }


}

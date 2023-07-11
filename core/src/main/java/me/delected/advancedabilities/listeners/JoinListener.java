package me.delected.advancedabilities.listeners;

import me.delected.advancedabilities.AdvancedAbilities;
import me.delected.advancedabilities.api.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {

        Player player = e.getPlayer();
        if (!player.hasPermission("advancedabilities.update") || !AdvancedAbilities.getInstance().isUpdateAvailable()) {
            return;
        }

        String latestVersion = AdvancedAbilities.getInstance().getLatestVersion();

        player.sendMessage("");
        player.sendMessage(ChatUtils.colorize("&6&lAdvanced&e&lAbilities &7- &6Update Available &7(&e&l"+latestVersion+"&r&7)"));
        player.sendMessage("");

       }





}


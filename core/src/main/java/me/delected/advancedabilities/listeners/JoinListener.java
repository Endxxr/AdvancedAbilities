package me.delected.advancedabilities.listeners;

import me.delected.advancedabilities.api.AdvancedAPI;
import me.delected.advancedabilities.api.ChatUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class JoinListener implements Listener {

    private final AdvancedAPI api;

    public JoinListener(AdvancedAPI api) {
        this.api = api;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {

        Player player = event.getPlayer();
        if (!player.hasPermission("advancedabilities.update") || !api.isUpdateAvailable()) {
            return;
        }

        String latestVersion = api.getLatestVersion();

        player.sendMessage("");
        player.sendMessage(ChatUtils.colorize("&6&lAdvanced&e&lAbilities &7- &6Update Available &7(&e&l"+latestVersion+"&r&7)"));
        player.sendMessage("");

       }





}


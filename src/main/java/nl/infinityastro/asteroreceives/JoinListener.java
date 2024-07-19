package nl.infinityastro.asteroreceives;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class JoinListener implements Listener {

    private final AsteroReceives plugin;
    private final DatabaseManager databaseManager;
    private final String serverName;
    private final Map<String, String> receiveCommands;

    public JoinListener(AsteroReceives plugin, DatabaseManager databaseManager, String serverName, Map<String, String> receiveCommands) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        this.serverName = serverName;
        this.receiveCommands = receiveCommands;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Haal alle receives op van de speler uit de database
        List<String> receives = databaseManager.getReceives(playerUUID);

        // Loop door alle receives en voer de bijbehorende commando's uit als ze beginnen met servername_
        for (String receive : receives) {
            if (receive.startsWith(serverName + "_")) {
                String key = receive.replaceFirst(serverName + "_", "");
                if (receiveCommands.containsKey(key)) {
                    String command = receiveCommands.get(key);
                    if (command != null) {
                        plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%player%", player.getName()));
                        removeReceive(player.getName(), receive);
                    } else {
                        plugin.getLogger().warning("Commando voor receive '" + receive + "' is null.");
                    }
                } else {
                    plugin.getLogger().warning("Geen commando gevonden voor receive '" + receive + "'.");
                }
            }
        }
    }

    private void removeReceive(String playerName, String receive) {
        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            plugin.getLogger().warning("Speler " + playerName + " is niet online.");
            return;
        }

        databaseManager.removeReceive(targetPlayer.getUniqueId(), receive);
        plugin.getLogger().info("Receive '" + receive + "' verwijderd van speler " + playerName);
    }
}


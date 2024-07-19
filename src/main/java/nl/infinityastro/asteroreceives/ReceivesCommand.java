package nl.infinityastro.asteroreceives;

import nl.infinityastro.asteroreceives.menusystem.PlayerMenuUtility;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ReceivesCommand implements CommandExecutor, TabCompleter {

    private final AsteroReceives plugin;
    private final DatabaseManager databaseManager;

    public ReceivesCommand(AsteroReceives plugin, DatabaseManager databaseManager) {
        this.plugin = plugin;
        this.databaseManager = databaseManager;
        plugin.getCommand("receives").setExecutor(this);
        plugin.getCommand("receives").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Dit commando kan alleen uitgevoerd worden door spelers.");
            return true;
        }

        Player player = (Player) sender;

        if (args.length == 3 && args[0].equalsIgnoreCase("add")) {
            String playerName = args[1];
            String receive = args[2];
            addReceive(playerName, receive);
            return true;
        } else if (args.length == 3 && args[0].equalsIgnoreCase("remove")) {
            String playerName = args[1];
            String receive = args[2];
            removeReceive(playerName, receive);
            return true;
        } else if (args.length == 2 && args[0].equalsIgnoreCase("view")) {
            if (!player.hasPermission("receives.view")) {
                player.sendMessage("Je hebt geen toestemming om dit commando uit te voeren.");
                return true;
            }

            String targetPlayerName = args[1];
            Player targetPlayer = plugin.getServer().getPlayer(targetPlayerName);

            if (targetPlayer == null) {
                player.sendMessage("Speler " + targetPlayerName + " is niet online.");
                return true;
            }

            PlayerMenuUtility playerMenuUtility = AsteroReceives.getPlayerMenuUtility(player);
            playerMenuUtility.setTarget(targetPlayer.getUniqueId());
            new ReceivesMenu(playerMenuUtility).open();

            return true;
        }

        return false;
    }

    private void addReceive(String playerName, String receive) {
        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            plugin.getLogger().warning("Speler " + playerName + " is niet online.");
            return;
        }
        databaseManager.addReceive(targetPlayer.getUniqueId(), receive);
        plugin.getLogger().info("Receive '" + receive + "' toegevoegd aan speler " + playerName);
    }

    private void removeReceive(String playerName, String receive) {
        Player targetPlayer = plugin.getServer().getPlayer(playerName);
        if (targetPlayer == null) {
            plugin.getLogger().warning("Speler " + playerName + " is niet online.");
            return;
        }

        if (databaseManager.removeReceive(targetPlayer.getUniqueId(), receive)) {
            plugin.getLogger().info("Receive '" + receive + "' verwijderd van speler " + playerName);
        } else {
            plugin.getLogger().info("Speler " + playerName + " heeft geen receive met de naam '" + receive + "'.");
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            List<String> completions = new ArrayList<>();
            completions.add("add");
            completions.add("remove");
            completions.add("view");
            return completions;
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("view") || args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            List<String> onlinePlayers = new ArrayList<>();
            for (Player onlinePlayer : plugin.getServer().getOnlinePlayers()) {
                onlinePlayers.add(onlinePlayer.getName());
            }
            return onlinePlayers;
        }
        return Collections.emptyList();
    }
}

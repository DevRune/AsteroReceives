package nl.infinityastro.asteroreceives;

import nl.infinityastro.asteroreceives.menusystem.MenuListener;
import nl.infinityastro.asteroreceives.menusystem.PlayerMenuUtility;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public final class AsteroReceives extends JavaPlugin {

    private DatabaseManager databaseManager;
    private JoinListener joinListener;
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig(); // Opslaan van de standaard config.yml indien deze niet bestaat

        // Database initialisatie
        setupDatabase();

        // Commando initialisatie
        getCommand("receives").setExecutor(new ReceivesCommand(this, databaseManager));

        // Configuratie laden
        String serverName = getConfig().getString("servername");
        Map<String, String> receiveCommands = new HashMap<>();
        getConfig().getConfigurationSection("receive_commands").getKeys(false).forEach(receive -> {
            receiveCommands.put(receive, getConfig().getString("receive_commands." + receive));
        });

        // JoinListener initialisatie en registratie
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        joinListener = new JoinListener(this, databaseManager, serverName, receiveCommands);
        getServer().getPluginManager().registerEvents(joinListener, this);

        getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " is ingeschakeld!");
    }

    @Override
    public void onDisable() {
        getLogger().info(getDescription().getName() + " v" + getDescription().getVersion() + " is uitgeschakeld.");
    }

    private void setupDatabase() {
        String host = getConfig().getString("database.host");
        int port = getConfig().getInt("database.port");
        String database = getConfig().getString("database.database");
        String username = getConfig().getString("database.username");
        String password = getConfig().getString("database.password");
        String type = getConfig().getString("database.type");

        String jdbcUrl = "jdbc:" + type + "://" + host + ":" + port + "/" + database;
        databaseManager = new DatabaseManager(jdbcUrl, username, password, type);
        return;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) { //See if the player has a playermenuutility "saved" for them

            //This player doesn't. Make one for them add it to the hashmap
            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p); //Return the object by using the provided player
        }
    }
}

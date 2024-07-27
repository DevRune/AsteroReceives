package nl.infinityastro.asteroreceives;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private Connection connection;
    private String url;
    private String user;
    private String password;
    private String dbType;

    public DatabaseManager(String url, String user, String password, String dbType) {
        this.connection = null;
        this.url = url;
        this.user = user;
        this.password = password;
        this.dbType = dbType.toLowerCase();
        loadDriver();
        createTable();
    }

    private void loadDriver() {
        try {
            if ("mysql".equals(dbType)) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            } else if ("mariadb".equals(dbType)) {
                Class.forName("org.mariadb.jdbc.Driver");
            } else {
                throw new IllegalArgumentException("Unsupported database type: " + dbType);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("JDBC Driver not found.", e);
        }
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_receives (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "receive TEXT NOT NULL" +
                ")";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addReceive(UUID playerUUID, String receive) {
        String sql = "INSERT INTO player_receives (player_uuid, receive) VALUES (?, ?)";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, receive);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean removeReceive(UUID playerUUID, String receive) {
        String sql = "DELETE FROM player_receives WHERE player_uuid = ? AND receive = ? LIMIT 1";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, receive);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<String> getReceives(UUID playerUUID) {
        List<String> receives = new ArrayList<>();
        String sql = "SELECT receive FROM player_receives WHERE player_uuid = ?";
        try (PreparedStatement statement = getConnection().prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                receives.add(resultSet.getString("receive"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return receives;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                if (url == null || user == null || password == null) {
                    throw new IllegalStateException("Database configuration is not set.");
                }
                connection = DriverManager.getConnection(url, user, password);
            } catch (SQLException e) {
                e.printStackTrace();
                throw new SQLException("Failed to connect to the database.");
            }
        }
        return connection;
    }
}


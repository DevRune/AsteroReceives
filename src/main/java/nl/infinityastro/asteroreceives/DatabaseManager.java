package nl.infinityastro.asteroreceives;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseManager {

    private final Connection connection;

    public DatabaseManager(Connection connection) {
        this.connection = connection;
        createTable();
    }

    private void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS player_receives (" +
                "id INT AUTO_INCREMENT PRIMARY KEY," +
                "player_uuid VARCHAR(36) NOT NULL," +
                "receive TEXT NOT NULL" +
                ")";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addReceive(UUID playerUUID, String receive) {
        String sql = "INSERT INTO player_receives (player_uuid, receive) VALUES (?, ?)";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, playerUUID.toString());
            statement.setString(2, receive);
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean removeReceive(UUID playerUUID, String receive) {
        String sql = "DELETE FROM player_receives WHERE player_uuid = ? AND receive = ? LIMIT 1";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
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
}


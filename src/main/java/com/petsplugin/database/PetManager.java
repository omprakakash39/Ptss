package com.petsplugin.database;

import com.petsplugin.PetsPlugin;
import com.petsplugin.enums.Pet;
import com.petsplugin.enums.Rarity;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PetManager {
    
    private final PetsPlugin plugin;
    private Connection connection;
    
    public PetManager(PetsPlugin plugin) {
        this.plugin = plugin;
    }
    
    public void initialize() {
        try {
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:" + plugin.getDataFolder().getAbsolutePath() + "/pets.db";
            connection = DriverManager.getConnection(url);
            createTables();
            plugin.getLogger().info("Database initialized successfully!");
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void createTables() throws SQLException {
        String createPlayerPetsTable = """
            CREATE TABLE IF NOT EXISTS player_pets (
                uuid TEXT NOT NULL,
                pet_name TEXT NOT NULL,
                rarity TEXT NOT NULL,
                amount INTEGER NOT NULL DEFAULT 1,
                PRIMARY KEY (uuid, pet_name, rarity)
            )
            """;
            
        String createActivePetsTable = """
            CREATE TABLE IF NOT EXISTS active_pets (
                uuid TEXT PRIMARY KEY,
                primary_pet TEXT,
                primary_rarity TEXT,
                secondary_pet TEXT,
                secondary_rarity TEXT
            )
            """;
            
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(createPlayerPetsTable);
            stmt.execute(createActivePetsTable);
        }
    }
    
    public void addPet(UUID uuid, Pet pet, Rarity rarity, int amount) {
        try {
            String checkQuery = "SELECT amount FROM player_pets WHERE uuid = ? AND pet_name = ? AND rarity = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, uuid.toString());
                checkStmt.setString(2, pet.name());
                checkStmt.setString(3, rarity.name());
                
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    int currentAmount = rs.getInt("amount");
                    String updateQuery = "UPDATE player_pets SET amount = ? WHERE uuid = ? AND pet_name = ? AND rarity = ?";
                    try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                        updateStmt.setInt(1, currentAmount + amount);
                        updateStmt.setString(2, uuid.toString());
                        updateStmt.setString(3, pet.name());
                        updateStmt.setString(4, rarity.name());
                        updateStmt.executeUpdate();
                    }
                } else {
                    String insertQuery = "INSERT INTO player_pets (uuid, pet_name, rarity, amount) VALUES (?, ?, ?, ?)";
                    try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                        insertStmt.setString(1, uuid.toString());
                        insertStmt.setString(2, pet.name());
                        insertStmt.setString(3, rarity.name());
                        insertStmt.setInt(4, amount);
                        insertStmt.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error adding pet: " + e.getMessage());
        }
    }
    
    public void removePet(UUID uuid, Pet pet, Rarity rarity, int amount) {
        try {
            String checkQuery = "SELECT amount FROM player_pets WHERE uuid = ? AND pet_name = ? AND rarity = ?";
            try (PreparedStatement checkStmt = connection.prepareStatement(checkQuery)) {
                checkStmt.setString(1, uuid.toString());
                checkStmt.setString(2, pet.name());
                checkStmt.setString(3, rarity.name());
                
                ResultSet rs = checkStmt.executeQuery();
                if (rs.next()) {
                    int currentAmount = rs.getInt("amount");
                    int newAmount = currentAmount - amount;
                    
                    if (newAmount <= 0) {
                        String deleteQuery = "DELETE FROM player_pets WHERE uuid = ? AND pet_name = ? AND rarity = ?";
                        try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                            deleteStmt.setString(1, uuid.toString());
                            deleteStmt.setString(2, pet.name());
                            deleteStmt.setString(3, rarity.name());
                            deleteStmt.executeUpdate();
                        }
                    } else {
                        String updateQuery = "UPDATE player_pets SET amount = ? WHERE uuid = ? AND pet_name = ? AND rarity = ?";
                        try (PreparedStatement updateStmt = connection.prepareStatement(updateQuery)) {
                            updateStmt.setInt(1, newAmount);
                            updateStmt.setString(2, uuid.toString());
                            updateStmt.setString(3, pet.name());
                            updateStmt.setString(4, rarity.name());
                            updateStmt.executeUpdate();
                        }
                    }
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error removing pet: " + e.getMessage());
        }
    }
    
    public Map<String, Integer> getPlayerPets(UUID uuid) {
        Map<String, Integer> pets = new HashMap<>();
        try {
            String query = "SELECT pet_name, rarity, amount FROM player_pets WHERE uuid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    String key = rs.getString("pet_name") + "_" + rs.getString("rarity");
                    pets.put(key, rs.getInt("amount"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting player pets: " + e.getMessage());
        }
        return pets;
    }
    
    public void setActivePets(UUID uuid, Pet primaryPet, Rarity primaryRarity, 
                             Pet secondaryPet, Rarity secondaryRarity) {
        try {
            String deleteQuery = "DELETE FROM active_pets WHERE uuid = ?";
            try (PreparedStatement deleteStmt = connection.prepareStatement(deleteQuery)) {
                deleteStmt.setString(1, uuid.toString());
                deleteStmt.executeUpdate();
            }
            
            String insertQuery = "INSERT INTO active_pets (uuid, primary_pet, primary_rarity, secondary_pet, secondary_rarity) VALUES (?, ?, ?, ?, ?)";
            try (PreparedStatement insertStmt = connection.prepareStatement(insertQuery)) {
                insertStmt.setString(1, uuid.toString());
                insertStmt.setString(2, primaryPet != null ? primaryPet.name() : null);
                insertStmt.setString(3, primaryRarity != null ? primaryRarity.name() : null);
                insertStmt.setString(4, secondaryPet != null ? secondaryPet.name() : null);
                insertStmt.setString(5, secondaryRarity != null ? secondaryRarity.name() : null);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error setting active pets: " + e.getMessage());
        }
    }
    
    public Map<String, String> getActivePets(UUID uuid) {
        Map<String, String> activePets = new HashMap<>();
        try {
            String query = "SELECT primary_pet, primary_rarity, secondary_pet, secondary_rarity FROM active_pets WHERE uuid = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    activePets.put("primary_pet", rs.getString("primary_pet"));
                    activePets.put("primary_rarity", rs.getString("primary_rarity"));
                    activePets.put("secondary_pet", rs.getString("secondary_pet"));
                    activePets.put("secondary_rarity", rs.getString("secondary_rarity"));
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting active pets: " + e.getMessage());
        }
        return activePets;
    }
    
    public int getPetCount(UUID uuid, Pet pet, Rarity rarity) {
        try {
            String query = "SELECT amount FROM player_pets WHERE uuid = ? AND pet_name = ? AND rarity = ?";
            try (PreparedStatement stmt = connection.prepareStatement(query)) {
                stmt.setString(1, uuid.toString());
                stmt.setString(2, pet.name());
                stmt.setString(3, rarity.name());
                ResultSet rs = stmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("amount");
                }
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error getting pet count: " + e.getMessage());
        }
        return 0;
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Error closing database: " + e.getMessage());
        }
    }
              }


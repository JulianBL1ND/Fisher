package me.bl1nd.fisher;

import org.bukkit.entity.Player;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerXPManager {

    private final Map<UUID, Integer> playerXPMap = new HashMap<>();
    private final Map<UUID, Integer> playerLevelMap = new HashMap<>();
    private static final int XP_PER_LEVEL = 1000;
    private final File dataFile;
    private final FileConfiguration dataConfig;

    public PlayerXPManager(File dataFolder) {
        dataFile = new File(dataFolder, "player_data.yml");
        dataConfig = YamlConfiguration.loadConfiguration(dataFile);
        loadPlayerData(); // Load data on instantiation
    }

    public void addXP(Player player, int xp) {
        UUID playerId = player.getUniqueId();
        int currentXP = playerXPMap.getOrDefault(playerId, 0);
        int newXP = currentXP + xp;
        int level = playerLevelMap.getOrDefault(playerId, 1);  // Start at level 1

        // Check for level up
        if (newXP >= XP_PER_LEVEL) {
            level++;
            newXP -= XP_PER_LEVEL;  // Carry over remaining XP to the next level
            playerLevelMap.put(playerId, level);
            player.sendMessage("Congratulations! You've leveled up to level " + level + "!");
        }

        // Update the player's XP and level
        playerXPMap.put(playerId, newXP);
        savePlayerData(player); // Save data on update
        updatePlayerXPBar(player);  // Update the XP bar with the new progress
    }

    public int getPlayerLevel(Player player) {
        return playerLevelMap.getOrDefault(player.getUniqueId(), 1);  // Start at level 1
    }

    public int getPlayerXP(Player player) {
        return playerXPMap.getOrDefault(player.getUniqueId(), 0);
    }

    public int getXPToNextLevel(Player player) {
        return XP_PER_LEVEL - getPlayerXP(player);
    }

    private void updatePlayerXPBar(Player player) {
        int currentXP = getPlayerXP(player);
        float xpProgress = (float) currentXP / XP_PER_LEVEL;  // Calculate progress towards the next level
        int level = getPlayerLevel(player);

        player.setLevel(level);  // Set the player's level above the XP bar
        player.setExp(xpProgress);  // Set the progress on the XP bar
    }

    public void resetXP(Player player) {
        UUID playerId = player.getUniqueId();
        playerXPMap.remove(playerId);
        playerLevelMap.remove(playerId);
        player.setLevel(1);  // Reset to level 1
        player.setExp(0);
        dataConfig.set(playerId.toString(), null); // Remove player data from the file
        saveDataFile(); // Save file after resetting
    }

    private void savePlayerData(Player player) {
        UUID playerId = player.getUniqueId();
        dataConfig.set(playerId + ".xp", playerXPMap.get(playerId));
        dataConfig.set(playerId + ".level", playerLevelMap.get(playerId));
        saveDataFile();
    }

    private void loadPlayerData() {
        for (String key : dataConfig.getKeys(false)) {
            UUID playerId = UUID.fromString(key);
            int xp = dataConfig.getInt(playerId + ".xp", 0);
            int level = dataConfig.getInt(playerId + ".level", 1);
            playerXPMap.put(playerId, xp);
            playerLevelMap.put(playerId, level);
        }
    }

    private void saveDataFile() {
        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

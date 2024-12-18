package hProjekt.controller;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller for managing the leaderboard functionality.
 * This class handles reading from, writing to, and initializing the leaderboard CSV file.
 */
public class LeaderboardController {
    private static final String CSV_PATH = "src/main/resources/leaderboard.csv";

    /**
     * Ensures the leaderboard CSV file exists.
     * If the file does not exist, it creates the file along with its parent directories
     * and writes a header row to the file.
     *
     * @throws IOException if an error occurs during file or directory creation.
     */
    public static void initializeCsv() throws IOException {
        Path csvFile = Paths.get(CSV_PATH);
        if (!Files.exists(csvFile)) {
            Files.createDirectories(csvFile.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(csvFile)) {
                writer.write("PlayerName,AI,Timestamp,Score\n"); // CSV Header
            }
        }
    }

    /**
     * Appends a new player's data to the leaderboard CSV file.
     * Ensures the file is initialized before writing. Each entry includes
     * the player's name, AI status, a timestamp, and the player's score.
     *
     * @param playerName The name of the player.
     * @param score The score achieved by the player.
     * @param ai Indicates whether the player is an AI (true) or a human (false).
     */
    public static void savePlayerData(String playerName, int score, boolean ai) {
        try {
            initializeCsv(); // Ensures the CSV exists
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_PATH, true))) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.write(String.format("%s,%b,%s,%d%n", playerName, ai, timestamp, score));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the leaderboard data from the CSV file and loads it into a list of LeaderboardEntry objects.
     * Ensures the file is initialized before reading.
     *
     * @return A list of LeaderboardEntry objects containing player data from the CSV file.
     */
    public static List<LeaderboardEntry> loadLeaderboardData() {
        List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();

        try {
            initializeCsv(); // Ensures the CSV exists
            BufferedReader reader = new BufferedReader(new FileReader(CSV_PATH));
            String line = reader.readLine(); // Skips the header row
            while ((line = reader.readLine()) != null) {
                String[] values = line.split(",");
                String playerName = values[0];
                boolean ai = Boolean.parseBoolean(values[1]);
                String timestamp = values[2];
                int score = Integer.parseInt(values[3]);
                leaderboardEntries.add(new LeaderboardEntry(playerName, ai, timestamp, score));
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return leaderboardEntries;
    }
}

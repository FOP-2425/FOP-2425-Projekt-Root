package hProjekt.controller;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class LeaderboardController {
    private static final String CSV_PATH = "src/main/resources/leaderboard.csv";

    // Initialisierung der CSV-Datei
    public static void initializeCsv() throws IOException {
        Path csvFile = Paths.get(CSV_PATH);
        if (!Files.exists(csvFile)) {
            Files.createDirectories(csvFile.getParent());
            try (BufferedWriter writer = Files.newBufferedWriter(csvFile)) {
                writer.write("PlayerName,AI,Timestamp,Score\n"); // CSV-Header
            }
        }
    }

    //  Daten in die CSV zu schreiben
    public static void savePlayerData(String playerName, int score, boolean ai) {
        try {
            initializeCsv();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(CSV_PATH, true))) {
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                writer.write(String.format("%s,%b,%s,%d%n", playerName, ai, timestamp, score));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<LeaderboardEntry> loadLeaderboardData() {
        List<LeaderboardEntry> leaderboardEntries = new ArrayList<>();

        try {
            initializeCsv(); // Stellt sicher, dass die CSV existiert
            BufferedReader reader = new BufferedReader(new FileReader(CSV_PATH));
            String line = reader.readLine(); // Überspringt die Header-Zeile
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

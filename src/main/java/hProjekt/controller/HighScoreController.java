package hProjekt.controller;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Singleton controller for managing the high score.
 */
public class HighScoreController {
    private static final String HIGHSCORE_FILE = "highscore.txt";
    private static HighScoreController instance;
    private int highScore;

    /**
     * Private constructor to enforce Singleton pattern.
     * Loads the high score from the file.
     */
    private HighScoreController() {
        loadHighScore();
    }

    /**
     * Returns the singleton instance of the HighScoreController.
     *
     * @return The singleton instance.
     */
    public static HighScoreController getInstance() {
        if (instance == null) {
            instance = new HighScoreController();
        }
        return instance;
    }

    /**
     * Returns the current high score.
     *
     * @return The high score.
     */
    public int getHighScore() {
        return highScore;
    }

    /**
     * Updates the high score if the new score is higher and saves it to the file.
     *
     * @param newScore The new score to consider.
     */
    public void updateHighScore(int newScore) {
        if (newScore > highScore) {
            highScore = newScore;
            saveHighScore();
        }
    }

    /**
     * Loads the high score from the file.
     */
    private void loadHighScore() {
        try {
            if (Files.exists(Path.of(HIGHSCORE_FILE))) {
                String content = Files.readString(Path.of(HIGHSCORE_FILE)).trim();
                highScore = Integer.parseInt(content);
            } else {
                highScore = 0;
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Failed to load high score: " + e.getMessage());
            highScore = 0;
        }
    }

    /**
     * Saves the current high score to the file.
     */
    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGHSCORE_FILE))) {
            writer.write(String.valueOf(highScore));
        } catch (IOException e) {
            System.err.println("Failed to save high score: " + e.getMessage());
        }
    }
}

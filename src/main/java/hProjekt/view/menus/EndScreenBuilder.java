package hProjekt.view.menus;

import hProjekt.controller.HighScoreController;
import hProjekt.model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;
import javafx.util.Builder;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Builder for the End Screen.
 */
public class EndScreenBuilder implements Builder<Region> {
    private final Runnable loadMainMenuAction;
    private final List<Player> players;

    public EndScreenBuilder(Runnable loadMainMenuAction, List<Player> players) {
        this.loadMainMenuAction = loadMainMenuAction;
        this.players = players;
    }

    @Override
    public Region build() {
        // Sort players by credits (descending)
        List<Player> sortedPlayers = new ArrayList<>(players);
        sortedPlayers.sort(Comparator.comparingInt(Player::getCredits).reversed());

        // Update and retrieve the Highscore
        HighScoreController highScoreController = HighScoreController.getInstance();
        int currentHighscore = highScoreController.getHighScore();
        int newScore = sortedPlayers.isEmpty() ? 0 : sortedPlayers.get(0).getCredits();
        if (newScore > currentHighscore) {
            highScoreController.updateHighScore(newScore);
            currentHighscore = newScore;
        }

        // Root container for the entire screen
        BorderPane rootContainer = new BorderPane();
        rootContainer.setStyle("-fx-background-color: #1f1f2e;"); // Dark background
        rootContainer.setPadding(new Insets(20));

        // Leaderboard container
        VBox leaderboard = new VBox(20); // Reduced vertical spacing
        leaderboard.setAlignment(Pos.CENTER_LEFT);
        leaderboard.setPadding(new Insets(0, 0, 0, 150)); // Extra left padding to shift leaderboard to the right
        leaderboard.setMaxWidth(850);
        leaderboard.setMinWidth(850);

        // Leaderboard title
        Label title = new Label("Leaderboard");
        title.setStyle("""
                -fx-font-size: 36px;
                -fx-text-fill: #ffffff;
                -fx-font-family: "Arial Black", sans-serif;
                -fx-padding: 15px 0;
                -fx-alignment: center;
                """);
        title.setAlignment(Pos.CENTER);

        // Highscore Label (Top-right corner)
        HBox highscoreBox = new HBox();
        highscoreBox.setAlignment(Pos.CENTER);
        highscoreBox.setPadding(new Insets(10));
        highscoreBox.setStyle("""
                -fx-background-color: rgba(0, 0, 0, 0.35);
                -fx-background-radius: 10px;
                """);
        highscoreBox.setPrefWidth(200); // Fixed width for the highscore box
        highscoreBox.setMaxWidth(200);
        highscoreBox.setPrefHeight(40);
        highscoreBox.setMaxHeight(40);

        Label highscoreLabel = new Label("Highscore: " + currentHighscore);
        highscoreLabel.setFont(new Font("Arial", 18)); // Same font size as player names
        highscoreLabel.setTextFill(Color.WHITE);
        highscoreBox.getChildren().add(highscoreLabel);

        // Align highscoreBox to the top-right corner
        StackPane topRightContainer = new StackPane(highscoreBox);
        StackPane.setAlignment(highscoreBox, Pos.TOP_RIGHT);
        StackPane.setMargin(highscoreBox, new Insets(20, 20, 0, 0)); // Add some margin from the top-right corner

        // Table settings
        double rankWidth = 70;
        double namePlaceholderWidth = 150; // Placeholder for names
        double scorePlaceholderWidth = 60; // Smaller width for scores

        // Add players to the leaderboard
        for (int i = 0; i < sortedPlayers.size(); i++) {
            Player player = sortedPlayers.get(i);

            HBox row = new HBox(20);
            row.setAlignment(Pos.CENTER_LEFT);
            row.setPrefWidth(850);
            row.setMinHeight(30); // Consistent height for rows
            row.setMaxHeight(30);

            // Rank column
            HBox rankBox = new HBox();
            rankBox.setAlignment(Pos.CENTER);
            rankBox.setMinWidth(rankWidth);
            if (i < 3) {
                ImageView medal = new ImageView();
                medal.setFitHeight(40);
                medal.setFitWidth(40);
                switch (i) {
                    case 0 -> medal.setImage(new Image(getClass().getResourceAsStream("/images/gold_medal.png")));
                    case 1 -> medal.setImage(new Image(getClass().getResourceAsStream("/images/silver_medal.png")));
                    case 2 -> medal.setImage(new Image(getClass().getResourceAsStream("/images/bronze_medal.png")));
                }
                rankBox.getChildren().add(medal);
            } else {
                Label rankLabel = new Label((i + 1) + ".");
                rankLabel.setFont(new Font("Arial", 18));
                rankLabel.setTextFill(Color.WHITE);
                rankBox.getChildren().add(rankLabel);
            }

            // Player name
            Label nameLabel = new Label(player.getName());
            nameLabel.setFont(new Font("Arial", 18));
            nameLabel.setTextFill(player.getColor());
            nameLabel.setMinWidth(namePlaceholderWidth);
            nameLabel.setMaxWidth(namePlaceholderWidth);
            nameLabel.setAlignment(Pos.CENTER_LEFT);

            // Star rating
            int credits = player.getCredits();
            int stars = Math.max(1, Math.min(credits / 500, 5));
            HBox starsBox = new HBox(5);
            starsBox.setAlignment(Pos.CENTER_LEFT);
            for (int j = 0; j < stars; j++) {
                Label star = new Label("★");
                star.setFont(new Font("Arial", 21));
                star.setTextFill(Color.GOLD);
                starsBox.getChildren().add(star);
            }
            for (int j = stars; j < 5; j++) {
                Label star = new Label("☆");
                star.setFont(new Font("Arial", 21));
                star.setTextFill(Color.GRAY);
                starsBox.getChildren().add(star);
            }

            // Credits
            Label creditsLabel = new Label(String.valueOf(credits));
            creditsLabel.setFont(new Font("Arial", 18));
            creditsLabel.setTextFill(Color.WHITE);
            creditsLabel.setMinWidth(scorePlaceholderWidth);
            creditsLabel.setAlignment(Pos.CENTER_RIGHT);

            // Add all elements to the row
            row.getChildren().addAll(rankBox, nameLabel, starsBox, creditsLabel);
            leaderboard.getChildren().add(row);
        }

        // Back to Main Menu Button
        Button backToMenuButton = new Button("Back to Main Menu");
        backToMenuButton.setFont(new Font("Arial", 18));
        backToMenuButton.setOnAction(event -> loadMainMenuAction.run());
        backToMenuButton.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #0078d7, #005bb5);
                -fx-text-fill: white;
                -fx-background-radius: 10px;
                -fx-padding: 10;
                -fx-cursor: hand;
                """);
        backToMenuButton.setOnMouseEntered(event -> backToMenuButton.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #005bb5, #003f8a);
                -fx-text-fill: white;
                -fx-background-radius: 10px;
                -fx-padding: 10;
                -fx-cursor: hand;
                """));
        backToMenuButton.setOnMouseExited(event -> backToMenuButton.setStyle("""
                -fx-background-color: linear-gradient(to bottom, #0078d7, #005bb5);
                -fx-text-fill: white;
                -fx-background-radius: 10px;
                -fx-padding: 10;
                """));

        // Place elements into the layout
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        BorderPane.setMargin(title, new Insets(20, 0, 0, 0));

        BorderPane.setAlignment(leaderboard, Pos.CENTER);
        BorderPane.setMargin(leaderboard, new Insets(0, 0, 100, 0));

        BorderPane.setAlignment(backToMenuButton, Pos.BOTTOM_CENTER);
        BorderPane.setMargin(backToMenuButton, new Insets(0, 0, 20, 0));

        rootContainer.setTop(new StackPane(title, topRightContainer));
        rootContainer.setCenter(leaderboard);
        rootContainer.setBottom(backToMenuButton);

        return rootContainer;
    }
}

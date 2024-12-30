package hProjekt.view.menus.overlays;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.model.Player;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import kotlin.jvm.JvmStatic;

public class PlayerOverlayView extends VBox {
    private Map<Player, Label> playerCreditsLabels = new HashMap<>();

    public PlayerOverlayView(List<Player> players) {
        configureOverlayStyle();

        // Title label
        Label titleLabel = new Label("Players");
        titleLabel.setFont(new javafx.scene.text.Font("Arial", 18));
        titleLabel.setTextFill(Color.WHITE);
        this.getChildren().add(titleLabel);

        // Player information
        for (Player player : players) {
            this.getChildren().add(createPlayerBox(player));
        }
    }

    private void configureOverlayStyle() {
        this.setPadding(new Insets(10));
        this.setSpacing(10);
        this.setBackground(
                new Background(new BackgroundFill(Color.rgb(42, 42, 59, 0.8), new CornerRadii(8), Insets.EMPTY)));
        this.setStyle("-fx-background-color: rgba(0, 0, 0, 0.5); -fx-background-radius: 10; -fx-padding: 10;");
        this.setAlignment(Pos.TOP_LEFT);
        this.setMaxWidth(200);
        this.setPrefHeight(USE_COMPUTED_SIZE);
    }

    private HBox createPlayerBox(Player player) {
        HBox playerBox = new HBox(10);
        playerBox.setAlignment(Pos.CENTER_LEFT);

        // Player Names with custom player color
        Label nameLabel = new Label(player.getName());
        nameLabel.setFont(new javafx.scene.text.Font("Arial", 14));
        nameLabel.setTextFill(player.getColor());

        // Credits Score
        Label creditsLabel = new Label("Credits: " + player.getCredits());
        creditsLabel.setFont(new javafx.scene.text.Font("Arial", 14));
        creditsLabel.setTextFill(Color.WHITE);
        playerCreditsLabels.put(player, creditsLabel);

        playerBox.getChildren().addAll(nameLabel, creditsLabel);

        // Add "CPU" indicator if the player is an AI
        if (player.isAi()) {
            Label cpuLabel = new Label("CPU");
            cpuLabel.setFont(new javafx.scene.text.Font("Arial", 10));
            cpuLabel.setTextFill(Color.WHITE);
            cpuLabel.setPadding(new Insets(2, 6, 2, 6)); // Padding for better appearance
            cpuLabel.setBackground(new Background(new BackgroundFill(Color.GRAY, new CornerRadii(5), Insets.EMPTY)));
            playerBox.getChildren().add(cpuLabel);
        }

        return playerBox;
    }

    /**
     * Updates the player credits labels.
     *
     * @param players the list of players to update
     */
    @StudentImplementationRequired
    public void updatePlayerCredits(List<Player> players) {
        for (Player player : players) {
            playerCreditsLabels.get(player).setText("Credits: " + player.getCredits());
        }
    }
}

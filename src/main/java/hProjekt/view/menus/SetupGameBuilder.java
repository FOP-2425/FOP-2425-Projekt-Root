package hProjekt.view.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Builder;

import java.util.ArrayList;
import java.util.List;

import hProjekt.Config;

public class SetupGameBuilder implements Builder<Region> {
    private final Runnable loadMainMenuAction;
    private final Runnable loadGameSceneAction;

    private final VBox playerContainer;
    private final List<HBox> playerBoxes;
    private final Button addPlayerButton;
    private final int maxPlayers = Config.MAX_PLAYERS;

    public SetupGameBuilder(Runnable loadGameSceneAction, Runnable loadMainMenuAction) {
        this.loadGameSceneAction = loadGameSceneAction;
        this.loadMainMenuAction = loadMainMenuAction;

        playerBoxes = new ArrayList<>();
        playerContainer = new VBox(10);
        playerContainer.setAlignment(Pos.CENTER);

        addPlayerButton = new Button("+ Add Player");
        addPlayerButton.getStyleClass().add("button-add");
        addPlayerButton.setOnAction(event -> addPlayer());
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();

        // Create "Back to Main Menu" button
        Button backButton = new Button("Back to Main Menu");
        backButton.getStyleClass().add("button-back");
        backButton.setOnAction(event -> loadMainMenuAction.run());

        // Place "Back to Main Menu" button at top-left corner
        HBox topBar = new HBox(backButton);
        topBar.setAlignment(Pos.TOP_LEFT);
        topBar.setPadding(new Insets(10));

        // Main content in center
        VBox mainContent = new VBox(20);
        mainContent.setAlignment(Pos.CENTER);
        mainContent.setPadding(new Insets(20));

        // Title
        Label titleLabel = new Label("Setup Game");
        titleLabel.getStyleClass().add("text-title");

        // Player management section
        addPlayer(); // Start with Player 1
        addPlayer(); // Start with Player 2

        // Map selection dropdown
        Label mapLabel = new Label("Select a Map:");
        mapLabel.getStyleClass().add("label");
        ComboBox<String> mapSelector = new ComboBox<>();
        mapSelector.getItems().addAll("Generate Random Map", "Germany", "Ireland", "Map Editor Custom #1"); // Example map options
        mapSelector.setMaxWidth(200);
        mapSelector.setValue("Generate Random Map"); // Default selection
        mapSelector.getStyleClass().add("combo-box");

        // Start Game button
        Button startGameButton = new Button("Start Game");
        startGameButton.getStyleClass().add("start-game-button");
        startGameButton.setOnAction(event -> loadGameSceneAction.run());

        // Layout management
        mainContent.getChildren().addAll(titleLabel, playerContainer, addPlayerButton, mapLabel, mapSelector, startGameButton);
        root.setTop(topBar);
        root.setCenter(mainContent);
        root.getStylesheets().add(getClass().getResource("/css/setupgamemenu.css").toExternalForm());

        return root;
    }

    private void addPlayer() {
        if (playerBoxes.size() >= maxPlayers) return; // Limit to maxPlayers

        HBox outerBox = new HBox();
        outerBox.setAlignment(Pos.CENTER);

        HBox playerBox = new HBox(5); 
        playerBox.setAlignment(Pos.CENTER_LEFT); 

        // Create trash can button (only visible for players 3-6)
        ImageView trashIcon = new ImageView(new Image(getClass().getResourceAsStream("/images/trash.png")));
        trashIcon.setFitWidth(20); // Set the desired size for the icon
        trashIcon.setFitHeight(20);
        trashIcon.setPreserveRatio(true);

        Button removeButton = new Button();
        removeButton.setGraphic(trashIcon);
        removeButton.setStyle("-fx-background-color: transparent; -fx-padding: 5;"); // Transparent background and padding for easier clicking
        removeButton.setOnAction(event -> removePlayer(outerBox));

        if (playerBoxes.size() < 2) {
            removeButton.setVisible(false); // Hide for first 2 players
        }

        // Centered input field for player name
        TextField playerNameField = new TextField("Player " + (playerBoxes.size() + 1));
        playerNameField.setPromptText("Enter Player Name");
        playerNameField.setMaxWidth(150);

        // Conditionally create the CPU toggle button or a placeholder region
        Region cpuPlaceholder = new Region();
        cpuPlaceholder.setPrefSize(60, 30); // Same size as the CPU button to maintain alignment
        ToggleButton cpuToggle = null;

        if (playerBoxes.size() > 0) { // Only add the CPU toggle if it's not Player 1
            cpuToggle = new ToggleButton("CPU");
            cpuToggle.getStyleClass().add("toggle-button");
            HBox.setMargin(cpuToggle, new Insets(0, 0, 0, 5)); // Margin on left side of CPU toggle button
            playerBox.getChildren().addAll(removeButton, playerNameField, cpuToggle);
        } else {
            playerBox.getChildren().addAll(removeButton, playerNameField, cpuPlaceholder); // Add placeholder
        }

        // Setting margins to reduce excessive space between elements
        HBox.setMargin(removeButton, new Insets(0, 5, 0, 0)); // Margin on right side of trashcan button
        HBox.setMargin(playerNameField, new Insets(0, 5, 0, 5)); // Margin around the text field

        outerBox.getChildren().add(playerBox);
        playerContainer.getChildren().add(outerBox);
        playerBoxes.add(outerBox);

        // Update visibility of add player button
        updateAddPlayerButtonVisibility();
    }

    private void removePlayer(HBox outerBox) {
        if (playerBoxes.size() > 2) { // Minimum 2 players
            playerContainer.getChildren().remove(outerBox);
            playerBoxes.remove(outerBox);
            updateAddPlayerButtonVisibility();
        }
    }

    private void updateAddPlayerButtonVisibility() {
        addPlayerButton.setVisible(playerBoxes.size() < maxPlayers);
    }
}

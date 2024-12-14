package hProjekt.view;

import java.util.function.Consumer;

import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class GameBoardBuilder implements Builder<Region> {
    private final Region map;
    private final Consumer<ActionEvent> endButtonAction;
    private Region gameInfoOverlay;
    private Region playerOverlay;
    private Region rollDiceOverlay;
    private Region spinCityOverlay;

    public GameBoardBuilder(final Region map, final Region gameInfoOverlay, final Region playerOverlay,
            final Region rollDiceOverlay, final Region spinCityOverlay, Consumer<ActionEvent> endButtonAction) {
        this.map = map;
        this.gameInfoOverlay = gameInfoOverlay;
        this.playerOverlay = playerOverlay;
        this.rollDiceOverlay = rollDiceOverlay;
        this.spinCityOverlay = spinCityOverlay;
        this.endButtonAction = endButtonAction;
    }

    @Override
    public Region build() {
        BorderPane mapRoot = new BorderPane();
        mapRoot.setCenter(map);

        // Create Overlays
        // PlayerOverlayView playerOverlay = new
        // PlayerOverlayView(gameController.getState().getPlayers());
        // GameInfoOverlayView gameInfoOverlay = new GameInfoOverlayView();
        // RollDiceOverlayView rollDiceOverlay = new
        // RollDiceOverlayView(gameController);

        // Debug Button
        Button endScreenButton = new Button("Go to End Screen");
        endScreenButton.setOnAction(endButtonAction::accept);

        // Wrap the button in a VBox for padding and alignment
        VBox topRightContainer = new VBox(endScreenButton);
        topRightContainer.setPadding(new Insets(10));
        topRightContainer.setAlignment(Pos.TOP_RIGHT); // Ensure alignment within the VBox
        topRightContainer.setMaxWidth(Region.USE_PREF_SIZE); // Prevent stretching
        topRightContainer.setMaxHeight(Region.USE_PREF_SIZE); // Prevent stretching

        // Update gameInfoOverlay with active player info
        // PlayerController activePlayerController =
        // gameController.getActivePlayerController();
        // gameInfoOverlay.setPlayerStatus(activePlayerController);

        // Create containers for overlays
        VBox playerOverlayContainer = new VBox(playerOverlay);
        playerOverlayContainer.setPadding(new Insets(10));
        playerOverlayContainer.setMaxHeight(Region.USE_PREF_SIZE);
        playerOverlayContainer.setMaxWidth(Region.USE_PREF_SIZE);

        VBox gameInfoOverlayContainer = new VBox(gameInfoOverlay);
        gameInfoOverlayContainer.setPadding(new Insets(10));
        gameInfoOverlayContainer.setMaxHeight(Region.USE_PREF_SIZE);
        gameInfoOverlayContainer.setMaxWidth(Region.USE_PREF_SIZE);

        // Root layout
        StackPane root = new StackPane();
        root.getChildren().addAll(mapRoot, playerOverlayContainer, gameInfoOverlayContainer, rollDiceOverlay, spinCityOverlay,
                topRightContainer);

        // Position the overlays
        StackPane.setAlignment(playerOverlayContainer, Pos.TOP_LEFT);
        StackPane.setAlignment(gameInfoOverlayContainer, Pos.TOP_CENTER);
        StackPane.setAlignment(rollDiceOverlay, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(topRightContainer, Pos.TOP_RIGHT); // Fix alignment for the top-right container
        StackPane.setAlignment(spinCityOverlay, Pos.BOTTOM_RIGHT);

        // Allow the map to process mouse events when overlays don't consume them
        makeOverlayTransparentForMouseEvents(playerOverlayContainer);
        makeOverlayTransparentForMouseEvents(gameInfoOverlayContainer);
        makeOverlayTransparentForMouseEvents(rollDiceOverlay);
        makeOverlayTransparentForMouseEvents(topRightContainer);
        makeOverlayTransparentForMouseEvents(spinCityOverlay);

        return root;
    }

    /**
     * Ensures that a Node only consumes mouse events it actually needs and passes
     * through all others.
     *
     * @param node the node to make transparent for unnecessary mouse events
     */
    private void makeOverlayTransparentForMouseEvents(Region node) {
        node.setPickOnBounds(false); // Ensures only visible parts react to mouse events
    }
}

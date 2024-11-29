package hProjekt.controller.gui.controllers.scene;

import java.util.ArrayList;
import java.util.List;

import hProjekt.Config;
import hProjekt.controller.GameController;
import hProjekt.controller.PlayerController;
import hProjekt.controller.gui.controllers.HexGridController;
import hProjekt.model.GameState;
import hProjekt.model.HexGridImpl;
import hProjekt.model.Player;
import hProjekt.view.menus.overlays.GameInfoOverlayView;
import hProjekt.view.menus.overlays.PlayerOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

public class MapSceneController implements SceneController {
    private final HexGridController controller;
    private final GameController gameController;

    public MapSceneController() {
        GameState gameState = new GameState(new HexGridImpl(Config.TOWN_NAMES), new ArrayList<>());
        this.controller = new HexGridController(gameState.getGrid());
        this.gameController = new GameController(gameState);
    }

    public MapSceneController(GameState gameState) {
        this.controller = new HexGridController(gameState.getGrid());
        this.gameController = new GameController(gameState);
    }

    @Override
    public Builder<Region> getBuilder() {
        BorderPane mapRoot = new BorderPane();
        mapRoot.setCenter(controller.getBuilder().build());
    
        // Create Overlays
        PlayerOverlayView playerOverlay = new PlayerOverlayView(gameController.getState().getPlayers());
        GameInfoOverlayView gameInfoOverlay = new GameInfoOverlayView();
        RollDiceOverlayView rollDiceOverlay = new RollDiceOverlayView(gameController);
    
        // Debug Button
        Button endScreenButton = new Button("Go to End Screen");
        endScreenButton.setOnAction(event -> {
            List<Player> players = gameController.getState().getPlayers();
            SceneController.loadEndScreenScene(players);
        });
    
        // Wrap the button in a VBox for padding and alignment
        VBox topRightContainer = new VBox(endScreenButton);
        topRightContainer.setPadding(new Insets(10));
        topRightContainer.setAlignment(Pos.TOP_RIGHT); // Ensure alignment within the VBox
        topRightContainer.setMaxWidth(Region.USE_PREF_SIZE); // Prevent stretching
        topRightContainer.setMaxHeight(Region.USE_PREF_SIZE); // Prevent stretching
    
        // Update gameInfoOverlay with active player info
        PlayerController activePlayerController = gameController.getActivePlayerController();
        gameInfoOverlay.setPlayerStatus(activePlayerController);
    
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
        root.getChildren().addAll(mapRoot, playerOverlayContainer, gameInfoOverlayContainer, rollDiceOverlay, topRightContainer);
    
        // Position the overlays
        StackPane.setAlignment(playerOverlayContainer, Pos.TOP_LEFT);
        StackPane.setAlignment(gameInfoOverlayContainer, Pos.TOP_CENTER);
        StackPane.setAlignment(rollDiceOverlay, Pos.BOTTOM_CENTER);
        StackPane.setAlignment(topRightContainer, Pos.TOP_RIGHT); // Fix alignment for the top-right container
    
        // Allow the map to process mouse events when overlays don't consume them
        makeOverlayTransparentForMouseEvents(playerOverlayContainer);
        makeOverlayTransparentForMouseEvents(gameInfoOverlayContainer);
        makeOverlayTransparentForMouseEvents(rollDiceOverlay);
        makeOverlayTransparentForMouseEvents(topRightContainer);
    
        return () -> root;
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

    @Override
    public String getTitle() {
        return "Map";
    }
}

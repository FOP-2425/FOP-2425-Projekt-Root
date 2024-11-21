package hProjekt.controller.gui.controllers.scene;

import java.util.ArrayList;
import java.util.List;

import hProjekt.Config;
import hProjekt.controller.GameController;
import hProjekt.controller.PlayerController;
import hProjekt.controller.gui.controllers.HexGridController;
import hProjekt.model.GameState;
import hProjekt.model.HexGridImpl;
import hProjekt.view.menus.overlays.GameInfoOverlayView;
import hProjekt.view.menus.overlays.PlayerOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
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
        root.getChildren().addAll(mapRoot, playerOverlayContainer, gameInfoOverlayContainer, rollDiceOverlay);

        // Position the overlays
        StackPane.setAlignment(playerOverlayContainer, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setAlignment(gameInfoOverlayContainer, javafx.geometry.Pos.TOP_CENTER);
        StackPane.setAlignment(rollDiceOverlay, javafx.geometry.Pos.BOTTOM_CENTER);

        return () -> root;
    }

    @Override
    public String getTitle() {
        return "Map";
    }
}

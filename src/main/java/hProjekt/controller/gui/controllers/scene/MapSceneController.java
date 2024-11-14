package hProjekt.controller.gui.controllers.scene;

import java.util.ArrayList;

import hProjekt.Config;
import hProjekt.controller.GameController;
import hProjekt.controller.gui.controllers.HexGridController;
import hProjekt.model.GameState;
import hProjekt.model.HexGridImpl;
import hProjekt.view.menus.overlays.PlayerOverlayView;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
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

        // Player overlay
        PlayerOverlayView playerOverlay = new PlayerOverlayView(gameController.getState().getPlayers());
        StackPane overlayContainer = new StackPane(playerOverlay);
        overlayContainer.setMaxHeight(Region.USE_PREF_SIZE);
        overlayContainer.setMaxWidth(Region.USE_PREF_SIZE);

        StackPane root = new StackPane();
        root.getChildren().addAll(mapRoot, overlayContainer);
        StackPane.setAlignment(overlayContainer, javafx.geometry.Pos.TOP_LEFT);
        StackPane.setMargin(overlayContainer, new Insets(10));

        return () -> root;
    }

    @Override
    public String getTitle() {
        return "Map";
    }
}

package hProjekt.controller.gui.controllers.scene;

import java.util.List;

import hProjekt.controller.PlayerController;
import hProjekt.controller.gui.controllers.HexGridController;
import hProjekt.controller.gui.controllers.PlayerActionsController;
import hProjekt.model.GameState;
import hProjekt.model.Player;
import hProjekt.view.GameBoardBuilder;
import hProjekt.view.menus.overlays.GameInfoOverlayView;
import hProjekt.view.menus.overlays.PlayerOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class GameBoardController implements SceneController {
    private final HexGridController controller;
    private final Builder<Region> builder;
    private final GameInfoOverlayView gameInfoOverlayView;
    private final PlayerOverlayView playerOverlayView;
    private final RollDiceOverlayView rollDiceOverlayView;

    public GameBoardController(final GameState gameState,
            final Property<PlayerController> activePlayerControllerProperty, final IntegerProperty diceRollProperty,
            final IntegerProperty roundCounterProperty) {
        PlayerActionsController playerActionsController = new PlayerActionsController(activePlayerControllerProperty);
        this.controller = new HexGridController(gameState.getGrid());
        this.gameInfoOverlayView = new GameInfoOverlayView();
        this.playerOverlayView = new PlayerOverlayView(gameState.getPlayers());
        this.rollDiceOverlayView = new RollDiceOverlayView(playerActionsController::rollDiceButtonAction);
        this.builder = new GameBoardBuilder(controller.buildView(), gameInfoOverlayView, playerOverlayView,
                rollDiceOverlayView, event -> {
                    List<Player> players = gameState.getPlayers();
                    SceneController.loadEndScreenScene(players);
                });
        activePlayerControllerProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            Platform.runLater(() -> {
                gameInfoOverlayView.setPlayerStatus(newValue.getPlayer());
                playerOverlayView.updatePlayerCredits(gameState.getPlayers());
            });
        });
        roundCounterProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            Platform.runLater(() -> {
                gameInfoOverlayView.setRound(newValue.intValue());
            });
        });
        diceRollProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            Platform.runLater(() -> {
                rollDiceOverlayView.rollDice(newValue.intValue());
            });
        });
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }

    @Override
    public String getTitle() {
        return "Map";
    }
}

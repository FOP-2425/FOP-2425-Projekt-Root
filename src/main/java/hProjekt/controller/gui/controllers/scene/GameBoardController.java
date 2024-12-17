package hProjekt.controller.gui.controllers.scene;

import java.util.List;

import hProjekt.controller.PlayerController;
import hProjekt.controller.gui.controllers.HexGridController;
import hProjekt.controller.gui.controllers.PlayerActionsController;
import hProjekt.model.City;
import hProjekt.model.GameState;
import hProjekt.model.Player;
import hProjekt.view.GameBoardBuilder;
import hProjekt.view.menus.overlays.CityOverlayView;
import hProjekt.view.menus.overlays.GameInfoOverlayView;
import hProjekt.view.menus.overlays.PlayerOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.application.Platform;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import javafx.util.Pair;

public class GameBoardController implements SceneController {
    private final HexGridController hexGridController;
    private final Builder<Region> builder;
    private final GameInfoOverlayView gameInfoOverlayView;
    private final PlayerOverlayView playerOverlayView;
    private final RollDiceOverlayView rollDiceOverlayView;
    private final CityOverlayView spinCityOverlayView;
    private final GameState gameState;

    public GameBoardController(final GameState gameState,
            final Property<PlayerController> activePlayerControllerProperty, final IntegerProperty diceRollProperty,
            final IntegerProperty roundCounterProperty, final ReadOnlyProperty<Pair<City, City>> chosenCitiesProperty) {
        this.gameState = gameState;
        this.hexGridController = new HexGridController(gameState.getGrid());
        this.gameInfoOverlayView = new GameInfoOverlayView();
        this.playerOverlayView = new PlayerOverlayView(gameState.getPlayers());
        PlayerActionsController playerActionsController = new PlayerActionsController(activePlayerControllerProperty,
                this);
        this.spinCityOverlayView = playerActionsController.getCityOverlayView();
        this.rollDiceOverlayView = playerActionsController.getRollDiceOverlayView();
        this.builder = new GameBoardBuilder(hexGridController.buildView(), gameInfoOverlayView, playerOverlayView,
                rollDiceOverlayView, spinCityOverlayView, event -> {
                    List<Player> players = gameState.getPlayers();
                    SceneController.loadEndScreenScene(players);
                });
        activePlayerControllerProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            System.out.println("Active player: " + newValue.getPlayer().getName());
            Platform.runLater(() -> {
                gameInfoOverlayView.setPlayerStatus(newValue.getPlayer());
                updatePlayerInformation();
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
        chosenCitiesProperty.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                return;
            }
            Platform.runLater(() -> {
                spinCityOverlayView.spinCities(newValue.getKey().getName(), newValue.getValue().getName(),
                        gameState.getGrid().getCities().values().stream().map(City::getName).toList());
            });
        });
    }

    /**
     * Returns the hex grid controller.
     *
     * @return the hex grid controller
     */
    public HexGridController getHexGridController() {
        return hexGridController;
    }

    public void updatePlayerInformation() {
        Platform.runLater(() -> {
            playerOverlayView.updatePlayerCredits(gameState.getPlayers());
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

package hProjekt.controller.gui.controllers.scene;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hProjekt.controller.GamePhase;
import hProjekt.controller.PlayerController;
import hProjekt.controller.gui.controllers.HexGridController;
import hProjekt.controller.gui.controllers.PlayerActionsController;
import hProjekt.controller.gui.controllers.PlayerAnimationController;
import hProjekt.model.City;
import hProjekt.model.GameState;
import hProjekt.model.Player;
import hProjekt.model.TilePosition;
import hProjekt.view.GameBoardBuilder;
import hProjekt.view.menus.overlays.ChosenCitiesOverlayView;
import hProjekt.view.menus.overlays.CityOverlayView;
import hProjekt.view.menus.overlays.ConfirmationOverlayView;
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
    private final GameBoardBuilder builder;
    private final GameInfoOverlayView gameInfoOverlayView;
    private final PlayerOverlayView playerOverlayView;
    private final RollDiceOverlayView rollDiceOverlayView;
    private final ChosenCitiesOverlayView chosenCitiesOverlayView;
    private final CityOverlayView cityOverlayView;
    private final ConfirmationOverlayView confirmationOverlayView;
    private final GameState gameState;
    private final Map<Player, PlayerAnimationController> playerAnimationControllers = new HashMap<>();

    public GameBoardController(final GameState gameState,
            final Property<PlayerController> activePlayerControllerProperty, final IntegerProperty diceRollProperty,
            final IntegerProperty roundCounterProperty, final ReadOnlyProperty<Pair<City, City>> chosenCitiesProperty) {
        this.gameState = gameState;
        this.hexGridController = new HexGridController(gameState.getGrid());
        this.gameInfoOverlayView = new GameInfoOverlayView();
        this.playerOverlayView = new PlayerOverlayView(gameState.getPlayers());
        this.cityOverlayView = new CityOverlayView(gameState);
        this.confirmationOverlayView = new ConfirmationOverlayView();
        PlayerActionsController playerActionsController = new PlayerActionsController(activePlayerControllerProperty,
                this);
        this.chosenCitiesOverlayView = playerActionsController.getChosenCitiesOverlayView();
        this.rollDiceOverlayView = playerActionsController.getRollDiceOverlayView();
        this.builder = new GameBoardBuilder(hexGridController.buildView(), gameInfoOverlayView, playerOverlayView,
                rollDiceOverlayView, chosenCitiesOverlayView, cityOverlayView, confirmationOverlayView, event -> {
                    List<Player> players = gameState.getPlayers();
                    SceneController.loadEndScreenScene(players);
                });
        for (Player player : gameState.getPlayers()) {
            playerAnimationControllers.put(player,
                    new PlayerAnimationController(hexGridController.getBuilder(), player.getColor()));
        }
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
                chosenCitiesOverlayView.spinCities(newValue.getKey().getName(), newValue.getValue().getName(),
                        gameState.getGrid().getCities().values().stream().map(City::getName).toList());
                updateCityOverlay();
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

    public PlayerAnimationController getPlayerAnimationController(Player player) {
        return playerAnimationControllers.get(player);
    }

    public void updatePlayerInformation() {
        Platform.runLater(() -> {
            playerOverlayView.updatePlayerCredits(gameState.getPlayers());
        });
    }

    public GamePhase getGamePhase() {
        return gameState.getGamePhaseProperty().getValue();
    }

    public void updateCityOverlay() {
        Platform.runLater(() -> {
            System.out.println("Update City Overlay");
            cityOverlayView.updateCityList(true);
        });
    }

    public void updateConfirmationOverlay(String message, Runnable onYesAction, Runnable onNoAction) {
        builder.addConfirmationOverlay();
        Platform.runLater(() -> {
            confirmationOverlayView.setMessage(message);
            confirmationOverlayView.setOnYesAction(onYesAction);
            confirmationOverlayView.setOnNoAction(onNoAction);
        });
    }

    public void hideConfirmationOverlay() {
        builder.removeConfirmationOverlay();
    }

    public TilePosition getPlayerPosition(Player player) {
        return gameState.getPlayerPositions().get(player);
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

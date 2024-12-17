package hProjekt.controller.gui.controllers;

import java.util.List;
import java.util.Set;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.controller.PlayerController;
import hProjekt.controller.PlayerObjective;
import hProjekt.controller.actions.BuildRailAction;
import hProjekt.controller.actions.ChooseCitiesAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.controller.actions.RollDiceAction;
import hProjekt.controller.gui.controllers.scene.GameBoardController;
import hProjekt.model.Player;
import hProjekt.model.PlayerState;
import hProjekt.view.menus.overlays.CityOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import javafx.util.Subscription;

public class PlayerActionsController implements Controller {
    private final Property<PlayerController> playerControllerProperty = new SimpleObjectProperty<>();
    private final Property<PlayerState> playerStateProperty = new SimpleObjectProperty<>();
    private Subscription playerStateSubscription = Subscription.EMPTY;
    private final RollDiceOverlayView rollDiceOverlayView;
    private final CityOverlayView cityOverlayView;
    private final GameBoardController gameBoardController;

    /**
     * Creates a new PlayerActionsController.
     * It attaches listeners to populate the playerController, playerState and
     * playerObjective properties. This is necessary to ensure these properties are
     * always on the correct thread.
     * Additionally the PlayerActionsBuilder is created with all necessary event
     * handlers.
     *
     * <b>Do not touch this constructor.</b>
     *
     * @param gameBoardController      the game board controller
     * @param playerControllerProperty the property that contains the player
     *                                 controller that is currently active
     */
    @DoNotTouch
    public PlayerActionsController(
            final Property<PlayerController> playerControllerProperty,
            GameBoardController gameBoardController) {
        this.gameBoardController = gameBoardController;
        this.rollDiceOverlayView = new RollDiceOverlayView(this::rollDiceButtonAction);
        this.cityOverlayView = new CityOverlayView(this::chooseCitiesButtonAction);
        this.playerControllerProperty.subscribe((oldValue, newValue) -> {
            Platform.runLater(() -> {
                playerStateSubscription.unsubscribe();
                playerStateSubscription = newValue.getPlayerStateProperty().subscribe(
                        (oldState, newState) -> Platform.runLater(() -> this.playerStateProperty.setValue(newState)));
                this.playerStateProperty.setValue(newValue.getPlayerStateProperty().getValue());
            });
        });

        rollDiceOverlayView.disableRollDiceButton();

        playerControllerProperty.subscribe((oldValue, newValue) -> {
            Platform.runLater(() -> {
                if (newValue == null) {
                    return;
                }
                this.playerControllerProperty.setValue(newValue);
            });
        });
        Platform.runLater(() -> {
            this.playerControllerProperty.setValue(playerControllerProperty.getValue());
        });

        playerStateProperty.subscribe((oldValue, newValue) -> {
            updateUIBasedOnObjective(newValue.playerObjective());
        });

        if (getPlayerController() != null) {
            updateUIBasedOnObjective(getPlayerObjective());
        }
    }

    /**
     * Updates the UI based on the given objective. This includes enabling and
     * disabling buttons and prompting the user if necessary.
     * Also redraws the game board and updates the player information.
     *
     * @param objective the objective to check
     */
    @StudentImplementationRequired("H3.2")
    private void updateUIBasedOnObjective(final PlayerObjective objective) {
        System.out.println("objective: " + objective);
        rollDiceOverlayView.disableRollDiceButton();
        cityOverlayView.disableSpinButton();
        removeAllHighlights();
        updatePlayerInformation();

        if (getPlayer().isAi()) {
            return;
        }

        final Set<Class<? extends PlayerAction>> allowedActions = getPlayerObjective().getAllowedActions();
        if (allowedActions.contains(BuildRailAction.class)) {
            updateBuildableEdges();
        }
        if (allowedActions.contains(RollDiceAction.class)) {
            rollDiceOverlayView.enableRollDiceButton();
        }
        if (allowedActions.contains(ChooseCitiesAction.class)) {
            cityOverlayView.enableSpinButton();
        }
    }

    /**
     * Updates the player information in the game board.
     */
    @DoNotTouch
    private void updatePlayerInformation() {
        gameBoardController.updatePlayerInformation();
    }

    public void updateCityOverlay(String fromCity, String toCity, List<String> allCityNames) {
        cityOverlayView.spinCities(fromCity, toCity, allCityNames);
    }

    /**
     * Returns the player controller that is currently active.
     * Please do not use this method to get the playerState or playerObjective.
     * Use the {@link #getPlayerState()} and {@link #getPlayerObjective()} instead.
     *
     * @return the player controller that is currently active
     */
    @DoNotTouch
    private PlayerController getPlayerController() {
        return playerControllerProperty.getValue();
    }

    /**
     * Returns the player state of the player that is currently active.
     *
     * @return the player state of the player that is currently active
     */
    @DoNotTouch
    private PlayerState getPlayerState() {
        return playerStateProperty.getValue();
    }

    /**
     * Returns the player objective of the player that is currently active.
     *
     * @return the player objective of the player that is currently active
     */
    @DoNotTouch
    private PlayerObjective getPlayerObjective() {
        return getPlayerState().playerObjective();
    }

    /**
     * Returns the player that is currently active.
     *
     * @return the player that is currently active
     */
    @DoNotTouch
    private Player getPlayer() {
        return getPlayerController().getPlayer();
    }

    private HexGridController getHexGridController() {
        return gameBoardController.getHexGridController();
    }

    /**
     * Removes all highlights from the game board.
     */
    @DoNotTouch
    private void removeAllHighlights() {
        getHexGridController().getEdgeControllers().forEach(EdgeController::unhighlight);
        getHexGridController().unhighlightTiles();
    }

    public RollDiceOverlayView getRollDiceOverlayView() {
        return rollDiceOverlayView;
    }

    public CityOverlayView getCityOverlayView() {
        return cityOverlayView;
    }

    /**
     * The action that is triggered when the roll dice button is clicked.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    public void rollDiceButtonAction(final ActionEvent event) {
        getPlayerController().triggerAction(new RollDiceAction());
    }

    public void chooseCitiesButtonAction(final ActionEvent event) {
        getPlayerController().triggerAction(new ChooseCitiesAction());
    }

    public void updateBuildableEdges() {
        getPlayerState().buildableRailEdges().stream()
                .map(edge -> getHexGridController().getEdgeControllersMap().get(edge)).forEach(ec -> ec
                        .highlight(e -> {
                            getPlayerController().triggerAction(new BuildRailAction(ec.getEdge()));
                        }));
    }

    @Override
    public Builder<Region> getBuilder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBuilder'");
    }

}

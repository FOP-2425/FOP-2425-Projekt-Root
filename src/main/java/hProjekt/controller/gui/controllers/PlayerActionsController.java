package hProjekt.controller.gui.controllers;

import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import hProjekt.controller.PlayerController;
import hProjekt.controller.PlayerObjective;
import hProjekt.controller.actions.BuildRailAction;
import hProjekt.controller.actions.ChooseCitiesAction;
import hProjekt.controller.actions.ChooseRailsAction;
import hProjekt.controller.actions.ConfirmDrive;
import hProjekt.controller.actions.DriveAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.controller.actions.RollDiceAction;
import hProjekt.controller.gui.controllers.scene.GameBoardController;
import hProjekt.model.Edge;
import hProjekt.model.Player;
import hProjekt.model.PlayerState;
import hProjekt.view.menus.overlays.ChosenCitiesOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.util.Subscription;

public class PlayerActionsController {
    private final Property<PlayerController> playerControllerProperty = new SimpleObjectProperty<>();
    private final Property<PlayerState> playerStateProperty = new SimpleObjectProperty<>();
    private Subscription playerStateSubscription = Subscription.EMPTY;
    private final RollDiceOverlayView rollDiceOverlayView;
    private final ChosenCitiesOverlayView cityOverlayView;
    private final GameBoardController gameBoardController;
    private final ObservableSet<Edge> selectedEdges = FXCollections.observableSet();
    private final SetChangeListener<Edge> selctedEdgesListener = (change) -> {
        if (change.wasAdded() && change.getSet().size() >= Config.MAX_RENTABLE_DISTANCE) {
            getPlayerState().choosableEdges().stream().filter(Predicate.not(selectedEdges::contains))
                    .map(edge -> getHexGridController().getEdgeControllersMap().get(edge))
                    .forEach(ec -> ec.unhighlight());
            return;
        }

        if (change.wasRemoved() && change.getSet().size() == Config.MAX_RENTABLE_DISTANCE - 1) {
            updateChooseableEdges(
                    getPlayerState().choosableEdges().stream().filter(Predicate.not(selectedEdges::contains))
                            .collect(Collectors.toSet()));
        }
    };

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
        this.cityOverlayView = new ChosenCitiesOverlayView(this::chooseCitiesButtonAction);
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
        selectedEdges.removeListener(selctedEdgesListener);

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
        if (allowedActions.contains(ChooseRailsAction.class)) {
            selectedEdges.clear();
            updateChooseableEdges();
            selectedEdges.addListener(selctedEdgesListener);
            gameBoardController.updateConfirmationOverlay("Rent selected rails?", this::confirmSelectedRails, () -> {
                selectedEdges.clear();
                updateChooseableEdges();
            });
        }
        if (allowedActions.contains(ConfirmDrive.class)) {
            getPlayerState().rentedEdges().stream().forEach(edge -> {
                getHexGridController().getEdgeControllersMap().get(edge).highlight();
            });
            if (getPlayerState().hasPath()) {
                gameBoardController.updateConfirmationOverlay("Rent highlighted edges and drive?",
                        () -> confirmDrive(true),
                        () -> confirmDrive(false));
            } else {
                gameBoardController.updateConfirmationOverlay(
                        "Could not find path to target city. Do you want to rent different edges?",
                        () -> confirmDrive(false), () -> confirmDrive(true));
            }
        }
        if (allowedActions.contains(DriveAction.class)) {
            gameBoardController.getPlayerAnimationController(getPlayer()).addTrain();
            getPlayerState().drivableTiles().keySet().stream().forEach(tile -> {
                getHexGridController().getTileControllersMap().get(tile).highlight(e -> {
                    getPlayerController().triggerAction(new DriveAction(tile));
                    gameBoardController.getPlayerAnimationController(getPlayer())
                            .animatePlayer(getPlayerState().drivableTiles().get(tile));
                });
            });
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

    public ChosenCitiesOverlayView getChosenCitiesOverlayView() {
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

    private void chooseEdgeHandler(EdgeController ec) {
        ec.highlight(event -> {
            selectedEdges.add(ec.getEdge());
            ec.selected(event2 -> {
                selectedEdges.remove(ec.getEdge());
                chooseEdgeHandler(ec);
            });
        });
    }

    public void updateChooseableEdges() {
        updateChooseableEdges(getPlayerState().choosableEdges());
    }

    private void updateChooseableEdges(Set<Edge> edges) {
        edges.stream().map(edge -> getHexGridController().getEdgeControllersMap().get(edge))
                .forEach(this::chooseEdgeHandler);
    }

    public void confirmSelectedRails() {
        getPlayerController().triggerAction(new ChooseRailsAction(selectedEdges));
    }

    public void confirmDrive(boolean accept) {
        getPlayerController().triggerAction(new ConfirmDrive(accept));
    }
}

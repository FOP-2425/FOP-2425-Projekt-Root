package hProjekt.controller.gui;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import hProjekt.controller.GamePhase;
import hProjekt.controller.PlayerController;
import hProjekt.controller.PlayerObjective;
import hProjekt.controller.actions.BuildRailAction;
import hProjekt.controller.actions.ChooseCitiesAction;
import hProjekt.controller.actions.ChooseRailsAction;
import hProjekt.controller.actions.ConfirmBuildAction;
import hProjekt.controller.actions.ConfirmDrive;
import hProjekt.controller.actions.DriveAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.controller.actions.RollDiceAction;
import hProjekt.controller.gui.scene.GameBoardController;
import hProjekt.model.Edge;
import hProjekt.model.Player;
import hProjekt.model.PlayerState;
import hProjekt.model.Tile;
import hProjekt.model.TilePosition;
import hProjekt.view.menus.overlays.ChosenCitiesOverlayView;
import hProjekt.view.menus.overlays.RollDiceOverlayView;
import javafx.application.Platform;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.SetChangeListener;
import javafx.event.ActionEvent;
import javafx.util.Pair;
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
        for (Edge edge : change.getSet()) {
            getHexGridController().getEdgeControllersMap().get(edge).highlight();
        }
    };
    private final Property<Tile> selectedTile = new SimpleObjectProperty<>();
    private Subscription selectedTileSubscription = Subscription.EMPTY;
    private ObservableList<Edge> selectedRailPath = FXCollections.observableArrayList();
    private final ListChangeListener<Edge> selectedRailPathListener = (change) -> {
        getHexGridController().getEdgeControllers().forEach(EdgeController::hideLabel);
        change.getList().forEach(edge -> {
            EdgeController edgeController = getHexGridController().getEdgeControllersMap().get(edge);
            edgeController.setCostLabel(edge.getBuildingCost(),
                    edge.getTotalParallelCost(getPlayer()));
        });
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
            if (newValue == null) {
                return;
            }
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
        resetUiToBaseState();
        removeAllHighlights();
        updatePlayerInformation();

        if (getPlayer().isAi()) {
            return;
        }

        final Set<Class<? extends PlayerAction>> allowedActions = getPlayerObjective().getAllowedActions();
        if (allowedActions.contains(BuildRailAction.class)) {
            addBuildHandlers();
        }
        if (allowedActions.contains(RollDiceAction.class)) {
            rollDiceOverlayView.enableRollDiceButton();
        }
        if (allowedActions.contains(ChooseCitiesAction.class)) {
            cityOverlayView.enableSpinButton();
        }
        if (allowedActions.contains(ChooseRailsAction.class)) {
            configureRailSelection();
        }
        if (allowedActions.contains(ConfirmDrive.class)) {
            showRentingConfirmation();
        }
        if (allowedActions.contains(DriveAction.class)) {
            updateDriveableTiles();
        }
    }

    private void showRentingConfirmation() {
        getPlayerState().rentedEdges().stream().forEach(edge -> {
            getHexGridController().getEdgeControllersMap().get(edge).highlight();
        });
        if (getPlayerState().hasPath()) {
            gameBoardController.updateConfirmationOverlay("Rent highlighted rails and drive?",
                    () -> confirmDrive(true),
                    () -> confirmDrive(false));
        } else {
            gameBoardController.updateConfirmationOverlay(
                    "Could not find path to target city. Do you want to rent different rails?",
                    () -> confirmDrive(false), () -> confirmDrive(true));
        }
        selectedEdges.clear();
    }

    private void configureRailSelection() {
        selectedEdges.clear();
        selectedEdges.addListener(selctedEdgesListener);
        addChooseEdgesHandlers();
        gameBoardController.updateConfirmationOverlay("Rent selected rails?", this::confirmSelectedRails, () -> {
            selectedEdges.clear();
        });
    }

    private void resetUiToBaseState() {
        rollDiceOverlayView.disableRollDiceButton();
        cityOverlayView.disableSpinButton();
        gameBoardController.hideConfirmationOverlay();
        selectedEdges.removeListener(selctedEdgesListener);
        selectedTileSubscription.unsubscribe();
        getHexGridController().getEdgeControllers().forEach(EdgeController::hideLabel);
        selectedRailPath.removeListener(selectedRailPathListener);
    }

    private void updateDriveableTiles() {
        gameBoardController.getPlayerAnimationController(getPlayer())
                .setPosition(gameBoardController.getPlayerPosition(getPlayer()));
        gameBoardController.getPlayerAnimationController(getPlayer()).showTrain();
        getPlayerState().rentedEdges().stream().forEach(edge -> {
            getHexGridController().getEdgeControllersMap().get(edge).highlight();
        });
        getPlayerState().drivableTiles().keySet().stream().forEach(tile -> {
            getHexGridController().getTileControllersMap().get(tile).highlight(e -> {
                getHexGridController().unhighlightTiles();
                gameBoardController.getPlayerAnimationController(getPlayer())
                        .animatePlayer(getPlayerState().drivableTiles().get(tile))
                        .setOnFinished(actionEvent -> getPlayerController()
                                .triggerAction(new DriveAction(tile)));
            });
        });
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
        getHexGridController().getTileControllers().forEach(TileController::removeMouseEnteredHandler);
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

    private void highlightStartingTiles() {
        Collection<Tile> startingTiles;
        selectedTile.setValue(null);
        if (getPlayer().getRails().isEmpty()) {
            startingTiles = getHexGridController().getHexGrid().getStartingCities().keySet().stream()
                    .map(position -> getHexGridController().getHexGrid().getTileAt(position)).toList();
        } else {
            startingTiles = getPlayer().getRails().keySet().stream().flatMap(set -> set.stream())
                    .map(position -> getHexGridController().getHexGrid().getTileAt(position)).toList();
        }
        for (Tile tile : startingTiles) {
            getHexGridController().getTileControllersMap().get(tile).highlight(e -> {
                getHexGridController().unhighlightTiles();
                getHexGridController().getTileControllersMap().get(tile).highlight(e2 -> {
                    selectedTile.setValue(null);
                    highlightStartingTiles();
                });
                selectedTile.setValue(tile);
            });
        }
    }

    private Integer drivingCostFunction(TilePosition from, TilePosition to) {
        return getHexGridController().getHexGrid().getEdge(from, to).getDrivingCost(from);
    }

    private List<Edge> findBuildPath(Tile hoveredTile, Tile selectedTile) {
        return getHexGridController().getHexGrid().findPath(
                selectedTile.getPosition(),
                hoveredTile.getPosition(),
                getHexGridController().getHexGrid().getEdges().values().stream()
                        .collect(Collectors.toSet()),
                this::drivingCostFunction);
    }

    private void highlightPath(BiFunction<Pair<Integer, Integer>, Integer, Boolean> terminateFunction,
            List<Edge> pathToHoveredTile) {
        highlightPath(terminateFunction, pathToHoveredTile, List.of());
    }

    private void highlightPath(BiFunction<Pair<Integer, Integer>, Integer, Boolean> terminateFunction,
            List<Edge> pathToHoveredTile, Collection<Edge> highlightedEdges) {
        getHexGridController().getEdgeControllers().stream().filter(ec -> !highlightedEdges.contains(ec.getEdge()))
                .forEach(EdgeController::unhighlight);

        selectedRailPath.clear();
        int buildingCost = 0;
        int parallelCost = 0;
        int distance = 0;

        for (Edge edge : pathToHoveredTile) {
            if (edge.getRailOwners().contains(getPlayer())) {
                continue;
            }

            buildingCost += edge.getBuildingCost();
            parallelCost += edge.getTotalParallelCost(getPlayer());
            distance++;

            if (terminateFunction.apply(new Pair<>(buildingCost, parallelCost), distance)) {
                break;
            }

            selectedRailPath.add(edge);
        }

        for (Edge edge : selectedRailPath) {
            getHexGridController().getEdgeControllersMap().get(edge).highlight();
        }
    }

    public void addBuildHandlers() {
        gameBoardController.updateConfirmationOverlay(
                String.format("Finish building? (%s budget left)", getPlayerState().buildingBudget()),
                () -> getPlayerController().triggerAction(new ConfirmBuildAction()), null);
        selectedRailPath.clear();
        selectedTileSubscription.unsubscribe();

        if (getPlayerState().buildingBudget() == 0) {
            return;
        }

        selectedRailPath.addListener(selectedRailPathListener);
        setupTileSelectionHandlers((tc, selectedTile) -> highlightPath(
                (costs, distance) -> costs.getKey() > getPlayerState()
                        .buildingBudget() || costs.getValue() > getPlayer().getCredits()
                        || (GamePhase.DRIVING_PHASE.equals(gameBoardController.getGamePhase())
                                && costs.getKey() + costs.getValue() > getPlayer().getCredits()),
                findBuildPath(tc.getTile(), selectedTile)),
                tc -> getPlayerController()
                        .triggerAction(new BuildRailAction(selectedRailPath)));
    }

    private void setupTileSelectionHandlers(BiConsumer<TileController, Tile> handleTileHover,
            Consumer<TileController> handleTileClick) {
        setupTileSelectionHandlers(handleTileHover, handleTileClick, Set.of());
    }

    private void setupTileSelectionHandlers(BiConsumer<TileController, Tile> handleTileHover,
            Consumer<TileController> handleTileClick, Set<Edge> highlightedEdges) {
        highlightStartingTiles();
        selectedTileSubscription = selectedTile.subscribe((oldValue, newValue) -> {
            if (newValue == null) {
                getHexGridController().getEdgeControllers().stream()
                        .filter(ec -> !highlightedEdges.contains(ec.getEdge()))
                        .forEach(EdgeController::unhighlight);
                getHexGridController().getTileControllers().forEach(TileController::removeMouseEnteredHandler);
                selectedRailPath.clear();
                return;
            }
            getHexGridController().getTileControllers().stream().filter(tc -> !tc.hasMouseClickedHandler())
                    .forEach(tc -> {
                        tc.setMouseEnteredHandler(e -> {
                            handleTileHover.accept(tc, newValue);
                        });
                        tc.setMouseClickedHandler(e -> {
                            if (selectedRailPath != null && !selectedRailPath.isEmpty()) {
                                handleTileClick.accept(tc);
                            }
                        });
                    });
        });
    }

    private List<Edge> findChoosenEdgesPath(Tile hoveredTile, Tile selectedTile) {
        return getHexGridController().getHexGrid().findPath(selectedTile.getPosition(), hoveredTile.getPosition(),
                Set.of(getPlayerState().choosableEdges(), getPlayer()
                        .getRails().values()).stream().flatMap(set -> set.stream()).collect(Collectors.toSet()),
                this::drivingCostFunction);
    }

    public void addChooseEdgesHandlers() {
        selectedRailPath.clear();
        selectedTileSubscription.unsubscribe();

        if (selectedEdges.size() == Config.MAX_RENTABLE_DISTANCE) {
            return;
        }

        setupTileSelectionHandlers((tc, selectedTile) -> highlightPath(
                (costs, distance) -> {
                    distance += selectedEdges.size();
                    return distance > Config.MAX_RENTABLE_DISTANCE || distance > getPlayer().getCredits();
                },
                findChoosenEdgesPath(tc.getTile(), selectedTile), selectedEdges),
                tc -> selectedEdges.addAll(selectedRailPath), selectedEdges);
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

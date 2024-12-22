package hProjekt.controller;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.Config;
import hProjekt.controller.actions.IllegalActionException;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.model.City;
import hProjekt.model.Edge;
import hProjekt.model.GameState;
import hProjekt.model.Player;
import hProjekt.model.PlayerState;
import hProjekt.model.Tile;
import hProjekt.model.TilePosition;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

/**
 * The PlayerController class represents a controller for a {@link Player} in
 * the game.
 * It manages the player's state, objectives, actions and all methods the UI
 * needs to interact with.
 * It receives objectives the player wants to achieve and waits for the UI or AI
 * to trigger any allowed actions. It then executes the actions and updates the
 * player's state.
 */
public class PlayerController {
    private final Player player;

    private final GameController gameController;

    private final BlockingDeque<PlayerAction> actions = new LinkedBlockingDeque<>();

    private final Property<PlayerState> playerStateProperty = new SimpleObjectProperty<>();

    private PlayerObjective playerObjective = PlayerObjective.IDLE;

    private int buildingBudget = 0; // Budget during building phase

    private Set<Edge> rentedEdges = new HashSet<>();

    private boolean hasPath = false;

    private boolean hasConfirmedPath = false;

    /**
     * Creates a new {@link PlayerController} with the given {@link GameController}
     * and {@link Player}.
     *
     * @param gameController the {@link GameController} that manages the game logic
     *                       and this controller is part of.
     * @param player         the {@link Player} this controller belongs to. It is
     *                       assumed that the player is valid.
     */
    @DoNotTouch
    public PlayerController(final GameController gameController, final Player player) {
        this.gameController = gameController;
        this.player = player;
    }

    /**
     * Returns the {@link Player}.
     *
     * @return the {@link Player}.
     */
    @DoNotTouch
    public Player getPlayer() {
        return player;
    }

    private GameState getState() {
        return gameController.getState();
    }

    /**
     * Returns a {@link Property} with the current {@link PlayerState}.
     *
     * @return a {@link Property} with the current {@link PlayerState}.
     */
    @DoNotTouch
    public Property<PlayerState> getPlayerStateProperty() {
        return playerStateProperty;
    }

    /**
     * Returns the current {@link PlayerState}.
     *
     * @return the current {@link PlayerState}.
     */
    public PlayerState getPlayerState() {
        return playerStateProperty.getValue();
    }

    /**
     * Updates the {@link #playerStateProperty} with the current
     * {@link PlayerState}.
     */
    @DoNotTouch
    private void updatePlayerState() {
        playerStateProperty
                .setValue(new PlayerState(getBuildableRails(), getPlayerObjective(), getChooseableEdges(),
                        getRentedEdges()));
    }

    /**
     * Returns the current {@link PlayerObjective}
     *
     * @return the current {@link PlayerObjective}
     */
    @DoNotTouch
    public PlayerObjective getPlayerObjective() {
        return playerObjective;
    }

    /**
     * Sets the value of the {@link #playerObjectiveProperty} to the given
     * objective.
     *
     * @param nextObjective the objective to set
     */
    @DoNotTouch
    public void setPlayerObjective(final PlayerObjective nextObjective) {
        playerObjective = nextObjective;
    }

    /**
     * Returns the building budget during the building phase.
     *
     * @return the building budget during the building phase
     */
    public int getBuildingBudget() {
        return buildingBudget;
    }

    /**
     * Sets the building budget during the building phase,
     *
     * @param amount the anount to set the building budget to
     */
    public void setBuildingBudget(int amount) {
        buildingBudget = amount;
    }

    public boolean hasPath() {
        return hasPath;
    }

    public void resetHasPath() {
        this.hasPath = false;
    }

    public boolean hasConfirmedPath() {
        return hasConfirmedPath;
    }

    public void resetHasConfirmedPath() {
        this.hasConfirmedPath = false;
    }

    public void resetRentedEdges() {
        rentedEdges = new HashSet<>();
    }

    public void resetDrivingPhase() {
        resetHasConfirmedPath();
        resetHasPath();
        resetRentedEdges();
    }

    /**
     * Rolls the dice.
     */
    public void rollDice() {
        gameController.castDice();
    }

    public void chooseCities() {
        gameController.chooseCities();
    }

    // Process Actions

    /**
     * Gets called from viewer thread to trigger an Action. This action will then be
     * waited for using the method {@link #waitForNextAction()}.
     *
     * @param action The Action that should be triggered next
     */
    @DoNotTouch
    public void triggerAction(final PlayerAction action) {
        actions.add(action);
    }

    /**
     * Takes the next action from the queue. This method blocks until an action is
     * in the queue.
     *
     * @return The next action
     * @throws InterruptedException if the thread is interrupted while waiting for
     *                              the next action
     */
    @DoNotTouch
    public PlayerAction blockingGetNextAction() throws InterruptedException {
        return actions.take();
    }

    /**
     * Waits for the next action and executes it.
     *
     * @param nextObjective the objective to set before the action is awaited
     * @return the executed action
     */
    @DoNotTouch
    public PlayerAction waitForNextAction(final PlayerObjective nextObjective) {
        setPlayerObjective(nextObjective);
        return waitForNextAction();
    }

    /**
     * Waits for a action to be triggered, checks if the action is allowed and then
     * executes it.
     * If a {@link IllegalActionException} is thrown, the action is ignored and the
     * next action is awaited. This is done to ensure only allowed actions are
     * executed.
     *
     * @return the executed action
     */
    @DoNotTouch
    public PlayerAction waitForNextAction() {
        try {
            updatePlayerState();
            // blocking, waiting for viewing thread
            final PlayerAction action = blockingGetNextAction();

            System.out.println("TRIGGER " + action + " [" + player.getName() + "]");

            if (!getPlayerObjective().allowedActions.contains(action.getClass())) {
                throw new IllegalActionException(String.format("Illegal Action %s performed. Allowed Actions: %s",
                        action, getPlayerObjective().getAllowedActions()));
            }
            action.execute(this);
            updatePlayerState();
            return action;
        } catch (final IllegalActionException e) {
            // Ignore and keep going
            e.printStackTrace();
            return waitForNextAction();
        } catch (final InterruptedException e) {
            throw new RuntimeException("Main thread was interrupted!", e);
        }
    }

    /**
     * Determines if the player can build a rail on the given edge.
     * Checks if the player has enough credits. If the game is in the building
     * phase, the building cost is checked against the
     * building budget and not the players credits.
     *
     * @param edge the edge to check
     * @return {@code true} if the player can build a rail on the given edge,
     */
    public boolean canBuildRail(Edge edge) {
        if (getState().getGamePhaseProperty().getValue().equals(GamePhase.BUILDING_PHASE)) {
            return edge.getBuildingCost() <= buildingBudget
                    && edge.getTotalParallelCost(player) <= player.getCredits();
        }
        return edge.getTotalBuildingCost(player) <= player.getCredits();
    }

    /**
     * Returns all edges the player can build a rail on.
     *
     * @return all edges the player can build a rail on
     */
    public Set<Edge> getBuildableRails() {
        Collection<Edge> ownedRails = getState().getGrid().getRails(player).values();
        Set<Edge> possibleConnections;
        if (ownedRails.isEmpty()) {
            possibleConnections = getState().getGrid().getStartingCities().keySet().stream()
                    .flatMap(position -> getState().getGrid().getTileAt(position).getEdges().stream())
                    .filter(this::canBuildRail)
                    .collect(Collectors.toSet());
            return possibleConnections;
        }
        possibleConnections = ownedRails.stream()
                .flatMap(rail -> rail.getConnectedEdges().stream()
                        .filter(edge -> !edge.getRailOwners().contains(player)))
                .filter(this::canBuildRail)
                .collect(Collectors.toSet());
        return possibleConnections;
    }

    /**
     * Tries to build a rail on the given edge.
     * Also removes the cost of building the rail from the player's credits or
     * building budget if the game is in the building phase.
     *
     * @param edge the edge to build the rail on
     * @throws IllegalActionException if the player cannot build a rail on the given
     *                                edge
     */
    public void buildRail(final Edge edge) throws IllegalActionException {
        Set<Edge> buildableRails = getBuildableRails();

        if (buildableRails.isEmpty() || !buildableRails.contains(edge)) {
            throw new IllegalActionException("Cannot build rail");
        }

        final boolean connectsUnconnectedCity = !Collections.disjoint(
                getState().getGrid().getUnconnectedCities().values().stream()
                        .filter(Predicate.not(City::isStartingCity))
                        .map(City::getPosition).toList(),
                edge.getAdjacentTilePositions());

        if (!edge.addRail(player)) {
            throw new IllegalActionException("Cannot build rail on the given edge");
        }

        int totalParallelCost = edge.getTotalParallelCost(player);

        if (totalParallelCost > 0) {
            Map<Player, Integer> parallelCost = edge.getParallelCost(player);
            for (Map.Entry<Player, Integer> entry : parallelCost.entrySet()) {
                entry.getKey().addCredits(entry.getValue());
            }
        }

        if (connectsUnconnectedCity) {
            player.addCredits(Config.CITY_CONNECTION_BONUS);
        }

        if (getState().getGamePhaseProperty().getValue().equals(GamePhase.BUILDING_PHASE)) {
            buildingBudget -= edge.getBuildingCost();
            player.removeCredits(totalParallelCost);
            return;
        }
        player.removeCredits(edge.getTotalBuildingCost(player));
    }

    public Set<Edge> getChooseableEdges() {
        if (player.getCredits() == 0 || !getState().getGamePhaseProperty().getValue().equals(GamePhase.DRIVING_PHASE)) {
            return Set.of();
        }

        Set<Edge> builtEdges = getState().getGrid().getRails(player).values().stream().collect(Collectors.toSet());
        Set<Edge> chooseableEdges = new HashSet<>();
        chooseableEdges.addAll(builtEdges.stream()
                .flatMap(edge -> edge.getConnectedEdges().stream().filter(Edge::hasRail)
                        .filter(e -> !e.getRailOwners().contains(player)))
                .distinct().toList());

        if (chooseableEdges.isEmpty()) {
            return chooseableEdges;
        }

        final List<Pair<Edge, Integer>> edgeQueue = new ArrayList<>(
                chooseableEdges.stream().map(edge -> new Pair<>(edge, 1)).toList());
        while (!edgeQueue.isEmpty()) {
            final Pair<Edge, Integer> currentPair = edgeQueue.removeFirst();
            for (Edge edge : currentPair.getKey().getConnectedEdges().stream()
                    .filter(Edge::hasRail)
                    .filter(edge -> !edge.getRailOwners().contains(player))
                    .filter(Predicate.not(chooseableEdges::contains)).toList()) {
                int newDistance = currentPair.getValue() + 1;
                if (newDistance <= Math.min(player.getCredits(), 10)) {
                    edgeQueue.add(new Pair<>(edge, newDistance));
                    chooseableEdges.add(edge);
                }
            }
        }

        return chooseableEdges;
    }

    public void chooseEdges(final Set<Edge> edges) throws IllegalActionException {
        Set<Edge> chooseableEdges = getChooseableEdges();
        hasPath = false;

        if (chooseableEdges.isEmpty() || !chooseableEdges.containsAll(edges)) {
            throw new IllegalActionException("Cannot choose edges");
        }
        if (edges.size() > Config.MAX_RENTABLE_DISTANCE) {
            throw new IllegalActionException("Cannot choose more than 10 edges");
        }

        Set<Edge> allAvailableEdges = List.of(getState().getGrid().getRails(player).values(), edges).stream()
                .flatMap(set -> set.stream())
                .filter(Edge::hasRail).collect(Collectors.toSet());
        Set<Edge> pathEdges = getState().getGrid().findPath(gameController.getStartingCity().getPosition(),
                gameController.getTargetCity().getPosition(), allAvailableEdges, Edge::getDrivingCost);
        if (pathEdges.isEmpty()) {
            rentedEdges = new HashSet<>();
            return;
        }

        hasPath = true;
        rentedEdges = pathEdges.stream().filter(edge -> !edge.getRailOwners().contains(player))
                .collect(Collectors.toSet());
    }

    private Set<Edge> getRentedEdges() {
        return Collections.unmodifiableSet(rentedEdges);
    }

    public void confirmPath(boolean confirm) {
        if (!confirm) {
            hasConfirmedPath = false;
            return;
        }

        hasConfirmedPath = true;
        if (hasPath) {
            getState().addDrivingPlayer(player);
            return;
        }
    }

    public boolean canDrive() {
        if (!getState().getGamePhaseProperty().getValue().equals(GamePhase.DRIVING_PHASE)) {
            return false;
        }

        if (getState().getDrivingPlayers().contains(player)) {
            return true;
        }

        return false;
    }

    /**
     * Calculates the tiles the player can drive to with the current dice roll.
     * If the player can reach the target city, it is the only tile returned.
     *
     * @return the tiles the player can drive to with the current dice roll
     */
    public Map<Tile, List<Tile>> getDrivableTiles() {
        if (!canDrive()) {
            return Map.of();
        }

        final Set<Edge> allAvailableEdges = List.of(getState().getGrid().getRails(player).values(), rentedEdges)
                .stream()
                .flatMap(set -> set.stream())
                .filter(Edge::hasRail).collect(Collectors.toSet());
        final Tile startNode = getState().getGrid().getTileAt(getState().getPlayerPositions().get(getPlayer()));
        final Set<Tile> visitedNodes = new HashSet<>(Set.of(startNode));
        final List<Pair<Tile, List<Tile>>> positionQueue = new ArrayList<>(List.of(new Pair<>(startNode, List.of(
                startNode))));
        final List<Integer> distanceQueue = new ArrayList<>(List.of(0));
        final Map<Tile, List<Tile>> drivableTiles = new HashMap<>();

        while (!positionQueue.isEmpty()) {
            final Pair<Tile, List<Tile>> currentPair = positionQueue.removeFirst();
            final TilePosition currentPosition = currentPair.getKey().getPosition();
            final int currentDistance = distanceQueue.removeFirst();
            for (Tile tile : currentPair.getKey().getConnectedNeighbours(allAvailableEdges)) {
                if (visitedNodes.contains(tile)) {
                    continue;
                }

                final int drivingCost = getState().getGrid().getEdge(currentPosition, tile.getPosition())
                        .getDrivingCost();
                int newDistance = currentDistance + drivingCost;

                if (newDistance <= gameController.getCurrentDiceRoll()) {
                    List<Tile> path = new ArrayList<>(currentPair.getValue());
                    path.add(currentPair.getKey());

                    if (gameController.getTargetCity().getPosition().equals(tile.getPosition())) {
                        return Map.of(tile, path);
                    }

                    if (newDistance < gameController.getCurrentDiceRoll()) {
                        positionQueue.add(new Pair<>(tile, path));
                        distanceQueue.add(newDistance);
                    } else {
                        drivableTiles.put(tile, path);
                    }
                }
            }
            visitedNodes.add(currentPair.getKey());
        }
        return drivableTiles;
    }

}

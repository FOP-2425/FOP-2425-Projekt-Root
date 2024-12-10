package hProjekt.controller;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.actions.IllegalActionException;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.model.Edge;
import hProjekt.model.Player;
import hProjekt.model.PlayerState;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;

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
                .setValue(new PlayerState(getBuildableRails(), getPlayerObjective()));
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

    /**
     * Rolls the dice.
     */
    public void rollDice() {
        gameController.castDice();
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
     * Checks if the player has enough credits. If the player's objective is
     * {@PlayerObjective.PLACE_RAIL}, the building cost is checked against the
     * building budget and not the players credits.
     *
     * @param edge the edge to check
     * @return {@code true} if the player can build a rail on the given edge,
     */
    public boolean canBuildRail(Edge edge) {
        if (playerObjective.equals(PlayerObjective.PLACE_RAIL)) {
            return edge.getBuildingCost() <= buildingBudget
                    && edge.getTotalParallelCost(player) <= player.getCredits();
        }
        return edge.getTotalCost(player) <= player.getCredits();
    }

    /**
     * Returns all edges the player can build a rail on.
     *
     * @return all edges the player can build a rail on
     */
    public Set<Edge> getBuildableRails() {
        Collection<Edge> ownedRails = gameController.getState().getGrid().getRails(player).values();
        Set<Edge> possibleConnections;
        if (ownedRails.isEmpty()) {
            possibleConnections = gameController.getState().getGrid().getStartingCities().keySet().stream()
                    .flatMap(position -> gameController.getState().getGrid().getTileAt(position).getEdges().stream())
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
     * building budget if the player's objective is {@PlayerObjective.PLACE_RAIL}.
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

        if (playerObjective.equals(PlayerObjective.PLACE_RAIL)) {
            buildingBudget -= edge.getBuildingCost();
            player.removeCredits(totalParallelCost);
            return;
        }
        player.removeCredits(edge.getTotalCost(player));
    }
}

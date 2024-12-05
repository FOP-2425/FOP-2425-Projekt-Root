package hProjekt.controller.gui.controllers;

import java.util.Set;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.controller.PlayerController;
import hProjekt.controller.PlayerObjective;
import hProjekt.controller.actions.BuildRailAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.controller.actions.RollDiceAction;
import hProjekt.model.Player;
import hProjekt.model.PlayerState;
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
            final Property<PlayerController> playerControllerProperty) {
        this.playerControllerProperty.subscribe((oldValue, newValue) -> {
            Platform.runLater(() -> {
                playerStateSubscription.unsubscribe();
                playerStateSubscription = newValue.getPlayerStateProperty().subscribe(
                        (oldState, newState) -> Platform.runLater(() -> this.playerStateProperty.setValue(newState)));
                this.playerStateProperty.setValue(newValue.getPlayerStateProperty().getValue());
            });
        });

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
        if (getPlayer().isAi()) {
            return;
        }

        final Set<Class<? extends PlayerAction>> allowedActions = getPlayerObjective().getAllowedActions();
        if (allowedActions.contains(BuildRailAction.class)) {

        }
        if (allowedActions.contains(RollDiceAction.class)) {

        }
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

    /**
     * The action that is triggered when the roll dice button is clicked.
     *
     * @param event the event that triggered the action
     */
    @DoNotTouch
    public void rollDiceButtonAction(final ActionEvent event) {
        getPlayerController().triggerAction(new RollDiceAction());
    }

    @Override
    public Builder<Region> getBuilder() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getBuilder'");
    }

}

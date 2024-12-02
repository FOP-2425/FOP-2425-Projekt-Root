package hProjekt.controller;

import java.util.Set;

import hProjekt.controller.actions.BuildRailAction;
import hProjekt.controller.actions.PlayerAction;

/**
 * This enum represents the different objectives a player can have and what
 * actions are allowed when the player has this
 * objective.
 */
public enum PlayerObjective {
    PLACE_RAIL(Set.of(BuildRailAction.class)),
    ROLL_DICE(Set.of()),
    IDLE(Set.of());

    final Set<Class<? extends PlayerAction>> allowedActions;

    PlayerObjective(Set<Class<? extends PlayerAction>> allowedActions) {
        this.allowedActions = allowedActions;
    }

    /**
     * Returns the actions that are allowed when the player has this objective.
     *
     * @return the actions that are allowed when the player has this objective
     */
    public Set<Class<? extends PlayerAction>> getAllowedActions() {
        return allowedActions;
    }
}

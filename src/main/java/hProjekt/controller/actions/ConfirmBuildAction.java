package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;

public record ConfirmBuildAction() implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        // stays empty
    }
}

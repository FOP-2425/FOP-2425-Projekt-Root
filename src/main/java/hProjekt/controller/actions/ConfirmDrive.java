package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;

public record ConfirmDrive(boolean accept) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.confirmPath(accept);
    }
}

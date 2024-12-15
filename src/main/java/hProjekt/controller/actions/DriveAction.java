package hProjekt.controller.actions;

import javax.swing.text.Position;

import hProjekt.controller.PlayerController;

public record DriveAction(Position position) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
}

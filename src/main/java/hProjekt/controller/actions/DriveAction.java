package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;
import hProjekt.model.Tile;

public record DriveAction(Tile targetTile) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.drive(targetTile);
    }
}

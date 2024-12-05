package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;
import hProjekt.model.Edge;

public record BuildRailAction(Edge edge) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.buildRail(edge);
    }
}

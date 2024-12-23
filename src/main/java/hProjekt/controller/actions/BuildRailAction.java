package hProjekt.controller.actions;

import java.util.List;

import hProjekt.controller.PlayerController;
import hProjekt.model.Edge;

public record BuildRailAction(List<Edge> edges) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.buildRails(edges);
    }
}

package hProjekt.controller.actions;

import java.util.Set;

import hProjekt.controller.PlayerController;
import hProjekt.model.Edge;

public record ChooseRailsAction(Set<Edge> choosenEdges) implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.chooseEdges(choosenEdges);
    }

}

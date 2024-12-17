package hProjekt.controller.actions;

import hProjekt.controller.PlayerController;

public class ChooseCitiesAction implements PlayerAction {

    @Override
    public void execute(PlayerController pc) throws IllegalActionException {
        pc.chooseCities();
    }
}

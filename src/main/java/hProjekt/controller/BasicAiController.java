package hProjekt.controller;

import java.util.List;
import java.util.Set;

import hProjekt.Config;
import hProjekt.controller.actions.BuildRailAction;
import hProjekt.controller.actions.ChooseCitiesAction;
import hProjekt.controller.actions.ChooseRailsAction;
import hProjekt.controller.actions.ConfirmBuildAction;
import hProjekt.controller.actions.ConfirmDrive;
import hProjekt.controller.actions.DriveAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.controller.actions.RollDiceAction;
import hProjekt.model.Edge;
import hProjekt.model.GameState;
import hProjekt.model.HexGrid;
import hProjekt.model.Tile;
import javafx.beans.property.Property;

public class BasicAiController extends AiController {

    public BasicAiController(final PlayerController playerController, final HexGrid hexGrid, final GameState gameState,
            final Property<PlayerController> activePlayerController) {
        super(playerController, hexGrid, gameState, activePlayerController);
    }

    @Override
    protected void executeActionBasedOnObjective(PlayerObjective objective) {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            System.out.println("Something weird happend");
        }

        final Set<Class<? extends PlayerAction>> allowedActions = playerController.getPlayerObjective()
                .getAllowedActions();
        if (allowedActions.contains(RollDiceAction.class)) {
            playerController.triggerAction(new RollDiceAction());
        }
        if (allowedActions.contains(BuildRailAction.class)
                && !playerController.getPlayerState().buildableRailEdges().isEmpty()) {
            int randomIndex = Config.RANDOM.nextInt(playerController.getPlayerState().buildableRailEdges().size());
            playerController.triggerAction(new BuildRailAction(
                    List.of(playerController.getPlayerState().buildableRailEdges()
                            .toArray(Edge[]::new)[randomIndex])));
        }
        if (allowedActions.contains(ConfirmBuildAction.class) && playerController.getPlayerState().buildableRailEdges()
                .isEmpty()) {
            playerController.triggerAction(new ConfirmBuildAction());
        }
        if (allowedActions.contains(ChooseCitiesAction.class)) {
            playerController.triggerAction(new ChooseCitiesAction());
        }
        if (allowedActions.contains(ConfirmDrive.class)) {
            playerController.triggerAction(new ConfirmDrive(true));
        }
        if (allowedActions.contains(DriveAction.class)) {
            int randomIndex = Config.RANDOM.nextInt(playerController.getPlayerState().drivableTiles().size());
            playerController
                    .triggerAction(new DriveAction(
                            playerController.getPlayerState().drivableTiles().keySet()
                                    .toArray(Tile[]::new)[randomIndex]));
        }
        if (allowedActions.contains(ChooseRailsAction.class)) {
            playerController.triggerAction(new ChooseRailsAction(Set.of()));
        }
    }
}

package hProjekt.controller.gui.controllers.scene;

import java.util.List;

import hProjekt.model.GameSetup;
import hProjekt.model.GameSetupImpl;
import hProjekt.view.menus.SetupGameBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class SetupGameSceneController implements SceneController {
    private final Builder<Region> builder;
    private final GameSetup gameSetup;

    public SetupGameSceneController() {
        this.gameSetup = new GameSetupImpl();

        builder = new SetupGameBuilder(
            () -> loadGameSceneWithSetupData(), 
            SceneController::loadMainMenuScene,
            gameSetup
        );
    }

    @Override
    public String getTitle() {
        return "Setup Game";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }

    /**
     * Loads the game scene with the setup data.
     */
    private void loadGameSceneWithSetupData() {
        // Here utilize gameSetup to pass any setup data to the game logic

        System.out.println("Starting game with setup: ");
        List<String> playerNames = gameSetup.getPlayerNames();
        StringBuilder playersInfo = new StringBuilder("  - Players:\n");
        for (int i = 0; i < playerNames.size(); i++) {
            boolean isAi = gameSetup.isPlayerAi(i);
            String color = gameSetup.getPlayerColor(i);
            playersInfo.append("      - (")
                    .append(i + 1)
                    .append(") ")
                    .append(playerNames.get(i))
                    .append(" (")
                    .append(isAi ? "AI" : "not AI")
                    .append("), ")
                    .append(color)
                    .append("\n");
        }
        if (playersInfo.length() > 0 && playersInfo.charAt(playersInfo.length() - 1) == '\n') {
            playersInfo.deleteCharAt(playersInfo.length() - 1);
        }
        System.out.println(playersInfo);
        System.out.println("  - Selected Map: " + gameSetup.getMapSelection());

        // Call actual game loading logic
        SceneController.loadGameScene();
    }
}

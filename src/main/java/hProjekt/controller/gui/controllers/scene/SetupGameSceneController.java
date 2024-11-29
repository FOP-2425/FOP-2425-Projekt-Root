package hProjekt.controller.gui.controllers.scene;

import hProjekt.Config;
import hProjekt.controller.GameController;
import hProjekt.controller.gui.SceneSwitcher;
import hProjekt.model.GameSetup;
import hProjekt.model.GameSetupImpl;
import hProjekt.model.GameState;
import hProjekt.model.HexGridImpl;
import hProjekt.view.menus.SetupGameBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class SetupGameSceneController implements SceneController {
    private final Builder<Region> builder;
    private final GameSetup gameSetup;

    public SetupGameSceneController() {
        this.gameSetup = new GameSetupImpl();

        builder = new SetupGameBuilder(
            this::loadGameSceneWithSetupData, 
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
        System.out.println("Starting game with setup: ");
        List<String> playerNames = gameSetup.getPlayerNames();
        GameState gameState = new GameState(new HexGridImpl(Config.TOWN_NAMES), new ArrayList<>());
        GameController gameController = new GameController(gameState);

        // Use GameState's newPlayer method to add players
        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            boolean isAi = gameSetup.isPlayerAi(i);
            String colorHex = gameSetup.getPlayerColor(i);
            Color playerColor = Color.web(colorHex);

            // Create a new player and add it to the GameState
            gameController.getState().newPlayer(playerName, playerColor, isAi);
        }

        // Print players for debugging
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

        // Call the game scene with the existing GameState
        SceneSwitcher.getInstance().loadScene(new MapSceneController(gameController.getState()));
    }

}

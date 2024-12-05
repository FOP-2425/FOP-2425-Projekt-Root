package hProjekt.controller.gui.controllers.scene;

import java.util.List;

import hProjekt.model.GameSetup;
import hProjekt.model.GameSetupImpl;
import hProjekt.model.GameState;
import hProjekt.view.menus.SetupGameBuilder;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.util.Builder;

public class SetupGameSceneController implements SceneController {
    private final Builder<Region> builder;
    private final GameSetup gameSetup;
    private final GameState gameState;

    public SetupGameSceneController(final GameState gameState) {
        this.gameSetup = new GameSetupImpl();
        this.gameState = gameState;

        builder = new SetupGameBuilder(
                this::loadGameSceneWithSetupData,
                SceneController::loadMainMenuScene,
                gameSetup);
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

        // Use GameState's newPlayer method to add players
        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i);
            boolean isAi = gameSetup.isPlayerAi(i);
            String colorHex = gameSetup.getPlayerColor(i);
            Color playerColor = Color.web(colorHex);

            // Create a new player and add it to the GameState
            gameState.newPlayer(playerName, playerColor, isAi);
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
        SceneController.loadGameScene();
    }

}

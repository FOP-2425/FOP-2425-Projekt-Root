package hProjekt.controller.gui.scene;

import java.util.List;

import hProjekt.controller.LeaderboardController;
import hProjekt.model.Player;
import hProjekt.view.menus.EndScreenBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class EndScreenSceneController implements SceneController {
    private final Builder<Region> builder;

    public EndScreenSceneController(List<Player> players) {
        for (Player player : players) {
            LeaderboardController.savePlayerData(player.getName(), player.getCredits(), player.isAi());
        }
        this.builder = new EndScreenBuilder(SceneController::loadMainMenuScene, players);
    }

    @Override
    public String getTitle() {
        return "End Screen";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }
}

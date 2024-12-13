package hProjekt.controller.gui.controllers.scene;

import hProjekt.view.menus.LeaderboardBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

/**
 * Controller for the Leaderboard Scene.
 */
public class LeaderboardSceneController implements SceneController {

    private final Builder<Region> builder;

    public LeaderboardSceneController() {
        this.builder = new LeaderboardBuilder();
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }

    @Override
    public String getTitle() {
        return "Leaderboard";
    }
}

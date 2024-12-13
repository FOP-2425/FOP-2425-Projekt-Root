package hProjekt.controller.gui.controllers.scene;

import hProjekt.view.menus.MainMenuBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class MainMenuSceneController implements SceneController {
    private final Builder<Region> builder;

    public MainMenuSceneController() {
        builder = new MainMenuBuilder(
                SceneController::loadSetupGameScene,
                SceneController::loadLeaderboardScene,
                SceneController::loadSettingsScene,
                SceneController::quit,
                SceneController::loadAboutScene);
    }

    @Override
    public String getTitle() {
        return "Main Menu";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }
}

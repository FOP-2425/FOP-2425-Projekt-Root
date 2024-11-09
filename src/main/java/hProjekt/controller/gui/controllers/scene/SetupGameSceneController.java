package hProjekt.controller.gui.controllers.scene;

import hProjekt.view.menus.SetupGameBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class SetupGameSceneController implements SceneController {
    private final Builder<Region> builder;

    public SetupGameSceneController() {
        builder = new SetupGameBuilder(
            SceneController::loadGameScene,
            SceneController::loadMainMenuScene);
    }

    @Override
    public String getTitle() {
        return "Setup Game";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }
}

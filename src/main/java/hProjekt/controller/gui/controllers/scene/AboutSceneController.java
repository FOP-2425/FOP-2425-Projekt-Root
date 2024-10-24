package hProjekt.controller.gui.controllers.scene;

import hProjekt.view.menus.AboutBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class AboutSceneController implements SceneController {
    private final Builder<Region> builder;

    public AboutSceneController() {
        builder = new AboutBuilder(SceneController::loadMainMenuScene);
    }

    @Override
    public String getTitle() {
        return "About";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }
}

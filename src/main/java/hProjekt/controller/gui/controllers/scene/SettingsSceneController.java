package hProjekt.controller.gui.controllers.scene;

import hProjekt.view.menus.SettingsBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class SettingsSceneController implements SceneController {
    private final Builder<Region> builder;

    public SettingsSceneController() {
        builder = new SettingsBuilder(
                SceneController::loadMainMenuScene 
        );
    }

    @Override
    public String getTitle() {
        return "Settings";
    }

    @Override
    public Builder<Region> getBuilder() {
        return builder;
    }
}

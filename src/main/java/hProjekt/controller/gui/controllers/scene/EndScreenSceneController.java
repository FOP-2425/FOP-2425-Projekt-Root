package hProjekt.controller.gui.controllers.scene;

import hProjekt.model.Player;
import hProjekt.view.menus.EndScreenBuilder;
import javafx.scene.layout.Region;
import javafx.util.Builder;

import java.util.List;

public class EndScreenSceneController implements SceneController {
    private final Builder<Region> builder;

    public EndScreenSceneController(List<Player> players) {
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

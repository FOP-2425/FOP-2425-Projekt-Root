package hProjekt.controller.gui.controllers.scene;

import hProjekt.controller.gui.SceneSwitcher;
import hProjekt.controller.gui.SceneSwitcher.SceneType;
import javafx.scene.control.Button;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;
import javafx.stage.Stage;
import javafx.scene.Scene;


public class MainMenuSceneController implements SceneController{

    @Override
    public Builder<Region> getBuilder() {
        return () -> {
            VBox layout = new VBox(20);
            layout.setPrefSize(300,200);
            Button generateMapButton = new Button("Generate Random Map");
            generateMapButton.setOnAction(event -> {
                SceneSwitcher.getInstance().loadScene(SceneType.MAP);
            });
            layout.getChildren().addAll(generateMapButton);
            return layout;
        };
    }

    @Override
    public String getTitle() {
        return "Main Menu";
    }

}

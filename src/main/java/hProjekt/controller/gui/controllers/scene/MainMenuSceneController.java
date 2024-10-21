package hProjekt.controller.gui.controllers.scene;

import hProjekt.controller.gui.SceneSwitcher;
import hProjekt.controller.gui.SceneSwitcher.SceneType;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Builder;
import javafx.util.Duration;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;

public class MainMenuSceneController implements SceneController {

    @Override
    public String getTitle() {
        return "MAIN MENU";
    }

    @Override
    public Builder<Region> getBuilder() {
        return () -> {
            StackPane root = new StackPane();

            String videoPath = getClass().getResource("/images/train.mp4").toExternalForm();
            Media media = new Media(videoPath);
            MediaPlayer mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setCycleCount(5);

            MediaView mediaView = new MediaView(mediaPlayer);
            mediaView.setFitWidth(1920);
            mediaView.setFitHeight(1080);
            mediaView.setPreserveRatio(false);

            mediaPlayer.play();

            VBox centerBox = new VBox(20);
            centerBox.setAlignment(Pos.CENTER);
            centerBox.setPadding(new Insets(50));

            
            Text title = new Text(""); //"Dampfross", wenn das mit dem Font klappt...
            title.getStyleClass().add("text-title");
            

            Button startGameButton = new Button("Start Game");
            startGameButton.setMinWidth(200);
            startGameButton.setOnAction(event -> {
                mediaPlayer.stop();
                SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.MAP);
            });
            startGameButton.getStyleClass().add("button");

            Button mapEditorButton = new Button("Map Editor");
            mapEditorButton.setMinWidth(200);
            mapEditorButton.setOnAction(event -> {
                mediaPlayer.stop();
            });
            mapEditorButton.getStyleClass().add("button");

            Button settingsButton = new Button("Settings");
            settingsButton.setMinWidth(200);
            settingsButton.setOnAction(event -> {
                mediaPlayer.stop();
                SceneController.loadSettingsScene();
            });
            settingsButton.getStyleClass().add("button");

            centerBox.getChildren().addAll(title, startGameButton, mapEditorButton, settingsButton);

            HBox bottomBox = new HBox();
            bottomBox.setPadding(new Insets(20));
            bottomBox.setAlignment(Pos.BOTTOM_LEFT);

            Button exitButton = new Button("Exit");
            exitButton.setMinWidth(150);
            exitButton.setOnAction(event -> {
                mediaPlayer.stop();
                SceneController.quit();
            });
            exitButton.getStyleClass().add("button");

            BorderPane layout = new BorderPane();
            layout.setCenter(centerBox);
            layout.setBottom(bottomBox);
            bottomBox.getChildren().add(exitButton);

            root.getChildren().addAll(mediaView, layout);
            root.getStylesheets().add(getClass().getResource("/css/mainmenu.css").toExternalForm());

            return root;
        };
    }
}

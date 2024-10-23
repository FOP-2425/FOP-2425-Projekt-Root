package hProjekt.view.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.util.Builder;

public class MainMenuBuilder implements Builder<Region> {
    private final Runnable loadGameSceneAction;
    private final Runnable loadMapEditorAction;
    private final Runnable loadSettingsAction;
    private final Runnable loadAboutSceneAction;
    private final Runnable quitAction;

    public MainMenuBuilder(Runnable loadGameSceneAction, Runnable loadMapEditorAction, Runnable loadSettingsAction,
            Runnable quitAction, Runnable loadAboutSceneAction) {
        this.loadGameSceneAction = loadGameSceneAction;
        this.loadMapEditorAction = loadMapEditorAction;
        this.loadSettingsAction = loadSettingsAction;
        this.quitAction = quitAction;
        this.loadAboutSceneAction = loadAboutSceneAction;
    }

    @Override
    public Region build() {
        StackPane root = new StackPane();

        // Play background video
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

        // Create Title (Font does not work somehow...)
        Text title = new Text("Dampfross");
        title.getStyleClass().add("text-title");

        // Create Start Game Button
        Button startGameButton = new Button("Start Game");
        startGameButton.setMinWidth(200);
        startGameButton.setOnAction(event -> {
            mediaPlayer.stop();
            loadGameSceneAction.run();
        });
        startGameButton.getStyleClass().add("button");

        // Create Map Editor Button
        Button mapEditorButton = new Button("Map Editor");
        mapEditorButton.setMinWidth(200);
        mapEditorButton.setOnAction(event -> {
            mediaPlayer.stop();
            loadMapEditorAction.run();
        });
        mapEditorButton.getStyleClass().add("button");

        // Create Settings Button
        Button settingsButton = new Button("Settings");
        settingsButton.setMinWidth(200);
        settingsButton.setOnAction(event -> {
            mediaPlayer.stop();
            loadSettingsAction.run();
        });
        settingsButton.getStyleClass().add("button");

        // Add them to the box in the middle
        centerBox.getChildren().addAll(title, startGameButton, mapEditorButton, settingsButton);

        // Create box for Exit Button
        HBox bottomBox = new HBox();
        bottomBox.setPadding(new Insets(20));
        bottomBox.setAlignment(Pos.BOTTOM_LEFT);

        // Create Exit Button
        Button exitButton = new Button("Exit");
        exitButton.setMinWidth(150);
        exitButton.setOnAction(event -> {
            mediaPlayer.stop();
            quitAction.run();
        });
        exitButton.getStyleClass().add("button");

        BorderPane layout = new BorderPane();
        layout.setCenter(centerBox);
        layout.setBottom(bottomBox);
        bottomBox.getChildren().add(exitButton);

        root.getChildren().addAll(mediaView, layout);

        // Add CSS style
        root.getStylesheets().add(getClass().getResource("/css/mainmenu.css").toExternalForm());

        return root;
    }

}

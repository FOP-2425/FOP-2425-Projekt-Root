package hProjekt.view.menus;



import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class SettingsBuilder implements Builder<Region> {
    private final Runnable returnToMainMenuAction;

    public SettingsBuilder(Runnable returnToMainMenuAction) {
        this.returnToMainMenuAction = returnToMainMenuAction;
    }

    @Override
    public Region build() {
        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #1f1f2e;");
        root.setPadding(new Insets(20));

        // Grid for settings
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(15);

        // Title
        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        root.setTop(title);

        // Setting 1: Dice Sides
        Label diceSidesLabel = new Label("Dice Sides:");
        diceSidesLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> diceSidesSpinner = new Spinner<>();
        diceSidesSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 9, 6));

        // Setting 2: Map Scale
        Label mapScaleLabel = new Label("Map Scale:");
        mapScaleLabel.setStyle("-fx-text-fill: white;");
        Slider mapScaleSlider = new Slider(10, 30, 15);
        mapScaleSlider.setMajorTickUnit(5);
        mapScaleSlider.setShowTickLabels(true);
        mapScaleSlider.setShowTickMarks(true);

        // Setting 3: Starting Credits
        Label startingCreditsLabel = new Label("Starting Credits:");
        startingCreditsLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> startingCreditsSpinner = new Spinner<>();
        startingCreditsSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(10, 50, 20));

        // Add components to grid
        grid.add(diceSidesLabel, 0, 0);
        grid.add(diceSidesSpinner, 1, 0);

        grid.add(mapScaleLabel, 0, 1);
        grid.add(mapScaleSlider, 1, 1);

        grid.add(startingCreditsLabel, 0, 2);
        grid.add(startingCreditsSpinner, 1, 2);

        root.setCenter(grid);

        // Back Button
        Button backButton = new Button("Back to Main Menu");
        backButton.setMinWidth(200);
        backButton.setOnAction(event -> returnToMainMenuAction.run());
        backButton.getStyleClass().add("button");
        BorderPane.setAlignment(backButton, Pos.BOTTOM_CENTER);
        root.setBottom(backButton);

        // Add CSS style
        root.getStylesheets().add(getClass().getResource("/css/settings.css").toExternalForm());

        return root;
    }
}

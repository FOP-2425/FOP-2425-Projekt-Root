package hProjekt.controller.gui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.layout.*;
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

        // Title
        Label title = new Label("Settings");
        title.setStyle("-fx-font-size: 24px; -fx-text-fill: white;");
        BorderPane.setAlignment(title, Pos.TOP_CENTER);
        root.setTop(title);

        // Grid for categorized settings
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(20);
        grid.setVgap(20);

        // Create each category box
        VBox mapSettingsBox = createCategoryBox("Map Settings", createMapSettingsContent());
        VBox gameMechanicsBox = createCategoryBox("Game Mechanics", createGameMechanicsContent());
        VBox probabilitiesBox = createCategoryBox("Probabilities", createProbabilitiesContent());
        VBox radiusSettingsBox = createCategoryBox("Radius Settings", createRadiusSettingsContent());
        VBox tileCostsBox = createCategoryBox("Tile Costs", createTileCostsContent());

        // Add boxes to grid (3x2 layout)
        grid.add(mapSettingsBox, 0, 0);
        grid.add(gameMechanicsBox, 1, 0);
        grid.add(probabilitiesBox, 0, 1);
        grid.add(radiusSettingsBox, 1, 1);
        grid.add(tileCostsBox, 0, 2, 2, 1);

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

    private VBox createCategoryBox(String title, Pane content) {
        VBox box = new VBox(10);
        box.setPadding(new Insets(15));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: #2b2b3b; -fx-background-radius: 10; -fx-border-radius: 10;");

        Label boxTitle = new Label(title);
        boxTitle.setStyle("-fx-font-size: 18px; -fx-text-fill: white;");
        box.getChildren().addAll(boxTitle, content);

        return box;
    }

    private Pane createMapSettingsContent() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER);

        // Map Scale
        Label mapScaleLabel = new Label("Map Scale:");
        mapScaleLabel.setStyle("-fx-text-fill: white;");
        Slider mapScaleSlider = new Slider(10, 30, 15);
        mapScaleSlider.setMajorTickUnit(5);
        mapScaleSlider.setShowTickLabels(true);
        mapScaleSlider.setShowTickMarks(true);

        pane.add(mapScaleLabel, 0, 0);
        pane.add(mapScaleSlider, 1, 0);

        return pane;
    }

    private Pane createGameMechanicsContent() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER);

        // Dice Sides
        Label diceSidesLabel = new Label("Dice Sides:");
        diceSidesLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> diceSidesSpinner = new Spinner<>(1, 9, 6);

        // Starting Credits
        Label startingCreditsLabel = new Label("Starting Credits:");
        startingCreditsLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> startingCreditsSpinner = new Spinner<>(10, 50, 20);

        // Min Players
        Label minPlayersLabel = new Label("Min Players:");
        minPlayersLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> minPlayersSpinner = new Spinner<>(2, 6, 2);

        // Max Players
        Label maxPlayersLabel = new Label("Max Players:");
        maxPlayersLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> maxPlayersSpinner = new Spinner<>(2, 6, 6);

        // City Connection Bonus
        Label cityConnectionBonusLabel = new Label("City Connection Bonus:");
        cityConnectionBonusLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> cityConnectionBonusSpinner = new Spinner<>(1, 20, 6);

        pane.add(diceSidesLabel, 0, 0);
        pane.add(diceSidesSpinner, 1, 0);
        pane.add(startingCreditsLabel, 0, 1);
        pane.add(startingCreditsSpinner, 1, 1);
        pane.add(minPlayersLabel, 0, 2);
        pane.add(minPlayersSpinner, 1, 2);
        pane.add(maxPlayersLabel, 0, 3);
        pane.add(maxPlayersSpinner, 1, 3);
        pane.add(cityConnectionBonusLabel, 0, 4);
        pane.add(cityConnectionBonusSpinner, 1, 4);

        return pane;
    }

    private Pane createProbabilitiesContent() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER);

        // City Base Probability
        Label cityBaseProbLabel = new Label("City Base Probability:");
        cityBaseProbLabel.setStyle("-fx-text-fill: white;");
        Slider cityBaseProbSlider = new Slider(0.0, 1.0, 0.3);
        cityBaseProbSlider.setMajorTickUnit(0.1);
        cityBaseProbSlider.setShowTickLabels(true);
        cityBaseProbSlider.setShowTickMarks(true);

        // City At Coast Probability
        Label cityAtCoastProbLabel = new Label("City At Coast Probability:");
        cityAtCoastProbLabel.setStyle("-fx-text-fill: white;");
        Slider cityAtCoastProbSlider = new Slider(0.0, 1.0, 0.1);
        cityAtCoastProbSlider.setMajorTickUnit(0.1);
        cityAtCoastProbSlider.setShowTickLabels(true);
        cityAtCoastProbSlider.setShowTickMarks(true);

        // City Near Mountain Probability
        Label cityNearMountainProbLabel = new Label("City Near Mountain Probability:");
        cityNearMountainProbLabel.setStyle("-fx-text-fill: white;");
        Slider cityNearMountainProbSlider = new Slider(0.0, 1.0, 0.05);
        cityNearMountainProbSlider.setMajorTickUnit(0.1);
        cityNearMountainProbSlider.setShowTickLabels(true);
        cityNearMountainProbSlider.setShowTickMarks(true);

        // City Near City Probability
        Label cityNearCityProbLabel = new Label("City Near City Probability:");
        cityNearCityProbLabel.setStyle("-fx-text-fill: white;");
        Slider cityNearCityProbSlider = new Slider(0.0, 1.0, 0.001);
        cityNearCityProbSlider.setMajorTickUnit(0.1);
        cityNearCityProbSlider.setShowTickLabels(true);
        cityNearCityProbSlider.setShowTickMarks(true);

        pane.add(cityBaseProbLabel, 0, 0);
        pane.add(cityBaseProbSlider, 1, 0);
        pane.add(cityAtCoastProbLabel, 0, 1);
        pane.add(cityAtCoastProbSlider, 1, 1);
        pane.add(cityNearMountainProbLabel, 0, 2);
        pane.add(cityNearMountainProbSlider, 1, 2);
        pane.add(cityNearCityProbLabel, 0, 3);
        pane.add(cityNearCityProbSlider, 1, 3);

        return pane;
    }

    private Pane createRadiusSettingsContent() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER);

        // Radius for Mountain
        Label mountainRadiusLabel = new Label("Mountain Radius:");
        mountainRadiusLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> mountainRadiusSpinner = new Spinner<>(1, 5, 1);

        // Radius for City
        Label cityRadiusLabel = new Label("City Radius:");
        cityRadiusLabel.setStyle("-fx-text-fill: white;");
        Spinner<Integer> cityRadiusSpinner = new Spinner<>(1, 5, 3);

        pane.add(mountainRadiusLabel, 0, 0);
        pane.add(mountainRadiusSpinner, 1, 0);
        pane.add(cityRadiusLabel, 0, 1);
        pane.add(cityRadiusSpinner, 1, 1);

        return pane;
    }

    private Pane createTileCostsContent() {
        GridPane pane = new GridPane();
        pane.setHgap(10);
        pane.setVgap(10);
        pane.setAlignment(Pos.CENTER);

        // Tile Building Costs (Simple Labels for now)
        Label tileBuildingCostLabel = new Label("Tile Building Costs:");
        tileBuildingCostLabel.setStyle("-fx-text-fill: white;");
        Label tileDrivingCostLabel = new Label("Tile Driving Costs:");
        tileDrivingCostLabel.setStyle("-fx-text-fill: white;");

        pane.add(tileBuildingCostLabel, 0, 0);
        pane.add(tileDrivingCostLabel, 0, 1);

        return pane;
    }
}

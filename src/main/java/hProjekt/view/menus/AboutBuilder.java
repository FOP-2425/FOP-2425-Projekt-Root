package hProjekt.view.menus;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Builder;

public class AboutBuilder implements Builder<Region> {

    private final Runnable loadMainMenuAction;

    public AboutBuilder(Runnable loadMainMenuAction) {
        this.loadMainMenuAction = loadMainMenuAction;
    }

    @Override
    public VBox build() {
        VBox root = new VBox(20);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);

        // Logo
        ImageView logo = new ImageView(new Image(getClass().getResourceAsStream("/images/dampfross_logo_white.png")));
        logo.setFitWidth(300);
        logo.setPreserveRatio(true);

        Text gameDescription = new Text("Dampfross is a strategic train simulation game where you manage railway networks and expand your empire.");
        gameDescription.getStyleClass().add("text-description");
        gameDescription.setFill(Color.WHITE);

        // Developer Information
        Text developerInfo = new Text("Developed by Technical University of Darmstadt, Department of Computer Science, Chair of Algorithms.");
        developerInfo.getStyleClass().add("text-subheading");
        developerInfo.setFill(Color.WHITE); 

        // Credits
        Text credits = new Text("Credits:\n" +
                "Programmers: Alice, Bob\n" +
                "Testing: Alice, Bob");
        credits.getStyleClass().add("text-credits");
        credits.setFill(Color.WHITE);

        // License Information
        Text licenseInfo = new Text("Built using JavaFX.\n" +
                "Licensed under the MIT License.");
        licenseInfo.getStyleClass().add("text-license");
        licenseInfo.setFill(Color.WHITE); 

        // Acknowledgements
        Text acknowledgements = new Text("Special thanks to our dedicated supporters.");
        acknowledgements.getStyleClass().add("text-acknowledgements");
        acknowledgements.setFill(Color.WHITE); 

        // Legal Information
        Text legalInfo = new Text("© 2024 TU Darmstadt Chair of Algorithms. All rights reserved.\n" +
                "All trademarks are the property of their respective owners.");
        legalInfo.getStyleClass().add("text-legal");
        legalInfo.setFill(Color.WHITE); 

        // Back to Main Menu Button
        Button backButton = new Button("Back to Main Menu");
        backButton.setOnAction(event -> loadMainMenuAction.run());
        backButton.getStyleClass().add("button");
        root.getChildren().addAll(logo, gameDescription, developerInfo, credits, licenseInfo, acknowledgements, legalInfo, backButton);

        // Add css style
        root.getStylesheets().add(getClass().getResource("/css/about.css").toExternalForm());

        return root;
    }
}

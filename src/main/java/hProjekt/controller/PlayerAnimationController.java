package hProjekt.controller;

import hProjekt.model.Player;
import hProjekt.model.Tile;
import hProjekt.model.TilePosition;
import hProjekt.view.HexGridBuilder;
import javafx.animation.PathTransition;
import javafx.animation.SequentialTransition;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.util.Duration;

import java.util.List;

/**
 * Controls the player animation, moving a circle along a path of Tiles with a train icon inside.
 */
public class PlayerAnimationController {

    private final HexGridBuilder hexGridBuilder;
    private final StackPane playerContainer;
    private final Circle playerCircle;
    private final ImageView trainImage;

    /**
     * Creates a new PlayerAnimationController.
     *
     * @param hexGridBuilder the HexGridBuilder to calculate tile positions
     */
    public PlayerAnimationController(HexGridBuilder hexGridBuilder, Color playerColor) {
        this.hexGridBuilder = hexGridBuilder;

        // Create the circle representing the player
        this.playerCircle = new Circle(40, playerColor);

        // Create the train ImageView
        this.trainImage = new ImageView(new Image(getClass().getResourceAsStream("/images/train.png")));
        trainImage.setFitWidth(45); 
        trainImage.setFitHeight(45); 
        trainImage.setPreserveRatio(true);

        // Create a StackPane to center the image on the circle
        this.playerContainer = new StackPane(playerCircle, trainImage);
        this.playerContainer.setTranslateX(0);
        this.playerContainer.setTranslateY(0);

        // Add the container to the HexGridPane
        this.hexGridBuilder.getHexGridPane().getChildren().add(playerContainer);
    }

    /**
     * Animates the playerCircle along the given list of Tiles.
     *
     * @param tiles the list of Tiles to follow
     */
    public void animatePlayer(List<Tile> tiles, Color playerColor) {
        if (tiles == null || tiles.size() < 2) {
            throw new IllegalArgumentException("At least two tiles are required for the animation.");
        }

        // Sequential Transition to combine multiple path transitions
        SequentialTransition animationSequence = new SequentialTransition();

        for (int i = 0; i < tiles.size() - 1; i++) {
            Tile currentTile = tiles.get(i);
            Tile nextTile = tiles.get(i + 1);

            // Get the center points of the current and next tile
            TilePosition currentPosition = currentTile.getPosition();
            TilePosition nextPosition = nextTile.getPosition();

            Point2D currentCenter = hexGridBuilder.calculatePositionCenterOffset(currentPosition);
            Point2D nextCenter = hexGridBuilder.calculatePositionCenterOffset(nextPosition);

            // Create a path between the two tile centers
            Path path = new Path();
            path.getElements().add(new MoveTo(currentCenter.getX(), currentCenter.getY()));
            path.getElements().add(new LineTo(nextCenter.getX(), nextCenter.getY()));

            // Create a PathTransition for this segment
            PathTransition transition = new PathTransition();
            transition.setNode(playerContainer); 
            transition.setPath(path);
            transition.setDuration(Duration.seconds(4)); 
            transition.setCycleCount(1);

            // Add the transition to the sequence
            animationSequence.getChildren().add(transition);
        }

        // Play the animation
        animationSequence.play();
    }
}

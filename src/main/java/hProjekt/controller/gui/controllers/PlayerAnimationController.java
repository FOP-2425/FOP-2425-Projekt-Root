package hProjekt.controller.gui.controllers;

import java.util.List;

import hProjekt.model.Tile;
import hProjekt.model.TilePosition;
import hProjekt.view.HexGridBuilder;
import javafx.animation.Animation;
import javafx.animation.Interpolator;
import javafx.animation.PathTransition;
import javafx.animation.ScaleTransition;
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

/**
 * Controls the player animation, moving a circle along a path of Tiles with a
 * train icon inside.
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
     * @param playerColor    the color of the player's circle
     */
    public PlayerAnimationController(HexGridBuilder hexGridBuilder, Color playerColor) {
        this.hexGridBuilder = hexGridBuilder;

        // Create the circle representing the player
        this.playerCircle = new Circle(36, playerColor);

        // Create the train ImageView
        this.trainImage = new ImageView(new Image(getClass().getResourceAsStream("/images/train.png")));
        trainImage.setFitWidth(42);
        trainImage.setFitHeight(42);
        trainImage.setPreserveRatio(true);

        // Create a StackPane to center the image on the circle
        this.playerContainer = new StackPane(playerCircle, trainImage);
        this.playerContainer.setTranslateX(0);
        this.playerContainer.setTranslateY(0);
        playerContainer.setMouseTransparent(true);
        this.hexGridBuilder.getHexGridPane().getChildren().add(playerContainer);
        hideTrain();
    }

    /**
     * Animates the playerCircle along the given list of Tiles.
     *
     * @param tiles the list of Tiles to follow
     * @param red
     */
    public Animation animatePlayer(List<Tile> tiles) {
        if (tiles == null || tiles.size() < 2) {
            throw new IllegalArgumentException("At least two tiles are required for the animation.");
        }
        // Sequential Transition to combine multiple path transitions
        SequentialTransition animationSequence = new SequentialTransition();
        Path path = new Path();

        for (int i = 0; i < tiles.size() - 1; i++) {
            Tile currentTile = tiles.get(i);
            Tile nextTile = tiles.get(i + 1);

            // Get the center points of the current and next tile
            TilePosition currentPosition = currentTile.getPosition();
            TilePosition nextPosition = nextTile.getPosition();

            Point2D currentCenter = hexGridBuilder.calculatePositionCenterOffset(currentPosition);
            Point2D nextCenter = hexGridBuilder.calculatePositionCenterOffset(nextPosition);

            // Create a path between the two tile centers
            path.getElements().add(new MoveTo(currentCenter.getX(),
                    currentCenter.getY()));
            path.getElements().add(new LineTo(nextCenter.getX(), nextCenter.getY()));

        }
        // Create a PathTransition for this segment
        PathTransition transition = new PathTransition();
        transition.setNode(playerContainer);
        transition.setPath(path);
        transition.setDuration(Duration.seconds(1));
        transition.setInterpolator(Interpolator.EASE_BOTH);
        transition.setCycleCount(1);

        // Add the transition to the sequence
        animationSequence.getChildren().add(transition);

        // Add pulsating animation before the main animation
        ScaleTransition pulseBefore = createPulseTransition();

        // Add pulsating animation after the main animation
        ScaleTransition pulseAfter = createPulseTransition();

        // Combine all animations into a single SequentialTransition
        SequentialTransition fullAnimation = new SequentialTransition(pulseBefore,
                animationSequence, pulseAfter);
        showTrain();
        fullAnimation.play();
        return fullAnimation;
    }

    public void setPosition(TilePosition position) {
        Point2D center = hexGridBuilder.calculatePositionCenterOffset(position);
        playerContainer.setTranslateX(center.getX() - playerContainer.getWidth() / 2);
        playerContainer.setTranslateY(center.getY() - playerContainer.getHeight() / 2);
    }

    public void showTrain() {
        playerContainer.setVisible(true);
    }

    public void hideTrain() {
        playerContainer.setVisible(false);
    }

    /**
     * Creates a pulsating scale transition for the playerCircle.
     *
     * @return the ScaleTransition animation
     */
    private ScaleTransition createPulseTransition() {
        ScaleTransition pulse = new ScaleTransition(Duration.seconds(0.3), playerContainer);
        pulse.setFromX(1.0);
        pulse.setFromY(1.0);
        pulse.setToX(2.0); // Scale up to 200% of the original size
        pulse.setToY(2.0);
        pulse.setAutoReverse(true); // Return to the original size
        pulse.setCycleCount(2); // Scale up and down once
        return pulse;
    }
}

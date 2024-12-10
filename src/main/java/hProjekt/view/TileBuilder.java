package hProjekt.view;

import hProjekt.model.Tile;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.util.Builder;

/**
 * A Builder to create views for {@link Tile}s.
 * Renders the {@link Tile} with a resource icon, a label for the roll number
 * and the robber if present.
 * Has methods to highlight and unhighlight the tile.
 */
public class TileBuilder implements Builder<Region> {
    private final Tile tile;
    private final StackPane pane = new StackPane();

    /**
     * Creates a new TileBuilder for the given {@link Tile}.
     *
     * @param tile the tile to render
     */
    public TileBuilder(final Tile tile) {
        this.tile = tile;
        styleAndSizeTile(pane);
        // printComputedStyles(pane);
    }

    /**
     * Returns the {@link Tile} this builder renders.
     *
     * @return the tile
     */
    public Tile getTile() {
        return tile;
    }

    @Override
    public Region build() {
        pane.getChildren().clear();
        final VBox mainBox = new VBox();
        final StackPane resourcePane = new StackPane();

        mainBox.getChildren().addAll(resourcePane, createLabels());
        mainBox.setAlignment(Pos.CENTER);
        pane.getChildren().addAll(mainBox);
        return pane;
    }

    /**
     * Styles and resizes the tile so it is rendered as a hexagon via css.
     *
     * @param stackPane
     */
    private void styleAndSizeTile(final StackPane stackPane) {
        stackPane.getStylesheets().add("css/hexmap.css");
        stackPane.getStyleClass().add("hex-tile");
        stackPane.maxHeightProperty().bind(tile.heightProperty());
        stackPane.maxWidthProperty().bind(tile.widthProperty());
        stackPane.minHeightProperty().bind(tile.heightProperty());
        stackPane.minWidthProperty().bind(tile.widthProperty());
        stackPane.setBackground(Background.fill(tile.getType().color));
    }

    /**
     * Creates a VBox with labels for the position, resource and roll number of the
     * tile.
     *
     * @return the created VBox
     */
    private VBox createLabels() {
        final VBox labelBox = new VBox();
        final Label positionLabel = new Label(tile.getPosition().toString());
        positionLabel.getStyleClass().add("highlighted-label");
        positionLabel.setFont(new Font(4));
        final Label resourceLabel = new Label(tile.getType().toString());
        resourceLabel.getStyleClass().add("highlighted-label");
        // labelBox.getChildren().addAll(positionLabel);

        labelBox.setAlignment(Pos.CENTER);
        return labelBox;
    }

    /**
     * Highlights the tile and sets a handler for mouse clicks.
     *
     * @param hanlder the handler to call when the tile is clicked
     */
    public void highlight(final Runnable hanlder) {
        pane.getStyleClass().add("selectable");
        pane.setOnMouseClicked(e -> hanlder.run());
    }

    /**
     * Removes the highlight and the handler for mouse clicks.
     */
    public void unhighlight() {
        pane.getStyleClass().remove("selectable");
        pane.setOnMouseClicked(null);
    }
}

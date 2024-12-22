package hProjekt.controller.gui.controllers;

import java.util.function.Consumer;

import hProjekt.model.Tile;
import hProjekt.view.TileBuilder;
import javafx.scene.input.MouseEvent;

/**
 * The controller for a tile.
 */
public class TileController implements Controller {
    private final TileBuilder builder;

    /**
     * Creates a new tile controller.
     *
     * @param tile the tile to render
     */
    public TileController(final Tile tile) {
        builder = new TileBuilder(tile);
    }

    /**
     * Returns the tile.
     *
     * @return the tile
     */
    public Tile getTile() {
        return builder.getTile();
    }

    /**
     * Highlights the tile.
     *
     * @param handler the handler to call when the tile is clicked
     */
    public void highlight(final Consumer<Tile> handler) {
        builder.highlight(() -> handler.accept(getTile()));
    }

    /**
     * Unhighlights the tile.
     */
    public void unhighlight() {
        builder.unhighlight();
    }

    public void addMouseEnteredHandler(final Consumer<MouseEvent> handler) {
        builder.addMouseEnteredHandler(handler);
    }

    public void removeMouseEnteredHandler() {
        builder.removeMouseEnteredHandler();
    }

    @Override
    public TileBuilder getBuilder() {
        return builder;
    }
}

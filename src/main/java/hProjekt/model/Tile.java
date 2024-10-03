package hProjekt.model;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.model.TilePosition.EdgeDirection;
import javafx.beans.value.ObservableDoubleValue;
import javafx.scene.paint.Color;

/**
 * Represents a tile in the game grid.
 * A tile has six sides ({@link Edge}s), six vertices
 * ({@link Intersection}s), a
 * {@link ResourceType} and a roll number.
 */
@DoNotTouch
public interface Tile {

    /**
     * Returns the position of this tile.
     *
     * @return the position of this tile
     */
    TilePosition getPosition();

    /**
     * Returns the type of this tile.
     *
     * @return the type of this tile
     */
    Type getType();

    /**
     * Returns the height of this tile.
     *
     * @return the height of this tile
     */
    ObservableDoubleValue heightProperty();

    /**
     * Returns the width of this tile.
     *
     * @return the width of this tile
     */
    ObservableDoubleValue widthProperty();

    /**
     * Returns the hex grid this tile is part of.
     *
     * @return the hex grid this tile is part of
     */
    HexGrid getHexGrid();

    /**
     * Returns all neighbours of this tile.
     *
     * @return all neighbours of this tile
     */
    default Set<Tile> getNeighbours() {
        return getHexGrid().getTiles().entrySet().stream()
                .filter(entrySet -> TilePosition.neighbours(getPosition()).contains(entrySet.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    /**
     * Returns the tile next to the given edge.
     *
     * @param direction the direction of the edge
     * @return the neighbouring tile
     */
    default Tile getNeighbour(final EdgeDirection direction) {
        return getHexGrid().getTileAt(TilePosition.neighbour(getPosition(), direction));
    }

    default boolean isAtCaost() {
        return getNeighbours().size() < 6;
    }

    /**
     * Returns the edge on the given edge.
     *
     * @param direction the direction of the edge
     * @return the edge on the given edge
     */
    Edge getEdge(EdgeDirection direction);

    Set<Edge> getEdges();

    /**
     * Add a rail on the given edge.
     * Check {@link HexGrid#addRail(TilePosition, TilePosition, Player, boolean)}
     * for details.
     *
     * @param direction the direction of the edge
     * @param owner     the player who owns the rail
     * @return whether the rail was added
     */
    boolean addRail(EdgeDirection direction, Player owner);

    boolean hasCity();

    /**
     * An enumeration containing all available tile types.
     * Custom tile types need to be added to this list manually.
     */
    enum Type {
        PLAIN(Color.GREEN),
        MOUNTAIN(Color.BROWN);

        /**
         * The color of the tile.
         */
        public final Color color;

        Type(final Color color) {
            this.color = color;
        }
    }
}

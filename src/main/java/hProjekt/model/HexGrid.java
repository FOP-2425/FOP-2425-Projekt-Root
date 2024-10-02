package hProjekt.model;

import java.util.Map;
import java.util.Set;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.model.TilePosition.EdgeDirection;
import javafx.beans.property.DoubleProperty;
import javafx.beans.value.ObservableDoubleValue;

/**
 * Holds all the information displayed on the hexagonal grid and information for
 * rendering. In short, the game board.
 * Specifically, information on...
 * <ul>
 * <li>tiles and their logical and graphical properties (position, height,
 * width, etc.)</li>
 * <li>edges</li>
 * <li>intersections</li>
 * <li>the robber / bandit</li>
 * </ul>
 * are saved in and modified by instances of this interface.
 */
@DoNotTouch
public interface HexGrid {

    // Tiles

    /**
     * Returns the width of a tile.
     *
     * @return the width of a tile
     */
    double getTileWidth();

    /**
     * Returns the height of a tile.
     *
     * @return the height of a tile
     */
    double getTileHeight();

    /**
     * Returns the size of a tile.
     *
     * @return the size of a tile
     */
    double getTileSize();

    /**
     * Returns the width of a tile as an {@link ObservableDoubleValue}.
     *
     * @return the width of a tile as an {@link ObservableDoubleValue}
     */
    ObservableDoubleValue tileWidthProperty();

    /**
     * Returns the height of a tile as an {@link ObservableDoubleValue}.
     *
     * @return the height of a tile as an {@link ObservableDoubleValue}
     */
    ObservableDoubleValue tileHeightProperty();

    /**
     * Returns the size of a tile as an {@link DoubleProperty}.
     *
     * @return the size of a tile as an {@link DoubleProperty}
     */
    DoubleProperty tileSizeProperty();

    /**
     * Returns all tiles of the grid as a set.
     *
     * @return all tiles of the grid as a set
     */
    Map<TilePosition, Tile> getTiles();

    /**
     * Returns the tile at the given q and r coordinate.
     *
     * @param q the q-coordinate of the tile
     * @param r the r-coordinate of the tile
     * @return the tile at the given row and column
     */
    Tile getTileAt(int q, int r);

    /**
     * Returns the tile at the given position.
     *
     * @param position the position of the tile
     * @return the tile at the given position
     */
    Tile getTileAt(TilePosition position);

    // Edges / Roads

    /**
     * Returns all edges of the grid.
     *
     * @return all edges of the grid
     */
    Map<Set<TilePosition>, Edge> getEdges();

    /**
     * Returns the edge between the given positions.
     *
     * @param position0 the first position
     * @param position1 the second position
     * @return the edge between the given intersections
     */
    Edge getEdge(TilePosition position0, TilePosition position1);

    /**
     * Returns all rails of the given player.
     *
     * @param player the player to get the rails of
     * @return all rails of the given player
     */
    Map<Set<TilePosition>, Edge> getRails(Player player);

    /**
     * Adds the given rail to the grid. Does not check or remove the player's
     * resources.
     *
     * @param position0 the first position of the rail
     * @param position1 the second position of the rail
     * @param player    the player that owns the rail
     * @return whether the rail was added
     */
    boolean addRail(TilePosition position0, TilePosition position1, Player player);

    /**
     * Adds the given rail to the grid relative to the given tile.
     * See {@link HexGrid#addRail(TilePosition, TilePosition, Player, boolean)}
     * for details.
     *
     * @param tile          the tile the rail is next to
     * @param edgeDirection the direction of the edge the rail is on
     * @param player        the player that owns the rail
     * @param checkVillages whether to check if the player has a connected village
     * @return whether the rail was added
     */
    default boolean addRail(
            final Tile tile, final EdgeDirection edgeDirection, final Player player) {
        return tile.addRail(edgeDirection, player);
    }

    /**
     * Removes the rail between the given positions.
     *
     * @param position0 the first position
     * @param position1 the second position
     * @return whether the rail was removed
     */
    boolean removeRail(TilePosition position0, TilePosition position1);

    /**
     * Removes the rail at the given edge.
     *
     * @param rail (the edge of) the rail to remove
     * @return {@code true}, if the rail has been successfully removed,
     *         {@code false} otherwise
     */
    default boolean removeRail(final Edge rail) {
        return removeRail(rail.getPosition1(), rail.getPosition2());
    }

    Map<TilePosition, City> getCities();

    City getCityAt(TilePosition position);
}

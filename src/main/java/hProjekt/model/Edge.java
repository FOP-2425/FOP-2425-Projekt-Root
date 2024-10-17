package hProjekt.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.property.Property;

/**
 * Holds information on an edge connecting two tile centers.
 * An edge is defined by two adjacent {@link TilePosition}s.
 */
public interface Edge {

    /**
     * Returns the HexGrid instance this edge is placed in.
     *
     * @return the HexGrid instance this edge is placed in
     */
    HexGrid getHexGrid();

    /**
     * Returns the first position.
     *
     * @return the first position
     */
    TilePosition getPosition1();

    /**
     * Returns the second position.
     *
     * @return the second position
     */
    TilePosition getPosition2();

    /**
     * Returns {@code true} if the given edge connects to this edge and
     * {@code false} otherwise.
     *
     * @param other the other edge
     * @return whether the two edges are connected
     */
    default boolean connectsTo(Edge other) {
        return getAdjacentTilePositions().contains(other.getPosition1())
                || getAdjacentTilePositions().contains(other.getPosition2());
    }

    /**
     * Returns the {@link TilePosition}s that this edge lies between.
     *
     * @return the adjacent tile positions
     */
    default Set<TilePosition> getAdjacentTilePositions() {
        return Set.of(getPosition1(), getPosition2());
    }

    /**
     * Returns all edges that connect to this edge in the grid.
     *
     * @return all edges connected to this one
     */
    default Set<Edge> getConnectedEdges() {
        return getHexGrid().getEdges().entrySet().stream().map(entry -> entry.getValue())
                .filter(edge -> connectsTo(edge)).collect(Collectors.toUnmodifiableSet());
    }

    /**
     * Returns {@code true} if a player has built a rail on this edge and
     * {@code false} otherwise.
     *
     * @return whether a player has placed a rail on this edge
     */
    default boolean hasRail() {
        return getRailOwnersProperty().getValue() != null || !getRailOwnersProperty().getValue().isEmpty();
    }

    /**
     * Returns the rail's owner, if a rail has been built on this edge.
     *
     * @return the rail's owner, if a rail has been built on this edge
     */
    Property<List<Player>> getRailOwnersProperty();

    default List<Player> getRailOwners() {
        return getRailOwnersProperty().getValue();
    }

    /**
     * Returns the connected rails of the given player.
     *
     * @param player the player to check for.
     * @return the connected rails.
     */
    Set<Edge> getConnectedRails(Player player);

    int getCost(Player player);
}

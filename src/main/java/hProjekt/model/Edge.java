package hProjekt.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import hProjekt.Config;
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
        return getRailOwnersProperty().getValue() != null && !getRailOwnersProperty().getValue().isEmpty();
    }

    /**
     * Adds a rail for the given player to this edge.
     * Checks if the player can build a rail on this edge.
     * This method does not verify that the player has enough credits to build a
     * rail.
     *
     * The following conditions must be met:
     * - The player hasn't already built a rail here
     * - The player has built a rail on a connected edge
     * - If the player hasn't built any rails yet, the edge must be connected to a
     * starting city
     *
     * @param player the player to add the rail for
     * @return {@code true} if the rail was added, {@code false} otherwise
     */
    default boolean addRail(Player player) {
        if (getRailOwners().contains(player) || (player.getRails().size() > 0
                && getConnectedEdges().stream().noneMatch(e -> e.getRailOwners().contains(player)))
                || (player.getRails().size() == 0 && Collections.disjoint(getHexGrid().getStartingCities().keySet(),
                        getAdjacentTilePositions()))) {
            return false;
        }
        return getRailOwnersProperty().getValue().add(player);
    }

    /**
     * Removes the rail of the given player from this edge.
     *
     * @param player the player to remove the rail for
     * @return {@code true} if the rail was removed, {@code false} otherwise
     */
    default boolean removeRail(Player player) {
        return getRailOwnersProperty().getValue().remove(player);
    }

    /**
     * Returns the rail's owners, if a rail has been built on this edge.
     *
     * @return the rail's owners, if a rail has been built on this edge
     */
    Property<List<Player>> getRailOwnersProperty();

    /**
     * Returns the rail's owners, if a rail has been built on this edge.
     *
     * @return the rail's owners, if a rail has been built on this edge
     */
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

    /**
     * Returns the cost of building a rail on this edge.
     *
     * @return the cost of building a rail on this edge
     */
    default int getBuildingCost() {
        return Config.TILE_TYPE_TO_COST_MAP.get(getAdjacentTilePositions().stream()
                .map(position -> getHexGrid().getTileAt(position).getType()).collect(Collectors.toUnmodifiableSet()));
    }

    /**
     * Returns the cost that needs to be paid to each player that has already built
     * on this edge.
     *
     * @param player the player to calculate the parallel cost for
     * @return the cost that needs to be paid to each player that has already built
     *         on this edge
     */
    default Map<Player, Integer> getParallelCost(Player player) {
        final Map<Player, Integer> result = new HashMap<>();
        if (!getRailOwners().isEmpty() && !(getRailOwners().size() == 1 && getRailOwners().contains(player))) {
            if (Collections.disjoint(getHexGrid().getCities().keySet(), getAdjacentTilePositions())) {
                getRailOwners().stream().forEach(p -> result.put(p, 5));
            } else {
                getRailOwners().stream().forEach(p -> result.put(p, 3));
            }
        }

        getAdjacentTilePositions().stream()
                .flatMap(position -> {
                    if (getHexGrid().getCityAt(position) != null) {
                        return Stream.empty();
                    }
                    Set<Player> owners = getHexGrid().getTileAt(position).getEdges().stream()
                            .filter(Predicate.not(this::equals)).flatMap(edge -> edge.getRailOwners().stream())
                            .collect(Collectors.toUnmodifiableSet());
                    if (owners.contains(player)) {
                        return Stream.empty();
                    }
                    return owners.stream();
                }).forEach(p -> result.put(p, Math.max(result.getOrDefault(p, 0), 1)));

        return result;
    }

    /**
     * Returns the total cost that needs to be paid by the player to build a rail on
     * this edge if other players have already built on this edge.
     *
     * @param player the player to calculate the total parallel cost for
     * @return the total cost that needs to be paid by the player to build a rail
     */
    default int getTotalParallelCost(Player player) {
        return getParallelCost(player).values().stream().reduce(0, Integer::sum);
    }

    /**
     * Returns the total cost the player has to pay to build a rail on this edge.
     * The total cost is the sum of the building cost and the parallel cost times
     * the number of players that have already built on this edge.
     *
     * @param player the player to calculate the total cost for
     * @return the total cost the player has to pay to build a rail on this edge
     */
    default int getTotalCost(Player player) {
        return getBuildingCost() + getTotalParallelCost(player);
    }
}

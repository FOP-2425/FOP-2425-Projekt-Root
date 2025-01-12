package hProjekt.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
import javafx.beans.property.Property;
import javafx.util.Pair;

/**
 * Default implementation of {@link Edge}.
 *
 * @param grid       the HexGrid instance this edge is placed in
 * @param position1  the first position
 * @param position2  the second position
 * @param railOwners the road's owner, if a road has been built on this edge
 */
public record EdgeImpl(
        HexGrid grid,
        TilePosition position1,
        TilePosition position2,
        Property<List<Player>> railOwners) implements Edge {

    @Override
    public HexGrid getHexGrid() {
        return grid;
    }

    @Override
    public TilePosition getPosition1() {
        return position1;
    }

    @Override
    public TilePosition getPosition2() {
        return position2;
    }

    @Override
    public Property<List<Player>> getRailOwnersProperty() {
        return railOwners;
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public Set<Edge> getConnectedRails(final Player player) {
        return getConnectedEdges().stream()
                .filter(Edge::hasRail)
                .filter(edge -> edge.getRailOwnersProperty().getValue().contains(player))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public Map<Player, Integer> getRentingCost(Player player) {
        if (getRailOwners().contains(player)) {
            return Map.of();
        }
        return getRailOwners().stream().collect(Collectors.toMap(p -> p, p -> 1));
    }

    @Override
    public int getDrivingCost(TilePosition from) {
        if (!getAdjacentTilePositions().contains(from)) {
            throw new IllegalArgumentException("The given position is not adjacent to this edge.");
        }
        return Config.TILE_TYPE_TO_DRIVING_COST
                .get(new Pair<Tile.Type, Tile.Type>(getHexGrid().getTileAt(from).getType(),
                        getHexGrid().getTileAt(getPosition1().equals(from) ? getPosition2() : getPosition1())
                                .getType()));
    }

    @Override
    public int getTotalBuildingCost(Player player) {
        return getBuildingCost() + getTotalParallelCost(player);
    }

    @Override
    public int getTotalParallelCost(Player player) {
        return getParallelCost(player).values().stream().reduce(0, Integer::sum);
    }

    @Override
    public Map<Player, Integer> getParallelCost(Player player) {
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

    @Override
    public int getBuildingCost() {
        return Config.TILE_TYPE_TO_BUILDING_COST.get(getAdjacentTilePositions().stream()
                .map(position -> getHexGrid().getTileAt(position).getType()).collect(Collectors.toUnmodifiableSet()));
    }

    @Override
    public List<Player> getRailOwners() {
        return getRailOwnersProperty().getValue();
    }

    @Override
    public boolean removeRail(Player player) {
        return getRailOwnersProperty().getValue().remove(player);
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public boolean addRail(Player player) {
        if (getRailOwners().contains(player) || (player.getRails().size() > 0
                && getConnectedEdges().stream().noneMatch(e -> e.getRailOwners().contains(player)))
                || (player.getRails().size() == 0 && Collections.disjoint(getHexGrid().getStartingCities().keySet(),
                        getAdjacentTilePositions()))) {
            return false;
        }
        return getRailOwnersProperty().getValue().add(player);
    }

    @Override
    public boolean hasRail() {
        return getRailOwnersProperty().getValue() != null && !getRailOwnersProperty().getValue().isEmpty();
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public boolean connectsTo(Edge other) {
        return getAdjacentTilePositions().contains(other.getPosition1())
                || getAdjacentTilePositions().contains(other.getPosition2());
    }

    @Override
    public Set<TilePosition> getAdjacentTilePositions() {
        return Set.of(getPosition1(), getPosition2());
    }

    @Override
    @StudentImplementationRequired("P1.3")
    public Set<Edge> getConnectedEdges() {
        return getHexGrid().getEdges().entrySet().stream().map(entry -> entry.getValue())
                .filter(edge -> connectsTo(edge)).collect(Collectors.toUnmodifiableSet());
    }
}

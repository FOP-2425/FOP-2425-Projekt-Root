package hProjekt.model;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import javafx.beans.property.Property;

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
    @StudentImplementationRequired("H1.3")
    public boolean connectsTo(final Edge other) {
        return getAdjacentTilePositions().contains(other.getPosition1())
                || getAdjacentTilePositions().contains(other.getPosition2());
    }

    @Override
    public Property<List<Player>> getRoadOwnersProperty() {
        return railOwners;
    }

    @Override
    @StudentImplementationRequired("H1.3")
    public Set<Edge> getConnectedRails(final Player player) {
        return getConnectedEdges().stream()
                .filter(Edge::hasRail)
                .filter(edge -> edge.getRoadOwnersProperty().getValue().equals(player))
                .collect(Collectors.toUnmodifiableSet());
    }

    @Override
    public int getCost(Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCost'");
    }
}

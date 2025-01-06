package hProjekt.model;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.model.TilePosition.EdgeDirection;
import javafx.beans.value.ObservableDoubleValue;

/**
 * Holds information on a tile.
 *
 * @param position       this tile's position
 * @param type           the type of this tile
 * @param heightProperty the height of this tile
 * @param widthProperty  the width of this tile
 * @param hexGrid        the grid this tile is placed in
 * @see Tile
 * @see TilePosition
 */
public record TileImpl(
        TilePosition position,
        Type type,
        ObservableDoubleValue heightProperty,
        ObservableDoubleValue widthProperty,
        HexGrid hexGrid) implements Tile {

    /**
     * Alternative constructor with q- and r-coordinates instead of a
     * {@link TilePosition}.
     *
     * @param q              the q-coordinate of this tile in the grid
     * @param r              the r-coordinate of this tile in the grid
     * @param type           the type of this tile
     * @param heightProperty the height of this tile
     * @param widthProperty  the width of this tile
     * @param hexGrid        the grid this tile is placed in
     */
    @DoNotTouch
    public TileImpl(
            final int q,
            final int r,
            final Type type,
            final ObservableDoubleValue heightProperty,
            final ObservableDoubleValue widthProperty,
            final HexGrid hexGrid) {
        this(new TilePosition(q, r), type, heightProperty, widthProperty, hexGrid);
    }

    @Override
    public TilePosition getPosition() {
        return position;
    }

    @Override
    public HexGrid getHexGrid() {
        return hexGrid;
    }

    @Override
    public Edge getEdge(final EdgeDirection direction) {
        final TilePosition neighbour = TilePosition.neighbour(this.position, direction);
        return this.hexGrid.getEdges().get(Set.of(this.position, neighbour));
    }

    @Override
    public boolean hasCity() {
        return getHexGrid().getCityAt(position) != null;
    }

    @Override
    public Set<Edge> getEdges() {
        return Collections.unmodifiableSet(
                EdgeDirection.stream().map(this::getEdge).filter(edge -> edge != null).collect(Collectors.toSet()));
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public Set<Tile> getNeighbours() {
        return getHexGrid().getTiles().entrySet().stream()
                .filter(entrySet -> TilePosition.neighbours(getPosition()).contains(entrySet.getKey()))
                .map(Map.Entry::getValue)
                .collect(Collectors.toSet());
    }

    @Override
    public Tile getNeighbour(final EdgeDirection direction) {
        return getHexGrid().getTileAt(TilePosition.neighbour(getPosition(), direction));
    }

    @Override
    public boolean isAtCoast() {
        return getNeighbours().size() < 6;
    }

    @Override
    public Set<Tile> getConnectedNeighbours(Set<Edge> connectingEdges) {
        return getNeighbours().stream().filter(neighbour -> connectingEdges.stream().filter(
                edge -> edge.getAdjacentTilePositions()
                        .containsAll(Set.of(neighbour.getPosition(), this.getPosition())))
                .count() > 0)
                .collect(Collectors.toSet());
    }

    @Override
    public Set<Edge> getRails(Player player) {
        return getEdges().stream().filter(edge -> edge.getRailOwners().contains(player)).collect(Collectors.toSet());
    }
}

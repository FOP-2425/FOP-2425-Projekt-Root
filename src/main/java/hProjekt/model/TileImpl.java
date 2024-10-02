package hProjekt.model;

import java.util.Set;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.model.TilePosition.EdgeDirection;
import javafx.beans.value.ObservableDoubleValue;

/**
 * Holds information on a tile.
 *
 * @param position       this tile's position
 * @param type           the resource type this tile can produce
 * @param rollNumber     the number that needs to be rolled to yield the
 *                       resource
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
     * @param type           the resource type this tile can produce
     * @param rollNumber     the number that needs to be rolled to yield the
     *                       resource
     * @param heightProperty the height of this tile
     * @param widthProperty  the width of this tile
     * @param hexGrid        the grid this tile is placed in
     */
    @DoNotTouch
    public TileImpl(
            final int q,
            final int r,
            final Type type,
            final int rollNumber,
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
    public boolean addRail(final EdgeDirection direction, final Player owner) {
        return this.hexGrid.addRail(this.position, TilePosition.neighbour(this.position, direction), owner);
    }

    @Override
    public boolean hasCity() {
        return getHexGrid().getCityAt(position) != null;
    }

    @Override
    public Set<Edge> getEdges() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getEdges'");
    }

    @Override
    public Type getType() {
        return type;
    }
}

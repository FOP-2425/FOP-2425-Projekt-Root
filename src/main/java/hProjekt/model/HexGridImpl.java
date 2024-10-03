package hProjekt.model;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.model.TilePosition.EdgeDirection;
import hProjekt.util.NameGenerator;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableDoubleValue;

/**
 * Default implementation of {@link HexGrid}.
 */
public class HexGridImpl implements HexGrid {

    private final Map<TilePosition, Tile> tiles = new HashMap<>();
    private final Map<Set<TilePosition>, Edge> edges = new HashMap<>();
    private final Map<TilePosition, City> cities = new HashMap<>();
    private final ObservableDoubleValue tileWidth;
    private final ObservableDoubleValue tileHeight;
    private final DoubleProperty tileSize = new SimpleDoubleProperty(50);
    private final Random random = new Random();

    /**
     * Creates a new HexGrid with the given scale.
     *
     * @param scale the scale of the grid
     * @throws IOException
     */
    @DoNotTouch
    public HexGridImpl(final int scale) throws IOException {
        this.tileHeight = Bindings.createDoubleBinding(() -> tileSize.get() * 2, tileSize);
        this.tileWidth = Bindings.createDoubleBinding(() -> Math.sqrt(3) * tileSize.get(), tileSize);
        initTiles(scale);
        initEdges();

        String[] names = new String[0];

        try {
            names = Files.readAllLines(Paths.get(
                    HexGridImpl.class.getResource("/town_names_ger.txt").toURI())).toArray(String[]::new);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        initCities(36, new NameGenerator(names, 3, random));
    }

    /**
     * Performs a random walk starting at the given position.
     * The walk will be of the given length.
     * Adds tiles of the given type.
     *
     * @param start  the starting position
     * @param type   the type of the tiles to add
     * @param length the length of the walk
     */
    @DoNotTouch
    private void doRandomWalk(final TilePosition start, final Tile.Type type, final int length) {
        TilePosition current = start;
        for (int i = 0; i < length; i++) {
            final TilePosition next = TilePosition.neighbour(current,
                    EdgeDirection.VALUES.get(random.nextInt(EdgeDirection.SIZE)));
            addTile(next, type);
            current = next;
        }
    }

    /**
     * Checks if the neighbouring tiles of the given position satisfy the given
     * predicate.
     * Checks the tiles in the given radius.
     *
     * @param center    the center position
     * @param predicate the predicate to check
     * @param radius    the radius to check
     * @return true if the predicate is satisfied by a neighbouring tile, false
     *         otherwise
     */
    @DoNotTouch
    private boolean isNear(final TilePosition center, final Predicate<Tile> predicate, final int radius) {
        boolean[] found = { false };
        TilePosition.forEachSpiral(center, radius, (position, params) -> {
            if (predicate.test(tiles.get(position))) {
                found[0] = true;
                return true;
            }
            return false;
        });
        return found[0];
    }

    /**
     * Initializes the tiles in this grid.
     * Performs several random walks to create a random map.
     * The map will contain plains and mountains.
     *
     * @param grid_scale the scale of the grid
     */
    @DoNotTouch
    private void initTiles(final int grid_scale) {
        final TilePosition center = new TilePosition(0, 0);
        addTile(center, Tile.Type.PLAIN);

        for (int i = 0; i < 10 * grid_scale; i++) {
            TilePosition start = tiles.keySet().stream().skip(random.nextInt(tiles.size())).findFirst().get();
            doRandomWalk(start, Tile.Type.PLAIN, 3 * grid_scale);
        }

        for (int i = 0; i < 4 * grid_scale; i++) {
            TilePosition start = tiles.keySet().stream().skip(random.nextInt(tiles.size())).findFirst().get();
            doRandomWalk(start, Tile.Type.MOUNTAIN, (int) (0.5 * grid_scale));
        }
    }

    @DoNotTouch
    private void initCities(int amount, NameGenerator nameGenerator) {
        while (cities.size() < amount) {
            Tile tile = tiles.values().stream().skip(random.nextInt(tiles.size())).findFirst().get();

            if (tile.getType() != Tile.Type.PLAIN) {
                continue;
            }

            double probability = 0.3;
            if (tile.isAtCaost()) {
                probability = 0.1;
            }

            if (isNear(tile.getPosition(), t -> t != null && t.getType() == Tile.Type.MOUNTAIN, 1)) {
                probability = 0.05;
            }

            if (isNear(tile.getPosition(), t -> t != null && cities.get(t.getPosition()) != null, 3)) {
                probability = 0.001;
            }

            if (random.nextDouble() < probability) {
                final City city = new CityImpl(tile.getPosition(), nameGenerator.generateName(10), false, this);
                this.cities.put(tile.getPosition(), city);
            }
        }
    }

    /**
     * Initializes the edges in this grid.
     */
    @DoNotTouch
    private void initEdges() {
        for (final var tile : this.tiles.values()) {
            Arrays.stream(TilePosition.EdgeDirection.values())
                    .filter(ed -> this.tiles.containsKey(TilePosition.neighbour(tile.getPosition(), ed)))
                    .forEach(
                            ed -> this.edges.putIfAbsent(
                                    Set.of(
                                            tile.getPosition(),
                                            TilePosition.neighbour(tile.getPosition(), ed)),
                                    new EdgeImpl(
                                            this,
                                            tile.getPosition(),
                                            TilePosition.neighbour(tile.getPosition(), ed),
                                            new SimpleObjectProperty<>(new ArrayList<>()))));
        }
    }

    // Tiles

    @Override
    public double getTileWidth() {
        return tileWidth.get();
    }

    @Override
    public double getTileHeight() {
        return tileHeight.get();
    }

    @Override
    public double getTileSize() {
        return tileSize.get();
    }

    @Override
    public ObservableDoubleValue tileWidthProperty() {
        return tileWidth;
    }

    @Override
    public ObservableDoubleValue tileHeightProperty() {
        return tileHeight;
    }

    @Override
    public DoubleProperty tileSizeProperty() {
        return tileSize;
    }

    @Override
    public Map<TilePosition, Tile> getTiles() {
        return Collections.unmodifiableMap(tiles);
    }

    @Override
    public Tile getTileAt(final int q, final int r) {
        return getTileAt(new TilePosition(q, r));
    }

    @Override
    public Tile getTileAt(final TilePosition position) {
        return tiles.get(position);
    }

    /**
     * Adds a new tile to the grid.
     *
     * @param position            position of the new tile
     * @param type                type of the new tile
     * @param rollNumberGenerator a supplier returning the new tile's roll number
     */
    private void addTile(final TilePosition position, final Tile.Type type) {
        tiles.put(position, new TileImpl(position, type, tileHeight, tileWidth, this));
    }

    // Edges / Roads

    @Override
    public Map<Set<TilePosition>, Edge> getEdges() {
        return Collections.unmodifiableMap(edges);
    }

    @Override
    public Edge getEdge(final TilePosition position0, final TilePosition position1) {
        return edges.get(Set.of(position0, position1));
    }

    @Override
    @StudentImplementationRequired("H1.3")
    public Map<Set<TilePosition>, Edge> getRails(final Player player) {
        return Collections.unmodifiableMap(edges.entrySet().stream()
                .filter(entry -> entry.getValue().hasRail())
                .filter(entry -> entry.getValue().getRailOwners().equals(player))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Override
    @StudentImplementationRequired("H1.3")
    public boolean addRail(final TilePosition position0, final TilePosition position1, final Player player) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCities'");
    }

    @Override
    public boolean removeRail(final TilePosition position0, final TilePosition position1) {
        edges.get(Set.of(position0, position1)).getRoadOwnersProperty().setValue(null);
        return true;
    }

    @Override
    public Map<TilePosition, City> getCities() {
        return Collections.unmodifiableMap(cities);
    }

    @Override
    public City getCityAt(TilePosition position) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getCityAt'");
    }
}

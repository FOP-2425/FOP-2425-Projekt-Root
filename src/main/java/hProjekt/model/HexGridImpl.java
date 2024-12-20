package hProjekt.model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;
import org.tudalgo.algoutils.student.annotation.StudentImplementationRequired;

import hProjekt.Config;
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
    private final Random random = Config.RANDOM;

    /**
     * Creates a new HexGrid with the given scale.
     *
     * @param scale the scale of the grid
     * @throws IOException
     */
    @DoNotTouch
    public HexGridImpl(final int scale, final int numberOfCities, final String[] names) {
        this.tileHeight = Bindings.createDoubleBinding(() -> tileSize.get() * 2, tileSize);
        this.tileWidth = Bindings.createDoubleBinding(() -> Math.sqrt(3) * tileSize.get(), tileSize);
        initTiles(scale);
        initEdges();

        initCities(numberOfCities, new NameGenerator(names, 3, random));
    }

    /**
     * Creates a new HexGrid with the default values.
     *
     * @throws IOException
     */
    public HexGridImpl(String[] names) {
        this(Config.MAP_SCALE, Config.NUMBER_OF_CITIES, names);
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

    /**
     * Initializes the cities in this grid.
     * The cities will be placed randomly on the map.
     * The names of the cities will be generated using the given name generator.
     * The cities are placed based on certain rules:
     * - Cities will only be placed on plains
     * - Cities are placed with a base probability of 0.3
     * - If the tile is at the coast the probability is 0.1
     * - If the tile is near a mountain the probability is 0.05
     * - If the tile is near another city the probability is 0.001
     *
     * @param amount        the amount of cities to place
     * @param nameGenerator the name generator to use
     */
    @DoNotTouch
    private void initCities(int amount, NameGenerator nameGenerator) {
        int startingCitiesAdded = 0;

        while (cities.size() < amount) {
            Tile tile = tiles.values().stream().skip(random.nextInt(tiles.size())).findFirst().get();

            if (tile.getType() != Tile.Type.PLAIN) {
                continue;
            }

            double probability = Config.CITY_BASE_PROBABILTY;

            if (tile.isAtCaost()) {
                probability = Config.CITY_AT_COAST_PROBABILTY;
            }

            if (isNear(tile.getPosition(), t -> t != null && t.getType() == Tile.Type.MOUNTAIN,
                    Config.CITY_NEAR_MOUNTAIN_RADIUS)) {
                probability = Config.CITY_NEAR_MOUNTAIN_PROBABILTY;
            }

            if (isNear(tile.getPosition(), t -> t != null && cities.get(t.getPosition()) != null,
                    Config.CITY_NEAR_CITY_RADIUS)) {
                probability = Config.CITY_NEAR_CITY_PROBABILTY;
            }

            if (random.nextDouble() < probability) {
                boolean isStartingCity = false;
                if (startingCitiesAdded < Config.NUMBER_OF_STARTING_CITIES
                        && (amount - cities.size() <= Config.NUMBER_OF_STARTING_CITIES - startingCitiesAdded
                                || random.nextBoolean())) {
                    isStartingCity = true;
                    startingCitiesAdded++;
                }

                final City city = new CityImpl(tile.getPosition(), nameGenerator.generateName(10), isStartingCity,
                        this);
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
                .filter(entry -> entry.getValue().getRailOwners().contains(player))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Override
    public Map<TilePosition, City> getCities() {
        return Collections.unmodifiableMap(cities);
    }

    @Override
    public City getCityAt(TilePosition position) {
        return cities.get(position);
    }

    @Override
    public Map<TilePosition, City> getConnectedCities() {
        return Collections.unmodifiableMap(cities.entrySet().stream()
                .filter(entry -> edges.values().stream().filter(edge -> edge.hasRail())
                        .anyMatch(edge -> edge.getAdjacentTilePositions().contains(entry.getKey())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Override
    public Map<TilePosition, City> getUnconnectedCities() {
        Set<TilePosition> cityPositions = new HashSet<>(cities.keySet());
        cityPositions.removeAll(getConnectedCities().keySet());
        return Collections.unmodifiableMap(cityPositions.stream().collect(Collectors.toMap(p -> p, cities::get)));
    }

    @Override
    public Map<TilePosition, City> getStartingCities() {
        return getCities().values().stream().filter(City::isStartingCity)
                .collect(Collectors.toMap(City::getPosition, Function.identity()));
    }
}

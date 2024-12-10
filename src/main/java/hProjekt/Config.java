package hProjekt;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import hProjekt.model.Tile;

public class Config {
    /**
     * The global source of randomness.
     */
    public static final Random RANDOM = new Random();

    /**
     * The number of sides on each die.
     * Maximum is 9.
     */
    public static final int DICE_SIDES = 6;

    /**
     * The number of starting cities.
     */
    public static final int NUMBER_OF_STARTING_CITIES = 3;

    /**
     * The number of cities on the board.
     */
    public static final int NUMBER_OF_CITIES = (DICE_SIDES * DICE_SIDES) - NUMBER_OF_STARTING_CITIES;

    /**
     * The base probability of a city being generated on a tile.
     */
    public static final double CITY_BASE_PROBABILTY = 0.3;

    /**
     * The probability of a city being generated on a tile if it is at the coast.
     */
    public static final double CITY_AT_COAST_PROBABILTY = 0.1;

    /**
     * The probability of a city being generated on a tile if it is near a mountain.
     */
    public static final double CITY_NEAR_MOUNTAIN_PROBABILTY = 0.05;

    /**
     * The radius around a tile to check for mountains.
     */
    public static final int CITY_NEAR_MOUNTAIN_RADIUS = 1;

    /**
     * The probability of a city being generated on a tile if it is near a city.
     */
    public static final double CITY_NEAR_CITY_PROBABILTY = 0.001;

    /**
     * The radius around a tile to check for other cities.
     */
    public static final int CITY_NEAR_CITY_RADIUS = 3;

    /**
     * The minimum required number of players in a game.
     */
    public static final int MIN_PLAYERS = 2;

    /**
     * The maximum allowed number of players in a game.
     */
    public static final int MAX_PLAYERS = 6;

    /**
     * The number of credits each player starts with.
     */
    public static final int STARTING_CREDITS = 20;

    /**
     * The number of credits a player receives if he is the first to connect the
     * city.
     */
    public static final int CITY_CONNECTION_BONUS = 6;

    /**
     * The scale of the map, bigger values mean a bigger map.
     */
    public static final int MAP_SCALE = 15;

    public static final Map<Set<Tile.Type>, Integer> TILE_TYPE_TO_BUILDING_COST = Map.of(
            Set.of(Tile.Type.PLAIN), 1, Set.of(Tile.Type.PLAIN, Tile.Type.MOUNTAIN), 3,
            Set.of(Tile.Type.MOUNTAIN), 5);

    public static final Map<Set<Tile.Type>, Integer> TILE_TYPE_TO_DRIVING_COST = Map.of(
            Set.of(Tile.Type.PLAIN, Tile.Type.MOUNTAIN), 2, Set.of(Tile.Type.PLAIN), 1, Set.of(Tile.Type.MOUNTAIN), 1);

    public static final String[] TOWN_NAMES;

    static {
        String[] names = new String[0];
        try {
            names = Files.readAllLines(Paths.get(Config.class.getResource("/town_names_ger.txt").toURI()))
                    .toArray(String[]::new);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        TOWN_NAMES = names;
    }
}

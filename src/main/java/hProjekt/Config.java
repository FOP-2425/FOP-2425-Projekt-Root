package hProjekt;

import java.util.Iterator;
import java.util.stream.IntStream;

public class Config {
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

    /**
     * The iterator to generate roll numbers for the cities.
     * There are sides of a dice squared roll numbers in total.
     * Each digit ranges from 1 to the number of sides of a dice.
     * For example a 6 sided dice would generate the following sequence:
     * 11,12,13,14,15,16,21,22,...,66
     */
    public static final Iterator<Integer> ROLL_NUMBER_ITERATOR = IntStream
            .iterate(11, (i) -> i / 10 <= DICE_SIDES, (i) -> {
                if (i % 10 == DICE_SIDES) {
                    return (i / 10 + 1) * 10 + 1;
                }
                return i + 1;
            }).iterator();
}

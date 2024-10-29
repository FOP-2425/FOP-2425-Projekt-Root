package hProjekt.model;

import java.util.Collections;
import java.util.Set;

/**
 * Represents a city on the board.
 *
 * @param position       the position of the city
 * @param name           the name of the city
 * @param isStartingCity whether the city is a starting city
 * @param rollNumbers    the roll numbers that belong to the city
 * @param hexGrid        the HexGrid instance this city is placed in
 */
public record CityImpl(TilePosition position, String name, boolean isStartingCity, Set<Integer> rollNumbers,
        HexGrid hexGrid) implements City {

    @Override
    public String getName() {
        return name;
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
    public boolean isStartingCity() {
        return isStartingCity;
    }

    @Override
    public Set<Integer> getRollNumbers() {
        return Collections.unmodifiableSet(rollNumbers);
    }

}

package hProjekt.model;

import java.util.Collections;
import java.util.Set;

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

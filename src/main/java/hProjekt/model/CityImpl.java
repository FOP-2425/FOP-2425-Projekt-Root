package hProjekt.model;

import java.util.List;

public record CityImpl(TilePosition position, String name, boolean isStartingCity, HexGrid hexGrid) implements City {

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
    public List<Integer> getRollNumbers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRollNumbers'");
    }

}

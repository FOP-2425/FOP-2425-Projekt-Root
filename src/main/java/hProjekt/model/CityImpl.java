package hProjekt.model;

import java.util.List;

public record CityImpl(TilePosition position, String name, boolean isStartingCity, HexGrid hexGrid) implements City {

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getName'");
    }

    @Override
    public TilePosition getPosition() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPosition'");
    }

    @Override
    public HexGrid getHexGrid() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getHexGrid'");
    }

    @Override
    public boolean isStartingCity() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'isStartingCity'");
    }

    @Override
    public List<Integer> getRollNumbers() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRollNumbers'");
    }

}

package hProjekt.model;

import java.util.List;

public interface City {
    String getName();

    TilePosition getPosition();

    List<Integer> getRollNumbers();

    HexGrid getHexGrid();

    boolean isStartingCity();
}

package hProjekt.model;

import java.util.Set;

public interface City {
    String getName();

    TilePosition getPosition();

    Set<Integer> getRollNumbers();

    HexGrid getHexGrid();

    boolean isStartingCity();
}

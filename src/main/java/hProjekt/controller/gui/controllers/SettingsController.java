package hProjekt.controller.gui.controllers;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import hProjekt.Config;
import hProjekt.model.Tile;
import javafx.beans.property.*;

public class SettingsController {

    // Properties for Game Settings
    public final IntegerProperty diceSides = new SimpleIntegerProperty(Config.DICE_SIDES);
    public final IntegerProperty minPlayers = new SimpleIntegerProperty(Config.MIN_PLAYERS);
    public final IntegerProperty maxPlayers = new SimpleIntegerProperty(Config.MAX_PLAYERS);

    // Properties for Credit Settings
    public final IntegerProperty startingCredits = new SimpleIntegerProperty(Config.STARTING_CREDITS);
    public final IntegerProperty cityConnectionBonus = new SimpleIntegerProperty(Config.CITY_CONNECTION_BONUS);

    // Properties for Map Settings
    public final IntegerProperty mapScale = new SimpleIntegerProperty(Config.MAP_SCALE);
    public final IntegerProperty startingCities = new SimpleIntegerProperty(Config.NUMBER_OF_STARTING_CITIES);
    public final IntegerProperty mountainRadius = new SimpleIntegerProperty(Config.CITY_NEAR_MOUNTAIN_RADIUS);
    public final IntegerProperty cityRadius = new SimpleIntegerProperty(Config.CITY_NEAR_CITY_RADIUS);

    // Properties for Map Balancing Settings
    public final DoubleProperty cityBaseProbability = new SimpleDoubleProperty(Config.CITY_BASE_PROBABILTY);
    public final DoubleProperty cityAtCoastProbability = new SimpleDoubleProperty(Config.CITY_AT_COAST_PROBABILTY);
    public final DoubleProperty nearMountainProbability = new SimpleDoubleProperty(Config.CITY_NEAR_MOUNTAIN_PROBABILTY);
    public final DoubleProperty nearCityProbability = new SimpleDoubleProperty(Config.CITY_NEAR_CITY_PROBABILTY);

    // Properties for Building Costs
    public final IntegerProperty plainToPlainBuildingCost = new SimpleIntegerProperty(Config.TILE_TYPE_TO_BUILDING_COST.get(Set.of(Tile.Type.PLAIN)));
    public final IntegerProperty plainToMountainBuildingCost = new SimpleIntegerProperty(Config.TILE_TYPE_TO_BUILDING_COST.get(Set.of(Tile.Type.PLAIN, Tile.Type.MOUNTAIN)));
    public final IntegerProperty mountainToMountainBuildingCost = new SimpleIntegerProperty(Config.TILE_TYPE_TO_BUILDING_COST.get(Set.of(Tile.Type.MOUNTAIN)));

    // Properties for Driving Costs
    public final IntegerProperty plainToPlainDrivingCost = new SimpleIntegerProperty(Config.TILE_TYPE_TO_DRIVING_COST.get(Set.of(Tile.Type.PLAIN)));
    public final IntegerProperty plainToMountainDrivingCost = new SimpleIntegerProperty(Config.TILE_TYPE_TO_DRIVING_COST.get(Set.of(Tile.Type.PLAIN, Tile.Type.MOUNTAIN)));
    public final IntegerProperty mountainToMountainDrivingCost = new SimpleIntegerProperty(Config.TILE_TYPE_TO_DRIVING_COST.get(Set.of(Tile.Type.MOUNTAIN)));

    /**
     * Updates Config values with the current settings.
     */

     public void applySettings() {
    System.out.println("Saving settings...");

    // Game Settings
    Config.DICE_SIDES = diceSides.get();
    Config.MIN_PLAYERS = minPlayers.get();
    Config.MAX_PLAYERS = maxPlayers.get();

    // Credit Settings
    Config.STARTING_CREDITS = startingCredits.get();
    Config.CITY_CONNECTION_BONUS = cityConnectionBonus.get();

    // Map Settings
    Config.MAP_SCALE = mapScale.get();
    Config.NUMBER_OF_STARTING_CITIES = startingCities.get();
    Config.CITY_NEAR_MOUNTAIN_RADIUS = mountainRadius.get();
    Config.CITY_NEAR_CITY_RADIUS = cityRadius.get();

    // Map Balancing Settings
    Config.CITY_BASE_PROBABILTY = cityBaseProbability.get();
    Config.CITY_AT_COAST_PROBABILTY = cityAtCoastProbability.get();
    Config.CITY_NEAR_MOUNTAIN_PROBABILTY = nearMountainProbability.get();
    Config.CITY_NEAR_CITY_PROBABILTY = nearCityProbability.get();

    // Building Costs (convert immutable map to mutable HashMap)
    Map<Set<Tile.Type>, Integer> buildingCostMap = new HashMap<>(Config.TILE_TYPE_TO_BUILDING_COST);
    buildingCostMap.put(Set.of(Tile.Type.PLAIN), plainToPlainBuildingCost.get());
    buildingCostMap.put(Set.of(Tile.Type.PLAIN, Tile.Type.MOUNTAIN), plainToMountainBuildingCost.get());
    buildingCostMap.put(Set.of(Tile.Type.MOUNTAIN), mountainToMountainBuildingCost.get());
    Config.TILE_TYPE_TO_BUILDING_COST = Collections.unmodifiableMap(buildingCostMap);

    // Driving Costs (convert immutable map to mutable HashMap)
    Map<Set<Tile.Type>, Integer> drivingCostMap = new HashMap<>(Config.TILE_TYPE_TO_DRIVING_COST);
    drivingCostMap.put(Set.of(Tile.Type.PLAIN), plainToPlainDrivingCost.get());
    drivingCostMap.put(Set.of(Tile.Type.PLAIN, Tile.Type.MOUNTAIN), plainToMountainDrivingCost.get());
    drivingCostMap.put(Set.of(Tile.Type.MOUNTAIN), mountainToMountainDrivingCost.get());
    Config.TILE_TYPE_TO_DRIVING_COST = Collections.unmodifiableMap(drivingCostMap);
}

    /**
     * Resets all properties to their original default values.
     */
    public void resetToDefaults() {
        System.out.println("Resetting settings...");

        // Reset Game Settings
        diceSides.set(6);
        minPlayers.set(2);
        maxPlayers.set(6);

        // Reset Credit Settings
        startingCredits.set(20);
        cityConnectionBonus.set(6);

        // Reset Map Settings
        mapScale.set(15);
        startingCities.set(3);
        mountainRadius.set(1);
        cityRadius.set(3);

        // Reset Map Balancing Settings
        cityBaseProbability.set(0.3);
        cityAtCoastProbability.set(0.1);
        nearMountainProbability.set(0.05);
        nearCityProbability.set(0.001);

        // Reset Building Costs
        plainToPlainBuildingCost.set(1);
        plainToMountainBuildingCost.set(3);
        mountainToMountainBuildingCost.set(5);

        // Reset Driving Costs
        plainToPlainDrivingCost.set(1);
        plainToMountainDrivingCost.set(2);
        mountainToMountainDrivingCost.set(1);
    }
}

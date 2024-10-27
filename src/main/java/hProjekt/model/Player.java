package hProjekt.model;

import java.util.Map;
import java.util.Set;

import javafx.scene.paint.Color;

public interface Player {

    /**
     * Returns the hexGrid instance
     *
     * @return the hexGrid instance
     */
    HexGrid getHexGrid();

    /**
     * Returns the name of the player.
     *
     * @return the name of the player
     */
    String getName();

    /**
     * Returns the Player ID, aka the Index of the Player, starting with 1
     *
     * @return the Player ID
     */
    int getID();

    /**
     * Returns the color of the player.
     *
     * @return the color of the player
     */
    Color getColor();

    /**
     * Returns true if the player is an AI, false otherwise.
     *
     * @return true if the player is an AI, false otherwise
     */
    default boolean isAi() {
        return false;
    }

    /**
     * Returns the number of credits the player has.
     *
     * @return the number of credits the player has
     */
    public int getCredits();

    /**
     * Adds the given amount of credits to the player.
     *
     * @param amount the amount of credits to add
     */
    public void addCredits(int amount);

    /**
     * Removes the given amount of credits from the player.
     *
     * @param amount the amount of credits to remove
     */
    public void removeCredits(int amount);

    /**
     * Returns all rails of the player.
     *
     * @return all rails of the player
     */
    default Map<Set<TilePosition>, Edge> getRails() {
        return getHexGrid().getRails(this);
    }
}

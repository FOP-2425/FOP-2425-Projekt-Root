package hProjekt.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.jetbrains.annotations.Nullable;
import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.Config;
import hProjekt.controller.GamePhase;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.paint.Color;

/**
 * Holds information on the game's state.
 */
@DoNotTouch
public final class GameState {

    /**
     * The {@link HexGrid} instance of this {@link GameState}.
     */
    private final HexGrid grid;

    /**
     * The {@link Player}s of this {@link GameState}.
     */
    private final List<Player> players;

    /**
     * The position of the {@link Player}s on the {@link HexGrid}. Only relevant for
     * the driving phase.
     */
    private final Map<Player, TilePosition> playerPositions = new HashMap<>();

    /**
     * The game over flag.
     */
    private final Property<Player> winnerProperty = new SimpleObjectProperty<>();

    /**
     * The current {@link GamePhase}.
     */
    private final Property<GamePhase> gamePhaseProperty = new SimpleObjectProperty<>(GamePhase.BUILDING_PHASE);

    /**
     * The cities that have already been driven to.
     */
    private final Set<City> chosenCities = Set.of();

    /**
     * Creates a new {@link GameState} with the given {@link HexGrid} and
     * {@link Player}s.
     *
     * @param grid    the {@link HexGrid}
     * @param players the {@link Player}s
     */
    public GameState(final HexGrid grid, final List<Player> players) {
        this.grid = grid;
        this.players = players;
    }

    /**
     * Returns the {@link HexGrid} instance of this {@link GameState}.
     *
     * @return the {@link HexGrid} instance of this {@link GameState}.
     */
    public HexGrid getGrid() {
        return grid;
    }

    /**
     * Returns an unmodifiable list of all {@link Player}s in this
     * {@link GameState}.
     *
     * @return an unmodifiable list of all {@link Player}s in this
     *         {@link GameState}.
     */
    public List<Player> getPlayers() {
        return Collections.unmodifiableList(players);
    }

    public Map<Player, TilePosition> getPlayerPositions() {
        return Collections.unmodifiableMap(playerPositions);
    }

    /**
     * Returns an unmodifiable set of all cities that have already been driven to.
     *
     * @return an unmodifiable set of all cities that have already been driven to.
     */
    public Set<City> getChosenCities() {
        return Collections.unmodifiableSet(chosenCities);
    }

    /**
     * Adds the given {@link City} to the set of cities that have already been
     * driven to.
     *
     * @param city the {@link City} to add
     */
    public void addChosenCity(final City city) {
        chosenCities.add(city);
    }

    /**
     * Returns a {@link Property} containing the winner of this {@link GameState}.
     *
     * @return a {@link Property} containing the winner of this {@link GameState}.
     */
    public Property<Player> getWinnerProperty() {
        return winnerProperty;
    }

    /**
     * Returns the {@link Property} containing the current {@link GamePhase}.
     *
     * @return the {@link Property} containing the current {@link GamePhase}
     */
    public Property<GamePhase> getGamePhaseProperty() {
        return gamePhaseProperty;
    }

    /**
     * Returns true if the game is over, false otherwise.
     *
     * @return true if the game is over, false otherwise.
     */
    public boolean isGameOver() {
        return winnerProperty.getValue() != null;
    }

    /**
     * Sets the winner of this {@link GameState}.
     *
     * @param winner the winner of this {@link GameState}
     */
    public void setWinner(final Player winner) {
        winnerProperty.setValue(winner);
    }

    /**
     * Adds the given {@link Player}s to this {@link GameState}.
     *
     * @param players the {@link Player}s to add
     * @return true if the {@link Player}s were added successfully, false otherwise
     */
    public boolean addPlayers(final Set<Player> players) {
        if (players.size() + this.players.size() > Config.MAX_PLAYERS) {
            return false;
        }
        this.players.addAll(players);
        return true;
    }

    /**
     * Adds the given {@link Player} to this {@link GameState}.
     *
     * @param player the {@link Player} to add
     * @return true if the {@link Player} was added successfully, false otherwise
     */
    public boolean addPlayer(final Player player) {
        return addPlayers(Set.of(player));
    }

    /**
     * Creates a new {@link Player} with the given {@link Color} and adds it to this
     * {@link GameState}.
     *
     * @param name  the name of the new {@link Player}
     * @param color the {@link Color} of the new {@link Player}
     * @return the new {@link Player}
     * @throws IllegalStateException if the {@link Player} could not be added
     */
    public Player newPlayer(final @Nullable String name, final Color color, final boolean isAi) {
        final Player player = new PlayerImpl.Builder(this.players.size() + 1)
                .color(color)
                .name(name)
                .ai(isAi)
                .build(this.grid);
        if (!addPlayer(player)) {
            throw new IllegalStateException("Cannot add more players");
        }
        return player;
    }

    @Override
    public int hashCode() {
        return Objects.hash(grid, players);
    }

    @Override
    public String toString() {
        return "GameState[" +
                "grid=" + grid + ", " +
                "players=" + players + ']';
    }
}

package hProjekt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.Config;
import hProjekt.controller.actions.ConfirmBuildAction;
import hProjekt.controller.actions.PlayerAction;
import hProjekt.model.City;
import hProjekt.model.GameState;
import hProjekt.model.HexGridImpl;
import hProjekt.model.Player;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.util.Pair;

/**
 * The GameController class represents the controller for the game logic.
 * It manages the game state, player controllers, dice rolling and the overall
 * progression of the game.
 * It tells the players controllers what to do and when to do it.
 */
public class GameController {
    private final GameState state;
    private final Map<Player, PlayerController> playerControllers;
    private final Supplier<Integer> dice;
    private final IntegerProperty currentDiceRoll = new SimpleIntegerProperty(0);
    private final IntegerProperty roundCounter = new SimpleIntegerProperty(0);
    private final Property<Pair<City, City>> chosenCitiesProperty = new SimpleObjectProperty<>();

    private final Property<PlayerController> activePlayerController = new SimpleObjectProperty<>();

    public GameController(GameState state, Supplier<Integer> dice) {
        this.state = state;
        this.playerControllers = new HashMap<>();
        this.dice = dice;
    }

    public GameController(GameState state) {
        this(state, () -> Config.RANDOM.nextInt(1, Config.DICE_SIDES + 1));
    }

    public GameController() {
        this(new GameState(new HexGridImpl(Config.TOWN_NAMES), new ArrayList<>()),
                () -> Config.RANDOM.nextInt(1, Config.DICE_SIDES + 1));
    }

    public GameState getState() {
        return state;
    }

    public Map<Player, PlayerController> getPlayerControllers() {
        return playerControllers;
    }

    public Property<PlayerController> activePlayerControllerProperty() {
        return activePlayerController;
    }

    public PlayerController getActivePlayerController() {
        return activePlayerController.getValue();
    }

    public IntegerProperty currentDiceRollProperty() {
        return currentDiceRoll;
    }

    public int getCurrentDiceRoll() {
        return currentDiceRoll.get();
    }

    public IntegerProperty roundCounterProperty() {
        return roundCounter;
    }

    public ReadOnlyProperty<Pair<City, City>> chosenCitiesProperty() {
        return chosenCitiesProperty;
    }

    public City getStartingCity() {
        return chosenCitiesProperty.getValue().getKey();
    }

    public City getTargetCity() {
        return chosenCitiesProperty.getValue().getValue();
    }

    public int castDice() {
        currentDiceRoll.set(dice.get());
        return currentDiceRoll.get();
    }

    public void chooseCities() {
        final List<City> tempCities = getState().getGrid().getCities().values().stream()
                .filter(city -> !getState().getChosenCities().contains(city)).collect(Collectors.toList());

        City startingCity = tempCities.get(Config.RANDOM.nextInt(tempCities.size()));
        tempCities.remove(startingCity);
        getState().addChosenCity(startingCity);
        City targetCity = tempCities.get(Config.RANDOM.nextInt(tempCities.size()));
        getState().addChosenCity(targetCity);

        chosenCitiesProperty.setValue(new Pair<>(startingCity, targetCity));
    }

    private void initPlayerControllers() {
        for (Player player : state.getPlayers()) {
            playerControllers.put(player, new PlayerController(this, player));
            // TODO: Add AI player controller
        }
    }

    public void startGame() {
        if (this.state.getPlayers().size() < Config.MIN_PLAYERS) {
            throw new IllegalStateException("Not enough players");
        }
        if (playerControllers.isEmpty()) {
            initPlayerControllers();
        }

        // Bauphase
        getState().getGamePhaseProperty().setValue(GamePhase.BUILDING_PHASE);
        while (state.getGrid().getCities().values().size() - state.getGrid().getConnectedCities().size() > 3) {
            roundCounter.set(roundCounter.get() + 1);
            final int diceRollingPlayerIndex = (roundCounter.get() - 1) % state.getPlayers().size();
            withActivePlayer(
                    playerControllers.get(state.getPlayers().get(diceRollingPlayerIndex)),
                    () -> {
                        getActivePlayerController().waitForNextAction(PlayerObjective.ROLL_DICE);
                    });
            for (int i = 0; i < state.getPlayers().size(); i++) {
                final Player player = state.getPlayers()
                        .get((i + diceRollingPlayerIndex) % state.getPlayers().size());
                final PlayerController pc = playerControllers.get(player);
                withActivePlayer(pc, () -> {
                    pc.setBuildingBudget(getCurrentDiceRoll());
                    PlayerAction action = pc.waitForNextAction(PlayerObjective.PLACE_RAIL);
                    while (!(action instanceof ConfirmBuildAction)) {
                        action = pc.waitForNextAction();
                    }
                });
            }
        }

        // Fahrphase
        getState().getGamePhaseProperty().setValue(GamePhase.DRIVING_PHASE);
        roundCounter.set(0);
        while (getState().getChosenCities().size() < getState().getGrid().getCities().size()) {
            roundCounter.set(roundCounter.get() + 1);
            getState().resetDrivingPlayers();
            getState().resetPlayerPositions();

            if (roundCounter.get() % 3 == 0) {
                getState().getPlayers().stream().sorted((p1, p2) -> Integer.compare(p1.getCredits(), p2.getCredits()))
                        .forEachOrdered((player) -> {
                            final PlayerController pc = playerControllers.get(player);
                            pc.setBuildingBudget(Config.MAX_BUILDINGBUDGET_DRIVING_PHASE);
                            withActivePlayer(pc, () -> {
                                pc.setBuildingBudget(getCurrentDiceRoll());
                                PlayerAction action = pc.waitForNextAction(PlayerObjective.PLACE_RAIL);
                                while (!(action instanceof ConfirmBuildAction)) {
                                    action = pc.waitForNextAction();
                                }
                            });
                        });
            }

            withActivePlayer(playerControllers
                    .get(getState().getPlayers().get((roundCounter.get() - 1) % state.getPlayers().size())), () -> {
                        getActivePlayerController().waitForNextAction(PlayerObjective.CHOOSE_CITIES);
                    });

            for (Player player : getState().getPlayers()) {
                playerControllers.get(player).resetDrivingPhase();
                getState().setPlayerPositon(player, getStartingCity().getPosition());
                withActivePlayer(playerControllers.get(player), () -> {
                    while (!getActivePlayerController().hasConfirmedPath()) {
                        getActivePlayerController().waitForNextAction(PlayerObjective.CHOOSE_PATH);
                        getActivePlayerController().waitForNextAction(PlayerObjective.CONFIRM_PATH);
                    }
                });
            }

            while (!getState().getPlayerPositions().values().stream()
                    .anyMatch(pos -> getTargetCity().getPosition().equals(pos))
                    && !getState().getDrivingPlayers().isEmpty()) {
                for (Player player : getState().getDrivingPlayers()) {
                    withActivePlayer(playerControllers.get(player), () -> {
                        getActivePlayerController().waitForNextAction(PlayerObjective.ROLL_DICE);
                        getActivePlayerController().waitForNextAction(PlayerObjective.DRIVE);
                    });
                }
            }
        }
    }

    /**
     * Executes the given {@link Runnable} and set the active player to the given
     * {@link PlayerController}.
     * After the {@link Runnable} is executed, the active player is set to
     * {@code null} and the objective is set to {@link PlayerObjective#IDLE}.
     *
     * @param pc The {@link PlayerController} to set as active player.
     * @param r  The {@link Runnable} to execute.
     */
    @DoNotTouch
    public void withActivePlayer(final PlayerController pc, final Runnable r) {
        activePlayerController.setValue(pc);
        r.run();
        pc.setPlayerObjective(PlayerObjective.IDLE);
        activePlayerController.setValue(null);
    }
}

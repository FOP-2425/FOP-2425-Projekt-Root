package hProjekt.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.Config;
import hProjekt.model.GameState;
import hProjekt.model.HexGridImpl;
import hProjekt.model.Player;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

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

    public int castDice() {
        currentDiceRoll.set(dice.get());
        return currentDiceRoll.get();
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
            System.out.println("Round " + roundCounter.get());
            withActivePlayer(
                    playerControllers.get(state.getPlayers().get(diceRollingPlayerIndex)),
                    () -> {
                        System.out.println("Player " + getActivePlayerController().getPlayer().getName()
                                + " is rolling the dice");
                        getActivePlayerController().waitForNextAction(PlayerObjective.ROLL_DICE);
                        getActivePlayerController().setBuildingBudget(getCurrentDiceRoll());

                        for (int i = 0; i < state.getPlayers().size(); i++) {
                            final Player player = state.getPlayers()
                                    .get((i + diceRollingPlayerIndex) % state.getPlayers().size());
                            final PlayerController pc = playerControllers.get(player);
                            withActivePlayer(pc, () -> {
                                pc.setBuildingBudget(getCurrentDiceRoll());
                                while (pc.getBuildingBudget() > 0) {
                                    pc.waitForNextAction(PlayerObjective.PLACE_RAIL);
                                }
                            });
                        }
                    });
        }
        getState().getGamePhaseProperty().setValue(GamePhase.DRIVING_PHASE);
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

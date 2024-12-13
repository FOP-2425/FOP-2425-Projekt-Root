package hProjekt.controller.gui.controllers.scene;

import java.util.List;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.gui.SceneSwitcher;
import hProjekt.controller.gui.controllers.Controller;
import hProjekt.model.Player;
import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * The controller for a scene.
 */
@DoNotTouch
public interface SceneController extends Controller {
    /**
     * Specifies the title of the {@link Stage}.
     *
     * @return The title of the {@link Stage}.
     */
    String getTitle();

    // --Setup Methods-- //

    /**
     * Terminates the application.
     */
    static void quit() {
        Platform.exit();
    }

    /**
     * Loads the main menu scene.
     */
    static void loadMainMenuScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.MAIN_MENU);
    }

    /**
     * Loads the setup game scene.
     */
    static void loadSetupGameScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.SETUP_GAME_MENU);
    }

    /**
     * Loads the create game scene.
     */
    static void loadCreateGameScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.SETUP_GAME_MENU);
    }

    /**
     * Loads the settings scene.
     */
    static void loadSettingsScene() {
        System.out.println("Loading settings");
    }

    /**
     * Loads the highscore scene.
     */
    static void loadHighscoreScene() {
        System.out.println("Loading highscores");
    }

    /**
     * Loads the map editor scene.
     */
    static void loadLeaderboardScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.LEADERBOARD);
    }

    /**
     * Loads the game scene.
     */
    static void loadGameScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.GAME_BOARD);
    }

    /**
     * Loads the about scene.
     */
    static void loadAboutScene() {
        SceneSwitcher.getInstance().loadScene(SceneSwitcher.SceneType.ABOUT);
    }

    /**
     *
     * Loads the end screen scene with the given GameState.
     *
     * @param players the players to display on the end screen.
     */
    static void loadEndScreenScene(List<Player> players) {
        SceneSwitcher.getInstance().loadEndScreenScene(players);
    }

}

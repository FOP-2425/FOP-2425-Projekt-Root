package hProjekt;

import java.io.OutputStream;
import java.io.PrintStream;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.gui.SceneSwitcher;
import hProjekt.controller.gui.SceneSwitcher.SceneType;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * The main application of the game.
 */
@DoNotTouch
public class MyApplication extends Application {
    // TODO: Uncomment when implemented
    // private final Consumer<GameController> gameLoopStart = gc -> {
    // final Thread gameLoopThread = new Thread(gc::startGame);
    // gameLoopThread.setName("GameLoopThread");
    // gameLoopThread.setDaemon(true);
    // gameLoopThread.start();
    // };

    @Override
    public void start(final Stage stage) throws Exception {
        System.setErr(new PrintStream(new OutputStream() {
            @Override
            public void write(final int b) {
                System.out.write(b);
            }
        }));

        stage.setMinWidth(1000);
        stage.setMinHeight(520);
        stage.setWidth(1280);
        stage.setHeight(720);

        // TODO: Uncomment when implemented
        SceneSwitcher.getInstance(stage, (gc) -> {
        }).loadScene(SceneType.MAP);
    }

    /**
     * The main method of the application.
     *
     * @param args The launch arguments of the application.
     */
    public static void main(final String[] args) {
        launch(args);
    }
}

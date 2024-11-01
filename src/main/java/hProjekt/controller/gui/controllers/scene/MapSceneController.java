package hProjekt.controller.gui.controllers.scene;

import java.io.IOException;

import hProjekt.controller.gui.controllers.HexGridController;
import hProjekt.model.HexGrid;
import hProjekt.model.HexGridImpl;
import javafx.scene.layout.Region;
import javafx.util.Builder;

public class MapSceneController implements SceneController {
    private final HexGridController controller;
    private final HexGrid grid;

    public MapSceneController() {
        try {
            this.grid = new HexGridImpl();
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
        this.controller = new HexGridController(grid);
    }

    @Override
    public Builder<Region> getBuilder() {
        return controller.getBuilder();
    }

    @Override
    public String getTitle() {
        return "Karte";
    }

}

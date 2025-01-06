package hProjekt.controller.gui;

import java.util.function.Consumer;

import hProjekt.model.Edge;
import hProjekt.view.EdgeLine;
import javafx.scene.input.MouseEvent;

/**
 * The controller for an edge.
 */
public class EdgeController {
    private final EdgeLine line;

    /**
     * Creates a new edge controller.
     *
     * @param edge the edge to render
     */
    public EdgeController(final Edge edge) {
        this.line = new EdgeLine(edge);
    }

    /**
     * Returns the edge.
     *
     * @return the edge
     */
    public Edge getEdge() {
        return line.getEdge();
    }

    /**
     * Returns the edge line.
     *
     * @return the edge line
     */
    public EdgeLine getEdgeLine() {
        return line;
    }

    /**
     * Highlights the edge.
     *
     * @param handler the handler to call when the edge is clicked
     */
    public void highlight(final Consumer<MouseEvent> handler) {
        line.highlight(handler);
    }

    public void highlight() {
        line.highlight();
    }

    public void selected(final Consumer<MouseEvent> deselectHandler) {
        line.selected(deselectHandler);
    }

    public void setLabel(final String text) {
        line.setLabel(text);
    }

    public void setCostLabel(Integer... costs) {
        line.setCostLabel(costs);
    }

    public void hideLabel() {
        line.hideLabel();
    }

    /**
     * Unhighlights the edge.
     */
    public void unhighlight() {
        line.unhighlight();
    }
}

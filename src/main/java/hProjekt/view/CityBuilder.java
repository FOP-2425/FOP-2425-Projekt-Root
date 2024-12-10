package hProjekt.view;

import hProjekt.model.City;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.TextAlignment;
import javafx.util.Builder;

public class CityBuilder implements Builder<Region> {
    private final City city;
    private final VBox pane = new VBox();
    private Node marker;

    public CityBuilder(final City city) {
        this.city = city;
    }

    public City getCity() {
        return city;
    }

    public double getMarkerWidth() {
        return marker.getLayoutBounds().getWidth();
    }

    public double getMarkerHeight() {
        return marker.getLayoutBounds().getHeight();
    }

    @Override
    public Region build() {
        pane.getChildren().clear();

        final Circle circle = new Circle(10);
        marker = circle;
        circle.setStroke(Color.BLACK);
        circle.setFill(city.isStartingCity() ? Color.RED : Color.BLACK);
        pane.getChildren().add(circle);

        final Label text = new Label(city.getName());
        text.setTextAlignment(TextAlignment.CENTER);
        text.getStyleClass().add("highlighted-label");
        pane.getChildren().add(text);

        pane.setAlignment(Pos.CENTER);
        // pane.setBackground(Background.fill(Color.WHITE));
        pane.setMouseTransparent(true);
        return pane;
    }
}

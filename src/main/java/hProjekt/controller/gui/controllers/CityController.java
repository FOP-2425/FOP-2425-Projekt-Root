package hProjekt.controller.gui.controllers;

import hProjekt.model.City;
import hProjekt.view.CityBuilder;

public class CityController implements Controller {
    private final CityBuilder builder;

    public CityController(final City city) {
        builder = new CityBuilder(city);
    }

    public City getCity() {
        return builder.getCity();
    }

    @Override
    public CityBuilder getBuilder() {
        return builder;
    }

    public void highlight() {
        builder.highlight();
    }

    public void unhighlight() {
        builder.unhighlight();
    }
}

package hProjekt.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.tudalgo.algoutils.student.annotation.DoNotTouch;

import hProjekt.controller.PlayerObjective;

/**
 * Holds information on a player's state.
 * that can be upgraded
 *
 * @param buildableRailEdges a set of edges where rail can be built
 */
@DoNotTouch
public record PlayerState(
        Set<Edge> buildableRailEdges,
        PlayerObjective playerObjective, Set<Edge> choosableEdges, Set<Edge> rentedEdges, boolean hasPath,
        Map<Tile, List<Tile>> drivableTiles, int buildingBudget) {
}

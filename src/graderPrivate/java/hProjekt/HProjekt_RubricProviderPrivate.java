package hProjekt;

import static org.tudalgo.algoutils.tutor.general.jagr.RubricUtils.criterion;

import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;

public class HProjekt_RubricProviderPrivate implements RubricProvider {

    private static final Criterion HProjekt_1_1 = Criterion.builder()
        .shortDescription("P1.1 | Daten des Players")
        .maxPoints(1)
        .minPoints(0)
        .addChildCriteria(
                criterion(
                    "Die Methoden getHexGrid, getname, getID, getColor und isAi geben für einen Spieler die korrekten Werte zurück.",
                    (JUnitTestRef) null
                )
        ).build();

    private static final Criterion HProjekt_1_2 = Criterion.builder()
        .shortDescription("P1.2 | Bankkonto des Players")
        .maxPoints(2)
        .minPoints(0)
        .addChildCriteria(
                criterion(
                    "Die Methode getCredits gibt die korrekte Anzahl Credits eines Spielers zurück.",
                    (JUnitTestRef) null
                ),
            criterion(
                "Die Methoden addCredits und removeCredits sind vollständig korrekt implementiert.",
                (JUnitTestRef) null
            )
        ).build();

    private static final Criterion HProjekt_1_3 = Criterion.builder()
        .shortDescription("P1.3 | Alle Schienen führen nach ...")
        .maxPoints(6)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode getRails gibt eine unveränderbare Sicht auf die Schienen des Spielers zurück.",
                (JUnitTestRef) null
            ),
            criterion(
                "Die Methode connectsTo gibt genau dann true zurück, wenn beide Edges ein gemeinsames Tile besitzen.",
                (JUnitTestRef) null
            ),
            criterion(
                "Die Methode getConnectedEdges gibt alle Kanten der, durch die Edge verbundenen, Tiles zurück.",
                (JUnitTestRef) null
            ),
            criterion(
                "Die Methode getConnectedRails gibt alle Schienen zurück, die an den beiden Tiles, die die Edge verbindet, anliegen und zum gegebenen Spieler gehören.",
                (JUnitTestRef) null
            ),
            criterion(
                "Die Methode addRail gibt true zurück und fügt eine Schiene hinzu, genau dann wenn alle Kriterien zum Bauen erfüllt sind.",
                2,
                (JUnitTestRef) null
            )
        ).build();

    private static final Criterion HProjekt_1_4 = Criterion.builder()
        .shortDescription("P1.4 | ...Rom - Implementation der Städte")
        .maxPoints(11)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode getNeighbour gibt das Tile zurück, welches in der richtigen Richtung angrenzt.",
                (JUnitTestRef) null
            ),
            criterion(
                "Die Methode getEdge gibt die Edge zurück, welche in der richtigen Richtung das Tile verbindet.",
                (JUnitTestRef) null
            ),
            criterion(
                "Die Methode getConnectedNeighbours gibt die korrekten Tiles zurück.",
                3,
                (JUnitTestRef) null
            ),
            criterion(
                "Die Methode getConnectedCities gibt eine unveränderbare Sicht auf alle verbundenen Städte zurück.",
                2,
                (JUnitTestRef) null
            ),
            criterion(
                "Die Methode getUnconnectedCities gibt eine unveränderbare Sicht auf alle noch nicht verbundenen Städte zurück.",
                2,
                (JUnitTestRef) null
            ),
            criterion("Die Methode getStartingCities gibt alle Startstädte zurück.",
                2,
                (JUnitTestRef) null
            )
        ).build();

    private static final Criterion HProjekt_1 = Criterion.builder()
        .shortDescription("P1 | Implementierung des Modells")
        .minPoints(0)
        .addChildCriteria(
            HProjekt_1_1,
            HProjekt_1_2,
            HProjekt_1_3,
            HProjekt_1_4
        ).build();

    public static final Rubric RUBRIC = Rubric.builder()
        .title("Projekt | Dampfross")
        .addChildCriteria(
            HProjekt_1
        ).build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }
}

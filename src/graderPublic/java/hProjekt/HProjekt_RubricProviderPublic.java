package hProjekt;

import static org.tudalgo.algoutils.tutor.general.jagr.RubricUtils.criterion;

import hProjekt.controller.LeaderboardControllerTests;
import hProjekt.model.PlayerImplTests;
import org.sourcegrade.jagr.api.rubric.Criterion;
import org.sourcegrade.jagr.api.rubric.JUnitTestRef;
import org.sourcegrade.jagr.api.rubric.Rubric;
import org.sourcegrade.jagr.api.rubric.RubricProvider;
import org.tudalgo.algoutils.tutor.general.jagr.RubricUtils;
import org.tudalgo.algoutils.tutor.general.json.JsonParameterSet;

public class HProjekt_RubricProviderPublic implements RubricProvider {

    private static final Criterion HProjekt_1_1 = Criterion.builder()
        .shortDescription("P1.1 | Daten des Players")
        .maxPoints(1)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methoden getHexGrid, getname, getID, getColor und isAi geben für einen Spieler die korrekten Werte zurück.",
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testGetHexGrid")),
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testGetName")),
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testGetId")),
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testGetColor")),
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testIsAi", boolean.class))))
        .build();

    private static final Criterion HProjekt_1_2 = Criterion.builder()
        .shortDescription("P1.2 | Bankkonto des Players")
        .maxPoints(2)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode getCredits gibt die korrekte Anzahl Credits eines Spielers zurück.",
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testGetCredits", int.class))),
            criterion(
                "Die Methoden addCredits und removeCredits sind vollständig korrekt implementiert.",
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testAddCredits", int.class)),
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testRemoveCredits", int.class)),
                JUnitTestRef.ofMethod(() -> PlayerImplTests.class.getDeclaredMethod("testRemoveCreditsNegative"))))
        .build();

    private static final Criterion HProjekt_1_3 = Criterion.builder()
        .shortDescription("P1.3 | Alle Schienen führen nach ...")
        .maxPoints(6)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode getRails gibt eine unveränderbare Sicht auf die Schienen des Spielers zurück."),
            criterion(
                "Die Methode connectsTo gibt genau dann true zurück, wenn beide Edges ein gemeinsames Tile besitzen."),
            criterion(
                "Die Methode getConnectedEdges gibt alle Kanten der, durch die Edge verbundenen, Tiles zurück."),
            criterion(
                "Die Methode getConnectedRails gibt alle Schienen zurück, die an den beiden Tiles, die die Edge verbindet, anliegen und zum gegebenen Spieler gehören."),
            criterion(
                "Die Methode addRail gibt true zurück und fügt eine Schiene hinzu, genau dann wenn alle Kriterien zum Bauen erfüllt sind.",
                2))
        .build();

    private static final Criterion HProjekt_1_4 = Criterion.builder()
        .shortDescription("P1.4 | ...Rom - Implementation der Städte")
        .maxPoints(11)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode getNeighbour gibt das Tile zurück, welches in der richtigen Richtung angrenzt."),
            criterion(
                "Die Methode getEdge gibt die Edge zurück, welche in der richtigen Richtung das Tile verbindet."),
            criterion(
                "Die Methode getConnectedNeighbours gibt die korrekten Tiles zurück.",
                3),
            criterion(
                "Die Methode getConnectedCities gibt eine unveränderbare Sicht auf alle verbundenen Städte zurück.",
                2),
            criterion(
                "Die Methode getUnconnectedCities gibt eine unveränderbare Sicht auf alle noch nicht verbundenen Städte zurück.",
                2),
            criterion("Die Methode getStartingCities gibt alle Startstädte zurück.",
                2))
        .build();

    private static final Criterion HProjekt_1 = Criterion.builder()
        .shortDescription("P1 | Implementierung des Modells")
        .minPoints(0)
        .addChildCriteria(
            HProjekt_1_1,
            HProjekt_1_2,
            HProjekt_1_3,
            HProjekt_1_4)
        .build();

    private static final Criterion HProjekt_2_1 = Criterion.builder()
        .shortDescription("P2.1 | Welche Gleise?")
        .maxPoints(4)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode canBuildRail gibt false zurück, wenn der Spieler auf der Kante bereits eine Schiene besitzt."),
            criterion(
                "Die Methode canBuildRail gibt false zurück, wenn der Spieler nicht genug Baubudget hat."),
            criterion(
                "Die Methode canBuildRail gibt false zurück, wenn der Spieler nicht genug Credits hat um die Parallelbaukosten zu zahlen."),
            criterion(
                "Die Methode canBuildRail gibt false zurück, wenn der Spieler in der Fahrphase nicht genug Credits hat um die gesamten Baukosten zu zahlen."),
            criterion(
                "Die Methode canBuildRail gibt true zurück, wenn alle Bedingungen zum Bauen erfüllt sind"),
            criterion(
                "Die Methode getBuildableRails gibt nur Kanten zurück, die der Spieler bebauen kann."),
            criterion(
                "Die Methode getBuildableRails gibt zu Beginn nur Kanten die an Startstädte angrenzen zurück."))
        .build();

    private static final Criterion HProjekt_2_2 = Criterion.builder()
        .shortDescription("P2.2 | Gleise bauen")
        .maxPoints(4)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode buildRail löst eine IllegalActionException aus, wenn die Kante nicht zu den baubaren Kanten gehört oder auf der Kante gebaut werden kann."),
            criterion(
                "Die Methode buildRail ruft Edge.addRail korrekt auf, um die Schiene zu bauen."),
            criterion(
                "Die Methode buildRail berechnet die parallelen Baukosten korrekt und zieht sie vom Spieler ab."),
            criterion(
                "Die Methode buildRail schreibt anderen Spielern die Einnahmen aus parallelen Baukosten korrekt gut."),
            criterion(
                "Die Methode buildRail reduziert das Baubudget des Spielers korrekt um die Basis-Baukosten."),
            criterion(
                "Die Methode buildRail zieht in der Bauphase nur die Parallelbaukosten von den Credits des Spielers ab."),
            criterion(
                "Die Methode buildRail zieht außerhalb der Bauphase die gesamten Baukosten (Basis- + Parallelbaukosten) ab."),
            criterion(
                "Die Methode buildRail erkennt das Erreichen einer unverbundenen Stadt korrekt und vergibt den CITY_CONNECTION_BONUS."),
            criterion(
                "Die Methode buildRail gewährt keinen CITY_CONNECTION_BONUS für Startstädte."))
        .build();

    private static final Criterion HProjekt_2_3 = Criterion.builder()
        .shortDescription("P2.3 | Städte verbinden und bauen")
        .maxPoints(4)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode executeBuildingPhase erhöht die Anzahl der Runden."),
            criterion(
                "Die Methode executeBuildingPhase bestimmt den würfelnden Spieler korrekt."),
            criterion(
                "Die Methode executeBuildingPhase löst die Würfelaktion korrekt aus"),
            criterion(
                "Die Methode executeBuildingPhase aktualisiert das Baubudget jedes Spielers."),
            criterion(
                "Die Methode executeBuildingPhase führt für jeden Spieler die Bauaktion aus."),
            criterion(
                "Die Methode executeBuildingPhase stoppt korrekt."))
        .build();

    private static final Criterion HProjekt_2_4 = Criterion.builder()
        .shortDescription("P2.4 | Prepare the race")
        .maxPoints(2)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode chooseCities wählt zufällig eine Start- und eine Zielstadt aus allen noch nicht gewählten Städten aus."),
            criterion(
                "Die Methode chooseCities stellt sicher, dass Start- und Zielstadt nicht identisch sind."),
            criterion(
                "Die Methode chooseCities markiert die ausgewählten Städte im GameState als 'ausgewählt'."),
            criterion(
                "Die Methode chooseCities speichert die Auswahl."))
        .build();

    private static final Criterion HProjekt_2_5 = Criterion.builder()
        .shortDescription("P2.5 | Darf ich fahren?")
        .maxPoints(4)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode canDrive gibt true zurück, wenn die aktuelle Spielphase die GamePhase.DRIVING_PHASE ist und der Spieler in der aktuellen Fahrerliste vorhanden ist."),
            criterion(
                "Die Methode canDrive gibt false zurück, wenn eine der Bedingungen nicht erfüllt ist."),
            criterion(
                "Die Methode drive wirft eine IllegalActionException, wenn der Spieler nicht fahren darf oder die Zielkachel nicht erreichbar ist."),
            criterion(
                "Die Methode drive aktualisiert die Position des Spielers korrekt nach einer gültigen Bewegung."),
            criterion(
                "Die Methode drive berechnet den Würfelüberschuss korrekt und speichert ihn, falls die Zielstadt erreicht wurde."))
        .build();

    private static final Criterion HProjekt_2_6 = Criterion.builder()
        .shortDescription("P2.6 | Die Qual der Wahl, welche Strecke nehmen ich denn?")
        .maxPoints(2)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode letPlayersChoosePath setzt den PlayerController für die Fahrphase korrekt zurück und setzt die Spielerposition auf die Startstadt."),
            criterion(
                "Die Methode letPlayersChoosePath fordert den Spieler korrekt auf, einen Pfad zu wählen."))
        .build();

    private static final Criterion HProjekt_2_7 = Criterion.builder()
        .shortDescription("P2.7 | Tchooo, Tchooo: Steuerung des Fahrens im Spiel")
        .maxPoints(3)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode handleDriving tut nichts, wenn keine Spieler fahren."),
            criterion(
                "Die Methode handleDriving setzt die Position des Spielers direkt auf die Zielstadt, wenn nur ein Spieler fährt."),
            criterion(
                "Die Methode handleDriving zieht zu Beginn jeder Runde Config.DICE_SIDES vom Würfelüberschuss der noch fahrenden Spieler ab, falls bereits ein Spieler die Zielstadt erreicht hat."),
            criterion(
                "Die Methode handleDriving lässt alle Spieler in der korrekten Reihenfolge würfel und fahren."),
            criterion(
                "Die Methode handleDriving stoppt, wenn genügend oder alle Spieler die Zielstadt erreicht haben."))
        .build();

    private static final Criterion HProjekt_2_8 = Criterion.builder()
        .shortDescription("P2.8 | Winner, Winner, Chicken-Dinner")
        .maxPoints(2)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode getWinners gibt nur Spieler zurück, die die Zielstadt erreicht haben."),
            criterion(
                "Die Methode getWinners sortiert die Spieler in absteigender Reihenfolge nach ihrem Würfelüberschuss."),
            criterion(
                "Die Methode getWinners gibt maximal so viele Gewinner zurück, wie es Einträge in Config.WINNING_CREDITS gibt."))
        .build();

    private static final Criterion HProjekt_2_9 = Criterion.builder()
        .shortDescription("P2.9 | Let the race begin!")
        .maxPoints(3)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode executeDrivingPhase erhöht zu Beginn jeder Runde die Anzahl der Runden."),
            criterion(
                "Die Methode executeDrivingPhase setzt die Liste der fahrenden Spieler, die Spielerpositionen und die Überschüsse im GameState korrekt zurück."),
            criterion(
                "Die Methode executeDrivingPhase ruft alle 3 Runden die Methode buildingDuringDrivingPhase() auf."),
            criterion(
                "Die Methode executeDrivingPhase fordert den korrekten Spieler auf, eine Start- und Zielstadt zu wählen."),
            criterion(
                "Die Methode executeDrivingPhase ruft die Methoden letPlayersChoosePath() und handleDriving() korrekt auf, um die Fahrphase durchzuführen."),
            criterion(
                "Die Methode executeDrivingPhase ruft getWinners() auf und aktualisiert die Credits der Spieler entsprechend."))
        .build();

    private static final Criterion HProjekt_2 = Criterion.builder()
        .shortDescription("P2 | Control the Flow!")
        .minPoints(0)
        .addChildCriteria(
            HProjekt_2_1,
            HProjekt_2_2,
            HProjekt_2_3,
            HProjekt_2_4,
            HProjekt_2_5,
            HProjekt_2_6,
            HProjekt_2_7,
            HProjekt_2_8,
            HProjekt_2_9)
        .build();

    private static final Criterion HProjekt_3_1 = Criterion.builder()
        .shortDescription("P3.1 | Leaderboard-Daten speichern")
        .maxPoints(3)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode savePlayerData verwendet das richtige CSV-Format.",
                JUnitTestRef.ofMethod(() -> LeaderboardControllerTests.class.getDeclaredMethod("testSavePlayerData_csvFormat", JsonParameterSet.class))),
            criterion(
                "Die Methode savePlayerData speichert den Namen, die Punkte und den Spielertyp korrekt in einer CSV-Datei.",
                JUnitTestRef.ofMethod(() -> LeaderboardControllerTests.class.getDeclaredMethod("testSavePlayerData_dataCorrect", JsonParameterSet.class))),
            criterion(
                "Die aktuelle Zeit wird korrekt formatiert.",
                JUnitTestRef.ofMethod(() -> LeaderboardControllerTests.class.getDeclaredMethod("testSavePlayerData_timestampFormat", JsonParameterSet.class))))
        .build();

    private static final Criterion HProjekt_3_2 = Criterion.builder()
        .shortDescription("P3.2 | Leaderboard-Daten laden")
        .maxPoints(3)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode loadLeaderboardData ignoriert die Kopfzeile der CSV-Datei und modifiziert die Datei nicht.",
                JUnitTestRef.ofMethod(() -> LeaderboardControllerTests.class.getDeclaredMethod("testLoadLeaderboardData_noMod", JsonParameterSet.class))),
            criterion(
                "Die Methode loadLeaderboardData liest alle gültigen Einträge aus der CSV-Datei und gibt sie als Liste von LeaderBoardEntry zurück.",
                2,
                JUnitTestRef.ofMethod(() -> LeaderboardControllerTests.class.getDeclaredMethod("testLoadLeaderboardData_dataCorrect", JsonParameterSet.class))))
        .build();

    private static final Criterion HProjekt_3 = Criterion.builder()
        .shortDescription("P3 | Highscore!")
        .minPoints(0)
        .addChildCriteria(
            HProjekt_3_1,
            HProjekt_3_2)
        .build();

    private static final Criterion HProjekt_4_1 = Criterion.builder()
        .shortDescription("P4.1 | Should I stay or should I go?")
        .maxPoints(1)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode updateUIBasedOnObjective setzt das GUI korrekt auf den Basiszustand zurück."),
            criterion(
                "Falls der aktuelle Spieler ein Computergegner ist, werden keine weiteren Änderungen am GUI vorgenommen."),
            criterion(
                "Die Methode updateUIBasedOnObjective konfiguriert das GUI entsprechend der erlaubten Aktionen."))
        .build();

    private static final Criterion HProjekt_4_2 = Criterion.builder()
        .shortDescription("P4.2 | Wo ist der Weg?")
        .maxPoints(5)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode trimPath verkürzt den Pfad korrekt und gibt alle Kanten des Pfads zurück.",
                4),
            criterion(
                "Die Methode highlightPath hebt alle übergebenen Edges korrekt hervor."))
        .build();

    private static final Criterion HProjekt_4_3 = Criterion.builder()
        .shortDescription("P4.3 | Wo solls eigentlich losgehen?")
        .maxPoints(5)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode highlightStartingTiles setzt das aktuell gewählte Tile korrekt auf null, bevor die Hervorhebung beginnt."),
            criterion(
                "Alle Tiles mit einer Startstadt werden korrekt hervorgehoben, wenn der Spieler noch keine Schienen gebaut hat."),
            criterion(
                "Alle Tiles mit angeschlossenen Schienen des Spielers werden korrekt hervorgehoben, wenn bereits gebaut wurde."),
            criterion(
                "Beim Anklicken eines hervorgehobenen Tiles wird dieses als ausgewähltes Tile gespeichert und alle anderen Hervorhebungen werden entfernt."),
            criterion(
                "Wenn das aktuell gewählte Tile erneut angeklickt wird, wird die Auswahl zurückgesetzt und die möglichen Starttiles erneut hervorgehoben."))
        .build();

    private static final Criterion HProjekt_4_4 = Criterion.builder()
        .shortDescription("P4.4 | Bob der Baumeister: Wir bauen Schienen!")
        .maxPoints(5)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Methode addBuildHandlers leert den selectedRailPath korrekt und entfernt die selectedTileSubscription."),
            criterion(
                "Der Bestätigungs-Dialog wird korrekt angezeigt, wenn der Spieler Schienen bauen soll."),
            criterion(
                "Die Methode tut nichts weiter, wenn das Baubudget des Spielers bereits aufgebraucht ist."),
            criterion(
                "Der selectedRailPathListener wird korrekt als Listener zum selectedRailPath hinzugefügt."),
            criterion(
                "Die Methode highlightTrimmedPath hebt korrekt den Pfad zwischen Starttile und Ziel-Tile basierend auf den Baukosten hervor."),
            criterion(
                "Wenn der Spieler auf ein Tile klickt, wird die BuildRailAction korrekt ausgeführt."))
        .build();

    private static final Criterion HProjekt_4 = Criterion.builder()
        .shortDescription("P4 | Dem User Interface etwas Leben einhauchen")
        .minPoints(0)
        .addChildCriteria(
            HProjekt_4_1,
            HProjekt_4_2,
            HProjekt_4_3,
            HProjekt_4_4)
        .build();

    private static final Criterion HProjekt_5_1 = Criterion.builder()
        .shortDescription("P5.1 | Neue Spielmechaniken")
        .maxPoints(15)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die erste neue Spielmechanik wurde in das Spiel integriert und ist verständlich dokumentiert.",
                1),
            criterion(
                "Die zweite neue Spielmechanik wurde in das Spiel integriert und ist verständlich dokumentiert.",
                1),
            criterion(
                "Die erste neue Spielmechanik bereichert das Spiel und ist nicht zu stark oder zu schwach.",
                1),
            criterion(
                "Die zweite neue Spielmechanik bereichert das Spiel und ist nicht zu stark oder zu schwach.",
                1),
            criterion(
                "Je nach Komplexität der ersten Mechanik können hier noch bis zu fünf weitere Punkte vergeben werden.",
                5),
            criterion(
                "Je nach Komplexität der zweiten Mechanik können hier noch bis zu fünf weitere Punkte vergeben werden.",
                5),
            criterion(
                "Es wurden 2 Spielmechaniken implementiert.",
                1))
        .grader(RubricUtils.manualGrader())
        .build();

    private static final Criterion HProjekt_5_2 = Criterion.builder()
        .shortDescription("P5.2 | KI als Gegner?")
        .maxPoints(15)
        .minPoints(0)
        .addChildCriteria(
            criterion(
                "Die Strategie des Computergegners ist gut dokumentiert und sinnvoll.", 7),
            criterion(
                "Die Strategie des Computergegners ist komplexer, als die der vorgegebenen KI.",
                1),
            criterion(
                "Der Computergegner ist implementiert und kann über das Menü ausgewählt werden.",
                1),
            criterion(
                "Der Computergegner kann alle Aktionen sinnvoll ausführen.",
                3),
            criterion(
                "Der Computergegner führt nur erlaubte Aktionen aus",
                1),
            criterion(
                "Wenn man zwei Computergegner gegeneinander spielen lässt, gewinnt einer der beiden.",
                2))
        .grader(RubricUtils.manualGrader())
        .build();

    private static final Criterion HProjekt_5 = Criterion.builder()
        .shortDescription("P5 | Weiterführende Aufgaben")
        .minPoints(0)
        .addChildCriteria(
            HProjekt_5_1,
            HProjekt_5_2)
        .build();

    public static final Rubric RUBRIC = Rubric.builder()
        .title("Projekt | Dampfross")
        .addChildCriteria(
            HProjekt_1,
            HProjekt_2,
            HProjekt_3,
            HProjekt_4,
            HProjekt_5)
        .build();

    @Override
    public Rubric getRubric() {
        return RUBRIC;
    }
}

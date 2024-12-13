package hProjekt.view.menus;

import hProjekt.controller.LeaderboardController;
import hProjekt.controller.LeaderboardEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Builder;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Builder for the Leaderboard Scene.
 */
public class LeaderboardBuilder implements Builder<Region> {

    @Override
    public Region build() {
        VBox root = new VBox();
        TableView<LeaderboardEntry> tableView = new TableView<>();

        setupTableColumns(tableView);
        loadLeaderboardData(tableView);

        root.getChildren().add(tableView);
        return root;
    }

    private void setupTableColumns(TableView<LeaderboardEntry> tableView) {
        TableColumn<LeaderboardEntry, String> playerColumn = new TableColumn<>("Player Name");
        playerColumn.setCellValueFactory(cellData -> cellData.getValue().playerNameProperty());

        TableColumn<LeaderboardEntry, Boolean> aiColumn = new TableColumn<>("AI");
        aiColumn.setCellValueFactory(cellData -> cellData.getValue().aiProperty());

        TableColumn<LeaderboardEntry, String> timestampColumn = new TableColumn<>("Timestamp");
        timestampColumn.setCellValueFactory(cellData -> cellData.getValue().timestampProperty());

        TableColumn<LeaderboardEntry, Integer> scoreColumn = new TableColumn<>("Score");
        scoreColumn.setCellValueFactory(cellData -> cellData.getValue().scoreProperty().asObject());

        tableView.getColumns().addAll(playerColumn, aiColumn, timestampColumn, scoreColumn);
    }

    private void loadLeaderboardData(TableView<LeaderboardEntry> tableView) {
        ObservableList<LeaderboardEntry> entries = FXCollections.observableArrayList();

        Path csvFile = Paths.get("src/main/resources/leaderboard.csv");
        if (Files.exists(csvFile)) {
            try (BufferedReader reader = Files.newBufferedReader(csvFile)) {
                String line;
                boolean skipHeader = true; // Skip the header line
                while ((line = reader.readLine()) != null) {
                    if (skipHeader) {
                        skipHeader = false;
                        continue;
                    }
                    String[] data = line.split(",");
                    if (data.length == 4) {
                        entries.add(new LeaderboardEntry(
                            data[0], // PlayerName
                            Boolean.parseBoolean(data[1]), // AI
                            data[2], // Timestamp
                            Integer.parseInt(data[3]) // Score
                        ));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        tableView.setItems(entries);
    }
}

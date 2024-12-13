package hProjekt.controller;

import javafx.beans.property.*;

public class LeaderboardEntry {
    private final StringProperty playerName;
    private final BooleanProperty ai;
    private final StringProperty timestamp;
    private final IntegerProperty score;

    public LeaderboardEntry(String playerName, boolean ai, String timestamp, int score) {
        this.playerName = new SimpleStringProperty(playerName);
        this.ai = new SimpleBooleanProperty(ai);
        this.timestamp = new SimpleStringProperty(timestamp);
        this.score = new SimpleIntegerProperty(score);
    }

    public String getPlayerName() {
        return playerName.get();
    }

    public StringProperty playerNameProperty() {
        return playerName;
    }

    public boolean isAi() {
        return ai.get();
    }

    public BooleanProperty aiProperty() {
        return ai;
    }

    public String getTimestamp() {
        return timestamp.get();
    }

    public StringProperty timestampProperty() {
        return timestamp;
    }

    public int getScore() {
        return score.get();
    }

    public IntegerProperty scoreProperty() {
        return score;
    }
}

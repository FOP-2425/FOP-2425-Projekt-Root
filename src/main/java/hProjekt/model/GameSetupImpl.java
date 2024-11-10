package hProjekt.model;

import java.util.ArrayList;
import java.util.List;

public class GameSetupImpl implements GameSetup {
    private List<String> playerNames;
    private List<Boolean> isAiList;
    private List<String> playerColors;
    private String mapSelection;

    public GameSetupImpl() {
        this.playerNames = new ArrayList<>();
        this.isAiList = new ArrayList<>();
        this.playerColors = new ArrayList<>();
        this.mapSelection = "";
    }

    @Override
    public void addOrUpdatePlayer(String playerName, boolean isAi, int playerIndex, String color) {
        if (playerIndex >= 0 && playerIndex < playerNames.size()) {
            // Update existing player
            playerNames.set(playerIndex, playerName);
            isAiList.set(playerIndex, isAi);
            playerColors.set(playerIndex, color);
        } else if (playerIndex >= playerNames.size()) {
            // Add new player
            playerNames.add(playerName);
            isAiList.add(isAi);
            playerColors.add(color);
        }
    }

    @Override
    public void removePlayer(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < playerNames.size()) {
            playerNames.remove(playerIndex);
            isAiList.remove(playerIndex);
            if (playerIndex < playerColors.size()) {
                playerColors.remove(playerIndex);
            }
        }
    }

    @Override
    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = new ArrayList<>(playerNames);
        while (isAiList.size() < playerNames.size()) {
            isAiList.add(false); // Default to non-AI
        }
        while (playerColors.size() < playerNames.size()) {
            playerColors.add(""); // Default to no color
        }
    }

    @Override
    public List<String> getPlayerNames() {
        return new ArrayList<>(playerNames);
    }

    @Override
    public void setPlayerAsAi(int playerIndex, boolean isAi) {
        if (playerIndex >= 0 && playerIndex < isAiList.size()) {
            isAiList.set(playerIndex, isAi);
        }
    }

    @Override
    public boolean isPlayerAi(int playerIndex) {
        return playerIndex >= 0 && playerIndex < isAiList.size() && isAiList.get(playerIndex);
    }

    @Override
    public void setMapSelection(String map) {
        this.mapSelection = map;
    }

    @Override
    public String getMapSelection() {
        return mapSelection;
    }

    @Override
    public void setPlayerColor(int playerIndex, String color) {
        if (playerIndex >= 0 && playerIndex < playerColors.size()) {
            playerColors.set(playerIndex, color);
        } else if (playerIndex >= playerColors.size()) {
            // Add color if the index is beyond the current size
            for (int i = playerColors.size(); i < playerIndex; i++) {
                playerColors.add(""); // Fill empty slots
            }
            playerColors.add(color);
        }
    }

    @Override
    public String getPlayerColor(int playerIndex) {
        if (playerIndex >= 0 && playerIndex < playerColors.size()) {
            return playerColors.get(playerIndex);
        }
        return ""; // Return empty string if index is invalid
    }
}

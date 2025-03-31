package game;
public class TurnManager {
    private int currentPlayer;
    private int totalPlayers;

    public TurnManager(int totalPlayers) {
        this.totalPlayers = totalPlayers;
        this.currentPlayer = 0;
    }

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public void nextTurn() {
        currentPlayer = (currentPlayer + 1) % totalPlayers;
    }
}

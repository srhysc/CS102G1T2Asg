package game;
/**
 * Helper class to keep track of whose turn it is during the game.
 * It loops through players, so once the last player plays, it starts back at player 0.
 */
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

    /**
     * Moves the turn to the next player.
     * Automatically loops back to 0 once it reaches the last player.
     */
    public void nextTurn() {
        currentPlayer = (currentPlayer + 1) % totalPlayers;
    }
}

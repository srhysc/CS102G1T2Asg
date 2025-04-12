package game;
/**
 * Helper class to keep track of whose turn it is during the game.
 * 
 * The TurnManager class loops through players, so once the last player plays, it starts back at player 0.
 */
public class TurnManager {
    private int currentPlayer;
    private int totalPlayers;

    /**
     * Constructs a new TurnManager.
     * 
     * @param totalPlayers The total number of players playing the game
     */
    public TurnManager(int totalPlayers) {
        this.totalPlayers = totalPlayers;
        this.currentPlayer = 0;
    }

    /**
     * Get the number of the current player.
     * 
     * @return The number of the current player
     */
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

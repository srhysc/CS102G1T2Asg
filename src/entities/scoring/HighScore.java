package entities.scoring;
/**
 * A simple tracker for player high scores.
 * Keeps count of how many times someone has won.
 */
public class HighScore {
    private String playerName;
    private int wins;

    /**
     * Creates a new HighScore record for a player.
     *
     * @param playerName The name of the player
     * @param wins How many wins the player has so far
     */
    public HighScore(String playerName, int wins) {
        this.playerName = playerName;
        this.wins = wins;
    }

    /**
     * Get the name of the player.
     * 
     * @return The name of the player
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Get the number of wins.
     * @return The number of wins 
     */
    public int getWins() {
        return wins;
    }

    /**
     * Call this whenever the player wins a game to bump their score up by one.
     */
    public void incrementWins() {
        this.wins++;
    }

    /**
     * Gives a String representation in the format Player: {playerName}, Wins: {wins}.
     * 
     * @return A String representation of the object
     */
    @Override
    public String toString() {
        return "Player: " + playerName + ", Wins: " + wins;
    }
}


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

    public String getPlayerName() {
        return playerName;
    }

    public int getWins() {
        return wins;
    }

    /**
     * Call this whenever the player wins a game to bump their score up by one.
     */
    public void incrementWins() {
        this.wins++;
    }

    @Override
    public String toString() {
        return "Player: " + playerName + ", Wins: " + wins;
    }
}


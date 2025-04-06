package entities.scoring;
public class HighScore {
    private String playerName;
    private int wins;

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

    public void incrementWins() {
        this.wins++;
    }

    @Override
    public String toString() {
        return "Player: " + playerName + ", Wins: " + wins;
    }
}


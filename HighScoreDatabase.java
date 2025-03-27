import java.io.*;
import java.util.*;

public class HighScoreDatabase {
    private static final String FILE_NAME = "WinsDatabase.txt";
    private Map<String, HighScore> highScores = new HashMap<>();

    public HighScoreDatabase() {
        loadScores();
    }

    public void loadScores() {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_NAME))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(", ");
                if (parts.length == 2) { // Ensure there are exactly two parts (playerName and wins)
                    String playerName = parts[0].split(": ")[1].trim();  // Get the player name after "Player: "
                    try {
                        int wins = Integer.parseInt(parts[1].split(": ")[1].trim());  // Get the number of wins after "Wins: "
                        highScores.put(playerName, new HighScore(playerName, wins));
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid wins format for " + playerName + ": " + parts[1]);
                    }
                } else {
                    System.out.println("Invalid line format in highscores file: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("No existing high scores found.");
        }
    }    

    // Save the high scores back to the file
    private void saveScores() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (HighScore highScore : highScores.values()) {
                writer.write(highScore.toString() + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Update a player's score
    public void updateHighScore(String playerName) {
        HighScore highScore = highScores.get(playerName);
        if (highScore != null) {
            highScore.incrementWins();
        } else {
            highScores.put(playerName, new HighScore(playerName, 1));
        }
        saveScores();
    }

    // Retrieve the high scores (for display or further use)
    public void displayHighScores() {
        highScores.values().forEach(System.out::println);
    }
}

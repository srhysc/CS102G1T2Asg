package entities.scoring;
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
        if (playerName.contains("CPU")) {
            return;
        } else if (highScore != null) {
            highScore.incrementWins();
        } else {
            highScores.put(playerName, new HighScore(playerName, 1));
        }
        saveScores();
    }

    public void displayHighScores() {
        // Create a list from the highScores values and sort by the number of wins
        List<HighScore> sortedScores = new ArrayList<>(highScores.values());
        
        // Sort the list based on wins in descending order
        sortedScores.sort((hs1, hs2) -> Integer.compare(hs2.getWins(), hs1.getWins()));
    
        // ANSI escape codes for colors
        String gold = "\033[0;33m";   // Gold color
        String silver = "\033[0;97m"; // Silver color
        String bronze = "\033[0;38;5;130m"; // Bronze color
        String reset = "\033[0m"; // Reset color to default
    
        // Display the top 3 winners in gold, silver, and bronze
        for (int i = 0; i < sortedScores.size(); i++) {
            HighScore highScore = sortedScores.get(i);
    
            if (i == 0) {
                // Gold for the 1st place
                System.out.println(gold + "First!! " + highScore + reset);
            } else if (i == 1) {
                // Silver for the 2nd place
                System.out.println(silver + "Second!! " + highScore + reset);
            } else if (i == 2) {
                // Bronze for the 3rd place
                System.out.println(bronze + "Third!! " + highScore + reset);
            } else {
                // Display the rest normally
                System.out.println(highScore);
            }
        }

    }
    
}

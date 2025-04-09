package entities.scoring;
import java.io.*;
import java.util.*;

import game.online.GameClient;

/**
 * Manages the high score data for the game.
 * 
 * This class is responsible for loading, saving, and updating the high scores of players.
 * It reads from a file called "WinsDatabase.txt" to retrieve past high scores and writes
 * updates back to the file when a player gets a new win. The top players' scores are displayed
 * with a little flair (gold, silver, bronze) for the first three positions.
 * 
 * The main operations in this class:
 * - {@code loadScores()} loads the existing scores from the file when the game starts.
 * - {@code saveScores()} saves the updated scores back to the file.
 * - {@code updateHighScore(String playerName)} increases a player's win count and saves it.
 * - {@code displayHighScores()} shows the current high scores, highlighting the top 3.
 * 
 * File format:
 * - Each line in the "WinsDatabase.txt" file represents one player's score in this format:
 *   "Player: [playerName], Wins: [winsCount]".
 * 
 * Notes:
 * - CPU players are ignored in the high score list.
 * - The scores are sorted in descending order, with the top player shown first in gold.
 */
public class HighScoreDatabase {
    private static final String FILE_NAME = "WinsDatabase.txt";
    private Map<String, HighScore> highScores = new HashMap<>();

    public HighScoreDatabase() {
        loadScores();
    }

    /**
     * Loads the high scores from the "WinsDatabase.txt" file.
     * 
     * This method reads the file line by line and parses each line to extract the player names
     * and their win counts. The data is then stored in the {@code highScores} map.
     * If the file doesn't exist or there's an error reading it, a message is printed, and no scores
     * are loaded.
     */
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

    /**
     * Saves the current high scores to the "WinsDatabase.txt" file.
     * 
     * This method writes the contents of the {@code highScores} map back to the file, formatting
     * each entry as "Player: [playerName], Wins: [winsCount]". Each player is written on a new line.
     * If an error occurs while saving the file, the stack trace will be printed for debugging.
     */
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

    /**
     * Updates the high score for the specified player.
     * 
     * This method checks if the player has already been recorded in the high scores list. If they have,
     * it increments their win count. If the player is not found in the list, a new entry is created with
     * one win. The updated scores are saved back to the file.
     * 
     * Note: CPU players are ignored and will not have their scores updated.
     *
     * @param playerName The name of the player whose score needs to be updated
     */
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

    /**
     * Displays the current high scores, with special formatting for the top 3 players.
     * 
     * This method sorts the high scores by the number of wins (in descending order) and prints the
     * top 3 players in a colorful format (gold, silver, bronze) for first, second, and third place,
     * respectively. The rest of the players are displayed normally.
     * 
     * The display uses ANSI escape codes for color:
     * - Gold for 1st place
     * - Silver for 2nd place
     * - Bronze for 3rd place
     * 
     * It also handles printing the players with their names and win counts in the sorted order.
     */
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
        
        System.out.println("========================");
        System.out.println("      HALL OF FAME      ");
        System.out.println("========================");
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

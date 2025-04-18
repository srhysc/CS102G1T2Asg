package game;

import entities.*;
import entities.comp.ComputerPlayer;
import entities.scoring.*;
import java.io.*;
import java.util.*;

/**
 * Handles the core gameplay mechanics and turn-based logic for the Parade card
 * game.
 * 
 * The GameLogic class is responsible for initializing the game state,
 * managing player turns (both human and computer), enforcing game rules,
 * handling the parade mechanics, and checking for game-ending conditions.
 * It includes methods to manage the start of the game, player actions,
 * the last round sequence, and score calculation at the end of the game.
 * 
 * This class acts as the engine of the game, coordinating interactions
 * between the deck, players, parade row, and scoring components.
 */
public class GameLogic {

    private static Scanner sc = new Scanner(System.in);
    private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    /**
     * Initializes the Parade card game by shuffling the deck,
     * dealing cards to each player, and setting up the initial parade.
     *
     * @param deck       the deck of cards used in the game
     * @param playerList the list of players participating in the game
     */
    public static void initalizeGame(Deck deck, ArrayList<Player> playerList) {
        // Shuffle the deck before dealing
        deck.shuffle();

        for (int i = 0; i < 39; i++) {
            deck.drawCard();
        }

        // Deal 5 cards to each player
        for (Player player : playerList) {
            for (int i = 0; i < 5; i++) {
                player.addToHand(deck.drawCard());
            }
        }

        // Initialize the parade with 6 cards from the deck
        for (int i = 0; i < 6; i++) {
            Parade.addCard(deck.drawCard());
        }

        // Indicate that the game has been initialized
        System.out.println("game initalized");
    }

    /**
     * Executes the main game loop for the Parade card game.
     * It initializes the game, manages each player's turn, handles player and
     * computer moves,
     * checks for game-ending conditions, and transitions to the last round when
     * appropriate.
     *
     * @param deck            the deck of cards used in the game
     * @param playerList      the list of players participating in the game
     * @param turnManager     the manager that keeps track of whose turn it is
     * @param isTwoPlayerGame a flag indicating if the game is a two-player variant
     */
    public static void playTurn(Deck deck, ArrayList<Player> playerList, TurnManager turnManager,
            boolean isTwoPlayerGame) {
        System.out.println("Starting Parade...");

        // Set up deck, hands, and parade
        initalizeGame(deck, playerList);

        boolean hasAllColours = false;
        String firstPlayerWithAllColours = null;

        System.out.println("Game begins! First player: " + playerList.get(0).getName());

        // Main game loop runs while there are still cards in the deck
        while (!deck.isEmpty()) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());

            System.out.println("\n\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Deck num : " + deck.getSize());
            
            // Changes parade into a string and add in a (place card here) at the end
            // so users will know where the new card will be placed
            String paradeRowString = Parade.getParadeRow().toString();
            if (paradeRowString.endsWith("]")) {
                paradeRowString = paradeRowString.substring(0, paradeRowString.length() - 1)
                                + ", (place card here)]";
            }

            // Now print the updated string
            System.out.println("Parade: " + paradeRowString);


            // If the player is a computer
            if (currentPlayer instanceof ComputerPlayer) {
                Card playedCard = ((ComputerPlayer) currentPlayer).playComputerMove(Parade.getParadeRow());
                ArrayList<Card> takenCards = Parade.removeCards(playedCard);
                Parade.addCard(playedCard);
                currentPlayer.addToCollected(takenCards);
                System.out.println(currentPlayer.getName() + " took: " + takenCards);
            } else {
                // If the player is a human
                System.out.println("Collected Cards: " + currentPlayer.getCollected());
                System.out.println("Your hand: " + currentPlayer.getHandWithIndex());

                int cardIndex = 0;

                // Validate user input: ensure it's a number, not empty, does not have leading and trailing
                // spaces and within valid card index range
                cardIndex = getValidCardIndexToPlay(sc, currentPlayer.getHand().size());

                
                // Play the selected card and resolve the parade effect
                Card playedCard = currentPlayer.playCard(cardIndex);
                ArrayList<Card> takenCards = new ArrayList<>(Parade.removeCards(playedCard));
                Parade.addCard(playedCard);
                currentPlayer.addToCollected(takenCards);

                System.out.println("You took: " + takenCards);
            }

            // Draw a new card from the deck
            currentPlayer.addToHand(deck.drawCard());

            // Check if any player has collected all 6 colors
            for (Player player : playerList) {
                if (player.checkColours()) {
                    hasAllColours = true;
                    firstPlayerWithAllColours = player.getName();
                    break;
                }
            }

            // If game-ending condition met, announce and break for final round
            if (hasAllColours) {
                System.out.println();
                System.out.println();
                System.out.println(firstPlayerWithAllColours + " has all 6 colours");
                System.out.println("Commencing last round");

                turnManager.nextTurn(); // Advance to next player for final round
                break;
            }

            // Proceed to the next player's turn
            turnManager.nextTurn();
        }

        // Start the last round of the game
        lastRound(deck, playerList, turnManager, isTwoPlayerGame);
    }

    /**
     * Executes the final round of the Parade card game.
     * Each player takes one last turn, playing a card and resolving the parade
     * effects.
     * After all players have played, the game ends and scores are tallied.
     *
     * @param deck            the deck of cards used in the game
     * @param playerList      the list of players in the game
     * @param turnManager     the turn manager that tracks the current player
     * @param isTwoPlayerGame true if the game is a 2-player variant, false
     *                        otherwise
     */
    public static void lastRound(Deck deck, ArrayList<Player> playerList, TurnManager turnManager,
            boolean isTwoPlayerGame) {

        // Each player gets one final turn
        for (int i = 0; i < playerList.size(); i++) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());

            System.out.println("\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Deck num : " + deck.getSize());
            System.out.println("Parade: " + Parade.getParadeRow());

            if (currentPlayer instanceof ComputerPlayer) {
                // Computer player makes an automated move
                Card playedCard = ((ComputerPlayer) currentPlayer).playComputerMove(Parade.getParadeRow());
                ArrayList<Card> takenCards = Parade.removeCards(playedCard);
                Parade.addCard(playedCard);
                currentPlayer.addToCollected(takenCards);
                System.out.println(currentPlayer.getName() + " took: " + takenCards);
            } else {
                // Human player: display hand and collected cards
                System.out.println("Collected Cards: " + currentPlayer.getCollected());
                System.out.println("Your hand: " + currentPlayer.getHandWithIndex());

                int cardIndex = 0;

                // Validate user input: ensure it's a number, not empty, does not have leading and trailing
                // spaces and within valid card index range
                cardIndex = getValidCardIndexToPlay(sc, currentPlayer.getHand().size());


                // Play selected card and resolve parade effect
                Card playedCard = currentPlayer.playCard(cardIndex);
                ArrayList<Card> takenCards = new ArrayList<>(Parade.removeCards(playedCard));
                Parade.addCard(playedCard);
                currentPlayer.addToCollected(takenCards);

                System.out.println("You took: " + takenCards);
            }

            // Draw a replacement card from the deck
            currentPlayer.addToHand(deck.drawCard());

            // Move to the next player's turn
            turnManager.nextTurn();
        }

        // End the game and calculate final results
        endGame(playerList, isTwoPlayerGame);
    }



    /**
     * Retrieves a valid card index from the user through input validation.
     * This method continuously prompts the user to enter a card index until user inputs a valid number 
     * within the correct range. 
     * <p> It ensures that the input:
     * <ul>
     * <li> Is not empty or composed of only spaces.</li>
     * <li> Does not contain leading or trailing spaces.</li>
     * <li> Is a valid integer.</li>
     * <li> Falls within the acceptable index range for the player's hand.</li>
     * </ul>
     * 
     * @param sc Scanner object for reading user input
     * @param currentPlayerHandSize The number of cards in the player's hand
     * @return The valid card index (0-based) selected by the user
     */
    public static int getValidCardIndexToPlay(Scanner sc, int currentPlayerHandSize) {
        int index = 0;

        while (true) {
            System.out.print("Choose a card index (1-" + currentPlayerHandSize + "): ");

            String input = sc.nextLine();
            
            
            if (input.trim().isEmpty()) {
                System.out.println("Input cannot be empty! Please choose again.");
                continue;
            }
            
            if (!input.equals(input.trim())) {
                System.out.println("Invalid input! Ensure there are no spaces before or after and that you enter a valid number. Please choose again.");
                continue;
            }                    
            
            int number = 0;
            try {
                number = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("Card index must be a number! Please choose again.");
                continue;
            }
            
            index = number - 1;
            
            if (index < 0 || index >= currentPlayerHandSize) {
                System.out.println("Invalid card number! Please choose again.");
                continue;
            }
            
            break; 
        }

        return index;
    }

    /**
     * Ends the Parade game by calculating scores, declaring the winner,
     * updating the high score database, and returning to the main menu.
     *
     * @param playerList      the list of players in the game
     * @param isTwoPlayerGame true if the game is a 2-player variant, false
     *                        otherwise
     */
    public static void endGame(ArrayList<Player> playerList, boolean isTwoPlayerGame) {
        System.out.println("\nGame Over! Calculating scores...");

        // Calculate final scores and determine the winner
        Player winner = Scoring.calculateScores(playerList, isTwoPlayerGame);
        System.out.println();
        System.out.println();

        if (winner != null) {
            // Update the winner's high score record
            HighScoreDatabase highScoreDatabase = new HighScoreDatabase();
            highScoreDatabase.updateHighScore(winner);
        }

        // Return to the main menu
        Game.main(null);
    }

    /**
     * Clears the console screen using ANSI escape codes.
     * Works in most terminal environments that support ANSI codes.
     */
    private static void clearConsole() {
        System.out.print("\033[H\033[2J"); // ANSI escape code to clear screen and move cursor to top-left
        System.out.flush(); // Ensure the output is immediately written to the console
    }

    /**
     * Flushes any leftover input from the input buffer.
     * This prevents unintended input from being processed, such as players
     * pressing keys when it's not their turn.
     */
    public static void flushInputBuffer() {
        try {
            // Discard any available input from the buffer
            while (System.in.available() > 0) {
                System.in.read();
            }
        } catch (IOException e) {
            System.err.println("Error while flushing input buffer: " + e.getMessage());
        }
    }
}

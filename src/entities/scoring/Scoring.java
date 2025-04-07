package entities.scoring;

import game.online.SocketPacket;
import entities.Card;
import entities.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import entities.comp.ComputerPlayer;

/**
 * Handles the end-of-game scoring logic for both local and online games.
 *
 * This class takes care of all the steps needed to finish a game and figure out how many
 * points each player has earned. It includes things like asking players to discard cards,
 * flipping majority card sets face-down for their respective players, figuring out who has the 
 * most of each card color, and calculating final scores.
 *
 * There are two main entry points:
 * - {@code calculateScores(List<Player> players)} is used in local games.
 * - {@code calculateScoresOnline(List<Player> players)} is used in online games,
 *   and returns a {@link SocketPacket} so results can be sent to each player.
 *
 * Key things this class does:
 * - Ask each player to discard two cards from their hand.
 * - Tally up all collected cards and award bonuses for majorities by color.
 * - Count up everyone's points and print them out (or send them over the network).
 *
 * Depends on:
 * - {@link Player} – represents each player in the game.
 * - {@link Card} – represents a card in the game (with color, value, and flipped state).
 * - {@link SocketPacket} – used to send scoring results in online games.
 *
 * Assumes that all normal gameplay (drawing, playing cards, etc.) is finished.
 * This class just wraps up the round and tells everyone how they did.
 */
public class Scoring {
    /**
     * 
     * Runs the full scoring process for a local game.
     * Players discard their cards, majority colours are worked out,
     * cards are flipped if needed, and scores are calculated and shown.
     * 
     * @param players         all the players in the game
     * @param isTwoPlayerGame true if it's a 2-player game, affects majority rules
     * @return the player who won, or null if it's a tie
     */
    public static Player calculateScores(ArrayList<Player> players, boolean isTwoPlayerGame) {
        promptDiscard(players, isTwoPlayerGame);
        Map<String, Integer> colourMajorities = determineMajorities(players, isTwoPlayerGame);
        flipCards(players, isTwoPlayerGame, colourMajorities);
        return displayScores(players, isTwoPlayerGame);

    }

    /**
     * 
     * Handles the score calculation for online games.
     * Assumes discards already happened. It figures out majorities, flips cards,
     * then builds a packet with the results and winner info for the client.
     * 
     * @param players         list of players still in the game
     * @param isTwoPlayerGame whether it's a 2-player game (used for rules)
     * @return a SocketPacket with game over message and scores
     */
    public static SocketPacket calculateOnlineScores(ArrayList<Player> players, boolean isTwoPlayerGame) {
        // online will pass the player list after the discard
        Map<String, Integer> colourMajorities = determineMajorities(players, isTwoPlayerGame);
        flipCards(players, isTwoPlayerGame, colourMajorities);

        // display scores is also different to handle online players
        SocketPacket sp = displayScoresOnline(players, isTwoPlayerGame);

        return sp;
    }

    /**
     * 
     * Gets each player to discard 2 cards from their hand.
     * For computer players, it auto-discards. For real players, it
     * prints the hand, takes input, and updates the cards.
     * 
     * @param players         list of all current players
     * @param isTwoPlayerGame whether or not it's a 2-player game
     */
    public static void promptDiscard(ArrayList<Player> players, boolean isTwoPlayerGame) {
        Scanner sc = new Scanner(System.in);

        // Allow each player to discard two cards
        for (Player player : players) {
            if (player instanceof ComputerPlayer) {
                ComputerPlayer cPlayer = (ComputerPlayer) player;
                System.out.print(cPlayer.getName() + " is discarding 2 cards");
                for (int i = 0; i < 3; i++) {
                    try {
                        Thread.sleep(500); // half second delay for each .
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    System.out.print(".");
                }
                System.out.println();
                cPlayer.discardTwoCards();
            } else {
                System.out.println(player.getName() + ", here are your cards in hand:");
                ArrayList<Card> hand = player.getHand();
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ": " + hand.get(i).getDetails());
                }

                System.out.println();

                // Discard first card
                int firstCardIndex = getValidCardIndex(sc, hand.size(),
                        "Choose the number of the 1st card to discard: ");
                Card discardedFirstCard = hand.remove(firstCardIndex);
                System.out.println("\n You discarded: " + discardedFirstCard.getDetails());

                // Update display after first discard
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ": " + hand.get(i).getDetails());
                }

                System.out.println();

                // Discard second card
                int secondCardIndex = getValidCardIndex(sc, hand.size(),
                        "Choose the number of the 2nd card to discard: ");
                Card discardedSecondCard = hand.remove(secondCardIndex);
                System.out.println("\n You discarded: " + discardedSecondCard.getDetails() + "\n");

                // Add remaining cards to front //im not sure this is correct
                player.addToCollected(hand);
                // while (!hand.isEmpty()) {
                // player.addToCollected(hand);

                // System.out.println("stuck");

                // System.out.println("test ");
            }

        }
    }

    /**
     * Determines which player has the majority for each card colour.
     * <p>
     * This method loops through all players and counts how many of each colour card
     * they've collected. For each colour, the highest count found becomes the "majority"
     * for that colour. If multiple players have the same number, it still counts as a majority,
     * but this method just returns the highest value – not who owns it.
     * <p>
     * For figuring out who should have their cards flipped down later in the scoring phase.
     *
     * @param players          the list of all players in the current game
     * @param isTwoPlayerGame  true if it's a 2-player game (majority is calculated with a 2 card surplus)
     * @return a map where the key is a colour (String) and the value is the highest number of cards
     *         any player has for that colour
     */
    public static Map<String, Integer> determineMajorities(ArrayList<Player> players, boolean isTwoPlayerGame) {
        // Determine majority for each colour, I used HashMaps cos its much more
        // efficient than counting with ArrayList and a switch statement
        Map<String, Integer> colourMajorities = new HashMap<>();
        for (Player player : players) {
            Map<String, Integer> colourCounts = new HashMap<>();

            for (Card card : player.getCollected()) {
                String colour = card.getColour();
                // Check if map already has colour
                if (colourCounts.containsKey(colour)) {
                    colourCounts.put(colour, colourCounts.get(colour) + 1);
                } else {
                    colourCounts.put(colour, 1);
                }
            }

            for (String colour : colourCounts.keySet()) {
                // Checks if colour not in colourMajorities or if new count is greater and
                // update the majority
                if (!colourMajorities.containsKey(colour) || colourCounts.get(colour) > colourMajorities.get(colour)) {
                    colourMajorities.put(colour, colourCounts.get(colour));
                }
            }
        }

        return colourMajorities;

    }

    /**
     * 
     * Flips cards face down for players who have majority in any colour.
     * In 2-player games, a player needs 2+ of a colour to gain majority.
     * Otherwise, they just need the highest count.
     * 
     * @param players          list of players
     * @param isTwoPlayerGame  special case rules if true
     * @param colourMajorities a map of which colour has what majority
     */
    public static void flipCards(ArrayList<Player> players, boolean isTwoPlayerGame,
            Map<String, Integer> colourMajorities) {

        // Flip majority cards face down
        for (Player player : players) {
            for (String colour : colourMajorities.keySet()) {
                int majorityCount = colourMajorities.get(colour);
                int countForPlayer = 0;

                // Count how many cards of this colour the player has
                for (Card card : player.getCollected()) {
                    if (card.getColour().equals(colour)) {
                        countForPlayer++;
                    }
                }

                // Check if this player has the majority
                boolean hasMajority;

                if (isTwoPlayerGame) {
                    hasMajority = countForPlayer >= majorityCount && countForPlayer >= 2; // Two-player rule
                } else {
                    hasMajority = countForPlayer == majorityCount; // Multi-player rule
                }

                if (hasMajority) {
                    // Flip all cards of this colour face down
                    for (Card card : player.getCollected()) {
                        if (card.getColour().equals(colour)) {
                            card.setFaceDown(true);
                        }
                    }
                }
            }
        }

    }

    /**
     * 
     * Calculates everyone's score, prints it, and figures out the winner.
     * Cards flipped face down only count as 1 point. If scores are tied,
     * whoever has fewer cards wins. If that's tied, there is no winner.
     * 
     * @param players         list of players
     * @param isTwoPlayerGame true if 2-player rules should apply
     * @return the winner, or null if it's a tie
     */
    public static Player displayScores(ArrayList<Player> players, boolean isTwoPlayerGame) {

        // Calculate scores
        Player winner = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Player player : players) {
            int score = 0;
            // System.out.println(player.getName());
            for (Card card : player.getCollected()) {
                // System.out.println("Is card face down: " + card.isFaceDown());
                // System.out.println("Value: " + card.getValue());
                if (card.isFaceDown()) {
                    score += 1; // Down cards count as 1 point
                } else {
                    score += card.getValue(); // Up cards count as their printed value
                }
            }

            player.setScore(score);
            System.out.println(player.getName() + "'s total score: " + score);
            System.out.println(player.getName() + "'s number of cards: " + player.getCollected().size());
            // System.out.println(player.getCollected());

            // winner is the one with the lower score or if equal score then smaller hand
            if (score < lowestScore) {
                lowestScore = score;
                winner = player;
            } else if (score == lowestScore) {
                if (winner != null && player.getCollected().size() < winner.getCollected().size()) {
                    winner = player;
                } else if (winner != null && player.getCollected().size() == winner.getCollected().size()) {
                    winner = null; // Everyone loses
                }
            }
        }

        System.out.println();

        if (winner != null) {
            System.out.println("The winner is: " + winner.getName() + " with a score of " + winner.getScore());
        } else {
            System.out
                    .println("Sometimes it is not about the game but the friends we made along the way, YOU ALL LOST!");
        }

        return winner;
    }

    /**
     * 
     * Online version of score display. Builds a string with scores and winner info.
     * Works the same as local scoring, but sends the output as a SocketPacket
     * for sending over the network. Also updates the high score DB if needed.
     * 
     * @param players         players in the online game
     * @param isTwoPlayerGame true if it's a 2-player game
     * @return a SocketPacket with score info and winner message
     */
    public static SocketPacket displayScoresOnline(ArrayList<Player> players, boolean isTwoPlayerGame) {
        // Calculate scores
        Player winner = null;
        int lowestScore = Integer.MAX_VALUE;

        StringBuilder returnSB = new StringBuilder();

        for (Player player : players) {
            int score = 0;
            // System.out.println(player.getName());
            for (Card card : player.getCollected()) {
                // System.out.println("Is card face down: " + card.isFaceDown());
                // System.out.println("Value: " + card.getValue());
                if (card.isFaceDown()) {
                    score += 1; // Down cards count as 1 point
                } else {
                    score += card.getValue(); // Up cards count as their printed value
                }
            }

            player.setScore(score);
            returnSB.append("\n " + player.getName() + "'s total score is " + score + " with "
                    + player.getCollected().size() + " collected");

            // winner is the one with the lower score or if equal score then smaller hand
            if (score < lowestScore) {
                lowestScore = score;
                winner = player;
            } else if (score == lowestScore) {
                if (winner != null && player.getCollected().size() < winner.getCollected().size()) {
                    winner = player;
                } else if (winner != null && player.getCollected().size() == winner.getCollected().size()) {
                    winner = null; // Everyone loses
                }
            }
        }

        System.out.println();

        if (winner != null) {

            returnSB.append("The winner is: " + winner.getName() + " with a score of " + winner.getScore());
            String colour = "\u001B[38;5;146m"; // Violet-ish colour
            String reset = "\u001B[0m";
            if (winner.getName().equals("AI Yeow Leong")) {
                System.out.println(colour + "I won!" + reset);
                System.out.println(colour + "I won!" + reset);
                System.out.println(colour + "I won!" + reset);
            } else if (winner.getName().equals("AI Jason Chan")) {
                System.out
                        .println(colour + "Now you know why I have been rated as the top 2% of profs at SMU!" + reset);
            } else {
                System.out.println(colour + "Hello, hello, I won!" + reset);

            }
            HighScoreDatabase highScoreDatabase = new HighScoreDatabase();
            highScoreDatabase.updateHighScore(winner.getName());

        } else {
            returnSB.append("Sometimes it is not about the game but the friends we made along the way, YOU ALL LOST!");
        }

        // message type 3 for game over
        SocketPacket sp = new SocketPacket(returnSB, null, 3, players);

        return sp;

    }

    /*
     * public static Player calculateScores(ArrayList<Player> players, boolean
     * isTwoPlayerGame) {
     * Scanner sc = new Scanner(System.in);
     * 
     * // Allow each player to discard two cards
     * for (Player player : players) {
     * if (player instanceof ComputerPlayer) {
     * ComputerPlayer cPlayer = (ComputerPlayer) player;
     * cPlayer.discardTwoCards();
     * } else {
     * System.out.println(player.getName() + ", here are your cards in hand:");
     * ArrayList<Card> hand = player.getHand();
     * for (int i = 0; i < hand.size(); i++) {
     * System.out.println((i + 1) + ": " + hand.get(i).getDetails());
     * }
     * 
     * System.out.println();
     * 
     * // Discard first card
     * int firstCardIndex = getValidCardIndex(sc, hand.size(),
     * "Choose the number of the 1st card to discard: ");
     * Card discardedFirstCard = hand.remove(firstCardIndex);
     * System.out.println("\n You discarded: " + discardedFirstCard.getDetails());
     * 
     * // Update display after first discard
     * for (int i = 0; i < hand.size(); i++) {
     * System.out.println((i + 1) + ": " + hand.get(i).getDetails());
     * }
     * 
     * System.out.println();
     * 
     * // Discard second card
     * int secondCardIndex = getValidCardIndex(sc, hand.size(),
     * "Choose the number of the 2nd card to discard: ");
     * Card discardedSecondCard = hand.remove(secondCardIndex);
     * System.out.println("\n You discarded: " + discardedSecondCard.getDetails() +
     * "\n");
     * 
     * // Add remaining cards to front //im not sure this is correct
     * player.addToCollected(hand);
     * // while (!hand.isEmpty()) {
     * // player.addToCollected(hand);
     * 
     * // System.out.println("stuck");
     * 
     * // System.out.println("test ");
     * }
     * 
     * }
     * 
     * // Determine majority for each colour, I used HashMaps cos its much more
     * // efficient than counting with ArrayList and a switch statement
     * Map<String, Integer> colourMajorities = new HashMap<>();
     * for (Player player : players) {
     * Map<String, Integer> colourCounts = new HashMap<>();
     * 
     * for (Card card : player.getCollected()) {
     * String colour = card.getColour();
     * // Check if map already has colour
     * if (colourCounts.containsKey(colour)) {
     * colourCounts.put(colour, colourCounts.get(colour) + 1);
     * } else {
     * colourCounts.put(colour, 1);
     * }
     * }
     * 
     * for (String colour : colourCounts.keySet()) {
     * // Checks if colour not in colourMajorities or if new count is greater and
     * update the majority
     * if (!colourMajorities.containsKey(colour) || colourCounts.get(colour) >
     * colourMajorities.get(colour)) {
     * colourMajorities.put(colour, colourCounts.get(colour));
     * }
     * }
     * }
     * 
     * // Flip majority cards face down
     * for (Player player : players) {
     * for (String colour : colourMajorities.keySet()) {
     * int majorityCount = colourMajorities.get(colour);
     * int countForPlayer = 0;
     * 
     * // Count how many cards of this colour the player has
     * for (Card card : player.getCollected()) {
     * if (card.getColour().equals(colour)) {
     * countForPlayer++;
     * }
     * }
     * 
     * // Check if this player has the majority
     * boolean hasMajority;
     * 
     * if (isTwoPlayerGame) {
     * hasMajority = countForPlayer >= majorityCount && countForPlayer >= 2; //
     * Two-player rule
     * } else {
     * hasMajority = countForPlayer == majorityCount; // Multi-player rule
     * }
     * 
     * 
     * if (hasMajority) {
     * // Flip all cards of this colour face down
     * for (Card card : player.getCollected()) {
     * if (card.getColour().equals(colour)) {
     * card.setFaceDown(true);
     * }
     * }
     * }
     * }
     * }
     * 
     * // Calculate scores
     * Player winner = null;
     * int lowestScore = Integer.MAX_VALUE;
     * 
     * for (Player player : players) {
     * int score = 0;
     * // System.out.println(player.getName());
     * for (Card card : player.getCollected()) {
     * // System.out.println("Is card face down: " + card.isFaceDown());
     * // System.out.println("Value: " + card.getValue());
     * if (card.isFaceDown()) {
     * score += 1; // Down cards count as 1 point
     * } else {
     * score += card.getValue(); // Up cards count as their printed value
     * }
     * }
     * 
     * player.setScore(score);
     * System.out.println(player.getName() + "'s total score: " + score);
     * System.out.println(player.getName() + "'s number of cards: " +
     * player.getCollected().size());
     * // System.out.println(player.getCollected());
     * 
     * // winner is the one with the lower score or if equal score then smaller hand
     * if (score < lowestScore) {
     * lowestScore = score;
     * winner = player;
     * } else if (score == lowestScore) {
     * if (winner != null && player.getCollected().size() <
     * winner.getCollected().size()) {
     * winner = player;
     * } else if (winner != null && player.getCollected().size() ==
     * winner.getCollected().size()) {
     * winner = null; // Everyone loses
     * }
     * }
     * }
     * 
     * System.out.println();
     * 
     * if (winner != null) {
     * System.out.println("The winner is: " + winner.getName() + " with a score of "
     * + winner.getScore());
     * } else {
     * System.out.
     * println("Sometimes it is not about the game but the friends we made along the way, YOU ALL LOST!"
     * );
     * }
     * 
     * return winner;
     * 
     * }
     */

    /**
     * 
     * Helper method to get a valid card index from user input.
     * Keeps asking until the user gives a number within range.
     * Adjusts for player input from counting from 1.
     * 
     * @param sc      Scanner to read user input
     * @param maxSize max number allowed (hand size)
     * @param prompt  message to show to the user
     * @return valid index (adjusted to start counting from 0)
     */
    public static int getValidCardIndex(Scanner sc, int maxSize, String prompt) {
        int index;
        do {
            System.out.print(prompt);
            index = sc.nextInt();
            if (index < 1 || index > maxSize) {
                System.out.println("Invalid choice! Please select a valid card number.");
            }
        } while (index < 1 || index > maxSize);
        return index - 1; // we count from 0 while player counts from 1
    }
}

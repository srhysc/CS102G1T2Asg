import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class Scoring {

    public static void calculateScores(ArrayList<Player> players, boolean isTwoPlayerGame) {
        Scanner sc = new Scanner(System.in);

        // Allow each player to discard two cards
        for (Player player : players) {
            System.out.println(player.getName() + ", here are your cards in hand:");
            ArrayList<Card> hand = player.getCardsInHand();
            for (int i = 0; i < hand.size(); i++) {
                System.out.println((i + 1) + ": " + hand.get(i).getDetails());
            }

            System.out.println();

            // Discard first card
            int firstCardIndex = getValidCardIndex(sc, hand.size(), "Choose the number of the 1st card to discard: ");
            Card discardedFirstCard = hand.remove(firstCardIndex - 1);
            System.out.println("\n You discarded: " + discardedFirstCard.getDetails());

            // Update display after first discard
            for (int i = 0; i < hand.size(); i++) {
                System.out.println((i + 1) + ": " + hand.get(i).getDetails());
            }

            System.out.println();

            // Discard second card
            int secondCardIndex = getValidCardIndex(sc, hand.size(), "Choose the number of the 2nd card to discard: ");
            Card discardedSecondCard = hand.remove(secondCardIndex - 1);
            System.out.println("\n You discarded: " + discardedSecondCard.getDetails() + "\n");

            // Add remaining cards to front
            while (!hand.isEmpty()) {
                player.addCardToFront(hand.remove(0));
            }
        }

        // Determine majority for each colour, I used HashMaps cos its much more efficient than counting with ArrayList and a switch statement
        Map<String, Integer> colourMajorities = new HashMap<>();
        for (Player player : players) {
            Map<String, Integer> colourCounts = new HashMap<>();

            // Count cards of each colour for this player
            for (Card card : player.getCardsInFront()) {
                String colour = card.getColour();
                // Get the current count of this colour from the map
                int currentCount = colourCounts.getOrDefault(colour, 0);
                int newCount = currentCount + 1;
                // Update the map with the new count
                colourCounts.put(colour, newCount);
            }

            for (String colour : colourCounts.keySet()) {
                // Checks if colour not in colourMajorities or if new count is greater
                if (!colourMajorities.containsKey(colour) || colourCounts.get(colour) > colourMajorities.get(colour)) {
                    colourMajorities.put(colour, colourCounts.get(colour));
                }
            }
        }

        // Flip majority cards face down
        for (Player player : players) {
            for (String colour : colourMajorities.keySet()) {
                int majorityCount = colourMajorities.get(colour);
                int countForPlayer = 0;

                // Count how many cards of this colour the player has
                for (Card card : player.getCardsInFront()) {
                    if (card.getColour().equals(colour)) {
                        countForPlayer++;
                    }
                }

                // Check if this player has the majority
                boolean hasMajority = isTwoPlayerGame
                        ? countForPlayer >= majorityCount && countForPlayer >= 2 // Two-player rule: at least 2 cards and equal or more than majority
                        : countForPlayer == majorityCount;   // Multi-player rule: just find majority

                if (hasMajority) {
                    // Flip all cards of this colour face down
                    for (Card card : player.getCardsInFront()) {
                        if (card.getColour().equals(colour)) {
                            card.setFaceDown(true);
                        }
                    }
                }
            }
        }

        // Calculate scores
        Player winner = null;
        int lowestScore = Integer.MAX_VALUE;

        for (Player player : players) {
            int score = 0;

            for (Card card : player.getCardsInFront()) {
                if (card.isFaceDown()) {
                    score += 1; // Down cards count as 1 point
                } else {
                    score += card.getValue(); // Up cards count as their printed value
                }
            }

            player.setScore(score);
            System.out.println(player.getName() + "'s total score: " + score);

            // winner is the one with the lower score or if equal score smaller hand
            if (score < lowestScore || (score == lowestScore && winner != null && 
                                        player.getCardsInFront().size() < winner.getCardsInFront().size())) {
                lowestScore = score; // Measure size of hand
                winner = player;
            } else if (score == lowestScore && winner != null && player.getCardsInFront().size() == winner.getCardsInFront().size()) {
                winner = null;
            }
        }

        if (winner != null) {
            System.out.println("The winner is: " + winner.getName() + " with a score of " + winner.getScore());    
        } else {
            System.out.println("THERE IS NO WINNER HAHAHAHA, YOU ALL LOST!");
        }
        
    }

    private static int getValidCardIndex(Scanner sc, int maxSize, String prompt) {
        int index;
        do {
            System.out.print(prompt);
            index = sc.nextInt();
            if (index < 1 || index > maxSize) {
                System.out.println("Invalid choice! Please select a valid card number.");
            }
        } while (index < 1 || index > maxSize);
        return index;
    }
}



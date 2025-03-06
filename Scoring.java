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

        // Determine majority for each color, I used HashMaps cos its much more efficient than counting with ArrayList and a switch statement
        Map<String, Integer> colorMajorities = new HashMap<>();
        for (Player player : players) {
            Map<String, Integer> colorCounts = new HashMap<>();

            // Count cards of each color for this player
            for (Card card : player.getCardsInFront()) {
                String color = card.getColor();
                // Get the current count of this color from the map
                int currentCount = colorCounts.getOrDefault(color, 0);
                int newCount = currentCount + 1;
                // Update the map with the new count
                colorCounts.put(color, newCount);
            }

            for (String color : colorCounts.keySet()) {
                // Checks if color not in colorMajorities or if new count is greater
                if (!colorMajorities.containsKey(color) || colorCounts.get(color) > colorMajorities.get(color)) {
                    colorMajorities.put(color, colorCounts.get(color));
                }
            }
        }

        // Flip majority cards face down
        for (Player player : players) {
            for (String color : colorMajorities.keySet()) {
                int majorityCount = colorMajorities.get(color);
                int countForPlayer = 0;

                // Count how many cards of this color the player has
                for (Card card : player.getCardsInFront()) {
                    if (card.getColor().equals(color)) {
                        countForPlayer++;
                    }
                }

                // Check if this player has the majority
                boolean hasMajority = isTwoPlayerGame
                        ? countForPlayer >= majorityCount && countForPlayer >= 2 // Two-player rule: at least 2 cards and equal or more than majority
                        : countForPlayer == majorityCount;   // Multi-player rule: just find majority

                if (hasMajority) {
                    // Flip all cards of this color face down
                    for (Card card : player.getCardsInFront()) {
                        if (card.getColor().equals(color)) {
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

            if (score < lowestScore || (score == lowestScore && winner != null && 
                                        player.getCardsInFront().size() < winner.getCardsInFront().size())) {
                lowestScore = score; // Measure size of hand
                winner = player;
            }
        }

        System.out.println("The winner is: " + winner.getName() + " with a score of " + winner.getScore());
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


// Class methods that I need:
class Card {
    private String color;
    private int value;
    private boolean faceDown;

    public Card(String color, int value) {
        this.color = color;
        this.value = value;
        this.faceDown = false;
    }

    public String getColor() {
        return color;
    }

    public int getValue() {
        return value;
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }

    public String getDetails() {
        return "Card [Color: " + color + ", Value: " + value + "]";
    }
}

class Player {
    private String name;
    private ArrayList<Card> cardsInFront;
    private ArrayList<Card> cardsInHand;
    private int score;

    public Player(String name) {
        this.name = name;
        this.cardsInFront = new ArrayList<>();
        this.cardsInHand = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public ArrayList<Card> getCardsInFront() {
        return cardsInFront;
    }

    public ArrayList<Card> getCardsInHand() {
        return cardsInHand;
    }

    public void addCardToFront(Card card) {
        cardsInFront.add(card);
    }

    public void addCardToHand(Card card) {
        cardsInHand.add(card);
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}

package entities.scoring;
import game.online.SocketPacket;
import entities.Card;
import entities.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import entities.comp.ComputerPlayer;

public class Scoring {

    public static Player calculateScores(ArrayList<Player> players, boolean isTwoPlayerGame){
        promptDiscard(players, isTwoPlayerGame);
        Map<String, Integer> colourMajorities = determineMajorities(players, isTwoPlayerGame);
        flipCards(players, isTwoPlayerGame, colourMajorities);
        return displayScores(players, isTwoPlayerGame);

    }

    public static SocketPacket calculateOnlineScores(ArrayList<Player> players, boolean isTwoPlayerGame){
        //online will pass the player list after the discard 
        Map<String, Integer> colourMajorities = determineMajorities(players, isTwoPlayerGame);
        flipCards(players, isTwoPlayerGame, colourMajorities);

        //display scores is also different to handle online players 
        SocketPacket sp = displayScoresOnline(players, isTwoPlayerGame);

        return sp;
    }

    public static void promptDiscard(ArrayList<Player> players, boolean isTwoPlayerGame){
        Scanner sc = new Scanner(System.in);

        // Allow each player to discard two cards
        for (Player player : players) {
            if (player instanceof ComputerPlayer) {
                ComputerPlayer cPlayer = (ComputerPlayer) player;
                cPlayer.discardTwoCards();
            } else {
                System.out.println(player.getName() + ", here are your cards in hand:");
                ArrayList<Card> hand = player.getHand();
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ": " + hand.get(i).getDetails());
                }

                System.out.println();

                // Discard first card
                int firstCardIndex = getValidCardIndex(sc, hand.size(), "Choose the number of the 1st card to discard: ");
                Card discardedFirstCard = hand.remove(firstCardIndex);
                System.out.println("\n You discarded: " + discardedFirstCard.getDetails());

                // Update display after first discard
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ": " + hand.get(i).getDetails());
                }

                System.out.println();

                // Discard second card
                int secondCardIndex = getValidCardIndex(sc, hand.size(), "Choose the number of the 2nd card to discard: ");
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

    public static Map<String, Integer> determineMajorities(ArrayList<Player> players, boolean isTwoPlayerGame){
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
                // Checks if colour not in colourMajorities or if new count is greater and update the majority
                if (!colourMajorities.containsKey(colour) || colourCounts.get(colour) > colourMajorities.get(colour)) {
                    colourMajorities.put(colour, colourCounts.get(colour));
                }
            }
        }

        return colourMajorities;

    }

    public static void flipCards(ArrayList<Player> players, boolean isTwoPlayerGame, Map<String, Integer> colourMajorities){

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

    public static Player displayScores(ArrayList<Player> players, boolean isTwoPlayerGame){

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
            System.out.println("Sometimes it is not about the game but the friends we made along the way, YOU ALL LOST!");
        }

        return winner;
    }

    public static SocketPacket displayScoresOnline(ArrayList<Player> players, boolean isTwoPlayerGame){
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
            returnSB.append("\n " + player.getName() + "'s total score is " + score + "with " + player.getCollected().size() + " collected");

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
            HighScoreDatabase highScoreDatabase = new HighScoreDatabase();
            highScoreDatabase.updateHighScore(winner.getName());
            
        } else {
            returnSB.append("Sometimes it is not about the game but the friends we made along the way, YOU ALL LOST!");
        }

        //message type 3 for game over
        SocketPacket sp = new SocketPacket(returnSB, null, 3, players);


        return sp;



    }




    /*
    public static Player calculateScores(ArrayList<Player> players, boolean isTwoPlayerGame) {
        Scanner sc = new Scanner(System.in);

        // Allow each player to discard two cards
        for (Player player : players) {
            if (player instanceof ComputerPlayer) {
                ComputerPlayer cPlayer = (ComputerPlayer) player;
                cPlayer.discardTwoCards();
            } else {
                System.out.println(player.getName() + ", here are your cards in hand:");
                ArrayList<Card> hand = player.getHand();
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ": " + hand.get(i).getDetails());
                }

                System.out.println();

                // Discard first card
                int firstCardIndex = getValidCardIndex(sc, hand.size(), "Choose the number of the 1st card to discard: ");
                Card discardedFirstCard = hand.remove(firstCardIndex);
                System.out.println("\n You discarded: " + discardedFirstCard.getDetails());

                // Update display after first discard
                for (int i = 0; i < hand.size(); i++) {
                    System.out.println((i + 1) + ": " + hand.get(i).getDetails());
                }

                System.out.println();

                // Discard second card
                int secondCardIndex = getValidCardIndex(sc, hand.size(), "Choose the number of the 2nd card to discard: ");
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
                // Checks if colour not in colourMajorities or if new count is greater and update the majority
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
            System.out.println("Sometimes it is not about the game but the friends we made along the way, YOU ALL LOST!");
        }

        return winner;

    }
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

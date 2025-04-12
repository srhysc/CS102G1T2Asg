package entities;

import java.io.*;
import java.util.*;

/**
 * The Player class is to represent each player in the game.
 * It keeps track of their hand (cards they're holding), the cards they’ve
 * collected,
 * their score, and handles stuff for online games like input/output streams.
 */
public class Player implements Serializable {
    private String name;
    private ArrayList<Card> hand;
    private ArrayList<Card> collected;
    private int score;

    // for online
    private transient ObjectOutputStream out;
    private transient ObjectInputStream in;

    /**
     * Constructs a new player with the given name.
     *
     * @param name The player's name
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.collected = new ArrayList<>();
    }

    public Player(String name, ObjectOutputStream output, ObjectInputStream input) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.collected = new ArrayList<>();
        this.out = output;
        this.in = input;
    }

    /**
     * Adds a card to the player’s hand.
     */
    public void addToHand(Card card) {
        if (card != null)
            hand.add(card);
    }

    /**
     * Adds multiple cards to the collected pile (cards taken from the parade).
     */
    public void addToCollected(ArrayList<Card> cards) {
        collected.addAll(cards);

        // Test for all 6 colours
        // collected.add(new Card("Red ", 1));
        // collected.add(new Card("Blue ", 1));
        // collected.add(new Card("Purple ", 1));
        // collected.add(new Card("Green ", 1));
        // collected.add(new Card("Grey ", 1));
        // collected.add(new Card("Orange ", 1));
    }

    /**
     * Removes a card from the hand by index and returns it.
     *
     * @param index Index of the card to play
     * @return The card played
     */
    public Card playCard(int index) {
        return hand.remove(index);
    }

    /**
     * Returns a string showing all cards in the hand, each with an index so players
     * can pick easily.
     *
     * @return Formatted hand string
     */
    public String getHandWithIndex() {
        String formattedHand = "";
        for (Card card : hand) {
            // System.out.println(str);
            int idx = hand.indexOf(card) + 1;
            formattedHand += card + " (" + idx + ")" + " ";
        }
        // System.out.println(formattedHand);
        return "[" + formattedHand + "]";
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public ArrayList<Card> getCollected() {
        return collected;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public ObjectOutputStream getOutputSteam() {
        return out;
    }

    public ObjectInputStream getInputSteam() {
        return in;
    }

    /**
     * Checks if the player has collected at least one card from *each* of the six
     * colours.
     * Used for ending the game.
     *
     * @return True if the player has all 6 colours, false otherwise
     */
    public boolean checkColours() {
        boolean hasRed = false;
        boolean hasBlue = false;
        boolean hasGreen = false;
        boolean hasPurple = false;
        boolean hasGrey = false;
        boolean hasOrange = false;

        for (Card card : getCollected()) {
            if (card.toString().startsWith("Red")) {
                hasRed = true;
            }
            if (card.toString().startsWith("Blue")) {
                hasBlue = true;
            }
            if (card.toString().startsWith("Purple")) {
                hasPurple = true;
            }
            if (card.toString().startsWith("Green")) {
                hasGreen = true;
            }
            if (card.toString().startsWith("Grey")) {
                hasGrey = true;
            }
            if (card.toString().startsWith("Orange")) {
                hasOrange = true;
            }

        }

        // System.out.println(hasRed);
        // System.out.println(hasBlue);
        // System.out.println(hasGreen);
        // System.out.println(hasPurple);
        // System.out.println(hasGrey);
        // System.out.println(hasOrange);
        return hasRed && hasBlue && hasGreen && hasPurple && hasGrey && hasOrange;
    }

    /**
     * Gives a String representation in the format {name} | Hand: {hand} + |
     * Collected: {collected}
     * 
     * @return a quick summary of the player — name, hand, and collected cards.
     */
    @Override
    public String toString() {
        return " | Player name: " + name + " | Hand: " + hand + " | Collected: " + collected;
    }

    // // Helper method to apply card formatting properly
    // public String formatCardList(List<Card> cards) {
    // return cards.stream().map(Card::toString).collect(Collectors.joining(" "));
    // }

}

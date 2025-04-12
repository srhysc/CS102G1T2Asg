package entities;

import java.io.*;
import java.util.*;

/**
 * Represents each player in the game.
 * The Player class keeps track of their hand (cards they're holding), the cards they’ve collected,
 * their score, and handles stuff for online games like input/output streams.
 */
public class Player implements Serializable {
    private String name;
    private ArrayList<Card> hand;
    private ArrayList<Card> collected;
    private int score;

    //for online
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

    /**
     * Constructs a new player with the specified name, output stream and input stream.
     * 
     * @param name The player's name
     * @param output The output stream used to send objects to the player
     * @param input The input stream used to receive objevts from the player
     */
    public Player(String name, ObjectOutputStream output, ObjectInputStream input){
        this.name = name;
        this.hand = new ArrayList<>();
        this.collected = new ArrayList<>();
        this.out = output;
        this.in = input;
    }

    /**
     * Adds a card to the player’s hand.
     * 
     * @param card The card to be added to the player's hand
     */
    public void addToHand(Card card) {
        if (card != null)
            hand.add(card);
    }

    /**
     * Adds multiple cards to the collected pile (cards taken from the parade).
     * 
     * @param cards An ArrayList of Card objects to be collected from the parade and 
     * added to the collected pile
     */
    public void addToCollected(ArrayList<Card> cards) {
        collected.addAll(cards);
    }

    /**
     * Removes a card from the player's hand by index and returns it.
     *
     * @param index Index of the card in the player's hand to be played
     * @return The Card being played
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
            int idx = hand.indexOf(card) + 1;
            formattedHand += card + " (" + idx + ")" + " ";
        }
        return "[" + formattedHand + "]";
    }

    /**
     * Get the list of cards currently held in the player's hand.
     * 
     * @return An ArrrayList of Card objects representing the player's hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Returns the list of cards that the player has collected.
     * 
     * @return An ArrrayList of Card objects representing the cards 
     * that the player has collected
     */
    public ArrayList<Card> getCollected() {
        return collected;
    }

    /**
     * Returns the name of the player.
     * 
     * @return The player's name
     */
    public String getName() {
        return name;
    }

   /**
    * Returns the current score of the player.

    * @return The player's score
    */
    public int getScore() {
        return score;
    }

    /**
     * Sets the player's score to the specified value.
     * 
     * @param score The value of the score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * Returns the ObjectOutputStream associated with the player.
     * This stream is used to send serialised objects (eg. game state updates)
     * 
     * @return an ObjectOutputStream used for sending objects
     */    
    public ObjectOutputStream getOutputSteam() {
        return out;
    }

    /**
     * Returns the ObjectOutputStream associated with the player.
     * This stream is used to receive serialised objects (eg. the player's moves)
     * 
     * @return an ObjectInputStream used for receiving objects
     */
    public ObjectInputStream getInputSteam() {
        return in;
    }

    /**
     * Checks if the player has collected at least one card from *each* of the six colours.
     * Used for ending the game.
     *
     * @return True if the player has all 6 colours, false otherwise
     */
    public boolean checkColours(){
        boolean hasRed = false;
        boolean hasBlue = false;
        boolean hasGreen = false;
        boolean hasPurple = false;
        boolean hasGrey = false;
        boolean hasOrange = false;

        for(Card card : getCollected()){
            if(card.toString().startsWith("Red")){
                hasRed = true;
            }
            if(card.toString().startsWith("Blue")){
                hasBlue = true;
            }
            if(card.toString().startsWith("Purple")){
                hasPurple = true;
            }
            if(card.toString().startsWith("Green")){
                hasGreen = true;
            }
            if(card.toString().startsWith("Grey")){
                hasGrey = true;
            }
            if(card.toString().startsWith("Orange")){
                hasOrange = true;
            }
            
        }
        

        return hasRed && hasBlue && hasGreen && hasPurple && hasGrey && hasOrange;
    }

    /**
     * Gives a String representation in the format {name} | Hand: {hand} + | Collected: {collected}
     * 
     * @return a quick summary of the player — name, hand, and collected cards.
     */
    @Override
    public String toString() {
        return " | Player name: " + name + " | Hand: " + hand + " | Collected: " + collected;
    }


}

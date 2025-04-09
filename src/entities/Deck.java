package entities;
import java.util.*;

/**
 * Represents a standard deck of cards used in the game.
 * The deck contains 66 cards total — 6 colours, each with values from 0 to 10.
 * You can shuffle the deck, draw cards, and check how many are left.
 *
 * <p>Example usage:</p>
 * <pre>
 *     Deck deck = new Deck();
 *     Card topCard = deck.drawCard();
 *     System.out.println("Drew: " + topCard);
 * </pre>
 */
public class Deck {
    private List<Card> cards;

    /**
     * Constructor for Deck.
     * Builds a full deck of 66 cards (6 colours × 11 values),
     * then shuffles it straight away.
     */
    public Deck(){
    
            //deck requirements are 
            //66 cards
            //6 colours with values ranging from 0 to 10
    
            String[] cardColours = {"Red ","Blue ","Purple ","Green ","Grey ","Orange "};    
            cards = new ArrayList<>();
    
            for (String colour : cardColours) {
                for(int i = 0; i <= 10; i++){
                    Card cardToBeAdded = new Card(colour, i);
                    cards.add(cardToBeAdded);
    
                }
            }
            shuffle();
        }

    /**
     * Shuffles the deck randomly using Java's built-in shuffle method.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws (removes and returns) the top card of the deck.
     * If the deck is empty, returns null.
     *
     * @return The top card or null if there are no cards left
     */
    public Card drawCard() {
        return cards.isEmpty() ? null : cards.remove(0);
    }

    /**
     * @return The number of cards that are currently left in the deck
     */
    public int getSize(){
        return this.cards.size();
    }

    /**
     * Checks if the deck is empty.
     *
     * @return True if there are no cards left, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}

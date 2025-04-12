package entities;

import java.util.*;

/**
 * Handles everything going on in the Parade row during the game.
 * The Parade is displayed as a shared line of cards that players add to, and
 * take cards from based on the card played.
 */
public class Parade {

    public static ArrayList<Card> paradeRow = new ArrayList<>();

    /**
     * Adds a card to the end of the Parade row.
     * Typically happens when a player plays a card on their turn.
     *
     * @param card The card to be added to the parade
     */
    public static void addCard(Card card) {
        paradeRow.add(card);
    }

    /**
     * After a player plays a card, we figure out which cards (if any)
     * they need to take from the Parade.
     *
     * Rule breakdown:
     * - Start from the beginning of the Parade and go up to (length - value of
     * played card)
     * - From that subset, the player takes any card that:
     * a) matches the played card’s colour, OR
     * b) has a value less than or equal to the played card
     *
     * Those cards are then removed from the Parade and returned.
     *
     * @param playedCard The card the player just added to the Parade
     * @return A list of cards that the player takes as a result
     */
    public static ArrayList<Card> removeCards(Card playedCard) {

        int idxLimit = paradeRow.size() - playedCard.getValue();
        ArrayList<Card> remainingCards = new ArrayList<>();
        if (idxLimit > 0) {
            remainingCards = new ArrayList<>(paradeRow.subList(0, idxLimit));
        }

        ArrayList<Card> takenCards = new ArrayList<>();
        for (Card card : remainingCards) {
            if (card.getColour().equals(playedCard.getColour()) || card.getValue() <= playedCard.getValue()) {
                takenCards.add(card);
            }
        }
        paradeRow.removeAll(takenCards);

        return takenCards;
    }

    /**
     * Returns the current state of the Parade row.
     *
     * @return The list of cards in the Parade
     */
    public static ArrayList<Card> getParadeRow() {
        return paradeRow;
    }

}

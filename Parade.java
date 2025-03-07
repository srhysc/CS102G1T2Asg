import java.util.*;

public class Parade {
    public static ArrayList<Card> paradeRow = new ArrayList<>();

    // public Parade(ArrayArrayList<Card> paradeList) {
    //     this.paradeList = paradeList;
    // }

    public static void addCard(Card card) {
        paradeRow.add(card);
    }

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

    public static ArrayList<Card> getParadeRow() {
        return paradeRow;
    }

}

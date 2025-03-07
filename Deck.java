import java.util.*;

public class Deck {
    private List<Card> cards;

    public Deck(){
    
            //deck requirements are 
            //66 cards
            //6 colours with values ranging from 0 to 10
    
            String[] cardColours = {"Red ","Blue ","Purple ","Green ","Grey ","Orange "};    
            cards = new ArrayList<>();
    
            for (String colour : cardColours) {
                for(int i = 0; i <= 3; i++){
                    Card cardToBeAdded = new Card(colour, i);
                    cards.add(cardToBeAdded);
    
                }
            }
            shuffle();
        }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    public Card drawCard() {
        return cards.isEmpty() ? null : cards.remove(0);
    }

    public int getSize(){
        return this.cards.size();
    }

    // ✅ Fix: Added isEmpty() method
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}

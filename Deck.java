import java.util.*;

public class Deck {
    ArrayList<Card> cardDeck = new ArrayList<>();

    public ArrayList<Card> generateDeck(){

        //deck requirements are 
        //66 cards
        //6 colours with values ranging from 0 to 10

        String[] cardColours = {"red","blue","purple","green","grey","orange"};    
        

        for (String colour : cardColours) {
            for(int i = 0; i <= 10; i++){
                Card cardToBeAdded = new Card(i, colour);
                cardDeck.add(cardToBeAdded);

            }
        }

        return cardDeck; 
    }


    public ArrayList<Card> shuffleDeck(){

        Collections.shuffle(cardDeck);
    
        return cardDeck;
    }


    //this one idk how we want to handle cos its either we choose by index or card or smth. if not
    //there needs to be a system to check if the drawed card is valid fromt he current parade
    //unless this is str into the parade 
    public Card drawCard(Card c){

        //remove the card from the deck and return the card
        cardDeck.remove(c);

        return c;
    }




}

import java.util.ArrayList;

public class Parade {

    ArrayList<Card> paradeList = new ArrayList<>();

    public Parade(ArrayList<Card> paradeList) {
        this.paradeList = paradeList;
    }
    
   

    public ArrayList<Card> addCard(Card card){
        paradeList.add(card);
        return paradeList;
    }

    public ArrayList<Card> removeCard(ArrayList<Card> cardsToBeRemoved){
        for (Card card : cardsToBeRemoved) {
            paradeList.remove(card);
       
        }
        return paradeList;
    }



    public ArrayList<Card> getParadeList() {
        return paradeList;
    }


}

import java.util.ArrayList;

public class Player {
   
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


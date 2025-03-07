import java.util.*;

public class Player {
    private String name;
    private ArrayList<Card> hand;
    private ArrayList<Card> collected;
    private int score;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.collected = new ArrayList<>();
    }

    public void addToHand(Card card) {
        if (card != null)
            hand.add(card);
    }

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

    public Card playCard(int index) {
        return hand.remove(index);
    }

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
        
        // System.out.println(hasRed);
        // System.out.println(hasBlue);
        // System.out.println(hasGreen);
        // System.out.println(hasPurple);
        // System.out.println(hasGrey);
        // System.out.println(hasOrange);
        return hasRed && hasBlue && hasGreen && hasPurple && hasGrey && hasOrange;
    }

    @Override
    public String toString() {
        return name + " | Hand: " + hand + " | Collected: " + collected;
    }
}

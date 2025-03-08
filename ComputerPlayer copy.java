import java.util.*; 

public class ComputerPlayer extends Player {

    private static Difficulty gameDifficulty;
    private Random random; 

    public ComputerPlayer(String name) {
        super(name);
        this.random = new Random(); 
    }

    // public ComputerPlayer(String name, ArrayList<Card> hand) {
    //     super(name);
       
    // }

    public static void setGameDifficulty(Difficulty difficulty) {
        gameDifficulty = difficulty; 
    }

    public static Difficulty getGameDifficulty() {
        return gameDifficulty; 
    }

    //VOID will probably become smth else depending on how we confirm each round will play
    public void playComputerMove(ArrayList <Card> parade){
        if (super.getHand().isEmpty()) {
            return; 
        }

        Card chosenCard; 
        if (gameDifficulty == Difficulty.EASY) {
            chosenCard = getHand().get(random.nextInt(getHand().size())); 
        } else {
            chosenCard = chooseBestCard(); 
        }
        // edit later to add medium 

        getHand().remove(chosenCard);
        parade.add(chosenCard); 
        System.out.println(getName() + "played:" + chosenCard);
    }

    public Card chooseBestCard() {
        ArrayList<Card> hand = getHand(); 
        Card bestCard = null; 
        int minPenalty = Integer.MAX_VALUE; 

        for (Card card : hand) {
            int penalty = calculatePenalty(card);
            if (penalty < minPenalty) {
                minPenalty = penalty;
                bestCard = card; 
            }
        }

        return bestCard != null ? bestCard : hand.get(0); 
    }

    private int calculatePenalty(Card card) {
        int penalty = 0; 
        ArrayList <Card> parade = Parade.getParadeRow(); 
        for (Card c : parade) {
            if (c.getColour().equals(card.getColour())) {
                penalty += 2; 
            } else if (c.getValue() <= card.getValue()) {
                penalty += 1; 
            }
        }
        penalty += (10 - card.getValue()); // so that cards with lower value have higher penalty --> not best card
        return penalty; 
    }

}


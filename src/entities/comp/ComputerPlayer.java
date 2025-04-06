package entities.comp;
import entities.Card;
import entities.Player;
import java.util.*; 

public class ComputerPlayer extends Player {

    private static Difficulty gameDifficulty;

    public ComputerPlayer(String name) {
        super(name);
    }

    public static void setGameDifficulty(Difficulty difficulty) {
        gameDifficulty = difficulty; 
    }

    public static Difficulty getGameDifficulty() {
        return gameDifficulty; 
    }

    public Card playComputerMove(ArrayList <Card> parade){
        if (super.getHand().isEmpty()) {
            return null; 
        }

        Card chosenCard; 
        if (gameDifficulty == Difficulty.EASY) {
            chosenCard = chooseRandomCard();
        } else if (gameDifficulty == Difficulty.HARD) {
            chosenCard = chooseBestCard(parade); 
        } else {
            Random r = new Random(); 
            boolean randomChoice = r.nextBoolean(); 
            if (randomChoice) {
                chosenCard = chooseRandomCard();
            } else {
                chosenCard = chooseBestCard(parade); 
            }
        }
        getHand().remove(chosenCard);
        // parade.add(chosenCard); 
        System.out.print(getName() + " is thinking");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);  // half second delay for each . by pausing the execution
            } catch (InterruptedException e) { // when another thread interrupts the sleeping thread
                Thread.currentThread().interrupt();
            }
            System.out.print(".");
        }
        System.out.println();  
        System.out.println(getName() + " played:" + chosenCard);
        return chosenCard;  
    }

    public Card chooseRandomCard() {
        Random random = new Random(); 
        return getHand().get(random.nextInt(getHand().size())); 
    }

    public Card chooseBestCard(ArrayList <Card> parade) {
        ArrayList<Card> hand = getHand(); 
        Card bestCard = null; 
        int minPenalty = Integer.MAX_VALUE; 

        for (Card card : hand) {
            int penalty = calculatePenalty(card, parade);
            if (penalty < minPenalty) {
                minPenalty = penalty;
                bestCard = card; 
            }
        }

        return bestCard != null ? bestCard : hand.get(0); 
    }

    private int calculatePenalty(Card card, ArrayList <Card> parade) {
        int penalty = 0; 
        //ArrayList <Card> parade = Parade.getParadeRow(); 
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

    public List<Card> discardTwoCards() {
        ArrayList<Card> hand = getHand();
        if (hand.size() != 4) {
            return new ArrayList<>();
        }
    
        // count collected card colors
        ArrayList<Card> collected = getCollected();
        Map<String, Integer> colourCounts = new HashMap<>();
        for (Card card : collected) {
            colourCounts.put(card.getColour(), colourCounts.getOrDefault(card.getColour(), 0) + 1);
        }
        // sort to remove any new colours not in collected first, then remove cards with higher value
        List<Card> sortedHand = new ArrayList<>(hand);
        sortedHand.sort((c1, c2) -> {
            boolean c1NewColour = !colourCounts.containsKey(c1.getColour());
            boolean c2NewColour = !colourCounts.containsKey(c2.getColour());
    
            if (c1NewColour && !c2NewColour) return -1;
            if (!c1NewColour && c2NewColour) return 1;
    
            return Integer.compare(c2.getValue(), c1.getValue()); // cards with higher value in front
        });
    
        List<Card> toDiscard = new ArrayList<>();
        toDiscard.add(sortedHand.get(0));
        toDiscard.add(sortedHand.get(1));
        
        hand.remove(sortedHand.get(0));
        hand.remove(sortedHand.get(1));

        return toDiscard;
    }

}


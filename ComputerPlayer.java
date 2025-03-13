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

    public void playComputerMove(ArrayList <Card> parade){
        if (super.getHand().isEmpty()) {
            return; 
        }

        Card chosenCard; 
        if (gameDifficulty == Difficulty.EASY) {
            chosenCard = chooseRandomCard();
        } else if (gameDifficulty == Difficulty.HARD) {
            chosenCard = chooseBestCard(); 
        } else {
            Random r = new Random(); 
            boolean randomChoice = r.nextBoolean(); 
            if (randomChoice) {
                chosenCard = chooseRandomCard();
            } else {
                chosenCard = chooseBestCard(); 
            }
        }
        getHand().remove(chosenCard);
        parade.add(chosenCard); 
        System.out.println(getName() + "played:" + chosenCard);
    }

    public Card chooseRandomCard() {
        Random random = new Random(); 
        return getHand().get(random.nextInt(getHand().size())); 
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


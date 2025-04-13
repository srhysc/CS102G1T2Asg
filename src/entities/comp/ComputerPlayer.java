package entities.comp;
import entities.Card;
import entities.Player;
import java.util.*; 

/**
 * ComputerPlayer represents an AI Player in Parade.
 * ComputerPlayer extends the Player Class and implements decision making based on the different difficulty levels.
 */

public class ComputerPlayer extends Player {

    private static Difficulty gameDifficulty;

    /**
     * Constructs a new ComputerPlayer with the specified name.
     * 
     * @param name The name of the computer player
     */
    public ComputerPlayer(String name) {
        super(name);
    }

    /**
     * Sets the difficulty level for all computer players.
     * 
     * @param difficulty The difficulty level to set (EASY, MEDIUM, HARD)
     */
    public static void setGameDifficulty(Difficulty difficulty) {
        gameDifficulty = difficulty; 
    }

    /**
     * Returns the current difficulty level for computer players.
     * 
     * @return The current difficulty level
     */
    public static Difficulty getGameDifficulty() {
        return gameDifficulty; 
    }

    /**
     * Decides on the computer's move based on the current difficulty level with a delay.
     * 
     * @param parade The current state of the parade (list of cards)
     * @return The card chosen to play, or null if the player's hand is empty
     */
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
        System.out.print(getName() + " is thinking");
        for (int i = 0; i < 3; i++) {
            try {
                Thread.sleep(500);  // half second delay for each . by pausing the execution
            } catch (InterruptedException e) { 
                Thread.currentThread().interrupt();
            }
            System.out.print(".");
        }
        System.out.println();  
        System.out.println(getName() + " played:" + chosenCard);
        String colour = "\u001B[38;5;146m"; // Violet-ish  colour
        String reset = "\u001B[0m";
        String phrase = "";
        
        if ("AI Yeow Leong".equals(super.getName())) {
            double rand = Math.random();
            if (rand < 0.2) {
                phrase = "Let's C... you are losing!";
            } else if (rand < 0.4) {
                phrase = "I have 25 years experience!";
            } else if (rand < 0.6) {
                phrase = "I have been playing Parade since Parade 1.0, before you were born!";
            } else if (rand < 0.8) {
                phrase = "Look Ma! I'm dominating!";
            } else if (rand < 0.9) {
                phrase = "Your laptop feels my aura! That's why your cards are bad!";
            } else {
                phrase = "**Gasp** MAGIC... Yeow Leong the magician!";
            }
        } else if ("AI Jason Chan".equals(super.getName())) {
            double rand = Math.random();
            if (rand < 0.2) {
                phrase = "Don't reply to this move unless you have any questions";
            } else if (rand < 0.4) {
                phrase = "I've done this before so you should follow me!";
            } else if (rand < 0.6) {
                phrase = "I will be revealing secrets that you will NOT find anywhere else...";
            } else if (rand < 0.8) {
                phrase = "My grades in Australia were higher than A+ in SMU!";
            } else {
                phrase = "Can students claim bragging rights and outsmart a prof?";
            }
        } else if ("AI VeryEvilCuteBunny".equals(super.getName())) {
            double rand = Math.random();
            if (rand < 0.2) {
                phrase = "What did the bunny say? I'm winning!";
            } else if (rand < 0.4) {
                phrase = "The bunny... you will see me sooner or later...";
            } else if (rand < 0.6) {
                phrase = "So What does this mean?";
            } else if (rand < 0.8) {
                phrase = "Hello, hello, r u OK?!";
            } else {
                phrase = "blah.";
            }
        }
        System.out.println(colour + phrase + reset);
        return chosenCard;  
    }

    /**
     * Selects a random card from the player's hand.
     * Used for EASY difficulty or when MEDIUM difficulty selects a random strategy.
     * 
     * @return A randomly selected card from the player's hand
     */
    public Card chooseRandomCard() {
        Random random = new Random(); 
        return getHand().get(random.nextInt(getHand().size())); 
    }

    /**
     * Selects the best card to play from the player's hand based on a penalty calculation algorithm.
     * Used for HARD difficulty or when MEDIUM difficulty selects the smart strategy.
     * 
     * @param parade The current state of the parade (list of cards)
     * @return The card with the lowest calculated penalty
     */
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

    /**
     * Calculates a penalty score for playing a specific card with the current parade state.
     * Lower penalty scores indicate better moves.
     * 
     * @param card The card to evaluate
     * @param parade The current state of the parade (list of cards)
     * @return The calculated penalty score for playing the card
     */
    private int calculatePenalty(Card card, ArrayList <Card> parade) {
        int penalty = 0;
        // simulates a new parade with card added - "pretend to play"
        ArrayList<Card> newParade = new ArrayList<>(parade);
        newParade.add(card); 
        int cardsToSkip = card.getValue();
        
        int skipIndex = newParade.size() - 1 - cardsToSkip;
        // skipIndex should be >= 0; 
        skipIndex = skipIndex > 0 ? skipIndex : 0;

        // evaluate penalty for cards in front of the skipped section
        for (int i = 0; i < skipIndex; i++) {
            Card c = newParade.get(i);
            if (c.getColour().equals(card.getColour()) || c.getValue() <= card.getValue()) {
                penalty += c.getValue(); // total value of cards you will take
            }
        }
        return penalty;
        
    }

    /**
     * Implements the end-game strategy for discarding two cards at the final stage of the game
     * The strategy prioritizes discarding cards of colors not in the player's collection, then cards with higher values.
     * 
     * @return A list containing the two cards to discard
     */
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
    
            if (c1NewColour && !c2NewColour) {
                return -1; 
            }
            if (!c1NewColour && c2NewColour) {
                return 1;
            }
            return Integer.compare(c2.getValue(), c1.getValue()); // sort in desc order, cards with higher value in front
        });
    
        List<Card> toDiscard = new ArrayList<>();
        toDiscard.add(sortedHand.get(0));
        toDiscard.add(sortedHand.get(1));
        
        hand.remove(sortedHand.get(0));
        hand.remove(sortedHand.get(1));

        return toDiscard;
    }

}


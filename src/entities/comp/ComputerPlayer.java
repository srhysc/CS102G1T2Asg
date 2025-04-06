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
        String colour = "\u001B[38;5;146m"; // Violet-ish  colour
        String reset = "\u001B[0m";
        String phrase = "";
        if (super.getName() == "AI Yeow Leong") {
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
        } else if (super.getName() == "AI Jason Chan") {
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
        } else if (super.getName() == "AI VeryEvilCuteBunny") {
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

    // public static String chooseRandomStatement() {
    //     double rand = Math.random();
    //         String result;
    //         if (rand < 0.4) {
    //             result = "AI Yeow Leong";
    //         } else if (rand < 0.8) {
    //             result = "AI Jason Chan";
    //         } else {
    //             result = "AI VeryEvilCuteBunny";
    //         }
    //     return result;
    // }

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


import java.util.ArrayList;
import java.util.Scanner;

public class ComputerPlayerTest {
    static ArrayList<Player> playerList = new ArrayList<>();
    private static Deck deck;
    private static Parade parade;
    private static TurnManager turnManager;
    private static Scanner scanner;

    public static void main(String[] args) {
        // Initialize game components
        deck = new Deck();
        parade = new Parade();
        scanner = new Scanner(System.in);

        // Add players (1 human + multiple AI)
        playerList.add(new Player("Human"));
        playerList.add(new ComputerPlayer("CPU 1"));
        playerList.add(new ComputerPlayer("CPU 2"));

        turnManager = new TurnManager(playerList.size());

        // Set game difficulty for AI
        ComputerPlayer.setGameDifficulty(Difficulty.HARD);
        System.out.println("Game difficulty set to: " + ComputerPlayer.getGameDifficulty());

        // Start the game
        startGame();
    }

    public static void startGame() {
        initializeGame();
        playTurn();
        endGame();
    }

    public static void initializeGame() {
        // Shuffle deck
        deck.shuffle();

        // Deal each player 5 cards
        for (Player player : playerList) {
            for (int i = 0; i < 5; i++) {
                player.addToHand(deck.drawCard());
            }
        }

        // Initialize parade with 6 cards
        for (int i = 0; i < 6; i++) {
            parade.addCard(deck.drawCard());
        }

        System.out.println("\nGame initialized. Parade: " + parade.getParadeRow());
    }

    public static void playTurn() {
        boolean hasAllColours = false;
        String firstPlayerWithAllColours = null;

        System.out.println("\nGame begins! First player: " + playerList.get(0).getName());

        while (!deck.isEmpty()) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
            System.out.println("\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Parade: " + parade.getParadeRow());
            System.out.println("Collected Cards: " + currentPlayer.getHand());

            if (currentPlayer instanceof ComputerPlayer) {
                // AI selects a card
                ((ComputerPlayer) currentPlayer).playComputerMove(parade.getParadeRow());
            } else {
                // Human selects a card
                System.out.println("Your hand: " + currentPlayer.getHandWithIndex());
                int cardIndex = getPlayerCardChoice(currentPlayer);
                Card playedCard = currentPlayer.playCard(cardIndex);

                // Resolve parade rules
                ArrayList<Card> takenCards = parade.removeCards(playedCard);
                parade.addCard(playedCard);
                currentPlayer.addToCollected(takenCards);
                System.out.println("You took: " + takenCards);
            }

            // Draw new card
            currentPlayer.addToHand(deck.drawCard());

            // Check if any player has all colours
            for (Player player : playerList) {
                if (player.checkColours()) {
                    hasAllColours = true;
                    firstPlayerWithAllColours = player.getName();
                    break;
                }
            }

            if (hasAllColours) {
                System.out.println("\n" + firstPlayerWithAllColours + " has collected all colours!");
                System.out.println("Commencing last round...");
                turnManager.nextTurn();
                break;
            }

            turnManager.nextTurn();
        }

        lastRound();
    }

    private static int getPlayerCardChoice(Player currentPlayer) {
        Scanner scanner = new Scanner(System.in);
        int cardIndex;
        while (true) {
            System.out.print("Choose a card index (1-" + currentPlayer.getHand().size() + "): ");
            try {
                cardIndex = scanner.nextInt() - 1;
                if (cardIndex >= 0 && cardIndex < currentPlayer.getHand().size()) {
                    break;
                }
                System.out.println("Invalid selection. Try again.");
            } catch (Exception e) {
                System.out.println("Invalid input. Enter a number.");
                scanner.next(); // Clear invalid input
            }
        }
        return cardIndex;
    }

    public static void lastRound() {
        for (int i = 0; i < playerList.size() - 1; i++) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
            System.out.println("\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Parade: " + parade.getParadeRow());
            System.out.println("Collected Cards: " + currentPlayer.getHand());

            if (currentPlayer instanceof ComputerPlayer) {
                ((ComputerPlayer) currentPlayer).playComputerMove(parade.getParadeRow());
            } else {
                System.out.println("Your hand: " + currentPlayer.getHandWithIndex());
                int cardIndex = getPlayerCardChoice(currentPlayer);
                Card playedCard = currentPlayer.playCard(cardIndex);

                // Resolve parade rules
                ArrayList<Card> takenCards = parade.removeCards(playedCard);
                parade.addCard(playedCard);
                currentPlayer.addToCollected(takenCards);
                System.out.println("You took: " + takenCards);
            }

            currentPlayer.addToHand(deck.drawCard());
            turnManager.nextTurn();
        }
    }

    public static void endGame() {
        System.out.println("\nGame Over! Calculating scores...");
        for (Player player : playerList) {
            int score = ScoringSystem.calculateScore(player);
            System.out.println(player.getName() + "'s final score: " + score);
        }
    }
}

// import java.util.ArrayList;

// public class ComputerPlayerTest {
//     public static void main(String[] args) {
//         // ComputerPlayer.setGameDifficulty(Difficulty.EASY);
//         ComputerPlayer.setGameDifficulty(Difficulty.HARD);
//         System.out.println("Game difficulty set to: " + ComputerPlayer.getGameDifficulty());

//         ComputerPlayer cpu1 = new ComputerPlayer("CPU 1");

//         // Create sample cards for the computer player's hand
//         cpu1.addToHand(new Card("Red", 5));
//         cpu1.addToHand(new Card("Blue", 5));
//         cpu1.addToHand(new Card("Green", 2));
//         cpu1.addToHand(new Card("Red", 7));
//         cpu1.addToHand(new Card("Orange", 9));

//         // Create a parade row with some existing cards
//         Parade.addCard(new Card("Red", 4));
//         Parade.addCard(new Card("Blue", 2));
//         Parade.addCard(new Card("Green", 1));
//         Parade.addCard(new Card("Orange", 5));
//         Parade.addCard(new Card("Purple", 9));
//         Parade.addCard(new Card("Grey", 0));
       
//         System.out.println(cpu1.getName() + "'s hand: " + cpu1.getHand());

//         cpu1.playComputerMove(Parade.getParadeRow());

//         System.out.println("Parade row after move: " + Parade.getParadeRow());

//         System.out.println(cpu1.getName() + "'s collected cards: " + cpu1.getCollected());
//     }
// }

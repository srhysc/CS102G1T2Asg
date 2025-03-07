import java.util.*;

public class Game {
    static ArrayList<Player> playerList = new ArrayList<>();
    private Deck deck;
    private Parade parade;
    private TurnManager turnManager;
    private Scanner scanner;

    public Game(ArrayList<Player> playerList) {
        this.playerList = playerList;
        this.deck = new Deck();
        // this.parade = Parade.Parade();
        this.turnManager = new TurnManager(playerList.size());
        this.scanner = new Scanner(System.in);
    }

    public void startGame() {
        System.out.println("Game styles");
        System.out.println("-----------");
        System.out.println("Local Play (1)");
        System.out.println("Play vs AI (2)");
        System.out.println("Play Online (3)");
        System.out.println("Choose game style:");
        int styleNumber = 0;
        Scanner sc1 = new Scanner(System.in);

        while (!(styleNumber >= 1 && styleNumber <= 3)) {
            try {
                String chosenStyle = sc1.nextLine();
                styleNumber = Integer.parseInt(chosenStyle);
                if (styleNumber < 1 || styleNumber > 3) {
                    System.out.println("Invalid game style! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid game style ! Please choose again");
            }

        }

        switch (styleNumber) {
            case (1):
                System.out.println();
                playTurn();
                break;
        }
    }

    public void playTurn() {
        System.out.println("Starting Parade...");

        // Shuffle deck and deal 5 cards to each player
        for (Player player : playerList) {
            for (int i = 0; i < 5; i++) {
                player.addToHand(deck.drawCard());
            }
        }

        // Initialize parade row with 6 cards
        for (int i = 0; i < 6; i++) {
            Parade.addCard(deck.drawCard());
        }

        boolean hasAllColours = false;
        String firstPlayerWithAllColours = null;

        System.out.println("Game begins! First player: " + playerList.get(0).getName());
        while (!deck.isEmpty()) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
            System.out.println("\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Deck num : " + deck.getSize());
            System.out.println("Parade: " + Parade.getParadeRow());
            System.out.println("Collected Cards: " + currentPlayer.getCollected());
            System.out.println("Your hand: " + currentPlayer.getHandWithIndex());

            // Player chooses a card to play
            System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
            int cardIndex = scanner.nextInt() - 1;
            while (cardIndex < 0 || cardIndex > 4) {
                System.out.println("Invalid card number");
                System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
                cardIndex = scanner.nextInt() - 1;
                System.out.println("Card index: " + cardIndex);
            }
            Card playedCard = currentPlayer.playCard(cardIndex);
            ArrayList<Card> takenCards = new ArrayList<>(parade.removeCards(playedCard));
            parade.addCard(playedCard);

            // Resolve parade rules
            currentPlayer.addToCollected(takenCards);
            System.out.println("You took: " + takenCards);

            // Draw a new card
            currentPlayer.addToHand(deck.drawCard());

            for (Player player : playerList) {
                if (player.checkColours()) {
                    hasAllColours = true;
                    firstPlayerWithAllColours = player.getName();
                    break;
                }
            }

            if (hasAllColours) {
                System.out.println();
                System.out.println();
                System.out.println(firstPlayerWithAllColours + " has all 6 colours");
                System.out.println("Commencing last round");
                // Next player's turn
                turnManager.nextTurn();
                break;
            }

            // Next player's turn
            turnManager.nextTurn();
        }
        lastRound();
    }

    public void lastRound() {
        for (int i = 0; i < playerList.size() - 1; i++) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
            System.out.println("\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Deck num : " + deck.getSize());
            System.out.println("Parade: " + Parade.getParadeRow());
            System.out.println("Collected Cards: " + currentPlayer.getCollected());
            System.out.println("Your hand: " + currentPlayer.getHandWithIndex());

            // Player chooses a card to play
            System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
            int cardIndex = scanner.nextInt() - 1;
            while (cardIndex < 0 || cardIndex > 4) {
                System.out.println("Invalid card number");
                System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
                cardIndex = scanner.nextInt() - 1;
                System.out.println("Card index: " + cardIndex);
            }
            Card playedCard = currentPlayer.playCard(cardIndex);
            ArrayList<Card> takenCards = new ArrayList<>(parade.removeCards(playedCard));
            parade.addCard(playedCard);

            // Resolve parade rules
            currentPlayer.addToCollected(takenCards);
            System.out.println("You took: " + takenCards);

            // Draw a new card
            currentPlayer.addToHand(deck.drawCard());

            turnManager.nextTurn();
        }
        endGame();
    }

    public static void checkGameEnd(boolean allColoursCollected) {
        // if (allColoursCollected || ) {
        // this.playTurn();

        // }
    }

    public static boolean checkGameEnd() {
        boolean gameEnd = false;

        // for (Player player : playerList) {
        // if (player.allColoursCollected()) {
        // gameEnd = true;
        // break;
        // }
        // }

        // if (gameEnd || deck.getCards() == 0) {
        // System.out.println("This is the last round!");
        // playTurn(); // Last Round to end up with 4 cards, I need to see how the play
        // turn is being handled
        // return true;
        // }

        return false;
    }

    public void endGame() {
        System.out.println("\nGame Over! Calculating scores...");
        for (Player player : playerList) {
            int score = ScoringSystem.calculateScore(player);
            System.out.println(player.getName() + "1's final score: " + score);
        }
    }

    public static void main(String[] args) {
        ArrayList<Player> playerList = new ArrayList<>();
        playerList.add(new Player("Alice"));
        playerList.add(new Player("Bob"));
        // playerList.add(new Player("Charlie"));

        Game game = new Game(playerList);
        game.startGame();
    }
}

package game;
import game.online.SocketPacket;
import entities.*;
import entities.scoring.*;

import java.io.*;
import java.util.*;

import entities.comp.ComputerPlayer;
import game.online.*;

public class GameLogic {

    private static Scanner sc = new Scanner(System.in);
    private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    public static void initalizeGame(Deck deck, ArrayList<Player> playerList) {
        // shuffle deck

        deck.shuffle();

        // deal each player 5 cards
        for (Player player : playerList) {
            for (int i = 0; i < 5; i++) {
                player.addToHand(deck.drawCard());
            }
        }

        // create parade
        // Initialize parade row with 6 cards
        for (int i = 0; i < 6; i++) {
            Parade.addCard(deck.drawCard());
        }
        System.out.println("game initalized");
    }

   

    public static void playTurn(Deck deck, ArrayList<Player> playerList, TurnManager turnManager,
            boolean isTwoPlayerGame) {
        System.out.println("Starting Parade...");

        initalizeGame(deck, playerList);

        boolean hasAllColours = false;
        String firstPlayerWithAllColours = null;

        System.out.println("Game begins! First player: " + playerList.get(0).getName());
        while (!deck.isEmpty()) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
            System.out.println("\n\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Deck num : " + deck.getSize());
            System.out.println("Parade: " + Parade.getParadeRow());
            if (currentPlayer instanceof ComputerPlayer) {

                
                Card playedCard = ((ComputerPlayer) currentPlayer).playComputerMove(Parade.getParadeRow());
                ArrayList<Card> takenCards = Parade.removeCards(playedCard);
                Parade.addCard(playedCard);
                currentPlayer.addToCollected(takenCards);
                System.out.println(currentPlayer.getName() + " took: " + takenCards);
            } else {

                System.out.println("Collected Cards: " + currentPlayer.getCollected());
                System.out.println("Your hand: " + currentPlayer.getHandWithIndex());

                // Player chooses a card to play
                System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
                int cardIndex = sc.nextInt() - 1;
                while (cardIndex < 0 || cardIndex > 4) {
                    System.out.println("Invalid card number! Please choose again");
                    System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
                    cardIndex = sc.nextInt() - 1;
                    System.out.println("Card index: " + cardIndex);
                }
                Card playedCard = currentPlayer.playCard(cardIndex);
                ArrayList<Card> takenCards = new ArrayList<>(Parade.removeCards(playedCard));
                Parade.addCard(playedCard);

                // Resolve parade rules
                currentPlayer.addToCollected(takenCards);
                System.out.println("You took: " + takenCards);
            }

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
        lastRound(deck, playerList, turnManager, isTwoPlayerGame);
    }

    public static void lastRound(Deck deck, ArrayList<Player> playerList, TurnManager turnManager,
            boolean isTwoPlayerGame) {
        for (int i = 0; i < playerList.size(); i++) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
            System.out.println("\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Deck num : " + deck.getSize());
            System.out.println("Parade: " + Parade.getParadeRow());

            // Player chooses a card to play
            if (currentPlayer instanceof ComputerPlayer) {
                Card playedCard = ((ComputerPlayer) currentPlayer).playComputerMove(Parade.getParadeRow());
                ArrayList<Card> takenCards = Parade.removeCards(playedCard);
                Parade.addCard(playedCard);
                currentPlayer.addToCollected(takenCards);
                System.out.println(currentPlayer.getName() + " took: " + takenCards);
            } else {
                System.out.println("Collected Cards: " + currentPlayer.getCollected());
                System.out.println("Your hand: " + currentPlayer.getHandWithIndex());
                System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
                int cardIndex = sc.nextInt() - 1;
                while (cardIndex < 0 || cardIndex > 4) {
                    System.out.println("Invalid card number! Please choose again");
                    System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
                    cardIndex = sc.nextInt() - 1;
                    System.out.println("Card index: " + cardIndex);
                }
                Card playedCard = currentPlayer.playCard(cardIndex);
                ArrayList<Card> takenCards = new ArrayList<>(Parade.removeCards(playedCard));
                Parade.addCard(playedCard);

                // Resolve parade rules
                currentPlayer.addToCollected(takenCards);
                System.out.println("You took: " + takenCards);
            }           

            // Draw a new card
            currentPlayer.addToHand(deck.drawCard());

            turnManager.nextTurn();
        }
        endGame(playerList, isTwoPlayerGame);
    }



    public static void endGame(ArrayList<Player> playerList, boolean isTwoPlayerGame) {
        System.out.println("\nGame Over! Calculating scores...");

        // Calculate scores and determine the winner
        Player winner = Scoring.calculateScores(playerList, isTwoPlayerGame);
        System.out.println();
        System.out.println();

        if (winner != null) {
            // Update high scores
            HighScoreDatabase highScoreDatabase = new HighScoreDatabase();
            highScoreDatabase.updateHighScore(winner.getName());
        }

        // Bring back to the main menu
        Game.main(null);
    }

    // Clear the console screen
    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    //to deal with players entering values when it isnt their turn 
    public static void flushInputBuffer() {
        try {
            // Check if there is input available in the buffer
            while (System.in.available() > 0) {
                System.in.read(); // Read and discard the input
            }
        } catch (IOException e) {
            System.err.println("Error while flushing input buffer: " + e.getMessage());
        }
    }
}

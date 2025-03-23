import java.io.*;
import java.util.*;
import java.net.*;


public class GameLogic {
    
    private static Scanner sc = new Scanner(System.in);
    private static BufferedReader stdIn =  new BufferedReader(new InputStreamReader(System.in));

    private static void initalizeGame(Deck deck, ArrayList<Player> playerList){
        //shuffle deck 

        deck.shuffle();

        //deal each player 5 cards
        for (Player player : playerList) {
            for (int i = 0; i < 5; i++) {
                player.addToHand(deck.drawCard());
            }
        }

        //create parade 
        // Initialize parade row with 6 cards
        for (int i = 0; i < 6; i++) {
            Parade.addCard(deck.drawCard());
        }
        System.out.println("game initalized");
    }

    public static void playOnlineTurn(Deck deck, ArrayList<Player> playerList, TurnManager turnManager, boolean isTwoPlayerGame, List<ObjectOutputStream> outputs, List<ObjectInputStream> inputs, Scanner sc) {
        initalizeGame(deck, playerList);

        System.out.println("Starting Parade...");
        boolean hasAllColours = false;
        String firstPlayerWithAllColours = null;


        System.out.println("Game begins! First player: " + playerList.get(0).getName());
        Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
        //to manage player disconnects
        while(!GameServer.playersSockets.isEmpty()){
            
            try{
                

                //rotate turns 

                //every turn send this set 
                    // Current board to current player
                    // friends boards to other players 


                //player index 0 is alw the local host 

                while (!deck.isEmpty()) {
                    
                    for (Player player : playerList){
                        if(player.getOutputSteam() != null){
                            ObjectOutputStream o = player.getOutputSteam();
                            o.writeObject(currentPlayer.getName());  //test it is this guys turns
                            o.flush();


                            //so after this ^ all players will get a message of the current players name 
                            //and will accordingly print it is "your turn" or someone elses turn 

                            //will pause the code and wait for the input from the client side
                            String inputChoice = (String) currentPlayer.getInputSteam().readObject();

                            broadcastToAll(playerList, inputChoice);
                            break;
                            
                        }
                        else{//local
                            
                            String move = sc.nextLine();
                            broadcastToAll(playerList, move);
                            break;
                            
                        }
                    }

                    

                    
                    
                    // Switch to next player
                    turnManager.nextTurn();
                    currentPlayer = playerList.get(turnManager.getCurrentPlayer());
                    
                    continue;
                }


            }
            //manage player disconnects
            catch (IOException | ClassNotFoundException e) {
                System.out.println("Player " + currentPlayer.getName() + " disconnected.");

                // Remove disconnected player
                int currentPlayerIndex = playerList.indexOf(currentPlayer);
                if (currentPlayerIndex > 0) { // Don't remove the server
                    GameServer.playersSockets.remove(currentPlayerIndex);
                    outputs.remove(currentPlayerIndex);
                    inputs.remove(currentPlayerIndex);
                    playerList.remove(currentPlayerIndex);
                }

                // If no players left, end the game
                if (GameServer.playersSockets.isEmpty()) {
                    System.out.println("All players disconnected. Ending game.");
                    break;
                }

                // Adjust current player index
                turnManager.nextTurn();
                currentPlayer = playerList.get(turnManager.getCurrentPlayer());
                //currentPlayer = currentPlayer % (GameServer.playersSockets.size() + 1);
            }
            

        }

    }

    public static void broadcastToAll(ArrayList<Player> playerList, String message) throws IOException{
            //broadcast move to all players
            for(Player player : playerList){
                if(player.getOutputSteam() != null){
                    ObjectOutputStream o = player.getOutputSteam();
                    o.writeObject(message);  //some guys move 
                    o.flush();
                }
            }
    }
   

    public static void playTurn(Deck deck, ArrayList<Player> playerList, TurnManager turnManager, boolean isTwoPlayerGame) {
        System.out.println("Starting Parade...");

        initalizeGame(deck, playerList);
  

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
            int cardIndex = sc.nextInt() - 1;
            while (cardIndex < 0 || cardIndex > 4) {
                System.out.println("Invalid card number");
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

    public static void lastRound(Deck deck, ArrayList<Player> playerList, TurnManager turnManager, boolean isTwoPlayerGame) {
        for (int i = 0; i < playerList.size() - 1; i++) {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
            System.out.println("\n" + currentPlayer.getName() + "'s turn!");
            System.out.println("Deck num : " + deck.getSize());
            System.out.println("Parade: " + Parade.getParadeRow());
            System.out.println("Collected Cards: " + currentPlayer.getCollected());
            System.out.println("Your hand: " + currentPlayer.getHandWithIndex());

            // Player chooses a card to play
            System.out.print("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
            int cardIndex = sc.nextInt() - 1;
            while (cardIndex < 0 || cardIndex > 4) {
                System.out.println("Invalid card number");
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

            // Draw a new card
            currentPlayer.addToHand(deck.drawCard());

            turnManager.nextTurn();
        }
        endGame(playerList, isTwoPlayerGame);
    }

    public static void endGame(ArrayList<Player> playerList, boolean isTwoPlayerGame) {
        System.out.println("\nGame Over! Calculating scores...");
        // for (Player player : playerList) {
        //     int score = ScoringSystem.calculateScore(player);
        //     System.out.println(player.getName() + "1's final score: " + score);
        // }
        Scoring.calculateScores(playerList, isTwoPlayerGame);


        //bring back to main menu 
        
        Game.main(null);
        
    }

}

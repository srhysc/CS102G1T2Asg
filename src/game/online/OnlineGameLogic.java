package game.online;


import entities.*;
import entities.scoring.*;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import game.Game;
import game.GameLogic;
import game.TurnManager;


public class OnlineGameLogic {

    private static final String colourRed = "\u001B[31m";
    private static final String reset = "\u001B[0m";

    public static void playOnlineTurn(Deck deck, ArrayList<Player> playerList, TurnManager turnManager,
            boolean isTwoPlayerGame, List<ObjectOutputStream> outputs, List<ObjectInputStream> inputs, Scanner sc) {

            
            GameLogic.initalizeGame(deck, playerList);

            // for testing
            for (int i = 0; i < 40; i++) {
                deck.drawCard();
            }

           
            int moveIndex = 0;
            boolean hasAllColours = false;
            String firstPlayerWithAllColours = null;
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
           
            while (!GameServer.playersSockets.isEmpty()) {

                try { // to manage player disconnects

                    //Ensure that on the new turn there are cards in the deck 
                    while (!deck.isEmpty()) {
                        clearConsole();
                        StringBuilder menuMessage = new StringBuilder();

                        //display menu message to all
                        menuMessage = buildMenuMessage(deck, currentPlayer);
                        SocketPacket sp = new SocketPacket(menuMessage, currentPlayer.getName(), 1, playerList);
                        broadcastToAll(playerList, sp);


                        //player input
                        if (currentPlayer.getInputSteam() == null) {
                            
                            moveIndex = getValidMoveIndex(sc, currentPlayer);  //get local player move 

                            //send movement announcement to all
                            sp = new SocketPacket(
                                new StringBuilder("Player " + currentPlayer.getName() + " made the move " + moveIndex),
                                currentPlayer.getName(),
                                0,
                                playerList
                            );

                            broadcastToAll(playerList, sp);


                        } else {
                            SocketPacket moveResponse = (SocketPacket) currentPlayer.getInputSteam().readObject();  //wait and read online game input

                            flushInputBuffer();  //prevent accidental input values

                            // broadcast player move
                            if (moveResponse.getMessageType() == 2) {
                                moveIndex = Integer.parseInt(moveResponse.getSb().toString());
                                
                            }

                        }

                        // recieve move and play
                        Card playedCard = currentPlayer.playCard(moveIndex);
                        ArrayList<Card> takenCards = new ArrayList<>(Parade.removeCards(playedCard));
                        Parade.addCard(playedCard);

                        // Resolve parade rules
                        currentPlayer.addToCollected(takenCards);
                        sp = new SocketPacket(
                                new StringBuilder("Player " + currentPlayer.getName() + " took " + takenCards),
                                currentPlayer.getName(), 0, playerList);
                        broadcastToAll(playerList, sp);

                        // Draw a new card
                        currentPlayer.addToHand(deck.drawCard());

                        //check for game ending situations 
                        for (Player player : playerList) {
                            if (player.checkColours()) {
                                hasAllColours = true;
                                firstPlayerWithAllColours = player.getName();
                                break;
                            }
                        }
                        if (hasAllColours) {

                            StringBuilder lastRoundAnnouncement = new StringBuilder();
                            lastRoundAnnouncement.append(
                                    "\n\n " + firstPlayerWithAllColours + " has all 6 colours \n Commencing last round \n");
                            SocketPacket lastRoundPacket = new SocketPacket(lastRoundAnnouncement, currentPlayer.getName(),
                                    0, playerList);
                            broadcastToAll(playerList, lastRoundPacket);

                            // Next player's turn
                            turnManager.nextTurn();
                            break;
                        }

                        // Switch to next player
                        turnManager.nextTurn();
                        currentPlayer = playerList.get(turnManager.getCurrentPlayer());

                        continue;
                    }

                    // online last round
                    onlineLastRound(deck, playerList, turnManager, isTwoPlayerGame, outputs, inputs, sc);
                }
                // manage player disconnects
                catch (IOException | ClassNotFoundException e) {

                    if (handlePlayerDisconnection(currentPlayer, playerList, outputs, inputs)){
                        // Adjust current player index
                        turnManager.nextTurn();
                        currentPlayer = playerList.get(turnManager.getCurrentPlayer());
                    }
                    else{
                        break; //stop game
                    }
                }

            }

        }

        

        public static void onlineLastRound(Deck deck, ArrayList<Player> playerList, TurnManager turnManager,
                boolean isTwoPlayerGame,
                List<ObjectOutputStream> outputs, List<ObjectInputStream> inputs, Scanner sc)
                throws IOException, ClassNotFoundException {
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());

            while (!GameServer.playersSockets.isEmpty()) {
                try {
                    
                    for (int i = 0; i < playerList.size(); i++) {
                        currentPlayer = playerList.get(turnManager.getCurrentPlayer());

                        // build final move request
                        StringBuilder moveRequest = new StringBuilder();
                        
                        
                        moveRequest.append("\n " + colourRed + "Final Round!!" + reset + "\n");
                        moveRequest.append(buildMenuMessage(deck, currentPlayer).toString());

                        SocketPacket lastMoveSp = new SocketPacket(moveRequest, currentPlayer.getName(), 1, playerList);
                        clearConsole();
                        broadcastToAll(playerList, lastMoveSp);


                        int moveIndex = 0;
                        // handle input
                        // if current player is the local host
                        if (currentPlayer.getInputSteam() == null) {

                            moveIndex = getValidMoveIndex(sc, currentPlayer);


                        } else {

                            // online player
                            SocketPacket moveResponse = (SocketPacket) currentPlayer.getInputSteam().readObject();

                            flushInputBuffer();
                            
                            // read the move
                            if (moveResponse.getMessageType() == 2) {

                                // input validation was done in the client side
                                moveIndex = Integer.parseInt(moveResponse.getSb().toString());


                                

                                
                            }

                        }

                        Card playedCard = currentPlayer.playCard(moveIndex);
                        ArrayList<Card> takenCards = new ArrayList<>(Parade.removeCards(playedCard));
                        Parade.addCard(playedCard);
                        // Resolve parade rules
                        currentPlayer.addToCollected(takenCards);
                        System.out.println("You took: " + takenCards);


                        String takenCardsString = takenCards.stream().
                                                            map(card -> card.toString()).
                                                            collect(Collectors.joining(", "));

                        SocketPacket sp = new SocketPacket(new StringBuilder("Player " +
                                currentPlayer.getName()
                                + " took the cards " + takenCardsString),
                                currentPlayer.getName(), 0,
                                playerList);

                        broadcastToAll(playerList, sp);


                        // Draw a new card
                        currentPlayer.addToHand(deck.drawCard());
                        turnManager.nextTurn();

                    }

                    // break when all turns played and go to end round
                    break;
                } // manage player disconnects
                catch (IOException | ClassNotFoundException e) {

                    if (handlePlayerDisconnection(currentPlayer, playerList, outputs, inputs)){
                        // Adjust current player index
                        turnManager.nextTurn();
                        currentPlayer = playerList.get(turnManager.getCurrentPlayer());
                    }
                    else{
                        break; //stop game
                    }

                
                }
            }

            endGameOnline(playerList, isTwoPlayerGame, turnManager, outputs, inputs, sc);

        }

        //settles the removal of 2 cards 
        public static void endGameOnline(ArrayList<Player> playerList, boolean isTwoPlayerGame, TurnManager turnManager,
                List<ObjectOutputStream> outputs, List<ObjectInputStream> inputs , Scanner sc)
                throws IOException, ClassNotFoundException {

            

            // build move request string
            StringBuilder sb = new StringBuilder();
            Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());

            while (true) {
                try {
                    // last round but structured for socket lan game
                    for (int i = 0; i < playerList.size(); i++) {
                        currentPlayer = playerList.get(turnManager.getCurrentPlayer());

                        sb = new StringBuilder();

                        clearConsole();

                        sb.append("\n" + currentPlayer.getName() + ", here are the cards in hand: \n");

                        ArrayList<Card> hand = currentPlayer.getHand();

                        for (int handIndex = 0; handIndex < hand.size(); handIndex++) {
                            sb.append((handIndex + 1) + ": " + hand.get(handIndex).getDetails() + "\n");
                        }

                        // broadcast to discard cards
                        SocketPacket sp = new SocketPacket(sb, currentPlayer.getName(), 4, playerList);
                        broadcastToAll(playerList, sp);

                        if (currentPlayer.getInputSteam() == null) {
                            System.out.println("Please discard 2 cards");

                            while (true) {

                                int firstCardIndex = Scoring.getValidCardIndex(sc, hand.size(),
                                        "Choose the number of the 1st card to discard: ");
                                int secondCardIndex = Scoring.getValidCardIndex(sc, hand.size(),
                                        "Choose the number of the 2nd card to discard: ");

                                if (firstCardIndex != secondCardIndex) {

                                    //to deal with hand index moving down 
                                    Card c1 = hand.get(firstCardIndex);
                                    Card c2 = hand.get(secondCardIndex);

                                    hand.remove(c1);
                                    hand.remove(c2);

                                    currentPlayer.addToCollected(hand);
                                    turnManager.nextTurn();
                                    break;
                                } else {
                                    System.out.println("Please do not choose 2 of the same index");
                                    continue;
                                }
                            }

                        } else {

                           
                            // this move reply should be in the form of "1,2"
                            SocketPacket moveResponse = (SocketPacket) currentPlayer.getInputSteam().readObject();

                            clearConsole();
                            flushInputBuffer();

                            // split
                            String responseString = moveResponse.getSb().toString();
                            String[] responses = responseString.split(",");

                            //to deal with hand index moving down 
                            Card c1 = hand.get(Integer.parseInt(responses[1]));
                            Card c2 = hand.get(Integer.parseInt(responses[0]));

                            hand.remove(c1);
                            hand.remove(c2);
                            

                            currentPlayer.addToCollected(hand);
                            turnManager.nextTurn();
                        }
                    }
                    break;
                } catch (IOException | ClassNotFoundException e) {

                    if (handlePlayerDisconnection(currentPlayer, playerList, outputs, inputs)){
                        // Adjust current player index
                        turnManager.nextTurn();
                        currentPlayer = playerList.get(turnManager.getCurrentPlayer());
                    }
                    else{
                        break; //stop game
                    }
                    
                }

            }
            // Calculate scores and determine the winner
            // Player winner = Scoring.calculateOnlineScores(playerList, isTwoPlayerGame);
            SocketPacket sp = Scoring.calculateOnlineScores(playerList, isTwoPlayerGame);
            System.out.println();
            System.out.println();

            broadcastToAll(playerList, sp);

            System.out.println("returning to main menu....");


            flushInputBuffer();

            sc.nextLine();
            GameServer.serverSocket.close();
            // Bring back to the main menu
            Game.main(null);

    }


    public static void broadcastToAll(ArrayList<Player> playerList, SocketPacket sp) throws IOException {
        // broadcast move to all players


        for (Player player : playerList) {
            if (player.getOutputSteam() != null) {
                ObjectOutputStream o = player.getOutputSteam();

                o.writeObject(sp); // some guys move
                o.flush();
            } else {

                int messageType = sp.getMessageType(); 

                switch (messageType) {
                    case 0: //announcement 
                        System.err.println(sp.getSb().toString());
                        break;

                    case 3: //game over
                        System.err.println(sp.getSb().toString());
                        break;
                
                    default:
                        if (!(sp.getCurrentPlayer().equals(player.getName()))){
                            System.err.println(sp.getSb().toString());
                            System.out.println("Please wait for your turn!");
                        }
                        else{
                            System.err.println(sp.getSb().toString());
                        }
                        break;
                }

               
                
                
            }
        }
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

    private static boolean handlePlayerDisconnection(Player currentPlayer, ArrayList<Player> playerList, List<ObjectOutputStream> outputs, List<ObjectInputStream> inputs){

        System.out.println("Player " + currentPlayer.getName() + " disconnected.");

        // Remove disconnected player
        int currentPlayerIndex = playerList.indexOf(currentPlayer);
        if (currentPlayerIndex > 0) { // Don't remove the server
            GameServer.playersSockets.remove(currentPlayerIndex);
            outputs.remove(currentPlayerIndex);
            inputs.remove(currentPlayerIndex);
            playerList.remove(currentPlayerIndex);
            return true;
        }
        // If no players left, end the game
        if (GameServer.playersSockets.isEmpty()) {
            System.out.println("All players disconnected. Ending game.");
            Game.main(null);
            return false;
        }
        return true;
        
    }

    private static StringBuilder buildMenuMessage(Deck deck, Player currentPlayer) {
        StringBuilder menuMessage = new StringBuilder();
        menuMessage.append("--------------------\n");
        menuMessage.append("Deck num       : " + deck.getSize() + "\n");
        menuMessage.append("Parade         : " + Parade.getParadeRow() + "\n");
        menuMessage.append("Current player : " + currentPlayer.getName() + "\n");
        menuMessage.append("Collected Cards: " + currentPlayer.getCollected() + "\n");
        menuMessage.append("Your hand      : " + currentPlayer.getHandWithIndex() + "\n");
        menuMessage.append("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): " + "\n");
        menuMessage.append("--------------------\n");
        return menuMessage;
    }

    private static boolean validateGameInput(String input, Player currentPlayer){
        int moveIndex = 0;
        Objects.requireNonNullElse(input, -1);
        try {
            moveIndex = Integer.parseInt(input) - 1;

            if (moveIndex >= 0 && moveIndex < currentPlayer.getHand().size()) {
                return true;
            } else {
                System.out.println("Invalid card number! Please choose again");
                return false;
            }
        } catch (NumberFormatException | InputMismatchException e) {// user puts in alphabetical
            System.out.println("Invalid card number! Please choose again");
            return false; 
        }
    }

    private static int getValidMoveIndex(Scanner sc, Player currentPlayer){
        while (true) {
                            
            System.out.println("Please enter the move");
            String input = sc.nextLine(); 
            
            if(validateGameInput(input, currentPlayer)){

                return Integer.parseInt(input) - 1; 
            }
            else{
                System.out.println("Invalid card number! Please choose again");
            }
        }
    }

    // Clear the console screen
    private static void clearConsole() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    
}

import java.io.*;
import java.net.Socket;
import java.util.*;

public class GameLogic {

    private static Scanner sc = new Scanner(System.in);
    private static BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

    private static void initalizeGame(Deck deck, ArrayList<Player> playerList) {
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

    public static void playOnlineTurn(Deck deck, ArrayList<Player> playerList, TurnManager turnManager,
            boolean isTwoPlayerGame, List<ObjectOutputStream> outputs, List<ObjectInputStream> inputs, Scanner sc) {
        initalizeGame(deck, playerList);

        System.out.println("Starting Parade...");
        boolean hasAllColours = false;
        String firstPlayerWithAllColours = null;

        System.out.println("Game begins! First player: " + playerList.get(0).getName());
        Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());
        // to manage player disconnects
        while (!GameServer.playersSockets.isEmpty()) {
            
          

            try {

                // rotate turns

                // every turn send this set
                // Current board to current player
                // friends boards to other players

                // player index 0 is alw the local host

                while (!deck.isEmpty()) {
                    clearConsole();
                    StringBuilder menuMessage = new StringBuilder();

                    // build the message to be sent to the players
                    menuMessage.append("--------------------\n");
                    menuMessage.append("0. Deck num : " + deck.getSize() + "\n");
                    menuMessage.append("1. Parade: " + Parade.getParadeRow() + "\n");
                    menuMessage.append("Current player: " + currentPlayer.getName() + "\n");
                    menuMessage.append("Collected Cards: " + currentPlayer.getCollected() + "\n");
                    menuMessage.append("Your hand: " + currentPlayer.getHandWithIndex() + "\n");
                    menuMessage.append("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): " + "\n");
                    menuMessage.append("--------------------\n");

                    for (int i = 0; i < playerList.size(); i++) {

                        Player player = playerList.get(i);

                        if (player.getOutputSteam() != null) {
                            ObjectOutputStream o = player.getOutputSteam();

                            SocketPacket sp = new SocketPacket(menuMessage, currentPlayer.getName(), 1, playerList);

                            o.writeObject(sp); // test it is this guys turns
                            o.flush();

                            // so after this ^ all players will get a message of the current players name
                            // and will accordingly print it is "your turn" or someone elses turn

                            // will pause the code and wait for the input from the client side
                            // if its this players turn wait

                        } else {// local

                            if (player != currentPlayer) {
                                System.out.println("It's players " + currentPlayer + " turn now. Please wait!");
                                
                            } else {
                                System.out.println("It's your turn ");
                                System.err.println(menuMessage.toString());
                                // SocketPacket sp = new SocketPacket(new StringBuilder(menuMessage),
                                // currentPlayer.getName());
                                // broadcastToAll(playerList, sp);
                            }
                        }
                    }

                    // System.out.println("Current player" + currentPlayer.getName());
                    // after all the instructions have been written to the players wait for current
                    // players turn
                    int moveIndex = 0;
                    if (currentPlayer.getInputSteam() == null) {
                        
                        System.out.println("Please enter the move");

                        while (true) {
                            // System.out.print("Choose a card index (1-" + currentPlayer.getHand().size() +
                            // "): ");
                            
                            String input = sc.nextLine(); // Read input as a string to validate

                            try {
                                moveIndex = Integer.parseInt(input) - 1;

                                if (moveIndex >= 0 && moveIndex < currentPlayer.getHand().size()) {
                                    break;// valid input
                                } else {
                                    System.out.println("Invalid card number! Please choose again");
                                }
                            } catch (NumberFormatException | InputMismatchException e) {// user puts in alphabetical
                                System.out.println("Invalid card number! Please choose again");
                            }
                        }

                        // input validation to make sure that input choice is numeric and less not less
                        // than 0 or not more than 4

                        SocketPacket sp = new SocketPacket(
                                new StringBuilder("Player " + currentPlayer.getName() + " made the move " + moveIndex),
                                currentPlayer.getName(), 0, playerList);
                        broadcastToAll(playerList, sp);
                    } else {
                        // here is the recieveng socket packet from the player

                        
                        SocketPacket moveResponse = (SocketPacket) currentPlayer.getInputSteam().readObject();
                        
                        flushInputBuffer();
                        
                        
                        // read the move
                        if (moveResponse.getMessageType() == 2) {

                            // input validation was done in the client side
                            moveIndex = Integer.parseInt(moveResponse.getSb().toString());

                            SocketPacket sp = new SocketPacket(new StringBuilder("Player " + currentPlayer.getName()
                                    + " made the move " + moveResponse.getSb().toString()), currentPlayer.getName(), 0,
                                    playerList);
                            broadcastToAll(playerList, sp);
                        }

                    }

                    // recieve move and play
                    Card playedCard = currentPlayer.playCard(moveIndex);
                    ArrayList<Card> takenCards = new ArrayList<>(Parade.removeCards(playedCard));
                    Parade.addCard(playedCard);

                    // Resolve parade rules
                    currentPlayer.addToCollected(takenCards);
                    SocketPacket sp = new SocketPacket(
                            new StringBuilder("Player " + currentPlayer.getName() + " took " + takenCards),
                            currentPlayer.getName(), 0, playerList);
                    broadcastToAll(playerList, sp);

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

                //online last round
                onlineLastRound(deck, playerList, turnManager, isTwoPlayerGame, outputs, inputs, sc);
            }
            // manage player disconnects
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
                    Game.main(null);
                    break;
                }

                // Adjust current player index
                turnManager.nextTurn();
                currentPlayer = playerList.get(turnManager.getCurrentPlayer());
                // currentPlayer = currentPlayer % (GameServer.playersSockets.size() + 1);
            }

        }

    }

    public static void broadcastToAll(ArrayList<Player> playerList, SocketPacket sp) throws IOException {
        // broadcast move to all players
        for (Player player : playerList) {
            if (player.getOutputSteam() != null) {
                ObjectOutputStream o = player.getOutputSteam();

                o.writeObject(sp); // some guys move
                o.flush();
            } else {
                System.err.println(sp.getSb().toString());
            }
        }
    }

    public static void onlineLastRound(Deck deck, ArrayList<Player> playerList, TurnManager turnManager, boolean isTwoPlayerGame,
     List<ObjectOutputStream> outputs, List<ObjectInputStream> inputs, Scanner sc) throws IOException, ClassNotFoundException{
        Player currentPlayer = playerList.get(turnManager.getCurrentPlayer());

        while(true){
            try {
                //last round but structured for socket lan game
                for (int i = 0; i < playerList.size(); i++) {
                    currentPlayer = playerList.get(turnManager.getCurrentPlayer());
                    System.out.println("\n" + currentPlayer.getName() + "'s turn!");
                    System.out.println("Deck num : " + deck.getSize());
                    System.out.println("Parade: " + Parade.getParadeRow());
    
                    //build move request
                    StringBuilder moveRequest = new StringBuilder();
    
                    moveRequest.append("\nCollectted Cards: " + currentPlayer.getCollected());
                    moveRequest.append("\n Your hand: " + currentPlayer.getHandWithIndex());
                    moveRequest.append("Choose a card index (1-" + (currentPlayer.getHand().size()) + "): ");
    
                    SocketPacket sp = new SocketPacket(moveRequest, currentPlayer.getName() , 2, playerList);
    
                    broadcastToAll(playerList, sp);
    
                    //handle input 
                    //if current player is the local host
                    if (currentPlayer.getInputSteam() == null){
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
                    else{
                        //online player
    
                        SocketPacket moveResponse = (SocketPacket) currentPlayer.getInputSteam().readObject();
                                
                        flushInputBuffer();
                        
                        int moveIndex = 0;
                        // read the move
                        if (moveResponse.getMessageType() == 2) {
    
                            // input validation was done in the client side
                            moveIndex = Integer.parseInt(moveResponse.getSb().toString());
                            
                            Card playedCard = currentPlayer.playCard(moveIndex);
                            ArrayList<Card> takenCards = new ArrayList<>(Parade.removeCards(playedCard));
                            Parade.addCard(playedCard);
    
                            // Resolve parade rules
                            currentPlayer.addToCollected(takenCards);
                            System.out.println("You took: " + takenCards);
    
    
                            // SocketPacket sp = new SocketPacket(new StringBuilder("Player " + currentPlayer.getName()
                            //         + " made the move " + moveResponse.getSb().toString()), currentPlayer.getName(), 0,
                            //         playerList);
                            // broadcastToAll(playerList, sp);
                        }
    
    
                    }
    
                    // Draw a new card
                    currentPlayer.addToHand(deck.drawCard());
    
                    turnManager.nextTurn();
    
                }

                //break when all turns played and go to end round
                break;
            } // manage player disconnects
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
                    Game.main(null);
                    break;
                    
                }
    
                // Adjust current player index
                turnManager.nextTurn();
                currentPlayer = playerList.get(turnManager.getCurrentPlayer());
                // currentPlayer = currentPlayer % (GameServer.playersSockets.size() + 1);
            }
        }
        
        
        endGameOnline(playerList, isTwoPlayerGame, turnManager);


    }

    public static void endGameOnline(ArrayList<Player> playerList, boolean isTwoPlayerGame, TurnManager turnManager) throws IOException, ClassNotFoundException {
        System.out.println("\nGame Over! Calculating scores...");

        //TODO ask the players to remove 2 cards each 

        //build move request string
        StringBuilder sb = new StringBuilder();
        for(Player player : playerList){
            //clear stringbuilder 
            sb.setLength(0);

            sb.append("\n" + player.getName() + ", here are the cards in hand: \n");
            ArrayList<Card> hand = player.getHand();
            for (int i = 0; i < hand.size(); i++) {
                sb.append((i + 1) + ": " + hand.get(i).getDetails() + "\n");
            }

            //broadcast to discard first card 
            sb.append("\n Please discard 2 cards\n");

            SocketPacket sp = new SocketPacket(sb, player.getName(), 4, playerList);
            broadcastToAll(playerList, sp);
            //handle local or socket 

            if(player.getInputSteam() == null){
                int firstCardIndex = Scoring.getValidCardIndex(sc, hand.size(), "Choose the number of the 1st card to discard: ");
                int secondCardIndex = Scoring.getValidCardIndex(sc, hand.size(), "Choose the number of the 2nd card to discard: ");
                
                while(true){
                    if(firstCardIndex != secondCardIndex){
                        Card discardedFirstCard = hand.remove(firstCardIndex);
                        Card discardedSecondCard = hand.remove(secondCardIndex);
                        player.addToCollected(hand);
                        break;
                    }
                    else{
                        System.out.println("Please do not choose 2 of the same index");
                    }
                }
                
                
            }
            else{
                //this move reply should be in the form of "1,2"
                SocketPacket moveResponse = (SocketPacket) player.getInputSteam().readObject();
                        
                flushInputBuffer();

                //split 
                String responseString = moveResponse.getSb().toString();
                String[] responses = responseString.split(",");

                Card discardedFirstCard = hand.remove(Integer.parseInt(responses[0]));
                Card discardedSecondCard = hand.remove(Integer.parseInt(responses[1]));
                
                player.addToCollected(hand);

            }


        }

        // Calculate scores and determine the winner
        //Player winner = Scoring.calculateOnlineScores(playerList, isTwoPlayerGame);
        SocketPacket sp = Scoring.calculateOnlineScores(playerList, isTwoPlayerGame);
        System.out.println();
        System.out.println(); 

        broadcastToAll(playerList, sp);

        System.out.println("Press enter to go back to main menu and close the server");

        GameServer.serverSocket.close();
        // Bring back to the main menu
        Game.main(null);
        
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
            System.out.println();
            System.out.println();
            System.out.println(currentPlayer.getName() + "'s turn!");
            System.out.println("Deck num : " + deck.getSize());
            System.out.println("Parade: " + Parade.getParadeRow());
            if (currentPlayer instanceof ComputerPlayer) {

                // System.out.println("Collected Cards: " + currentPlayer.getCollected());
                // System.out.println(currentPlayer.getName() + "'s hand: " +
                // currentPlayer.getHandWithIndex());
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

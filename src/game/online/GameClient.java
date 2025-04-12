package game.online;
import entities.Card;
import entities.scoring.Scoring;
import game.GameLogic;
import game.Game;

import java.io.*;
import java.io.ObjectInputFilter.Config;
import java.net.*;
import java.util.*;

/**
 * The GameClient class is responsible for managing the client-side logic of the online game.
 * It handles connecting to the game server, sending and receiving data, and managing player interactions.
 *
 * Key Responsibilities:
 * - Connect to the game server using the provided IP address and port.
 * - Handle communication with the server, including sending player moves and
 * receiving game updates.
 * - Display game menus, announcements, and prompts from the host to the player.
 * - Validate player input and ensure proper communication with the server.
 *
 * Key Features:
 * - Supports interactive gameplay for a single player in an online game.
 * - Handles server disconnections gracefully and provides feedback to the player.
 * - Displays game state updates, including other players' moves and announcements.
 * - Allows the player to make moves, including discarding cards during the final round.
 *
 * Dependencies:
 * - {@link SocketPacket} – Used for communication between the client and server.
 * - {@link Scoring} – Handles input validation for card selection.
 * - {@link GameLogic} – Provides utility methods for game logic, such as flushing input buffers.
 *
 * Assumptions:
 * - The server is running and accessible at the provided IP address and port.
 * - The player provides valid input when prompted.
 * - The game ends when the server sends a "Game Over" message or the connection
 * is closed.
 * - A player is only running one server on one computer
 */

public class GameClient {
    private static final int PORT = 1234;
    private static final int TIMEOUT = 4000;

    /**
     * Starts the client-side logic for connecting to the game server and managing gameplay.
     * Handles the connection setup, communication with the server, and player interactions.
     *
     * Responsibilities:
     * - Connect to the server using the provided IP address and port.
     * - Exchange player information with the server during the setup phase.
     * - Process server messages and handle game events such as move requests and game-over notifications.
     * - Validate player input and send moves to the server.
     *
     * Exceptions:
     * - Handles {@link IOException} and {@link ClassNotFoundException} for network communication errors.
     */
    public void startClient() {
        try (Scanner sc = new Scanner(System.in);) {

            // Request the host IP and establish connection
            Object[] hostIPDetails = requestHostIP(sc);
            String hostIP = (String) hostIPDetails[0];
            Socket socket = (Socket) hostIPDetails[1];


            System.out.println("Please wait the Host is still setting up the server!");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Request and validate the player's name
            String playerName = requestAndValidatePlayerName(in, out, sc);

            // Main game loop
            while (true) {
                try {
                    SocketPacket serverMessage = (SocketPacket) in.readObject();
                    if (!(serverMessage instanceof SocketPacket)) {
                        break; // exit if the message is invalid
                    }

                    GameLogic.flushInputBuffer();

                    switch (serverMessage.getMessageType()) {
                        case 0: // announcement
                            System.out.println("===============\n [Announcement] \n" + serverMessage.getSb().toString());
                            break;

                        case 1: // moveRequest
                            processMoveRequest(serverMessage, out, sc, playerName);
                            break;

                        case 3:
                            handleGameOver(serverMessage, sc);
                            break;

                        case 4:
                            processFinalMove(serverMessage, out, sc, playerName);
                            break;

                        default:
                            System.out.println("Unknown message type: " + serverMessage.getSb().toString());
                            break;
                    }
                } catch (ClassNotFoundException | EOFException e) {
                    System.out.println("Error reading data from server or server closed connection");
                    Game.main(null);
                    break;
                }
            }

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Handles the "Game Over" message from the server.
     * Displays the final game results and returns the player to the main menu.
     *
     * @param serverMessage The {@link SocketPacket} containing the game-over message.
     * @param sc            The {@link Scanner} object for reading user input.
     */
    private void handleGameOver(SocketPacket serverMessage, Scanner sc) {

        clearConsole();
        System.out.println("========================");
        System.out.println("        GAME OVER       ");
        System.out.println("========================");
        System.out.println(serverMessage.getSb().toString());
        System.out.println("Press enter to return to main menu");

        sc.nextLine();
        clearConsole();
        Game.main(null);

    }

    /**
     * Processes the final move request from the server.
     * Prompts the player to select 2 cards and sends the move to the server.
     *
     * @param serverMessage The {@link SocketPacket} containing the move request.
     * @param out           The {@link ObjectOutputStream} for sending data to the server.
     * @param sc            The {@link Scanner} object for reading user input.
     * @param playerName    The name of the current player.
     */
    private void processFinalMove(SocketPacket serverMessage, ObjectOutputStream out, Scanner sc, String playerName) {
        clearConsole();

        if (serverMessage.getCurrentPlayer().equals(playerName)) {
            System.out.println("===============\n [Final Move Request] \n" + serverMessage.getSb().toString());
            System.out.println("Please discard 2 cards");
            
            ArrayList<Card> hand = serverMessage.getPlayerWithName(playerName).getHand();

            try {
                discardCards(sc, hand, out, serverMessage);
            } catch (SocketException | EOFException e) {
                handleConnectionError(e);
            } catch (Exception e) {
                System.out.println("Invalid card number! Please choose again");
            }
        } else {
            // If it's not the player's turn, display the waiting message
            System.out.println("\n =============== \n [Waiting for " + serverMessage.getCurrentPlayer()
                    + "'s Move]" + " \n" + serverMessage.getSb().toString());
            System.out.println("It is currently " + serverMessage.getCurrentPlayer() + "'s turn. Please wait.");
        }
    }

    /**
     * Handles the process of discarding two cards during the final move request.
     * Prompts the player to select two distinct cards to discard and sends the
     * updated hand to the server.
     *
     * @param sc            The {@link Scanner} object for reading user input.
     * @param hand          The {@link ArrayList} of {@link Card} objects representing the player's hand.
     * @param out           The {@link ObjectOutputStream} for sending data to the server.
     * @param serverMessage The {@link SocketPacket} containing the move request and game state.
     * @throws IOException If an I/O error occurs while sending data to the server.
     */
    private void discardCards(Scanner sc, ArrayList<Card> hand, ObjectOutputStream out, SocketPacket serverMessage)
            throws IOException {
        while (true) {
            int firstCardIndex = Scoring.getValidCardIndex(sc, hand.size(),
                    "Choose the number of the 1st card to discard: ");
            int secondCardIndex = Scoring.getValidCardIndex(sc, hand.size(),
                    "Choose the number of the 2nd card to discard: ");

            if (firstCardIndex != secondCardIndex) {
                hand.remove(firstCardIndex);
                hand.remove(secondCardIndex);

                SocketPacket moveResponse = new SocketPacket(
                        new StringBuilder(firstCardIndex + "," + secondCardIndex),
                        serverMessage.currentPlayer, 2, null);
                out.writeObject(moveResponse);
                out.flush();
                return; // Exit the method after successfully discarding cards
            } else {
                System.out.println("Please do not choose 2 of the same index");
            }
        }
    }

    private void handleConnectionError(Exception e) {
        System.out.println("Error reading data from server or server closed connection");
        Game.main(null);
    }

    /**
     * Processes a move request from the server.
     * Prompts the player to select a card and sends the move to the server.
     *
     * @param serverMessage The {@link SocketPacket} containing the move request.
     * @param out           The {@link ObjectOutputStream} for sending data to the server.
     * @param sc            The {@link Scanner} object for reading user input.
     * @param playerName    The name of the current player.
     */
    private void processMoveRequest(SocketPacket serverMessage, ObjectOutputStream out, Scanner sc, String playerName) {
        clearConsole();

        // check if the client is the current player
        if (serverMessage.getCurrentPlayer().equals(playerName)) {
            
            System.out.println(
                    "\n =============== \n [Your Move] \n" + serverMessage.getSb().toString());
            System.out.println("Its your turn!! Please input your move");

            // check for input validation
            while (true) {
                try {
                    int input = getValidCardIndex(sc, serverMessage, playerName);

                    // send move to server as socket packet
                    SocketPacket moveResponse = new SocketPacket(new StringBuilder(input + ""),
                            serverMessage.currentPlayer, 2, null);
                    out.writeObject(moveResponse);
                    out.flush();
                    break;

                } catch (SocketException | EOFException e) {
                    System.out.println(
                            "Error reading data from server or server closed connection");
                    Game.main(null);
                    break;
                } catch (Exception e) {
                    System.out.println("Invalid card number! Please choose again");
                }
            }
        }
        else {
            //not users turn 
            System.out.println(
                    "\n =============== \n [Waiting for " + serverMessage.getCurrentPlayer()
                            + "'s Move]" + " \n" + serverMessage.getSb().toString());
        
            System.out.println("it is currently " + serverMessage.getCurrentPlayer()
                    + " turn please wait");
        }
    }

    /**
     * Prompts the user to enter a valid card index.
     *
     * @param sc            The {@link Scanner} object for reading user input.
     * @param serverMessage The {@link SocketPacket} containing the player's hand.
     * @param playerName    The name of the current player.
     * @return The valid card index selected by the player.
     */
    private int getValidCardIndex(Scanner sc, SocketPacket serverMessage, String playerName) {
        int handSize = serverMessage.getPlayerWithName(playerName).getHand().size();
        while (true) {
            try {
                System.out.print("Choose a card index (1-" + handSize + "): ");
                int input = Integer.parseInt(sc.nextLine()) - 1;
                if (input >= 0 && input < handSize) {
                    return input;
                }
                System.out.println("Invalid card number! Please choose again.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
    }

    /**
     * Clears the console screen to improve readability during gameplay.
     * Uses ANSI escape codes to reset the terminal display.
     */
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    /**
     * Prompts the user to enter the host IP address and attempts to connect to the server.
     * Repeats the prompt until a valid connection is established.
     *
     * Parameters:
     * - {@code sc} – A {@link Scanner} object for reading user input.
     *
     * Returns:
     * - An {@code Object[]} containing:
     *   - The host IP address as a {@link String}.
     *   - The connected {@link Socket} object.
     *
     * Exceptions:
     * - Handles connection errors and prompts the user to re-enter the IP address if the connection fails.
     */
    private static Object[] requestHostIP(Scanner sc){

        System.out.println("========================");
        System.out.println("       Join Server      ");
        System.out.println("========================");

        while(true){

            System.out.print("Please enter the host IP or type 'q' to quit: ");
            String hostIP = sc.nextLine();
            if (hostIP.equals("q")) {
                clearConsole();
                Game.main(null);
                break;

            }

            System.out.println("Attempting to connect to server!! ");

            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(hostIP, PORT), TIMEOUT); // 4000ms timeout
                System.out.println("Connected! ");
                return new Object[]{hostIP, socket};
                
            } catch (Exception e) {
                System.out.print("\n" + e.getMessage());
                System.out.println(" : The ip address may not be valid please try again!");
            }
        }

        return null;
    }

    private String requestAndValidatePlayerName(ObjectInputStream in, ObjectOutputStream out, Scanner sc)
            throws IOException, ClassNotFoundException {
        String playerName = "";
        while (true) {
            String nameRequest = (String) in.readObject();
            if (!nameRequest.equals("Success")) {
                System.out.println(nameRequest);
                playerName = sc.nextLine();
                out.writeObject(playerName);
                out.flush();
            } else {
                System.out.println("Connected to game server! Waiting for turns...");
                break;
            }
        }
        return playerName;
    }

}

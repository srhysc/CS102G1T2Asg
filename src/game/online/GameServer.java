package game.online;
import entities.*;
import game.*;
import java.io.*;
import java.net.*;
import java.util.*;

/**
 * The GameServer class is responsible for managing the server-side logic of the online game.
 * It handles player connections, lobby setup, and the initialization of the game.
 * 
 * <p> Key Responsibilities:
 * <ul>
 * <li> Start the server and bind it to a specific port.</li>
 * <li> Accept incoming client connections and manage player sockets.</li>
 * <li> Set up the game lobby, including player name validation and displaying the lobby status.</li>
 * <li> Initialize the game logic and manage the transition to gameplay.</li>
 * <li> Broadcast messages and updates to all connected players.</li>
 * </ul>
 * 
 * <p> Key Features:
 * <ul>
 * <li> Supports multiple players (2-6) in an online game.</li>
 * <li> Handles player disconnections gracefully.</li>
 * <li> Displays the server's IP address and port for clients to connect.</li>
 * <li> Uses a timeout mechanism to avoid blocking while waiting for new connections.</li>
 * </ul>
 *
 * <p> Dependencies:
 * <ul>
 * <li> {@link Player} – Represents each player in the game.</li>
 * <li> {@link Deck} – Represents the deck of cards used in the game.</li>
 * <li> {@link TurnManager} – Manages the turn order of players.</li>
 * <li> {@link OnlineGameLogic} – Contains the core game logic for online gameplay.</li>
 * </ul>
 *
 * Key Features:
 * - Supports multiple players (2-6) in an online game.
 * - Handles player disconnections gracefully.
 * - Displays the server's IP address and port for clients to connect.
 * - Uses a timeout mechanism to avoid blocking while waiting for new connections.
 *
 * Dependencies:
 * - {@link Player} – Represents each player in the game.
 * - {@link Deck} – Represents the deck of cards used in the game.
 * - {@link TurnManager} – Manages the turn order of players.
 * - {@link OnlineGameLogic} – Contains the core game logic for online gameplay.
 *
 * Assumptions:
 * - The server is hosted on a machine with a valid network configuration.
 * - Players connect using the provided IP address and port.
 * - The game ends when all players disconnect or the game logic concludes.
 * - Players who connect are also running the same game platform and not from their own code.
 * - The players joining are on the same network 
 */

public class GameServer {
    
    private static final int PORT = 1234;
    private static final int TIMEOUT = 500;

    /** A list of player sockets representing connected clients.*/
    public static List<Socket> playersSockets = new ArrayList<>();
    ArrayList<String> playerNames = new ArrayList<>();
    ArrayList<Player> multiplayerPlayerList = new ArrayList<>();
    private static List<ObjectOutputStream> outputs = new ArrayList<>();
    private static List<ObjectInputStream> inputs = new ArrayList<>();
    private boolean isTwoPlayerGame = false;

    /** The {@link ServerSocket} used to accept incoming client connections.*/
    public static ServerSocket serverSocket;


    /**
     * Starts the server-side logic for the online game.
     * Handles the initialization of the server, player connections, and game setup.
     *
     * @param deck The {@link Deck} object representing the deck of cards used in the game.
     */
    public void startServer(Deck deck){
        Scanner sc = new Scanner(System.in);
        try {

            String serverIP = initalizeServer();
            String ServerPlayerName = setupHostPlayer(sc);
            int numberOfPlayers = setupLobby(sc, ServerPlayerName, serverIP);   

        } 
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Server error: " + e.getMessage());
        }

        //done with players and confirmed lobby 
        TurnManager turnManager = new TurnManager(multiplayerPlayerList.size());

        if (multiplayerPlayerList.size() == 2){
            isTwoPlayerGame = true;
        }

        //start the game
        System.out.println("Online starting now");
        OnlineGameLogic.playOnlineTurn(deck, multiplayerPlayerList, turnManager, isTwoPlayerGame, outputs, inputs, sc );

    }

    /**
     * Clears the console screen to improve readability during server setup and gameplay.
     * Uses ANSI escape codes to reset the terminal display.
     */
    private static void clearConsole() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    /**
     * Displays the game lobby information, including the list of connected players and the server's IP address.
     * Updates the lobby status as players join and provides feedback to the server host.
     *
     * @param playerList The list of {@link Player} objects representing the connected players.
     * @param sc A {@link Scanner} object for reading user input.
     * @param ipAddress The IP address of the server.
     * @param maxSize The maximum number of players allowed in the game.
     * @return {@code true} if the lobby is successfully displayed.
     */
    private static boolean displayLobby(ArrayList<Player> playerList, Scanner sc, String ipAddress, int maxSize) {
        System.out.println("========================");
        System.out.println("       GAME LOBBY       ");
        System.out.println("IP: " + ipAddress);
        System.out.println("========================");

        // Display the list of players
        System.out.println("Players in lobby: " + playerList.size());
        System.out.println("Players:");
        for (Player player : playerList) {
            System.out.println("- " + player.getName());
        }

        // Display waiting message

        if(playerList.size() < maxSize){
            System.out.println("Waiting for more players... (" + playerList.size()+ "/" + maxSize + ")");
        }

        System.out.println("========================");
        return true;

        
    }

    /**
     * Retrieves the server's IP address for clients to connect.
     * Iterates through the network interfaces to find the first active IPv4 address.
     *
     * @return A {@link String} representing the server's IP address. If no valid IP address is found, 
     * returns "Unable to determine IP address".
     *
     * <p> Exceptions:
     * <ul>
     * <li> Handles {@link SocketException} for network interface errors.</li>
     * </ul>
     */
    private static String getServerIPAddress() {
        try {
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                if (networkInterface.isLoopback() || !networkInterface.isUp()) {
                    continue; // Skip loopback and inactive interfaces
                }
                Enumeration<InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress address = addresses.nextElement();
                    if (address instanceof Inet4Address) { // Only return IPv4 addresses
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return "Unable to determine IP address";
    }

    /**
     * Initializes the server by binding it to a port and displaying the server's IP address.
     *
     * @return The server's IP address as a {@link String}.
     * @throws IOException If an error occurs while creating the server socket.
     */
    private String initalizeServer() throws IOException{
        serverSocket = new ServerSocket(PORT);
        String serverIP = getServerIPAddress();
        System.out.println("Server started on port " + serverSocket.getLocalPort());
        System.out.println("========================");
        System.out.println("     Set up Server      ");
        System.out.println("========================");
        System.out.println("Your host IP is " + serverIP) ;

        return serverIP;
    }
    
    /**
     * Prompts the host player to enter their name and validates it.
     *
     * @param sc The {@link Scanner} object for reading user input.
     * @return The validated host player's name as a {@link String}.
     */
    private String setupHostPlayer(Scanner sc){
        System.out.print("Enter your name: ");
        String ServerPlayerName = sc.nextLine().strip();//strip leading/trailing spaces

        while (ServerPlayerName.isEmpty()) {
            System.out.println("Name cannot be empty. Please enter a valid name:");
            ServerPlayerName = sc.nextLine().strip();
        }

        playerNames.add(ServerPlayerName);
        multiplayerPlayerList.add(new Player(ServerPlayerName, null, null));
        return ServerPlayerName;
    }

    /**
     * Sets up the game lobby by managing player connections and validating player names.
     *
     * @param sc            The {@link Scanner} object for reading user input.
     * @param hostPlayerName The name of the host player.
     * @param serverIP      The server's IP address.
     * @return The number of players in the game.
     * @throws IOException If an error occurs while managing player connections.
     * @throws ClassNotFoundException If an error occurs while reading player data.
     */
    private int setupLobby(Scanner sc, String hostPLayerName, String serverIP) throws IOException, ClassNotFoundException{

        System.out.println("Select the number of players (2-6): ");
        int numberOfPlayers = 0;

        while (numberOfPlayers < 2 || numberOfPlayers > 6) {
            if (sc.hasNextInt()) {
                numberOfPlayers = sc.nextInt();
                sc.nextLine(); // Consume the newline character
                if (numberOfPlayers < 2 || numberOfPlayers > 6) {
                    System.out.println("Invalid input. Please enter a number between 2 and 6:");
                }
            } else {
                System.out.println("Invalid input. Please enter a number between 2 and 6:");
                sc.nextLine(); // Clear the invalid input
            }
        }

        //menu for server
        clearConsole();
        displayLobby(multiplayerPlayerList, sc, serverIP, numberOfPlayers);

        boolean confirmLobby = false;

        while(numberOfPlayers != multiplayerPlayerList.size()){
            try {
                // Accept new client connections
                serverSocket.setSoTimeout(TIMEOUT); // Set a timeout to avoid blocking
                Socket clientSocket = serverSocket.accept();
                ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
        
                // Ask for the new player's name
                out.writeObject("Successful Connection! Please Enter your name:");
                out.flush();
                
                while(true){
                    String playerName = (String) in.readObject();

                    if(playerNames.contains(playerName)){ 
                        out.writeObject("That name has been taken, try again! : ");
                        out.flush();
                    
                    } 
                    else if (playerName.strip().equals("")) {
                        out.writeObject("Name cant be empty, try again! : ");
                        out.flush();
                    }
                    else {
                        out.writeObject("Success");
                        out.flush();
                        Player newPlayer = new Player(playerName, out, in);
                        playerNames.add(playerName);

                        // Add the new player to the list
                        multiplayerPlayerList.add(newPlayer);
                        playersSockets.add(clientSocket);
                        outputs.add(out);
                        inputs.add(in);
                        break;
                    }
                }
            
            } catch (SocketTimeoutException e) {
                // No new connections, continue to check for server input
            }

            // Update the lobby display and ask for confirmation
            clearConsole();
            confirmLobby = displayLobby(multiplayerPlayerList, sc, serverIP,numberOfPlayers);
        }


        return numberOfPlayers;
    }
}

package game.online;
import entities.*;
import game.*;
import java.io.*;
import java.net.*;
import java.util.*;


public class GameServer {
    
    private static final int PORT = 1234;
    public static List<Socket> playersSockets = new ArrayList<>();
    ArrayList<Player> multiplayerPlayerList = new ArrayList<>();
    private static List<ObjectOutputStream> outputs = new ArrayList<>();
    private static List<ObjectInputStream> inputs = new ArrayList<>();
    private boolean isTwoPlayerGame = false;
    public static ServerSocket serverSocket;

    public void startServer(Deck deck){
        Scanner sc = new Scanner(System.in);
        try {

            serverSocket = new ServerSocket(PORT);
            String serverIP = getServerIPAddress();
            System.out.println("Server started on port " + serverSocket.getLocalPort());

           
            System.out.println("your ip for friends to connect is " + serverIP) ;
            System.out.print("Enter your name: ");
            String ServerPLayerName = sc.nextLine();
            multiplayerPlayerList.add(new Player(ServerPLayerName, null, null));

            System.out.println("Select the number of players (2-6): ");
            int numberOfPlayers = sc.nextInt();
            sc.nextLine();

            //menu for server
            clearConsole();
            displayLobby(multiplayerPlayerList, sc, serverIP);

            Boolean confirmLobby = false;

            while(numberOfPlayers != multiplayerPlayerList.size()){
                try {
                    // Accept new client connections
                    serverSocket.setSoTimeout(500); // Set a timeout to avoid blocking
                    Socket clientSocket = serverSocket.accept();
                    ObjectOutputStream out = new ObjectOutputStream(clientSocket.getOutputStream());
                    ObjectInputStream in = new ObjectInputStream(clientSocket.getInputStream());
            
                    // Ask for the new player's name
                    out.writeObject("Enter your name:");
                    out.flush();
            
                    String playerName = (String) in.readObject();
                    Player newPlayer = new Player(playerName, out, in);
            
                    // Add the new player to the list
                    multiplayerPlayerList.add(newPlayer);
                    playersSockets.add(clientSocket);
                    outputs.add(out);
                    inputs.add(in);
            
                } catch (SocketTimeoutException e) {
                    // No new connections, continue to check for server input
                }

                // Update the lobby display and ask for confirmation
                clearConsole();
                confirmLobby = displayLobby(multiplayerPlayerList, sc, serverIP);
            }
                

        } 
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Server error: " + e.getMessage());
        }

        //done with players and confirmed lobby 
        TurnManager turnManager = new TurnManager(multiplayerPlayerList.size());

        if (multiplayerPlayerList.size() > 1){
            isTwoPlayerGame = true;
        }

        //start the game
        System.out.println("Online starting now");
        OnlineGameLogic.playOnlineTurn(deck, multiplayerPlayerList, turnManager, isTwoPlayerGame, outputs, inputs, sc );




    }

    // Clear the console screen
    private static void clearConsole() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    // Display lobby with multiple lines
    private static boolean displayLobby(ArrayList<Player> playerList, Scanner sc, String ipAddress) {
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
        if (playerList.size() < 2) {
            System.out.println("Waiting for more players... (" + playerList.size() + "/6)");
        } else if (playerList.size() <= 6) {
            System.out.println("You can start the game now or wait for more players. (" + playerList.size() + "/6)");
        } else {
            System.out.println("The lobby is full. You can start the game.");
        }

        System.out.println("========================");
        return true;

        
    }

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


}

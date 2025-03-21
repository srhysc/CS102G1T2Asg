import java.io.*;
import java.net.*;
import java.util.*;


public class GameServer {
    
    private static final int PORT = 1234;
    private static List<Socket> playersSockets = new ArrayList<>();
    ArrayList<Player> multiplayerPlayerList = new ArrayList<>();
    private static List<ObjectOutputStream> outputs = new ArrayList<>();
    private static List<ObjectInputStream> inputs = new ArrayList<>();

    public void startServer(){

        try (ServerSocket serverSocket = new ServerSocket(PORT); Scanner sc = new Scanner(System.in);) {
            
            System.out.println("Server started on port ");
            System.out.println("Please enter your name");
            String ServerPLayerName = sc.nextLine();
            multiplayerPlayerList.add(new Player(ServerPLayerName));

            //menu for server
            clearConsole();
            displayLobby(multiplayerPlayerList, sc);

            Boolean confirmLobby = false;

            while(!confirmLobby){
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
                    Player newPlayer = new Player(playerName);
            
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
                confirmLobby = displayLobby(multiplayerPlayerList, sc);
            }
                
            
            


        } 
        catch (IOException | ClassNotFoundException e) {
            System.out.println("Server error: " + e.getMessage());
        }




    }

    // Clear the console screen
    private static void clearConsole() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }

    // Display lobby with multiple lines
    private static boolean displayLobby(ArrayList<Player> playerList, Scanner sc) {
        System.out.println("========================");
        System.out.println("       GAME LOBBY       ");
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

        // Ask the server host to confirm the lobby
        if (playerList.size() >= 2) {
            System.out.println("Press Enter to wait for more players, or type 'start' to begin the game:");
            String input = sc.nextLine();
            return input.equalsIgnoreCase("start");
        } else {
            System.out.println("Not enough players to start the game. Waiting for more players...");
            return false;
        }
    }


}

package game.online;
import entities.Card;
import entities.scoring.Scoring;
import game.GameLogic;
import game.Game;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * The GameClient class is responsible for managing the client-side logic of the online game.
 * It handles connecting to the game server, sending and receiving data, and managing player interactions.
 *
 * Key Responsibilities:
 * - Connect to the game server using the provided IP address and port.
 * - Handle communication with the server, including sending player moves and receiving game updates.
 * - Display game menus, announcements, and prompts to the player.
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
 * - The game ends when the server sends a "Game Over" message or the connection is closed.
 */

public class GameClient {
    private static final int PORT = 1234;

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
    public void startClient(){

        try( // Connect to the server
           Scanner sc = new Scanner(System.in);)
        {

            Object[] hostIPDetails = requestHostIP(sc);
            String hostIP = (String) hostIPDetails[0];
            Socket socket = (Socket) hostIPDetails[1];


            System.out.println("Please wait the Host is still setting up the server!");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            // Receive prompt for player name
            String playerName = "";
            
            
            while (true) {
                String nameRequest = (String) in.readObject();
                if(!(nameRequest.equals("Success"))){
                    System.out.println(nameRequest);
                    playerName = sc.nextLine();
                    out.writeObject(playerName);
                    out.flush();
                }
                else{
                    System.out.println("Connected to game server! Waiting for turns...");
                    break;
                }
            }
            

            
            

            while (true) {
                try{
                    SocketPacket serverMessage = (SocketPacket) in.readObject();
                    if(serverMessage instanceof SocketPacket){
                        //check the message type first 

                        GameLogic.flushInputBuffer();
                        System.out.println("reiceived smth!");
                        switch (serverMessage.getMessageType()){ 
                            case 0 : //announcement
                                System.out.println("===============\n [Announcement] \n" + serverMessage.getSb().toString());
                                break;
                            case 1 : //moveRequest
                                clearConsole();
                                

                                //check if the client is the current player 
                                if(serverMessage.getCurrentPlayer().equals(playerName)){
                                    //show the move request (probably the menu)
                                    System.out.println("\n =============== \n [Your Move] \n" + serverMessage.getSb().toString());
                                    



                                    //if it is ask for their input 
                                    System.out.println("Its your turn!! Please input your move");
                                    

                                    //check for input validation 
                                    while(true){
                                        try {
                                            String turnInput = sc.nextLine();
                                            int input = Integer.parseInt(turnInput) - 1;
    
                                            while (input < 0 || input > 4) {
                                                System.out.println("Invalid card number! Please choose again");
                                                //get player hand 
                                                int handSize = serverMessage.getPlayerWithName(playerName).getHand().size();
                                                System.out.print("Choose a card index (1-" + (handSize) + "): ");
                                                turnInput = sc.nextLine();
                                                input = Integer.parseInt(turnInput) - 1;
                                                System.out.println("Card index: " + input);
                                            }
    
                                             //send move to server
                                            //build socket packet 
                                            SocketPacket moveResponse = new SocketPacket(new StringBuilder(input + ""), serverMessage.currentPlayer, 2, null);
                                            out.writeObject(moveResponse);
                                            out.flush();
                                            break;
                                        } catch(SocketException | EOFException e){
                                            System.out.println("Error reading data from server or server closed connection");
                                            Game.main(null);
                                            break;
                                        }catch (Exception e) {
                                            // TODO: handle exception
                                            System.out.println("Invalid card number! Please choose again");
                                            
                                        }
                                    
    
                                    
    
                                            
                                        }
                                        System.out.println("move sent");//test
                                    }
                                    
                                    
                                else{
                                    //show the move request (probably the menu)
                                    System.out.println("\n =============== \n [Waiting for " + serverMessage.getCurrentPlayer() + "'s Move]"+ " \n" + serverMessage.getSb().toString());
                                    //message recieved but not the current player 
                                    System.out.println("it is currently " + serverMessage.getCurrentPlayer() + " turn please wait");
                                }

                                break;

                            case 3 :
                                clearConsole() ;
                                

                                System.out.println("========================");
                                System.out.println("        GAME OVER       ");
                                System.out.println("========================");
                                System.out.println(serverMessage.getSb().toString());

                                System.out.println("press enter to return to main menu");
                                sc.nextLine();
                                clearConsole();
                                Game.main(null);


                                break;

                            case 4 : 
                                clearConsole();
                                
                                if(serverMessage.getCurrentPlayer().equals(playerName)){
                                    while(true){
                                        try {
                                            System.out.println("===============\n [Final Move Request] \n" + serverMessage.getSb().toString());
                                            //get hand
                                            ArrayList<Card> hand = serverMessage.getPlayerWithName(playerName).getHand();
                                            System.out.println("Please discard 2 cards");

                                            int firstCardIndex = Scoring.getValidCardIndex(sc, hand.size(), "Choose the number of the 1st card to discard: ");
                                            int secondCardIndex = Scoring.getValidCardIndex(sc, hand.size(), "Choose the number of the 2nd card to discard: ");
                
                                            if(firstCardIndex != secondCardIndex){
                                                Card discardedFirstCard = hand.remove(firstCardIndex);
                                                Card discardedSecondCard = hand.remove(secondCardIndex);
                                                //send move to server
                                                //build socket packet 

                                                SocketPacket moveResponse = new SocketPacket(new StringBuilder(firstCardIndex+","+secondCardIndex), serverMessage.currentPlayer, 2, null);
                                                out.writeObject(moveResponse);
                                                out.flush();
                                                break;
                                                
                                            }
                                            else{
                                                System.out.println("Please do not choose 2 of the same index");
                                                throw new InputMismatchException();
                                            }
    
                                            
                                           } catch(SocketException | EOFException e){
                                            System.out.println("Error reading data from server or server closed connection");
                                            Game.main(null);
                                            break;
                                        }catch (Exception e) {
                                            // TODO: handle exception
                                            System.out.println("Invalid card number! Please choose again");
                                            
                                        }
                                    
                                            
                                    }
                                }
                                else{
                                      //show the move request (probably the menu)
                                      System.out.println("\n =============== \n [Waiting for " + serverMessage.getCurrentPlayer() + "'s Move]"+ " \n" + serverMessage.getSb().toString());
                                      //message recieved but not the current player 
                                      System.out.println("it is currently " + serverMessage.getCurrentPlayer() + " turn please wait");
                                }

                                    



                                break;
                            default:
                                
                                System.out.println(serverMessage.getMessageType());
                                System.out.println("Unknown message type: " + serverMessage.getSb().toString());
                                break;
                        }

                        
                    }

                
                }
                catch(ClassNotFoundException | EOFException e){
                    System.out.println("Error reading data from server or server closed connection");
                    Game.main(null);
                    break;
                }
            }



        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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

            System.out.print("Please enter the host IP : ");
            String hostIP = sc.nextLine();

            System.out.println("Attempting to connect to server!! ");

            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(hostIP, PORT), 4000); // 4000ms timeout
                System.out.println("Connected! ");
                return new Object[]{hostIP, socket};
                
            } catch (Exception e) {
                System.out.print("\n" + e.getMessage());
                System.out.println(" : The ip address may not be valid please try again!");
            }
        }
    }


}

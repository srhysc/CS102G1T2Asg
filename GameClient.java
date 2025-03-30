import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;


public class GameClient {
    private static final int PORT = 1234;

    public void startClient(){

        try( // Connect to the server
            //local host and port are hardcoded right now
            Socket socket = new Socket("localhost", PORT);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
            Scanner sc = new Scanner(System.in);)
        {
            // Receive prompt for player name
            System.out.println(in.readObject());
            System.out.print("Enter your name: ");
            String playerName = sc.nextLine();
            out.writeObject(playerName);
            out.flush();
            System.out.println("Connected to game server! Waiting for turns...");

            while (true) {
                try{
                    SocketPacket serverMessage = (SocketPacket) in.readObject();
                    if(serverMessage instanceof SocketPacket){
                        //check the message type first 

                        GameLogic.flushInputBuffer();
                        
                        switch (serverMessage.getMessageType()){ 
                            case 0 : //announcement
                                System.out.println("===============\n [Announcement] \n" + serverMessage.getSb().toString());
                                break;
                            case 1 : //moveRequest
                                clearConsole();
                                //show the move request (probably the menu)
                                System.out.println("\n =============== \n [Move Request] \n" + serverMessage.getSb().toString());

                                //check if the client is the current player 
                                if(serverMessage.getCurrentPlayer().equals(playerName)){

                                    



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
                                            } catch (Exception e) {
                                                // TODO: handle exception
                                                System.out.println("Invalid card number! Please choose again");
                                            }
                                        
    
                                    
    
                                            
                                        }
                                        System.out.println("move sent");//test
                                    }
                                    
                                    
                                else{
                                    //message recieved but not the current player 
                                    System.out.println("it is currently " + serverMessage.getCurrentPlayer() + " turn please wait");
                                }

                                break;

                            case 3 :
                                System.out.println("===============\n [GameOver] \n" + serverMessage.getSb().toString());

                                System.out.println("press enter to quit or when your host quits you will be returned to main menu");
                                Game.main(null);


                                break;

                            case 4 : 
                                System.out.println("===============\n [GameOver] \n" + serverMessage.getSb().toString());
                                
                                    while(true){
                                        try {

                                            //get hand
                                            ArrayList<Card> hand = serverMessage.getPlayerWithName(playerName).getHand();


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
    
                                            
                                            } catch (Exception e) {
                                                // TODO: handle exception
                                                System.out.println("Invalid card number! Please choose again");
                                            }
                                    
                                            
                                        }



                                break;
                            default:
                                break;
                        }

                        //System.out.println("Server: " + serverMessage.getSb().toString());
                    }

                    // if (serverMessage.getCurrentPlayer().equals(playerName)){
                    //     System.out.println("its your turn!!");

                    //     String testInput = sc.nextLine();

                    //     //send move to server
                    //     out.writeObject(testInput);
                    //     out.flush();
                    //     System.out.println("move sent");


                    // }
                    // else{
                    //     System.out.println("its "  + serverMessage.getCurrentPlayer() + " turn please wait");
                    // }
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


    // Clear the console screen
    public static void clearConsole() {
        System.out.print("\033[H\033[2J");  
        System.out.flush();  
    }
}

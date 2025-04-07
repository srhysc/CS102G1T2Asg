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


public class GameClient {
    private static final int PORT = 1234;

    public void startClient(){

        try( // Connect to the server
            //local host and port are hardcoded right now

            Scanner sc = new Scanner(System.in);

            
            )
        {
            String hostIP = "";
            Socket socket = null;
            while(true){
                System.out.print("please enter the host IP : ");
                hostIP = sc.nextLine();

                System.out.println("Attempting to connect to server!! ");
    
                try {
                    socket = new Socket();
                    socket.connect(new InetSocketAddress(hostIP, PORT), 4000); // 5000ms timeout
                    System.out.println("Connected! ");
                    break;
                } catch (Exception e) {
                    System.out.print("\n" + e.getMessage());
                    System.out.println(" : The ip address may not be valid please try again!");
                }
            }
            
            System.out.println("Please wait the Host is still setting up the server!");
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

            


            // Receive prompt for player name
            System.out.print(in.readObject());
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
                                    System.out.println("\n =============== \n [Move Request] \n" + serverMessage.getSb().toString());
                                    



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
                                System.out.println("===============\n [GameOver] \n" + serverMessage.getSb().toString());

                                System.out.println("press enter to quit or when your host quits you will be returned to main menu");
                                sc.nextLine();
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

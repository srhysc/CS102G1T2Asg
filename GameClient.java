import java.io.*;
import java.net.*;
import java.util.ArrayList;
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

                        switch (serverMessage.getMessageType()){ 
                            case 0 : //announcement
                                System.out.println("===============\n [Announcement] \n" + serverMessage.getSb().toString());
                                break;
                            case 1 : //moveRequest

                                //show the move request (probably the menu)
                                System.out.println("\n =============== \n [Move Request] \n" + serverMessage.getSb().toString());

                                //check if the client is the current player 
                                if(serverMessage.getCurrentPlayer().equals(playerName)){
                                    //if it is ask for their input 
                                    System.out.println("its your turn!! Please input your move");
                                    String turnInput = sc.nextLine();

                                    //send move to server
                                    //build socket packet 
                                    SocketPacket moveResponse = new SocketPacket(new StringBuilder(turnInput), serverMessage.currentPlayer, 2);
                                    out.writeObject(moveResponse);
                                    out.flush();

                                    System.out.println("move sent");//test
                                }
                                else{
                                    //message recieved but not the current player 
                                    System.out.println("it is currently " + serverMessage.getCurrentPlayer() + " turn please wait");
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
                    break;
                }
            }



        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }




    }

}

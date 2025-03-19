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

        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }




    }

}

package game.online;
import entities.Player;
import java.io.Serializable;
import java.util.ArrayList;

public class SocketPacket implements Serializable {
    public StringBuilder sb;
    public String currentPlayer; 
    public static String[] messageTypeList = {"announcement", "moveRequest", "moveResponse", "gameOver","remove2Cards"};
    public int messageType; 
    public ArrayList<Player> playerList;

    public SocketPacket(StringBuilder sb, String currentPlayer, int messageType, ArrayList<Player> playerList) {
        this.sb = sb;
        this.currentPlayer = currentPlayer;
        //check if messageType is valid 
        this.messageType = messageType;
        this.playerList = playerList;
        
    }

    public StringBuilder getSb() {
        return sb;
    }

    public void setSb(StringBuilder sb) {
        this.sb = sb;
    }

    public String getCurrentPlayer() {
        return currentPlayer;
    }

    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    public int getMessageType() {
        return messageType;
    }

    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    public ArrayList<Player> getPlayerList() {
        return playerList;
    }
    public Player getPlayerWithName(String name ) {
        for (Player player : playerList) {
            if (player.getName().equals(name)) {
                return player;
            }
        }

        return null;
       
    }

    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    

    
}

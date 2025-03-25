import java.io.Serializable;

public class SocketPacket implements Serializable {
    public StringBuilder sb;
    public String currentPlayer; 
    public static String[] messageTypeList = {"announcement", "moveRequest", "moveResponse", "gameOver"};
    public int messageType; 

    public SocketPacket(StringBuilder sb, String currentPlayer, int messageType) {
        this.sb = sb;
        this.currentPlayer = currentPlayer;
        //check if messageType is valid 
        this.messageType = messageType;
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

    

    
}

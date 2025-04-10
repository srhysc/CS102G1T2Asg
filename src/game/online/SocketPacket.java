package game.online;
import entities.Player;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Constructs a new SocketPacket object for communication between the server and clients.
 *
 * Parameters:
 * - {@code sb} – A {@link StringBuilder} containing the message content.
 * - {@code currentPlayer} – A {@link String} representing the name of the current player.
 * - {@code messageType} – An integer representing the type of message (e.g., announcement, move request).
 * - {@code playerList} – An {@link ArrayList} of {@link Player} objects representing the players in the game.
 */
public class SocketPacket implements Serializable {
    public StringBuilder sb;
    public String currentPlayer; 

    //reference for what different move numbers mean
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

    /**
     * Retrieves the message content stored in the SocketPacket.
     *
     * Returns:
     * - A {@link StringBuilder} containing the message content.
     */
    public StringBuilder getSb() {
        return sb;
    }

    /**
     * Updates the message content stored in the SocketPacket.
     *
     * Parameters:
     * - {@code sb} – A {@link StringBuilder} containing the new message content.
     */
    public void setSb(StringBuilder sb) {
        this.sb = sb;
    }

    /**
     * Retrieves the name of the current player associated with the SocketPacket.
     *
     * Returns:
     * - A {@link String} representing the name of the current player.
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Updates the name of the current player associated with the SocketPacket.
     *
     * Parameters:
     * - {@code currentPlayer} – A {@link String} representing the new current player name.
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Retrieves the type of message stored in the SocketPacket.
     *
     * Returns:
     * - An integer representing the message type (e.g., 0 for announcement, 1 for move request).
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Updates the type of message stored in the SocketPacket.
     *
     * Parameters:
     * - {@code messageType} – An integer representing the new message type.
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    /**
     * Retrieves the list of players associated with the SocketPacket.
     *
     * Returns:
     * - An {@link ArrayList} of {@link Player} objects representing the players in the game.
     */
    public ArrayList<Player> getPlayerList() {
        return playerList;
    }  

    /**
     * Retrieves a player object from the player list by their name.
     *
     * Parameters:
     * - {@code name} – A {@link String} representing the name of the player to retrieve.
     *
     * Returns:
     * - A {@link Player} object matching the given name, or {@code null} if no player with the name is found.
     */
    public Player getPlayerWithName(String name ) {
        for (Player player : playerList) {
            if (player.getName().equals(name)) {
                return player;
            }
        }

        return null;
       
    }

    /**
     * Updates the list of players associated with the SocketPacket.
     *
     * Parameters:
     * - {@code playerList} – An {@link ArrayList} of {@link Player} objects representing the new player list.
     */
    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    

    
}

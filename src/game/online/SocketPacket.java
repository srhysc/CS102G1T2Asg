package game.online;
import entities.Player;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Constructs a new SocketPacket object for communication between the server and clients.
 *<p>
 * Parameters:
 * <ul>
 * <li> {@code sb} – A {@link StringBuilder} containing the message content.</li>
 * <li> {@code currentPlayer} – A {@link String} representing the name of the current player.</li>
 * <li> {@code messageType} – An integer representing the type of message (e.g., announcement, move request).</li>
 * <li> {@code playerList} – An {@link ArrayList} of {@link Player} objects representing the players in the game.</li>
 * </ul>
 */
public class SocketPacket implements Serializable {
    /** Holds the message content. */
    public StringBuilder sb;

    /** Represents the name of the current player. */
    public String currentPlayer; 

    /**
     * Reference array that maps integer message types to human-readable descriptions.
     * <ul>
     *   <li>0: "announcement"</li>
     *   <li>1: "moveRequest"</li>
     *   <li>2: "moveResponse"</li>
     *   <li>3: "gameOver"</li>
     *   <li>4: "remove2Cards"</li>
     * </ul>
     */
    //reference for what different move numbers mean
    public static String[] messageTypeList = {"announcement", "moveRequest", "moveResponse", "gameOver","remove2Cards"};
    
    /** Represents the type of message (e.g., announcement, move request). */
    public int messageType; 
    
    /** An {@link ArrayList} of {@link Player} objects representing the players in the game. */
    public ArrayList<Player> playerList;

    /**
     * Constructs a new SocketPacket with a specificed sb, currentPlayer, messageType and playerList.
     *
     * @param sb the message content as a {@link StringBuilder}
     * @param currentPlayer the name of the current player
     * @param messageType an integer representing the message type (should correspond to {@code messageTypeList})
     * @param playerList the list of players in the game as an {@link ArrayList} of {@link Player} objects
     */

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
     * @return A {@link StringBuilder} containing the message content.
     */
    public StringBuilder getSb() {
        return sb;
    }

    /**
     * Updates the message content stored in the SocketPacket.
     *
     * @param sb A {@link StringBuilder} containing the new message content.
     */
    public void setSb(StringBuilder sb) {
        this.sb = sb;
    }

    /**
     * Retrieves the name of the current player associated with the SocketPacket.
     *
     * @return A {@link String} representing the name of the current player.
     */
    public String getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Updates the name of the current player associated with the SocketPacket.
     *
     * @param currentPlayer A {@link String} representing the new current player name.
     */
    public void setCurrentPlayer(String currentPlayer) {
        this.currentPlayer = currentPlayer;
    }

    /**
     * Retrieves the type of message stored in the SocketPacket.
     *
     * @return An integer representing the message type (e.g., 0 for announcement, 1 for move request).
     */
    public int getMessageType() {
        return messageType;
    }

    /**
     * Updates the type of message stored in the SocketPacket.
     *
     * @param messageType An integer representing the new message type.
     */
    public void setMessageType(int messageType) {
        this.messageType = messageType;
    }

    /**
     * Retrieves the list of players associated with the SocketPacket.
     *
     * @return An {@link ArrayList} of {@link Player} objects representing the players in the game.
     */
    public ArrayList<Player> getPlayerList() {
        return playerList;
    }  

    /**
     * Retrieves a player object from the player list by their name.
     *
     * @param name A {@link String} representing the name of the player to retrieve.
     * @return A {@link Player} object matching the given name, or {@code null} if no player with the name is found.
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
     * @param playerList An {@link ArrayList} of {@link Player} objects representing the new player list.
     */
    public void setPlayerList(ArrayList<Player> playerList) {
        this.playerList = playerList;
    }

    

    
}

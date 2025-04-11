package entities;
import java.io.Serializable;
/**
 * This class represents a card in the game.
 * Each card has a colour, a value (like points), and can be face up or face down.
 * Also includes some ANSI colour codes so the card colours are shown when printed to the console.
 * 
 * <p>Example usage:</p>
 * <pre>
 *     Card c = new Card("red", 5);
 *     System.out.println(c); // prints in red text
 * </pre>
 */
public class Card implements Serializable{

    private int value;
    private String colour;
    private boolean faceDown;
    private String colorCode = "\u001B[0m";
    

    public Card(String color, int value) {
        this.colour = color.trim();
        this.value = value;
        this.faceDown = false;
    }

    /**
     * Gets the value of the card (its points).
     * 
     * @return The value of the card
     */
    public int getValue() {
        return value;
    }

    /**
     * Face down cards mean that they are the majority set so their values are not computed.
     * 
     * @return True if card is faced down, false otherwise
     */
    public boolean isFaceDown() {
        return faceDown;
    }

    /**
     * Sets the value of the card.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * Gets the colour of the card.
     * 
     * @return The colour of the card
     */
    public String getColour() {
        return colour;
    }

    /**
    * Sets the card's colour. 
    * @param colour The colour of the card in String format (eg. "red", "blue", etc.)
    */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Flips the card face down or up. 
     * 
     * @param faceDown True for down, false for up
     */
    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }

    /**
     * Get the ANSI colour code for the card's colour. It is used for making console text coloured.
     * @return The ANSI colour code for the card’s colour
     * 
     */
    public String getcolorCode() {
        switch (colour.toLowerCase()) {
            case "red":    colorCode = "\u001B[31m"; break;
            case "blue":   colorCode = "\u001B[34m"; break;
            case "green":  colorCode = "\u001B[32m"; break;
            case "purple": colorCode = "\u001B[35m"; break;
            case "grey":   colorCode = "\u001B[38;5;245m"; break;
            case "orange": colorCode = "\u001B[38;5;214m"; break;
            default:       colorCode = "\u001B[0m";  // Reset if unknown color
        }

        return colorCode;
    }
    /**
     * Gets the card's details.
     * @return String format of the card’s details (colour + value), 
     * formatted with colour codes for console output.
     */
    public String getDetails() {
        String reset = "\u001B[0m";

        return "Card [Color: " + getcolorCode() + colour + reset + ", Value: " + getcolorCode() + value + reset + "]";
    }

    /**
     * Returns a simple string version of the card, coloured for console.
     */
    @Override
    public String toString() {

        String reset = "\u001B[0m";

        // System.out.println(colorCode + "what colour is this" + reset);
        return getcolorCode() + colour + " " + value + reset;  // Reset color after printing
    }

    // public static void main(String[] args) {
    //     Card c = new Card("red", 2);
    //     System.out.println(c);
    // }
}

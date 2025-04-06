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
     */
    public int getValue() {
        return value;
    }

    /**
     * Face down cards mean that they are the majority set so their values are not computed.
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
     */
    public String getColour() {
        return colour;
    }

    /**
    * Sets the card's colour (string format, like "red", "blue", etc.).
    */
    public void setColour(String colour) {
        this.colour = colour;
    }

    /**
     * Flips the card face down or up. (true for down, false for up)
     */
    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }

    /**
     * Returns the ANSI colour code for the card’s colour.
     * Used for making console text coloured.
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
     * Returns a string with the card’s details (colour + value), 
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

    public static void main(String[] args) {
        Card c = new Card("red", 2);
        System.out.println(c);
    }
}

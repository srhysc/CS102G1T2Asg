public class Card{

    private int value;
    private String colour;
    private boolean faceDown;
    private String colorCode = "\u001B[0m";
    

    public Card(String color, int value) {
        this.colour = color.trim();
        this.value = value;
        this.faceDown = false;
    }

    public int getValue() {
        return value;
    }

    public boolean isFaceDown() {
        return faceDown;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }

    public void setFaceDown(boolean faceDown) {
        this.faceDown = faceDown;
    }

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
    public String getDetails() {
        String reset = "\u001B[0m";

        return "Card [Color: " + getcolorCode() + colour + reset + ", Value: " + getcolorCode() + value + reset + "]";
    }

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

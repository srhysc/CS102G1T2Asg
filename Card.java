public class Card{

    private int value;
    private String colour;
    private boolean faceDown;
    

    public Card(String color, int value) {
        this.colour = color;
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

    public String getDetails() {
        return "Card [Color: " + colour + ", Value: " + value + "]";
    }

    @Override
    public String toString() {
        return colour + value;
    }
}

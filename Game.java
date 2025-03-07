import java.util.ArrayList;

public class Game {

    static ArrayList<Player> playerList = new ArrayList<>();
    static Deck deck;
    static Parade parade;
    // gameType will be a boolean, isTwoPlayerGame = True or isTwoPlayerGame = False
    static boolean gameType;

    public static void startGame() {
        // menu and wtv else
        // Need to know whether it is a two player game or more than two player game
    }

    public static void playTurn() {
        //
    }

    public static boolean checkGameEnd() {
        boolean gameEnd = false;

        for (Player player : playerList) {
            if (player.allColoursCollected()) {
                gameEnd = true;
                break;
            }
        }

        if (gameEnd || deck.getCards() == 0) {
            System.out.println("This is the last round!");
            playTurn(); // Last Round to end up with 4 cards, I need to see how the play turn is being handled
            return true;
        }
        
        return false;
    }

    public static void main(String[] args) {
        startGame();
        while (!checkGameEnd() == false) {
            playTurn();
        }
        Scoring.calculateScores(playerList, gameType);
    }
}

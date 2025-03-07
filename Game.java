import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    static ArrayList<Player> playerList = new ArrayList<>();
    static Deck deck;
    static Parade parade;
    // gameType will be a boolean, isTwoPlayerGame = True or isTwoPlayerGame = False
    static boolean gameType;

    public static void startGame() {
        System.out.println("Game styles");
        System.out.println("-----------");
        System.out.println("Local Play (1)");
        System.out.println("Play vs AI (2)");
        System.out.println("Play Online (3)");
        System.out.println("Choose game style:");
        int styleNumber = 0;
        Scanner sc1 = new Scanner(System.in);
    
        while (!(styleNumber >= 1 && styleNumber <=3)) {
            try {
                String chosenStyle = sc1.nextLine();
                styleNumber = Integer.parseInt(chosenStyle);
                if (styleNumber < 1 || styleNumber > 3) {
                    System.out.println("Invalid game style! Please choose again");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid game style ! Please choose again");
            }
            
        }

        
            
        
        // menu and wtv else
        // Need to know whether it is a two player game or more than two player game
    }

    public static void playTurn() {
        //
    }

    public static void checkGameEnd(boolean allColoursCollected) {
        if (allColoursCollected || ) {
            this.playTurn();

        }
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

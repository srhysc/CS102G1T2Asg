import java.util.ArrayList;
import java.util.Scanner;

public class Game {

    ArrayList<Player> playerList = new ArrayList<>();
    Deck deck;
    Parade parade;

    public static void startGame() {
        System.out.println("Game styles");
        System.out.println("-----------");
        System.out.println("Local Play (1)");
        System.out.println("Play vs AI (2)");
        System.out.println("Play Online (3)");
        System.out.println("Choose game style:");
        int styleNumber = 0;
    
        while (!(styleNumber >= 1 && styleNumber <=3)) {
            try {
                Scanner sc1 = new Scanner(System.in);
                String chosenStyle = sc1.nextLine();
                styleNumber = Integer.parseInt(chosenStyle);
                if (styleNumber < 1 || styleNumber > 3) {
                    System.out.println("Invalid game style! Please choose again");
                }
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid game style ! Please choose again");
            }
            
        }
    }

    public static void playTurn() {

    }

    // public static void checkGameEnd(boolean allColoursCollected) {
    //     if (allColoursCollected || ) {
    //         this.playTurn();

    //     }
    // }

    public static void main(String[] args) {
        startGame();
    }
}

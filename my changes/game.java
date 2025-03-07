package my changes;

public class game {
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
                
            } catch (NumberFormatException e) {
                System.out.println("Invalid game style ! Please choose again");
            }
            System.out.println("Invalid game style ! Please choose again");
        }
    }
}

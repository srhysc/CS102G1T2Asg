import java.util.*;

public class Game {
    static ArrayList<Player> playerList = new ArrayList<>();
    private Deck deck;
    private Parade parade;
    private TurnManager turnManager;
    private Scanner scanner;
    private boolean online = false;
    private Difficulty difficulty;
    private boolean isTwoPlayerGame;

    public boolean isTwoPlayerGame() {
        return isTwoPlayerGame;
    }

    public void setTwoPlayerGame(boolean isTwoPlayerGame) {
        this.isTwoPlayerGame = isTwoPlayerGame;
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public Game(ArrayList<Player> playerList) {
        Game.playerList = playerList;
        this.deck = new Deck();
        // this.parade = Parade.Parade();
        this.turnManager = new TurnManager(playerList.size());
        this.scanner = new Scanner(System.in);
    }

    public void printMenu() {
        System.out.println("===== Menu =====");
        System.out.println("1. Start Game");
        System.out.println("2. Settings");
        System.out.println("3. Help");
        System.out.print("Enter your choice: ");
        int userChoice = 0;
        Scanner sc = new Scanner(System.in);
    
        while (!(userChoice >= 1 && userChoice <= 3)) {
            try {
                String userInput = sc.nextLine();
                userChoice = Integer.parseInt(userInput);
                if (userChoice < 1 || userChoice > 3) {
                    System.out.println("Invalid option ! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid option ! Please choose again");
            } 

        }

        switch (userChoice) {
            case (1):
                System.out.println();
                startGame();
                break;
        }
    }

    public void selectHumanPlayers() {
        
        Scanner sc = new Scanner(System.in);
        System.out.print("Select the number of players (from 2 to 6): ");
        int noOfPlayers = 0;

        while (!(noOfPlayers >= 2 && noOfPlayers <= 6)) {
            try {
                String numberChosen = sc.nextLine();
                noOfPlayers = Integer.parseInt(numberChosen);
                if (noOfPlayers < 2) {
                    System.out.println("Too little players, unable to start game ! Please choose again");
                } else if (noOfPlayers > 6) {
                    System.out.println("Too many players, unable to start game ! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid option ! Please choose again");
            } 

        }
        if (noOfPlayers == 2) {
            setTwoPlayerGame(true);
        } else {
            setTwoPlayerGame(false);
        }

        playerList = new ArrayList<>();
        int currentNumber = 1;
        while (noOfPlayers > 0) {
            System.out.print("Player " + currentNumber + "'s name: ");
            String playerNames = sc.nextLine();
            playerList.add(new Player(playerNames));
            currentNumber++;
            noOfPlayers--;
        }
        
        
        turnManager = new TurnManager(playerList.size());

        GameLogic.playTurn(deck,playerList,turnManager,isTwoPlayerGame);
       

    }

    public void aiDifficultyLevel() {
        System.out.println("===== Difficulty Level =====");
        System.out.println("1. Easy");
        System.out.println("2. Intermediate");
        System.out.println("3. Difficult");
        System.out.print("Choose Difficulty Level: ");
        Scanner sc = new Scanner(System.in);
        int levelNo = 0;

        while (!(levelNo >= 1 && levelNo <= 3)) {
            try {
                String chosenLevelNo = sc.nextLine();
                levelNo = Integer.parseInt(chosenLevelNo);
                if (levelNo < 1 || levelNo > 3) {
                    System.out.println("Invalid difficulty level ! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid difficulty level ! Please choose again");
            }

        }
        if (levelNo == 1) {
            this.setDifficulty(Difficulty.EASY);
        } else if (levelNo == 2) {
            this.setDifficulty(Difficulty.MEDIUM);
        } else if (levelNo == 3) {
            this.setDifficulty(Difficulty.HARD);
        } 
        
    }

    public void selectAiPlayers() {
        Scanner sc = new Scanner(System.in);
        System.out.println("Your name: ");
        String Human = sc.nextLine();
        System.out.print("Select the number of AI players (from 1 to 5): ");
        int noOfAIPlayers = 0;

        while (!(noOfAIPlayers >= 1 && noOfAIPlayers <= 5)) {
            try {
                String noOfAIPlayersChosen = sc.nextLine();
                noOfAIPlayers = Integer.parseInt(noOfAIPlayersChosen);
                if (noOfAIPlayers < 1) {
                    System.out.println("Too little AI players, unable to start game ! Please choose again");
                } else if (noOfAIPlayers > 5) {
                    System.out.println("Too many AI players, unable to start game ! Please choose again");
                }
               
            } catch (NumberFormatException e) {
                System.out.println("Invalid option ! Please choose again");
            } 

        }
        if (noOfAIPlayers == 1) {
            setTwoPlayerGame(true);
        } else {
            setTwoPlayerGame(false);
        }

        playerList = new ArrayList<>();
        playerList.add(new Player(Human)); 
        int currentNumber = 1;
        while (noOfAIPlayers > 0) {
            String aiName = "CPU" + Integer.toString(currentNumber);
            ComputerPlayer ai = new ComputerPlayer(aiName);
            ai.setGameDifficulty(difficulty);
            playerList.add(ai);
            currentNumber++;
            noOfAIPlayers--;
        }
        turnManager = new TurnManager(playerList.size());
        System.out.println(playerList);
        GameLogic.playTurn(deck, playerList, turnManager, isTwoPlayerGame);
    }

    public void startGame() {
        System.out.println("===== Game styles =====");
        System.out.println("1. Local Play");
        System.out.println("2. Play vs AI");
        System.out.println("3. Play Online");
        System.out.print("Choose game style: ");
        int styleNumber = 0;
        Scanner sc1 = new Scanner(System.in);

        while (!(styleNumber >= 1 && styleNumber <= 3)) {
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

        switch (styleNumber) {
            case (1):
                System.out.println();
                selectHumanPlayers();
                break;
            case(2):
                System.out.println();
                aiDifficultyLevel();
                selectAiPlayers();
                break;
            case(3):
                multiplayerMenu();
                System.out.println();
                break;
                
        }
    }

    public void multiplayerMenu(){

        //chose between host or client
        System.out.println("===== Host or Join on LAN =====");
        System.out.println("1. Host");
        System.out.println("2. Join");
        System.out.print("Choose option: ");

        int menuChoice = 0;
        Scanner sc1 = new Scanner(System.in);

        while (!(menuChoice >= 1 && menuChoice <= 2)) {
            try {
                String chosenStyle = sc1.nextLine();
                menuChoice = Integer.parseInt(chosenStyle);
                if (menuChoice < 1 || menuChoice > 2) {
                    System.out.println("Invalid game style! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid game style ! Please choose again");
            }

        }

        switch (menuChoice) {
            case (1): //run host game server 
                System.out.println();
                this.online = true;
                
                new GameServer().startServer(deck); //give the entire game object
                break;
            case(2): //run client game server
                this.online = true;
                System.out.println();
                new GameClient().startClient();
                //String[] arguments = {"localhost", "12345"};
                //setUpClient(arguments);
                
                
                //new GameClient().startClient(arguments);
        }

    }


    public static void checkGameEnd(boolean allColoursCollected) {
        // if (allColoursCollected || ) {
        // this.playTurn();

        // }
    }

    public static boolean checkGameEnd() {
        boolean gameEnd = false;

        // for (Player player : playerList) {
        //     if (player.allColoursCollected()) {
        //     gameEnd = true;
        //     break;
        // }
        // }

        // if (gameEnd || deck.getCards() == 0) {
        //     System.out.println("This is the last round!");
        //     playTurn(); // Last Round to end up with 4 cards, I need to see how the play
        //     // turn is being handled
        //     return true;
        // }

        return false;
    }

    public void endGame() {
        System.out.println("\nGame Over! Calculating scores...");
        // for (Player player : playerList) {
        //     int score = ScoringSystem.calculateScore(player);
        //     System.out.println(player.getName() + "1's final score: " + score);
        // }
        Scoring.calculateScores(playerList, this.isTwoPlayerGame());


        //bring back to main menu 
        
        printMenu();
        
    }

    public static void main(String[] args) {
        // ArrayList<Player> playerList = new ArrayList<>();
        // playerList.add(new Player("Alice"));
        // playerList.add(new Player("Bob"));
        // // playerList.add(new Player("Charlie"));

        Game game = new Game(playerList);
       
        game.printMenu();

    }
}

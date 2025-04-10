package game;

import entities.Deck;
import entities.Parade;
import entities.Player;
import entities.comp.*;
import entities.scoring.*;
import game.audio.SoundPlayer;
import game.online.*;
import java.util.*;
import javax.sound.sampled.*;

/**
 * Represents the main controller for the game logic and flow.
 * The Game class handles player setup (human and AI), game configuration
 * (such as difficulty level and number of players), game progression,
 * and end-of-game conditions. It also manages user interaction via menus,
 * audio playback, and the main game loop.
 */

public class Game {
    static ArrayList<Player> playerList = new ArrayList<>();
    private Deck deck;
    private Parade parade;
    private TurnManager turnManager;
    private Scanner scanner;
    private boolean online = false;
    private Difficulty difficulty;
    private boolean isTwoPlayerGame;
    private HighScoreDatabase highScore = new HighScoreDatabase();
    private static FloatControl volumeControl;
    private static Clip clip;

    /**
     * Constructs a game.
     *
     * @param playList The list of players playing the game
     */
    public Game(ArrayList<Player> playerList) {
        Game.playerList = playerList;
        this.deck = new Deck();
        // this.parade = Parade.Parade();
        this.turnManager = new TurnManager(playerList.size());
        this.scanner = new Scanner(System.in);
    }

    /**
     * Checks if there are a total of 2 players playing the game.
     *
     * @returns True if are only 2 players, false otherwise
     */
    public boolean isTwoPlayerGame() {
        return isTwoPlayerGame;
    }

    /**
     * Sets the game mode to either two-player or other mode.
     * 
     * @param isTwoPlayerGame {@code true} if the game should be set to two-player
     *                        mode;
     *                        {@code false} otherwise
     */
    public void setTwoPlayerGame(boolean isTwoPlayerGame) {
        this.isTwoPlayerGame = isTwoPlayerGame;
    }

    /**
     * Sets the game difficulty level to either EASY, MEDIUM or HARD mode
     * 
     * @param difficulty {@code EASY} to set the game to easy mode;
     *                   {@code MEDIUM} to set the game to medium mode;
     *                   {@code HARD} to set the game to hard mode
     */
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    /**
     * Displays the main menu and processes user input to navigate to the selected
     * option.
     */
    // start menu display
    public void printMenu() {
        System.out.println("========================");
        System.out.println("          MENU          ");
        System.out.println("========================");
        System.out.println("1. Start Game");
        System.out.println("2. Settings (Volume Control)");
        System.out.println("3. Help");
        System.out.println("4. Hall of Fame");
        int userChoice = 0;
        Scanner sc = new Scanner(System.in);
        String userInput = "a";
        while (!(userChoice >= 1 && userChoice <= 4) && !userInput.equals("q")) {
            try {
                System.out.print("Enter your choice (1-4) or press q to quit: ");
                userInput = sc.nextLine();
                if (userInput.equals("q")) {
                    break;
                } else {
                    userChoice = Integer.parseInt(userInput);
                    if (userChoice < 1 || userChoice > 4) {
                        System.out.println("Invalid option! Please choose again");
                    }
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid option! Please choose again");
            }

        }

        GameClient.clearConsole();
        if (!userInput.equals("q")) {
            switch (userChoice) {
                case (1):
                    startGame();
                    break;
                case (2):
                    volumeMenu();
                    break;
                case (3):
                    helpMenu();
                    break;
                case (4):
                    highScore.displayHighScores();

                    System.out.println();
                    promptReturnToMenu(sc);
                    break;
            }
        }
    }

    /**
     * Displays the game instructions and rules for the Parade card game.
     * 
     * This help menu provides players with a detailed explanation of the game's
     * objective, components, setup steps, gameplay mechanics, endgame conditions,
     * and scoring rules. It also includes adjustments for two-player games.
     * 
     * Once the instructions are printed, the method returns the player to the main
     * menu.
     */
    public void helpMenu() {
        // Define the complete set of instructions using a multi-line string
        String instructions = """
                === Parade Card Game Instructions ===

                Objective:
                Get the lowest score by collecting as few points (cards) as possible.

                Components:
                - 66 cards in 6 colors (11 cards per color).
                - Each color has cards numbered from 0 to 10.

                Setup:
                1. Shuffle all cards.
                2. Deal 5 cards to each player (hand cards).
                3. Place 6 cards face-up in a row to form the parade line.
                4. Place the rest as a draw pile.

                Gameplay (On Your Turn):
                1. Play 1 card from your hand to the end of the parade line.
                2. Count how many cards are in the parade line before your played card. Let’s say it’s X.
                3. From the X cards before the played card, check if any must be taken:
                   - A card must be taken if:
                     - Its color matches the played card, or
                     - Its value is less than or equal to the value of the played card.
                4. Take all the matching cards (color or lower/equal value).
                5. The remaining cards stay in the parade line.
                6. Draw a new card from the draw pile (unless the pile is empty).

                Endgame Trigger:
                - When one player has no cards left in their hand, or
                - The draw pile is depleted.
                Then, each player plays all remaining hand cards (one per turn, no drawing).

                Scoring:
                1. Separate your collected cards by color.
                2. In each color:
                   - If you have the most cards of that color (alone or tied), each card in that color counts as 1 point.
                   - Otherwise, add up the face values.
                3. Add up all points across all colors.
                4. Lowest score wins.

                Optional (2-player game):
                - Remove one color before starting.
                """;

        System.out.println(instructions);

        // Return to the main menu after displaying instructions
        printMenu();
    }

    /**
     * Prompts the user to press 1 to return to the menu.
     */
    private void promptReturnToMenu(Scanner sc) {
        int choice = 0;
        while (choice != 1) {
            System.out.print("Press 1 to go back to menu: ");
            try {
                choice = Integer.parseInt(sc.nextLine());
                if (choice != 1) {
                    System.out.println("Invalid option! Please choose again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a number.");
            }
        }
        GameClient.clearConsole();
        printMenu();
    }

    /**
     * Allows the user to choose the number of human players (between 2 and 6) and
     * enter a unique username for each player.
     * Ensures that player names are not blank and there are no duplicates of
     * existing names.
     */
    // choosing number of HUMAN players and name
    public void selectHumanPlayers() {

        Scanner sc = new Scanner(System.in);
        int noOfPlayers = 0;

        while (!(noOfPlayers >= 2 && noOfPlayers <= 6)) {
            try {
                System.out.print("Select the number of players (2-6): ");
                String numberChosen = sc.nextLine();
                noOfPlayers = Integer.parseInt(numberChosen);
                if (noOfPlayers < 2) {
                    System.out.println("Too little players, unable to start game! Please choose again");
                } else if (noOfPlayers > 6) {
                    System.out.println("Too many players, unable to start game! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid option! Please choose again");
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
            System.out.print("Enter player " + currentNumber + "'s name: ");
            String playerNames = sc.nextLine().trim();
            boolean alreadyExists = false;

            while (playerNames == "") {
                System.out.println("Name cannot be blank");
                System.out.print("Enter player " + currentNumber + "'s name: ");
                playerNames = sc.nextLine();
            }

            for (Player player : playerList) {
                if (player.getName().equalsIgnoreCase(playerNames)) {
                    alreadyExists = true;
                    break;
                }
            }

            while (alreadyExists) {
                System.out.println("That name is already taken! Please choose a different name");
                System.out.print("Enter player " + currentNumber + "'s name: ");
                playerNames = sc.nextLine().trim();

                alreadyExists = false;
                for (Player player : playerList) {
                    if (player.getName().equalsIgnoreCase(playerNames)) {
                        alreadyExists = true;
                        break;
                    }
                }
            }

            playerList.add(new Player(playerNames));
            currentNumber++;
            noOfPlayers--;
        }

        turnManager = new TurnManager(playerList.size());

        GameLogic.playTurn(deck, playerList, turnManager, isTwoPlayerGame);

    }

    /**
     * Allows the user to choose the difficulty level for the AI game style.
     */
    // choosing difficulty level for AI game style
    public void aiDifficultyLevel() {
        System.out.println("========================");
        System.out.println("    DIFFICULTY LEVEL    ");
        System.out.println("========================");
        System.out.println("1. Easy");
        System.out.println("2. Medium");
        System.out.println("3. Difficult");
        System.out.println("4. Back");
        System.out.print("Enter your choice (1-4): ");
        Scanner sc = new Scanner(System.in);
        int levelNo = 0;

        while (!(levelNo >= 1 && levelNo <= 4)) {
            try {
                String chosenLevelNo = sc.nextLine();
                levelNo = Integer.parseInt(chosenLevelNo);
                if (levelNo < 1 || levelNo > 4) {
                    System.out.println("Invalid difficulty level! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid difficulty level! Please choose again");
            }

        }

        if (levelNo == 1) {
            this.setDifficulty(Difficulty.EASY);
        } else if (levelNo == 2) {
            this.setDifficulty(Difficulty.MEDIUM);
        } else if (levelNo == 3) {
            this.setDifficulty(Difficulty.HARD);
        } else if (levelNo == 4) {
            startGame();
        }
        GameClient.clearConsole();
    }

    /**
     * Allows the user to choose the number of AI players (between 1 and 5).
     */
    // choosing number of AI players
    public void selectAiPlayers() {
        Scanner sc = new Scanner(System.in);
        System.out.print("Enter your name: ");
        String Human = sc.nextLine();
        int noOfAIPlayers = 0;

        while (!(noOfAIPlayers >= 1 && noOfAIPlayers <= 5)) {
            try {
                System.out.print("Select the number of AI players (1-5): ");
                String noOfAIPlayersChosen = sc.nextLine();
                noOfAIPlayers = Integer.parseInt(noOfAIPlayersChosen);
                if (noOfAIPlayers < 1) {
                    System.out.println("Too little AI players, unable to start game! Please choose again");
                } else if (noOfAIPlayers > 5) {
                    System.out.println("Too many AI players, unable to start game! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid option! Please choose again");
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

            if (ComputerPlayer.getGameDifficulty() == Difficulty.HARD && currentNumber == 2) {
                double prof_chance = Math.random(); // 40% chance YL, 40% Jason, 20% Bunny
                String result;
                if (prof_chance < 0.4) {
                    result = "AI Yeow Leong";
                } else if (prof_chance < 0.8) {
                    result = "AI Jason Chan";
                } else {
                    result = "AI VeryEvilCuteBunny";
                }
                String aiName = result;
                ComputerPlayer ai = new ComputerPlayer(aiName);
                ai.setGameDifficulty(difficulty);
                playerList.add(ai);
            } else {
                String aiName = "CPU" + Integer.toString(currentNumber);
                ComputerPlayer ai = new ComputerPlayer(aiName);
                ai.setGameDifficulty(difficulty);
                playerList.add(ai);
            }

            currentNumber++;
            noOfAIPlayers--;
        }
        turnManager = new TurnManager(playerList.size());
        // System.out.println(playerList);
        GameLogic.playTurn(deck, playerList, turnManager, isTwoPlayerGame);
    }

    /**
     * Allows the user to choose the type of game styles.
     */
    // choosing game style
    public void startGame() {
        System.out.println("=========================");
        System.out.println("       GAME STYLES       ");
        System.out.println("=========================");
        System.out.println("1. Local Play");
        System.out.println("2. Play vs AI");
        System.out.println("3. Play Online");
        System.out.println("4. Back");
        int styleNumber = 0;
        Scanner sc1 = new Scanner(System.in);

        while (!(styleNumber >= 1 && styleNumber <= 4)) {
            try {
                System.out.print("Enter your choice (1-4): ");
                String chosenStyle = sc1.nextLine();
                styleNumber = Integer.parseInt(chosenStyle);
                if (styleNumber < 1 || styleNumber > 4) {
                    System.out.println("Invalid game style! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid game style! Please choose again");
            }

        }

        GameClient.clearConsole();

        switch (styleNumber) {
            case (1):
                selectHumanPlayers();
                break;
            case (2):
                aiDifficultyLevel();
                selectAiPlayers();
                break;
            case (3):
                multiplayerMenu();
                System.out.println();
                break;
            case (4):
                printMenu();

        }
    }

    /**
     * Plays the background music throughout the game.
     * 
     * @param audioFile The music to be played
     */

    /**
     * Allows user to control the volume of the background music.
     */
    public void volumeMenu() {

        volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        Scanner sc = new Scanner(System.in);
        String input;
        float currentVolume = volumeControl.getValue(); // Default volume level

        System.out.println("Volume control: Type '+' to increase, '-' to decrease, 'q' to quit volume menu.");
        while (true) {
            input = sc.nextLine();
            if (input.equals("+")) {
                currentVolume = Math.min(currentVolume + 2.0f, volumeControl.getMaximum());
                volumeControl.setValue(currentVolume);
                System.out.println("Increased volume to: " + currentVolume);
            } else if (input.equals("-")) {
                currentVolume = Math.max(currentVolume - 2.0f, volumeControl.getMinimum());
                volumeControl.setValue(currentVolume);
                System.out.println("Decreased volume to: " + currentVolume);
            } else if (input.equals("q")) {
                GameClient.clearConsole();
                printMenu();
            } else {
                System.out.println("Invalid input. Use '+', '-', or 'q'.");
            }
        }

    }

    /**
     * Allows user to choose between Host or Client.
     */
    public void multiplayerMenu() {

        // chose between host or client
        System.out.println("=============================");
        System.out.println("     HOST OR JOIN ON LAN     ");
        System.out.println("=============================");
        System.out.println("1. Host");
        System.out.println("2. Join on LAN");
        System.out.println("3. Back");
        System.out.print("Enter your choice (1-3):");

        int menuChoice = 0;
        Scanner sc1 = new Scanner(System.in);

        while (!(menuChoice >= 1 && menuChoice <= 3)) {
            try {
                String chosenStyle = sc1.nextLine();
                menuChoice = Integer.parseInt(chosenStyle);
                if (menuChoice < 1 || menuChoice > 3) {
                    System.out.println("Invalid game style! Please choose again");
                }

            } catch (NumberFormatException e) {
                System.out.println("Invalid game style! Please choose again");
            }

        }

        GameClient.clearConsole();
        switch (menuChoice) {
            case (1): // run host game server
                System.out.println();
                this.online = true;

                new GameServer().startServer(deck); // give the entire game object
                break;
            case (2): // run client game server
                this.online = true;
                System.out.println();
                new GameClient().startClient();
                // String[] arguments = {"localhost", "12345"};
                // setUpClient(arguments);

                // new GameClient().startClient(arguments);
            case (3):
                startGame();
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
        // if (player.allColoursCollected()) {
        // gameEnd = true;
        // break;
        // }
        // }

        // if (gameEnd || deck.getCards() == 0) {
        // System.out.println("This is the last round!");
        // playTurn(); // Last Round to end up with 4 cards, I need to see how the play
        // // turn is being handled
        // return true;
        // }

        return false;
    }

    /**
     * Ends the game and starts calculating the scores of each player.
     */
    public void endGame() {
        System.out.println("\nGame Over! Calculating scores...");
        // for (Player player : playerList) {
        // int score = ScoringSystem.calculateScore(player);
        // System.out.println(player.getName() + "1's final score: " + score);
        // }
        Scoring.calculateScores(playerList, this.isTwoPlayerGame());

        // bring back to main menu

        printMenu();

    }

    public static void main(String[] args) {
        // ArrayList<Player> playerList = new ArrayList<>();
        // playerList.add(new Player("Alice"));
        // playerList.add(new Player("Bob"));
        // // playerList.add(new Player("Charlie"));

        Game game = new Game(playerList);

        SoundPlayer.playSound("audio-editor-output");
        game.printMenu();

    }
}

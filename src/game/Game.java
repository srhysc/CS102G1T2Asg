package game;

import entities.Deck;
import entities.Player;
import entities.comp.*;
import entities.scoring.*;
import game.audio.SoundPlayer;
import game.online.*;
import java.util.*;

/**
 * Represents the main controller for the game logic and flow.
 * 
 * The Game class handles player setup (human and AI), game configuration
 * (such as difficulty level and number of players), game progression,
 * and end-of-game conditions. It also manages user interaction via menus,
 * audio playback, and the main game loop.
 */

public class Game {
    static ArrayList<Player> playerList = new ArrayList<>();
    private Deck deck;
    private TurnManager turnManager;
    private Difficulty difficulty;
    private boolean isTwoPlayerGame;
    private HighScoreDatabase highScore = new HighScoreDatabase();

    /**
     * Prints a banner with a typing animation effect.
     * <p>
     * Each character in the banner is printed with a slight delay to simulate
     * real-time typing.
     * After each line is printed, a short pause is added before printing the next
     * line.
     * This method also ensures the output is flushed immediately to reflect the
     * animation in real time.
     * </p>
     *
     * @param banner An array of strings where each string represents a line in the
     *               banner.
     */
    public static void printBanner(String[] banner) {
        try {
            for (String line : banner) {
                for (char c : line.toCharArray()) {
                    System.out.print(c);
                    System.out.flush(); // make animation real-time
                    Thread.sleep(2); // delay per character
                }
                System.out.println();
                Thread.sleep(50); // delay per line
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore interrupt status
            System.out.println("Animation interrupted.");
        }
    }

    /**
     * Constructs a game.
     *
     * @param playerList The list of players playing the game
     */
    public Game(ArrayList<Player> playerList) {
        Game.playerList = playerList;
        this.deck = new Deck();
        this.turnManager = new TurnManager(playerList.size());
    }

    /**
     * Checks if there are a total of 2 players playing the game.
     *
     * @return True if are only 2 players, false otherwise
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
     * <p>
     * The menu options are:
     * <ul>
     * <li>1. Start Game</li>
     * <li>2. Settings (Volume Control)</li>
     * <li>3. Help</li>
     * <li>4. Hall of Fame</li>
     * </ul>
     * <p>
     * The user chooses an option by entering 1–4 or 'q' to exit the game.
     * When the user enters an invalid input, the program will prompt them to
     * enter a valid choice.
     * Once the input is validated, the corresponding method is called to proceed
     * with the selected option.
     * 
     */
    // start menu display
    public void printMenu() {
        String[] banner = new String[] {
                "\u001B[38;5;220m██████╗  █████╗ ██████╗  █████╗ ██████╗ ███████╗\u001B[0m", // Yellow
                "\u001B[38;5;214m██╔══██╗██╔══██╗██╔══██╗██╔══██╗██╔══██╗██╔════╝\u001B[0m", // Yellow-orange
                "\u001B[38;5;208m██████╔╝███████║██████╔╝███████║██║  ██║█████╗  \u001B[0m", // Orange
                "\u001B[38;5;202m██╔═══╝ ██╔══██║██╔══██╗██╔══██║██║  ██║██╔══╝  \u001B[0m", // Red-orange
                "\u001B[38;5;129m██║     ██║  ██║██║  ██║██║  ██║██████╔╝███████╗\u001B[0m", // Purple
                "\u001B[38;5;93m╚═╝     ╚═╝  ╚═╝╚═╝  ╚═╝╚═╝  ╚═╝╚═════╝ ╚══════╝\u001B[0m" // Dark purple
        };

        Game.printBanner(banner);

        System.out.println("\u001B[38;5;220m┌────────────────────────────────────────────┐");
        System.out.println(
                "\u001B[38;5;214m│                \u001B[38;5;220mMENU                        \u001B[38;5;214m│");
        System.out.println("\u001B[38;5;220m├────────────────────────────────────────────┤");
        System.out.println(
                "\u001B[38;5;214m│  \u001B[38;5;220m1. Start Game                             \u001B[38;5;214m│");
        System.out.println(
                "\u001B[38;5;214m│  \u001B[38;5;220m2. Settings (Volume Control)              \u001B[38;5;214m│");
        System.out.println(
                "\u001B[38;5;214m│  \u001B[38;5;220m3. Help                                   \u001B[38;5;214m│");
        System.out.println(
                "\u001B[38;5;214m│  \u001B[38;5;220m4. Hall of Fame                           \u001B[38;5;214m│");
        System.out.println("\u001B[38;5;220m├────────────────────────────────────────────┤");
        System.out.println(
                "\u001B[38;5;214m│  \u001B[38;5;129mq. Quit                                   \u001B[38;5;214m│");
        System.out.println("\u001B[38;5;220m└────────────────────────────────────────────┘\u001B[0m");

        int userChoice = 0;
        Scanner sc = new Scanner(System.in);
        String userInput = "a";
        while (!(userChoice >= 1 && userChoice <= 4) && !userInput.equals("q")) {
            try {
                System.out.print("Enter your choice (1-4) or press q to quit: ");
                userInput = sc.nextLine();
                if (userInput.equals("q")) {
                    System.exit(0);
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
        System.out.print("\033[H\033[2J");
        System.out.flush();
        GameClient.clearConsole();
        if (!userInput.equals("q")) {
            switch (userChoice) {
                case (1):
                    startGame();
                    break;
                case (2):
                    SoundPlayer.volumeMenu(this);
                    break;
                case (3):
                    helpMenu();

                    System.out.println();
                    promptReturnToMenu(sc);
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
        // Some Basic Instructions
        String instructions = """
                \u001B[38;5;220m██╗███╗   ██╗███████╗████████╗██████╗ ██╗   ██╗ ██████╗████████╗██╗ ██████╗ ███╗   ██╗███████╗
                \u001B[38;5;214m██║████╗  ██║██╔════╝╚══██╔══╝██╔══██╗██║   ██║██╔════╝╚══██╔══╝██║██╔═══██╗████╗  ██║██╔════╝
                \u001B[38;5;208m██║██╔██╗ ██║███████╗   ██║   ██████╔╝██║   ██║██║        ██║   ██║██║   ██║██╔██╗ ██║███████╗
                \u001B[38;5;202m██║██║╚██╗██║╚════██║   ██║   ██╔══██╗██║   ██║██║        ██║   ██║██║   ██║██║╚██╗██║╚════██║
                \u001B[38;5;129m██║██║ ╚████║███████║   ██║   ██║  ██║╚██████╔╝╚██████╗   ██║   ██║╚██████╔╝██║ ╚████║███████║
                \u001B[38;5;93m╚═╝╚═╝  ╚═══╝╚══════╝   ╚═╝   ╚═╝  ╚═╝ ╚═════╝  ╚═════╝   ╚═╝   ╚═╝ ╚═════╝ ╚═╝  ╚═══╝╚══════╝\u001B[0m

                \u001B[38;5;220m=== Parade Card Game Instructions ===\u001B[0m

                \u001B[38;5;214mObjective:\u001B[0m
                Get the \u001B[38;5;93mlowest score\u001B[0m by collecting as few points (cards) as possible.

                \u001B[38;5;214mComponents:\u001B[0m
                - 66 cards in \u001B[38;5;129m6 colors\u001B[0m (11 cards per color).
                - Each color has cards numbered from \u001B[38;5;220m0\u001B[0m to \u001B[38;5;220m10\u001B[0m.

                \u001B[38;5;214mSetup:\u001B[0m
                1. \u001B[38;5;220mShuffle\u001B[0m all cards.
                2. \u001B[38;5;220mDeal 5 cards\u001B[0m to each player (hand cards).
                3. \u001B[38;5;220mPlace 6 cards face-up\u001B[0m in a row to form the parade line.
                4. Place the rest as a draw pile.

                \u001B[38;5;214mGameplay (On Your Turn):\u001B[0m
                1. \u001B[38;5;208mPlay 1 card\u001B[0m from your hand to the end of the parade line.
                2. Count how many cards are in the parade line before your played card. Let’s say it’s \u001B[38;5;220mX\u001B[0m.
                3. From the \u001B[38;5;220mX cards\u001B[0m before the played card, check if any must be taken:
                - A card must be taken if:
                    - \u001B[38;5;129mIts color matches\u001B[0m the played card, or
                    - \u001B[38;5;129mIts value is less than or equal to\u001B[0m the value of the played card.
                4. \u001B[38;5;202mTake all the matching cards\u001B[0m (color or lower/equal value).
                5. The remaining cards stay in the parade line.
                6. \u001B[38;5;220mDraw a new card\u001B[0m from the draw pile (unless the pile is empty).

                \u001B[38;5;214mEndgame Trigger:\u001B[0m
                - When \u001B[38;5;208mone player has no cards\u001B[0m left in their hand, or
                - The \u001B[38;5;208mdraw pile is depleted\u001B[0m.
                Then, each player plays all remaining hand cards (one per turn, no drawing).

                \u001B[38;5;214mScoring:\u001B[0m
                1. \u001B[38;5;220mSeparate\u001B[0m your collected cards by color.
                2. In each color:
                - If you have the \u001B[38;5;129mmost cards\u001B[0m of that color (alone or tied), each card in that color counts as \u001B[38;5;220m1 point\u001B[0m.
                - Otherwise, \u001B[38;5;202madd up the face values\u001B[0m.
                3. \u001B[38;5;208mAdd up all points\u001B[0m across all colors.
                4. \u001B[38;5;93mLowest score wins!\u001B[0m
                """;

        System.out.println(instructions);

        // Return to the main menu after displaying instructions
        // printMenu();
    }

    /**
     * Prompts the user to press 1 to return to the menu.
     * When the user enters an invalid input, the program will prompt them
     * to enter a valid choice.
     * 
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
     * When the user enters an invalid input, the program will prompt them to enter
     * a valid choice.
     * Ensures that player names are not blank and there are no duplicates of
     * existing names.
     * 
     */
    // choosing number of HUMAN players and name
    public void selectHumanPlayers() {
        String[] banner = new String[] {
                "\u001B[38;5;220m██████╗ ██╗   ██╗██████╗ \u001B[0m", // Yellow
                "\u001B[38;5;214m██╔══██╗██║   ██║██╔══██╗\u001B[0m", // Yellow-orange
                "\u001B[38;5;208m██████╔╝██║   ██║██████╔╝\u001B[0m", // Orange
                "\u001B[38;5;202m██╔═══╝ ╚██╗ ██╔╝██╔═══╝ \u001B[0m", // Red-orange
                "\u001B[38;5;129m██║      ╚████╔╝ ██║     \u001B[0m", // Purple
                "\u001B[38;5;93m╚═╝       ╚═══╝  ╚═╝     \u001B[0m" // Dark purple
        };

        Game.printBanner(banner);

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

            while (playerNames.isEmpty()) {
                System.out.println("Name cannot be blank! Please choose a different name");
                System.out.print("Enter player " + currentNumber + "'s name: ");
                playerNames = sc.nextLine().trim();
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

                while (playerNames.isBlank()) {
                    System.out.println("Name cannot be blank! Please choose a different name");
                    System.out.print("Enter player " + currentNumber + "'s name: ");
                    playerNames = sc.nextLine().trim();
                }

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
     * <p>
     * The menu options are:
     * <ul>
     * <li>1. Easy</li>
     * <li>2. Medium</li>
     * <li>3. Difficult</li>
     * <li>4. Back</li>
     * </ul>
     * <p>
     * The user chooses an option by entering 1–4.
     * When the user enters an invalid input, the program will prompt them to
     * enter a valid choice.
     * Once the input is validated, the corresponding method is called to proceed
     * with the selected option.
     * 
     */
    // choosing difficulty level for AI game style
    public void aiDifficultyLevel() {
        String[] banner = new String[] {
                "\u001B[38;5;220m██████╗ ██╗   ██╗███████╗\u001B[0m", // Yellow
                "\u001B[38;5;214m██╔══██╗██║   ██║██╔════╝\u001B[0m", // Yellow-orange
                "\u001B[38;5;208m██████╔╝██║   ██║█████╗  \u001B[0m", // Orange
                "\u001B[38;5;202m██╔═══╝ ╚██╗ ██╔╝██╔══╝  \u001B[0m", // Red-orange
                "\u001B[38;5;129m██║      ╚████╔╝ ███████╗\u001B[0m" // Purple
        };

        Game.printBanner(banner);

        System.out.println("\u001B[38;5;220m╔════════════════════════╗");
        System.out.println("\u001B[38;5;214m║    DIFFICULTY LEVEL    ║");
        System.out.println("\u001B[38;5;220m╠════════════════════════╣");
        System.out.println("\u001B[38;5;208m║ 1. Easy                ║");
        System.out.println("\u001B[38;5;202m║ 2. Medium              ║");
        System.out.println("\u001B[38;5;129m║ 3. Difficult           ║");
        System.out.println("\u001B[38;5;93m║ 4. Back                ║");
        System.out.println("\u001B[38;5;220m╚════════════════════════╝");

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
            SoundPlayer.playSound("EasyBot");
        } else if (levelNo == 2) {
            this.setDifficulty(Difficulty.MEDIUM);
            SoundPlayer.playSound("MediumBot");
        } else if (levelNo == 3) {
            this.setDifficulty(Difficulty.HARD);
            SoundPlayer.playSound("HardBoss");
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
        String[] banner = new String[] {
                "\u001B[38;5;220m██████╗ ██╗   ██╗███████╗\u001B[0m", // Yellow
                "\u001B[38;5;214m██╔══██╗██║   ██║██╔════╝\u001B[0m", // Yellow-orange
                "\u001B[38;5;208m██████╔╝██║   ██║█████╗  \u001B[0m", // Orange
                "\u001B[38;5;202m██╔═══╝ ╚██╗ ██╔╝██╔══╝  \u001B[0m", // Red-orange
                "\u001B[38;5;129m██║      ╚████╔╝ ███████╗\u001B[0m" // Purple
        };

        Game.printBanner(banner);

        Scanner sc = new Scanner(System.in);
        String humanName; // Variable declaration outside the loop

        do {
            System.out.print("Enter your name: ");
            humanName = sc.nextLine().trim();

            if (humanName.isBlank()) {
                System.out.println("Name cannot be blank! Please choose a different name.");
            }
        } while (humanName.isBlank());

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
        playerList.add(new Player(humanName));
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
        GameLogic.playTurn(deck, playerList, turnManager, isTwoPlayerGame);
    }

    /**
     * Allows the user to choose the type of game styles.
     * <p>
     * The menu options are:
     * <ul>
     * <li>1. Local Play</li>
     * <li>2. Play vs AI</li>
     * <li>3. Play Online</li>
     * <li>4. Back</li>
     * </ul>
     * <p>
     * The user chooses an option by entering 1–4.
     * When the user enters an invalid input, the program will prompt them to
     * enter a valid choice.
     * Once the input is validated, the corresponding method is called to proceed
     * with the selected option.
     * 
     */
    // choosing game style
    public void startGame() {
        String[] banner = new String[] {
                "\u001B[38;5;220m██████╗  █████╗ ███╗   ███╗███████╗    ███████╗████████╗██╗   ██╗██╗     ███████╗███████╗\u001B[0m", // Yellow
                "\u001B[38;5;214m██╔════╝ ██╔══██╗████╗ ████║██╔════╝    ██╔════╝╚══██╔══╝╚██╗ ██╔╝██║     ██╔════╝██╔════╝\u001B[0m", // Yellow-orange
                "\u001B[38;5;208m██║  ███╗███████║██╔████╔██║█████╗      ███████╗   ██║    ╚████╔╝ ██║     █████╗  ███████╗\u001B[0m", // Orange
                "\u001B[38;5;202m██║   ██║██╔══██║██║╚██╔╝██║██╔══╝      ╚════██║   ██║     ╚██╔╝  ██║     ██╔══╝  ╚════██║\u001B[0m", // Red-orange
                "\u001B[38;5;129m╚██████╔╝██║  ██║██║ ╚═╝ ██║███████╗    ███████║   ██║      ██║   ███████╗███████╗███████║\u001B[0m", // Purple
                "\u001B[38;5;93m ╚═════╝ ╚═╝  ╚═╝╚═╝     ╚═╝╚══════╝    ╚══════╝   ╚═╝      ╚═╝   ╚══════╝╚══════╝╚══════╝\u001B[0m" // Dark
                                                                                                                                     // purple
        };
        Game.printBanner(banner);

        System.out.println("\u001B[38;5;220m╔════════════════════════╗");
        System.out.println("\u001B[38;5;214m║       GAME STYLES      ║");
        System.out.println("\u001B[38;5;220m╠════════════════════════╣");
        System.out.println("\u001B[38;5;214m║ 1. Local Play          ║");
        System.out.println("\u001B[38;5;208m║ 2. Play vs AI          ║");
        System.out.println("\u001B[38;5;202m║ 3. Play Online         ║");
        System.out.println("\u001B[38;5;129m║ 4. Back                ║");
        System.out.println("\u001B[38;5;220m╚════════════════════════╝");
        System.out.println("\u001B[0m");

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
     * Allows user to choose between Host or Client.
     * <p>
     * The menu options are:
     * <ul>
     * <li>1. Host</li>
     * <li>2. Join on LAN</li>
     * <li>3. Back</li>
     * </ul>
     * <p>
     * The user chooses an option by entering 1–3.
     * When the user enters an invalid input, the program will prompt them to
     * enter a valid choice.
     * Once the input is validated, the corresponding method is called to proceed
     * with the selected option.
     * 
     */
    public void multiplayerMenu() {
        String[] banner = new String[] {
                "\u001B[38;5;220m██████╗ ███╗   ██╗██╗     ██╗███╗   ██╗███████╗\u001B[0m", // Yellow
                "\u001B[38;5;214m██╔═══██╗████╗  ██║██║     ██║████╗  ██║██╔════╝\u001B[0m", // Yellow-orange
                "\u001B[38;5;208m██║   ██║██╔██╗ ██║██║     ██║██╔██╗ ██║█████╗  \u001B[0m", // Orange
                "\u001B[38;5;202m██║   ██║██║╚██╗██║██║     ██║██║╚██╗██║██╔══╝  \u001B[0m", // Red-orange
                "\u001B[38;5;129m╚██████╔╝██║ ╚████║███████╗██║██║ ╚████║███████╗\u001B[0m", // Purple
                "\u001B[38;5;93m ╚═════╝ ╚═╝  ╚═══╝╚══════╝╚═╝╚═╝  ╚═══╝╚══════╝\u001B[0m" // Dark purple
        };
        Game.printBanner(banner);

        // chose between host or client
        System.out.println("\u001B[38;5;220m╔════════════════════════╗");
        System.out.println("\u001B[38;5;220m║  HOST OR JOIN ON LAN   ║");
        System.out.println("\u001B[38;5;220m╠════════════════════════╣");
        System.out.println("\u001B[38;5;214m║ 1. Host                ║");
        System.out.println("\u001B[38;5;208m║ 2. Join on LAN         ║");
        System.out.println("\u001B[38;5;202m║ 3. Back                ║");
        System.out.println("\u001B[38;5;220m╚════════════════════════╝");
        System.out.print("\u001B[38;5;220mEnter your choice (1-3): \u001B[0m");

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

                new GameServer().startServer(deck); // give the entire game object
                break;
            case (2): // run client game server
                System.out.println();
                new GameClient().startClient();
            case (3):
                startGame();
        }

    }

    /**
     * Ends the game and starts calculating the scores of each player.
     */
    public void endGame() {
        System.out.println("\nGame Over! Calculating scores...");
        Scoring.calculateScores(playerList, this.isTwoPlayerGame());

        // bring back to main menu

        printMenu();

    }

    /**
     * The main entry point of Parade Game.
     * 
     * @param args The command-line arguments (not used in this method)
     */
    public static void main(String[] args) {

        Game game = new Game(playerList);

        SoundPlayer.playSound("audio-editor-output");
        game.printMenu();

    }
}

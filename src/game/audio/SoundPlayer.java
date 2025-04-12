package game.audio;

import game.Game;
import game.online.GameClient;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

/**
 * Manages audio playback and volume control for the game.
 * 
 * The SoundPlayer class provides methods to play sound files from the resources folder and to adjust 
 * the volume of the currently playing audio clip interactively.
 *
 * <p>This class is designed to ensure that only one audio clip is active at a time.
 * Before playing a new sound, it stops any clip that is currently running.
 * Additionally, it offers a volume control menu that listens for user inputs through
 * the console, allowing the adjustment of sound levels during gameplay.</p>
 *
 * <p>Dependencies:
 * <ul>
 *     <li>Java Sound API: AudioSystem, AudioInputStream, Clip, FloatControl</li>
 *     <li>Standard IO classes: File, IOException</li>
 *     <li>Scanner for reading console input</li>
 *     <li>Classes from the game framework: Game and GameClient</li>
 * </ul>
 *
 */
public class SoundPlayer {
    private static Clip currentClip;
    private static FloatControl volumeControl;

    /**
     * Plays a sound file specified by its path (without extension) from the resources folder.
     *
     * <p>This method attempts to load a WAV file from the "resources" directory by appending
     * the ".wav" extension to the provided file path. Before playing the new clip, it stops
     * any previously playing clip to ensure that audio does not overlap. Once the new audio clip
     * is started, it is set to loop 500 times.</p>
     *
     * @param soundFilePath The relative file path (minus extension) to the sound file.
     *
     * @throws UnsupportedAudioFileException if the specified audio file is not supported.
     * @throws IOException if there is an error while reading the file.
     * @throws LineUnavailableException if a clip cannot be opened due to resource restrictions.
     *
     * <p>Note: The exceptions are caught within the method and a message is printed to the console.
     * In a production environment, better error handling might be required.</p>
     */
    // Method to play a sound file
    public static void playSound(String soundFilePath) {
        try {
            File soundFile = new File("resources/" + soundFilePath  + ".wav");
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();

            // Stop the previous clip if it's playing
            if (currentClip != null && currentClip.isRunning()) {
                currentClip.stop();
            }

            clip.open(audioIn);
            clip.start(); // Start playing the new sound
            clip.loop(500);
            currentClip = clip; // Set the currentClip to the new one

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Audio File not supported or unavailable. Terminating program...");
        }
    }

    /**
     * Allows user to control the volume of the background music.
     * The user can:
     * <ul>
     *   <li>Press '+' to increase the volume by 2.0 units (up to the maximum level).</li>
     *   <li>Press '-' to decrease the volume by 2.0 units (down to the minimum level).</li>
     *   <li>Press 'm' to mute the audio (set volume to the minimum level).</li>
     *   <li>Press 'q' to quit the volume menu and return to the main game menu.</li>
     * </ul>
     * <p>
     * The method uses a {@link Scanner} to read user input from the console and updates the 
     * {@link FloatControl} associated with the current audio clip to reflect changes in volume.
     *
     * @param game the current game instance; used to return to the main menu after the user quits the volume menu.
     */

    public static void volumeMenu(Game game) {

        volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
        Scanner sc = new Scanner(System.in);
        String input;
        float currentVolume = volumeControl.getValue(); // Default volume level

        System.out.println("Volume control: Type '+' to increase, '-' to decrease,'m' to mute, 'q' to quit volume menu.");
        while (true) {
            input = sc.nextLine();
            if (input.equals("+")) {
                currentVolume = Math.min(currentVolume + 2.0f, volumeControl.getMaximum());
                volumeControl.setValue(currentVolume);
                GameClient.clearConsole();
                System.out.println("Increased volume to: " + currentVolume + ", press 'q' to quit back to main menu.");
            } else if (input.equals("-")) {
                currentVolume = Math.max(currentVolume - 2.0f, volumeControl.getMinimum());
                volumeControl.setValue(currentVolume);
                GameClient.clearConsole();
                System.out.println("Decreased volume to: " + currentVolume + ", press 'q' to quit back to main menu.");
            } else if (input.equals("m")){
                volumeControl.setValue(volumeControl.getMinimum());
                GameClient.clearConsole();
                System.out.println("Decreased volume to: " + currentVolume + ", press 'q' to quit back to main menu.");
            
            } else if (input.equals("q")) {
                GameClient.clearConsole();
                game.printMenu();
            } else {
                System.out.println("Invalid input. Use '+', '-', 'm' or 'q'.");
            }
        }

    }
}

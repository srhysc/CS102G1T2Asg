package game.audio;

import game.Game;
import game.online.GameClient;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.sound.sampled.*;

public class SoundPlayer {
    private static Clip currentClip;
    private static FloatControl volumeControl;


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
     */
    public static void volumeMenu(Game game) {

        volumeControl = (FloatControl) currentClip.getControl(FloatControl.Type.MASTER_GAIN);
        Scanner sc = new Scanner(System.in);
        String input;
        float currentVolume = volumeControl.getValue(); // Default volume level

        System.out.println("Volume control: Type '+' to increase, '-' to decrease, 'q' to quit volume menu.");
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
            } else if (input.equals("q")) {
                GameClient.clearConsole();
                game.printMenu();
            } else {
                System.out.println("Invalid input. Use '+', '-', or 'q'.");
            }
        }

    }
}

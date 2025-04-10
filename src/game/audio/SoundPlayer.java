package game.audio;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundPlayer {
    private static Clip currentClip;

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
            currentClip = clip; // Set the currentClip to the new one

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }
}

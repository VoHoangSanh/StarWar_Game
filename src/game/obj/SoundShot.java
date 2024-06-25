package game.obj;

import javax.sound.sampled.*;
import java.io.IOException;
import java.net.URL;

public class SoundShot {
    public static void playShotSound() {
        playSound("/game/soundFX/shot-lazer.wav");
    }



    public static void playExplosionSound() {
        playSound("/game/soundFX/hit.wav");
        
    }

    private static void playSound(String soundFile) {
        try {
            URL url = SoundShot.class.getResource(soundFile);
            if (url == null) {
                System.err.println("Could not find sound file: " + soundFile);
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException | NullPointerException e) {
            e.printStackTrace();
        }
    }
}
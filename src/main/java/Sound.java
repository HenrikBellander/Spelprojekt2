import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
public class Sound {
    //instance methods
    public void playSound(String filepath) {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(filepath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioInputStream);
            clip.start();
        }catch(Exception e){
            System.out.println("Error in soundplayer: ");
            e.printStackTrace();
        }
    }
}


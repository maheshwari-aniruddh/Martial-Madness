import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

//plays sounds lmao
// put your wave files in a sounds/folder next to the project
// if the file isnt there it just skips so it wont crash

public class SoundManager
{
    private static Clip musicClip;

    private static AudioInputStream open(String fileName) throws Exception
    {
        java.net.URL url = SoundManager.class.getResource("/sounds/"+fileName);
        if(url != null)
        {
            return AudioSystem.getAudioInputStream(url);
        }
        return AudioSystem.getAudioInputStream(new File("sounds/"+fileName));
    }

    public static void play(String fileName)
    {
        try{
            AudioInputStream audioIn = open(fileName);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        }
        catch(Exception e)
        {
        }
    }

    public static void punch()
    {
        play("punch.wav");
    }

    public static void kick()
    {
        play("kick.wav");
    }

    public static void block()
    {
        play("block.wav");
    }

    public static void combo()
    {
        play("combo.wav");
    }

    public static void win()
    {
        play("win.wav");
    }

    public static void lose()
    {
        play("lose.wav");
    }

    public static void roundhouse()
    {
        play("roundhouse.wav");
    }

    public static void music()
    {
        try
        {
            if(musicClip!= null)
            {
                musicClip.stop();
                musicClip.close();
            }
            AudioInputStream audioIn = open("music.wav");
            musicClip = AudioSystem.getClip();
            musicClip.open(audioIn);
            musicClip.loop(Clip.LOOP_CONTINUOUSLY);
        }
        catch(Exception e)
        {
            System.out.println("music error");
        }
    }

    public static void stopMusic()
    {
        if(musicClip!= null)
        {
            musicClip.stop();
        }
    }
    
}
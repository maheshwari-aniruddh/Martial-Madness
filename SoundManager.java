import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

//plays sounds lmao
// put your wave files in a sounds/folder next to the project
// if the file isnt there it just skips so it wont crash

public class SoundManager
{
    private static Clip musicClip;

    public static void play(String fileName)
    {
        File f = new File("sounds/"+fileName);

        if(!f.exists())
        {
            return;
        }

        try{
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            clip.start();
        }
        catch(Exception e)
        {
            System.out.println("sound error: "+fileName);
        }
    }

    public static void punch()
    {
        play("punch.wav");
    }

    public static void kick()
    {
        play("kick.wave");
    }

    public static void block()
    {
        play("block.wav");
    }

    public static void combo()
    {
        play(combo.wav);
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
        File f = new File();
    }
}
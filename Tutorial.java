import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.smartcardio.Card;
import javax.smartcardio.CardPermission;
import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Insets;

import javax.swing.Timer;
import javax.swing.border.Border;

import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

class Tutorial extends JPanel implements KeyListener, ActionListener
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private Image clouds;
    private LevelPanelHolder lph;
    private Image bottom;
    private Image myFrames[][];

    private String[] instructions;
    private int step;

    private final int PUNCH = 0;
    private final int BLOCK = 1;
    private final int KICK = 2;
    private final int UPPERKICK = 3;
    private final int ROUNDHOUSE = 4;
    private final int FORWARD = 5;
    private final int BACKWARD= 6;

    private Timer frameTimer;
    private Image backPic;
    private int currentFrame;
    private int animationPlaying;
    private int imageX,imageY;
    private Image defaultImage;

    private Timer frameTimer;
    private Image backPic;
    private int currentFrame;
    private int animationPlaying;
    private int imageX, imageY;
    private Image defaultImage;

    private final int TOTAL_ANIMATIONS = 7;
    private int delayCounter;
    private final int DELAY_FRAMES =7;
    private Information info;
    
    public Tutorial(MartialMadnessHolder mmhIn, CardLayout cardsIn, LevelPanelHolder lphIn, GamePanel gpIn, Information infoIn)
    {
        info = infoIn;
        mmh = mmhIn;
        cards = cardsIn;
        lph = lphIn;
        defaultImage = info.getImage("default.png");
        bottom = info.getImage("bottom copy.ong");
        myFrames = gpIn.getAnimationArray();
        FixedPanelHolder fph = new FixedPanelHolder(mmhIn, cardsIn, true, lphI)
        setLayout(new BorderLayout());
        clouds = info.getMyImage("cloud.jpg");
        add(fph, BorderLayout.SOUTH);
        addKeyListener(this);
        frameTimer = new Timer(125, this);
        requestFocusInWindow();
        imageX = 200;
        imageY = 215;
        animationPlaying = -1;

        delayCounter = 0;
        step = 0;
        instructions = new String[] {"Press F to Punch", "Press D to Block","Press V to kick","Press R to uppercut", "Press E to uppercut"
            , "Press left arrow key to move left", "Press right arrow to move right")
        };
        public void openTextDialog()
        {
            JDialog dialog = new JDialog();
            dialog.setResizable(false);
            dialog.setTitle("Extra Instructions");
            dialog.setSize(600,400);
            JTextArea textArea = new JTextArea("Here are some extra instrctions to remember: \n\n - The enemy moves dynamically with you if you move forward, he moves forward, if you go back, he goes back"
            + "You can never hurt the enemy, this game is about learning to defend youself, not hurting others" +
            "\n\n The enemy's health auto depletes");
        }


    }







}

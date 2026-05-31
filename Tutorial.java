import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;

import javax.swing.JDialog;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Insets;

import javax.swing.Timer;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Font;

class Tutorial extends JPanel implements KeyListener, ActionListener
{
    private MartialMadnessHolder mmh; // instance of MartialMadnessHolder class
    private CardLayout cards; // CardLayout object to use the card layout methods
    private Image clouds; // background image for the tutorial panel
    private LevelPanelHolder lph; // the instance of the level panel holder
    private Image bottom; // bottom floor image
    private Image[][] myFrames;

    private String[] instructions; // string array containing the keybind instruction
    private int step; // the key pressed by user

    // Constants for the index of the different moves in the array
    private final int PUNCH = 0;
    private final int BLOCK = 1;
    private final int KICK = 2;
    private final int UPPERCUT = 3;
    private final int ROUNDHOUSE = 4;
    private final int FORWARD = 5;
    private final int BACKWARD = 6;

    private Timer frameTimer; // timer for the animations, determines speed of the animation
    private Image backPic; // background pics for all the animations.
    private int currentFrame; // the index of the current frame that is playing
    private int animationPlaying; // which type of animation playing (eg BLOCK, PUNCH, KICK)
    private int imageX, imageY; // x and y of the image to move left and right
    private Image defaultImage; // default stance of the player

    private final int TOTAL_ANIMATIONS = 7; // total animations in the game
    private int delayCounter; // integer to delay the last frame
    private final int DELAY_FRAMES = 3; // how long the last frame loads for
    private Information info; // instance of information class

    public Tutorial(MartialMadnessHolder mmhIn, CardLayout cardsIn, LevelPanelHolder lphIn, GamePanel gpIn, Information infoIn)
    {
        info = infoIn;
        mmh = mmhIn;
        cards = cardsIn;
        lph = lphIn;
        defaultImage = info.getImage("default.png");
        bottom = info.getImage("bottom copy.png");
        myFrames = gpIn.getAnimationArray();
        FixedPanelHolder fph = new FixedPanelHolder(mmh, cards, true, lph);
        setLayout(new BorderLayout());
        clouds = info.getImage("clouds.jpg");
        add(fph, BorderLayout.SOUTH);
        addKeyListener(this);
        frameTimer = new Timer(125, this);
        requestFocusInWindow();
        imageX = 200;
        imageY = 215;
        animationPlaying = -1;

        delayCounter = 0;
        step = 0;
        instructions = new String[] { "Press F to Punch",
                "Press D to Block",
                "Press V to Kick",
                "Press R to Uppercut",
                "Press E to Roundhouse",
                "Press Left Arrow Key to Move Left",
                "Press RIGHT Arrow Key to Move Right" };
    }

    // JDialog box with all extra instructions that opens after the final step in the tutorial
    public void openTextInputDialog()
    {
        JDialog dialog = new JDialog();
        dialog.setResizable(false);
        dialog.setTitle("Extra Instructions");
        dialog.setSize(600, 400);
        JTextArea textArea = new JTextArea("Here are some extra instructions to remember:" +
                "\n\n - The enemy moves dynamically with you, if you move forward he moves forward, if you go back, he goes back" +
                "\n\n - You can NEVER hurt the enemy, this game is about learning to defend yourself not hurting others" +
                "\n\n - The enemy's health auto depletes at a certain rate and you have to survive until they die" +
                "\n\n - Every time you hit the enemy, you get one point, and this compounds throughout your whole gameplay so after finishing all six levels, you get to the top of the leaderboard if you have the most points" +
                "\n\n - Helpful tip: Answering the questions in the extra credit allow you to get up to 15 more points and there are five multiple-choice questions so try to do them" +
                "\n\n - IMPORTANT: USE BLOCK TO STOP TAKING DAMAGE FROM THE ENEMY", 10, 30);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setMargin(new Insets(10, 10, 10, 10));
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        dialog.add(scrollPane, BorderLayout.CENTER);

        textArea.setEditable(false);
        dialog.setVisible(true);
    }

    // Key event method to see which key was pressed and displaying the appropriate animation
    public void keyPressed(KeyEvent evt)
    {
        requestFocusInWindow();
        int keyCode = evt.getKeyCode();
        if (step == 0)
        {
            if (keyCode == KeyEvent.VK_F)
            {
                setAnimation(PUNCH);
                step++;
            }
        }
        else if (step == 1)
        {
            if (keyCode == KeyEvent.VK_D)
            {
                setAnimation(BLOCK);
                step++;
            }
        }
        else if (step == 2)
        {
            if (keyCode == KeyEvent.VK_V)
            {
                setAnimation(KICK);
                step++;
            }
        }
        else if (step == 3)
        {
            if (keyCode == KeyEvent.VK_R)
            {
                setAnimation(UPPERCUT);
                step++;
            }
        }
        else if (step == 4)
        {
            if (keyCode == KeyEvent.VK_E)
            {
                setAnimation(ROUNDHOUSE);
                step++;
            }
        }
        else if (step == 5)
        {
            if (keyCode == KeyEvent.VK_LEFT)
            {
                setAnimation(BACKWARD);
                step++;
            }
        }
        else if (step == 6)
        {
            if (keyCode == KeyEvent.VK_RIGHT)
            {
                setAnimation(FORWARD);
            }
            step++;
        }
        if (step == 7)
        {
            openTextInputDialog();
        }
        repaint();
    }

    // This starts the player animation and resets the variables for the animations
    public void setAnimation(int typeIn)
    {
        animationPlaying = typeIn;
        currentFrame = 0;
        delayCounter = 0;
        frameTimer.start();
    }

    public void keyTyped(KeyEvent evt)
    {
    }

    public void keyReleased(KeyEvent evt)
    {
    }

    public void actionPerformed(ActionEvent evt)
    {
        Timer timerName = (Timer) evt.getSource();
        if (timerName == frameTimer)
        {
            if (animationPlaying >= 0)
            {
                int positionAdjustment = 0;

                // Calculate movement based on animation type
                if (animationPlaying == FORWARD)
                {
                    imageX += 30;
                }
                else if (animationPlaying == BACKWARD)
                {
                    imageX -= 30;
                }

                // Move to the next frame if available
                if (currentFrame < myFrames[animationPlaying].length - 1)
                {
                    currentFrame++;
                }
                // Otherwise, delay on the last frame
                else if (delayCounter < DELAY_FRAMES)
                {
                    delayCounter++;
                }
                // Reset animation when delay is done
                else
                {
                    currentFrame = 0;
                    animationPlaying = -1;
                    delayCounter = 0;
                }
            }
        }
        repaint();
    }

    // paints the instructions and background images and animations
    public void paintComponent(Graphics g)
    {
        requestFocusInWindow();
        super.paintComponent(g);

        g.drawImage(clouds, 0, -20, 800, 600, this);
        g.setColor(Color.ORANGE);
        g.drawImage(bottom, 0, 400, this);

        g.setColor(new Color(245, 235, 225));
        g.setFont(new Font("Arial", Font.BOLD, 24));

        if (animationPlaying >= 0 && animationPlaying < TOTAL_ANIMATIONS)
        {
            g.drawImage(myFrames[animationPlaying][currentFrame], imageX, imageY, 200, 200, this);
        }
        else
        {
            g.drawImage(defaultImage, imageX, imageY, 200, 200, this);
        }
        // draw the instructions after every step
        if (step < instructions.length)
        {
            g.drawString(instructions[step], 50, 50);
        }
        else
        {
            g.drawString("Tutorial Complete! Well done!", 50, 50);
        }
    }
}

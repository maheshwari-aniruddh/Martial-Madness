import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.KeyListener;
import java.awt.event.ActionEvent;
import java.awt.*;
import javax.swing.Timer;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.util.Scanner;

class BossRushPanel extends JPanel implements ActionListener, KeyListener, FocusListener
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private LevelPanelHolder lph;
    private Information info;

    private final int PUNCH = 0;
    private final int BLOCK = 1;
    private final int KICK = 2;
    private final int UPPERCUT = 3;
    private final int ROUNDHOUSE = 4;
    private final int FORWARD = 5;
    private final int BACKWARD = 6;
    private final int TOTAL_ANIMATIONS = 7;

    private double[][] rounds = {
        {5.0,6,5000.0},
        {7.0,5,4500.0},
        {10.0,4,3500.0},
        {13.0,3,2500.0},
        {15.0,2,2000.0},
        {20.0,1,1500.0}
    };

    private int roundNumber;
    private int score;
    private long roundStartTime;

    private static final int ARCH_BALANCED = 0;
    private static final int ARCH_AGGRESSIVE = 1;
    private static final int ARCH_DEFENSIVE = 2;
    private int enemyArchetype;
    private long lastTimePlayerAttacked = 0;

    private Image backPic;
    private Image bottom;

    private Image[][] animationFrames;
    private Image[][] enemyFrames;
    private Image defaultImage;
    private Image enemyDefaultImage;

    private Image[][] defaultPlayerFrames;
    private Image defaultPlayerStance;
    private Image[][] defaultEnemyFrames;
    private Image defaultEnemyStance;
    private Image[][] ryuPlayerFrames;
    private Image ryuPlayerStance;
    private Image[][] ryuEnemyFrames;
    private Image ryuEnemyStance;
    private Image[][] zangiefPlayerFrames;
    private Image zangiefPlayerStance;
    private Image[][] zangiefEnemyFrames;
    private Image zangiefEnemyStance;

    private int currentFrame;
    private int animationPlaying;
    private int currentAnimation;
    private int delayCounter;
    private final int DELAY_FRAMES = 3;

    private int imageX, imageY;
    private int enemyImageX, enenmyImageY;

    private int enemyCurrentFrame = 0;


}






public class BossRushPanel {

}

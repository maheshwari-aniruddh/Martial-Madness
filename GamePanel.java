import java.awt.Graphics;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.KeyEvent;
import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import apple.laf.JRSUIUtils.Images;

import javax.swing.JProgressBar;
import javax.swing.Timer;
import javax.swing.border.Border;

import java.awt.Image;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

class GamePanel extends JPanel, implements ActionListener, KeyListener,FocusListener, ChangeListener 
{
    private Color myProgressColor;
    private Color enemyProgressColor;
    private boolean hasFocus;
    private boolean pointGiven;
    private boolean haveWon;
    private int gameOver;
    private MartialMadnessHolder mmh;
    private Timer frameTimer;
    private CardLayout cards;
    private Image backPic;
    private JProgressBar myHealth, enemyHealth;
    private LevelPanelHolder lph;
    private String pictName;
    
    private Image[][] animationFrames;
    private Image[][] enemyFrames;
    private int currentFrame;
    private int animationPlaying;
    private int imageX, imageY;
    private int enemyImageX, enemyImageY;
    private boolean firstTime;
    private Image defaultImage;
    private Image enemyDefaultImage;


    private final int PUNCH = 0;
    private final int BLOCK = 1;
    private final int KICK = 2;
    private final int UPPERCUT = 3;
    private final int ROUNDHOUSE = 4;
    private final int FORWARD = 5;
    private final int BACKWARD = 6;
    private final int TOTAL_ANIMATIONS= 7;
    private int currentAnimation;

    private int delayCounter;
    private final int DELAY_FRAMES = 3;
    
    private Image bottom;
    private Timer countDownTimer;
    private int timeRemaining = 60;
    private boolean countDownStarted = false;

    private int enemyCurrentFrame = 0;
    private int enemyAnimationPlaying = -1;
    private int enemyDelayCounter = 0;
    private Timer enemyAttackTimer;
    private Timer enemyHealthTimer;
    private static final int MAX_ATTACK_RANGE = 200;
    private final int PUNCH_REBOUND = 75;
    private final int KICK_REBOUND  = 100;
    private final int UPPERCUT_REBOUND = 125;
    private final int ROUNDHOUSE_REBOUND = 600;
    private int ENEMY_FOLLOW_SPEED;
    private int ENEMY_ATTACK_INTERVAL;
    private double ENEMY_HEALTH_DEPLETION_RATE;

    private final int PUNCH_DAMAGE = 20;
    private final int KICK_DAMAGE = 25;
    private final int UPPERCUT_DAMAGE= 30;
    private final int ROUNDHOUSE_DAMAGE = 40;

    private boolean showPlayerAttackIndicator = false;
    private boolean showEnemyAttackIndicator = false;
    private int attackIndicatorX = 0;
    private int attackIndicatorY = 0;
    private double[] level;
    private Information info;

    public GamePanel(MartialMadnessHolder mmhIn, CardLayout cardsIn, LevelPanelHolder lphIn, Information infoIn, String pictNameIn, double[] levelIn)
    {
        info = infoIn;
        level = levelIn;
        pictName = pictNameIn;
        mmh = mmhIn;
        cards = cardsIn;
        lph = lphIn;
        currentFrame = 0;
        imageX = 200;
        imageY = 215;

        enemyImageX = 450;
        enemyImageY = 215;
        animationPlaying =-1;
        currentAnimation = -1;
        firstTime = true;
        delayCounter = 0;
        haveWon = false;
        pointGiven = true;

        ENEMY_FOLLOW_SPEED = (int)level[0];
        ENEMY_HEALTH_DEPLETION_RATE = level[1];
        ENEMY_ATTACK_INTERVAL = (int)level[2];

        myProgressColor = Color.GREEN;
        enemyProgressColor = Color.GREEN;
        myHealth = new JProgressBar(0,100);
        enemyHealth = new JProgressBar(0,100);
        defaultImage = info.getImage("default.png");
        defaultImage = info.getImage("enemy_default.png");

        setLayout(new BorderLayout());
        JPanel healthPanel = new JPanel();
        healthPanel.setLayout(null);
        healthPanel.setOpaque(false);
        healthPanel.setPreferredSize(new Dimension(800,70));

        myHealth.setValue(100);
        myHealth.setBounds(150,30,200,30);
        myHealth.setForeground(Color.GREEN);
        myHealth.setBorderPainted(true);
        myHealth.setBackground(Color.DARK_GRAY);
        myHealth.setStringPainted(true);
        myHealth.setString("Player Health");
        healthPanel.add(myHealth);

        enemyHealth.setValue(100);
        enemyHealth.setBounds(450,30,200,30);
        enemyHealth.setForeground(Color.RED);
        enemyHealth.setBorderPainted(true);
        enemyHealth.setBackground(Color.DARK_GRAY);
        enemyHealth.setString("Enemy Health");
        healthPanel.add(enemyHealth);

        myHealth.addChangeListener(this);
        enemyHealth.addChangeListener(this);

        loadAnimations("animations");
        loadAnimations("enemy_animations");

        FixelPanelHolder fph = new FixedPanelHolder(mmh, cards, true, lph);

        backPic = info.getMyImage(pictName);

        bottom = info.getMyImage("bottom copy.png");
        add(fph, BorderLayout.SOUTH);


        addKeyListener(this);
        addFocusListener(this);
        frameTimer = new Timer(105, this); 
        TimerHandler th = new TimerHandler



    }























}

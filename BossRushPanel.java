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
    private int enemyImageX, enemyImageY;

    private int enemyCurrentFrame = 0;
    private int enemyAnimationPlaying = -1;
    private int enemyDelayCounter = 0;

    private int playerHealth;
    private int enemyHealth;
    private int PLAYER_MAX_HEALTH;
    private int PLAYER_SPEED;
    private int FRAME_DELAY_MS;
    private double COMBO_MULT;

    private int ENEMY_FOLLOW_SPEED;
    private int ENEMY_ATTACK_INTERVAL;
    private double ENEMY_HEALTH_DEPLETION_RATE;

    private static final int MAX_ATTACK_RANGE = 200;

    private final int PUNCH_REBOUND = 75;
    private final int KICK_REBOUND = 100;
    private final int UPPERCUT_REBOUND = 125;
    private final int ROUNDHOUSE_REBOUND = 600;

    private final int PUNCH_DAMAGE = 20;
    private final int KICK_DAMAGE = 25;
    private final int UPPERCUT_DAMAGE = 30;
    private final int ROUNDHOUSE_DAMAGE = 40;

    private ComboQueue comboQueue;
    private String comboDisplayName = null;
    private Timer comboDisplayTimer;

    private int blockStamina = 100;
    private final int MAX_BLOCK_STAMINA = 100;
    private long blockStartTime = 0;
    private boolean parryFlash = false;
    private long lastLeftTap = 0;
    private long lastRightTap = 0;
    private long dodgeUtil = 0;
    private int shakeFrames = 0;

    private Timer frameTimer;
    private Timer enemyMoveTimer;
    private Timer enemyHealthTimer;
    
    private boolean hasFocus;
    private boolean isPaused = false;
    private boolean firstTime;
    private boolean rushOver;
    private boolean didWin;
    private boolean scoreSaved;

    private boolean showPlayerAttackIndicator = false;
    private boolean showEnemyAttackIndicator = false;
    private int attackIndicatorX = 0;
    private int attackIndicatorY = 0;

    public BossRushPanel(MartialMadnessHolder mmhIn, CardLayout cardsIn, LevelPanelHolder lphIn, Information infoIn)
    {
        mmh = mmhIn;
        cards = cardsIn;
        lph = lphIn;
        info = infoIn;

        backPic = info.getMyImage("mystical.jpg");
        bottom = info.getMyImage("bottom copy.png");

        defaultPlayerStance = info.getMyImage("animations/default/default.png");
        ryuPlayerStance = info.getMyImage("animation/default/default.png");
        zangiefPlayerStance = info.getMyImage("animation/zangief/default.png");\
        
        defaultEnemyStance = info.getMyImage("enemy_animations/default/default.png");
        ryuEnemyStance = info.getMyImage("enemy_animations/ryu/default.png");
        zangiefEnemyStance = info.getMyImage("enemy_animations/zangief/default.png");

        defaultPlayerFrames = loadCharacterAnimations("animations/default/default.png");
        ryuPlayerFrames = loadCharacterAnimations("animations/ryu/default.png");
        zangiefPlayerFrames = loadCharacterAnimations("animations/zangief/default.png");
        defaultEnemyFrames = loadCharacterAnimations("enemy_animations/default/default.png");
        ryuEnemyFrames = loadCharacterAnimations("enemy_animations/ryu/default.png");
        zangiefEnemyFrames = loadCharacterAnimations("enemy_animations/zangief/default.png");

        defaultImage = defaultPlayerStance;
        animationFrames = defaultPlayerFrames;
        enemyDefaultImage = defaultEnemyStance;
        enemyFrames = defaultEnemyFrames;

        comboQueue = new ComboQueue();

        setLayout(new BorderLayout());
        FixedPanelHolder fph = new FixedPanelHolder(mmh,cards,true,lph);
        add(fph,BorderLayout.SOUTH);

        addKeyListener(this);
        addFocusListener(this);

        frameTimer = new Timer(105,this);

        comboDisplayTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                comboDisplayName = null;
                repaint();
            }
        });

        comboDisplayTimer.setRepeats(false);

        enemyMoveTimer = new Timer(3000, this);
        enemyHealthTimer = new Timer(1000,this);

        hasFocus = true;
    }

    public void startRush()
    {
        String type = info.getCharacterType();
        if(type.equals("ninja"))
        {
            animationsFrames = ryuPlayerFrames;
            defaultImage = ryuPlayerStance;
            enemyFrames = ryuEnemyFrames;
            enemyDefaultImage = ryuEnemyStance;
            PLAYER_SPEED = 25;
            PLAYER_MAX_HEALTH = 80;
            FRAME_DELAY_MS. = 85;
            COMBO
            else_MULT= 1.2;
        }
        else if(type.equals("sumo"))
        {
            animationFrames = zangiefEnemyFrames;
            defaultImage = ryuPlayerStance;
            enemyFrames = ryuEnemyFrames;

            enemyDefaultImage = ryuEnemyStance;
            PLAYER_SPEED = 25;
            PLAYER_MAX_HEALTH = 80;
            FRAME_DELAY_MS = 85;
            COMBO_MULT = 1.2;

        }
        else if(type.equals("sumo"))
        {
            animationFrames = zangiefPlayerFrames;
            defaultImage = zangiefPlayerStance;
            enemyFrames = zangiefEnemyFrames;
            enemyDefaultImage = zangiefEnemyStance;
            PLAYER_SPEED = 14;
            PLAYER_MAX_HEALTH = 140;
            FRAME_DELAY_MS = 130;
            COMBO_MULT = 1.5;
        }
        else
        {
            animationFrames = defaultPlayerFrames;
            defaultImage = defaultPlayerStance;
            enemyFrames = defaultEnemyFrames;
            enemyDefaultImage = defaultEnemyStance;
            PLAYER_SPEED = 20;
            PLAYER_MAX_HEALTH = 100;
            FRAME_DELAY_MS = 105;
            COMBO_MULT = 1.0;
        }

        PLAYER_MAX_HEALTH = PLAYER_MAX_HEALTH+ShopPanel.healthBonus();
        PLAYER_SPEED = PLAYER_SPEED+ShopPanel.speedBonus();
        COMBO_MULT = COMBO_MULT+ShopPanel.comboBonus();


        roundNumber = 0;
        score = 0;
        playerHealth = PLAYER_MAX_HEALTH;
        firstTime = true;
        rushOver = false;
        didWin = false;
        scoreSaved = false;
        isPaused = false;

        frameTimer = new Timer(FRAME_DELAY_MS, this);
        loadRound(0);
        requestFocusInWindow();
        repaint();
    }

    private void loadRound(int n)
    {
        roundNumber = n;

        ENEMY_FOLLOW_SPEED = (int)rounds[n][0];
        ENEMY_HEALTH_DEPLETION_RATE = rounds[n][1];
        ENEMY_ATTACK_INTERVAL = (int) rounds[n][2];

        if(ENEMY_FOLLOW_SPEED<=7)
        {
            enemyArchetype = ARCH_BALANCED;
        }
        else if(ENEMY_FOLLOW_SPEED<=13)
        {
            enemyArchetype = ARCH_AGGRESSIVE;
        }
        else
        {
            enemyArchetype = ARCH_DEFENSIVE;
        }

        enemyHealth = 100;
        imageX = 200; 
        imageY = 215;
        enemyImageX = 450;
        enemyImageY = 215;

        animationPlaying = -1;
        currentAnimation = -1;
        currentFrame = 0;
        delayCounter = 0;
        enemyCurrentFrame = 0;
        enemyAnimationPlaying = -1;
        enemyDelayCounter = 0;
        blockStamina = MAX_BLOCK_STAMINA;

        comboQueue.clear();

        enemyMoveTimer.setDelay(ENEMY_ATTACK_INTERVAL);
        roundStartTime = System.currentTimeMillis();


    }

    private void beginRound()
    {
        firstTime = false;
        frameTimer.start();
        enemyMoveTimer.start();
        enemyHealthTimer.start();
        SoundManager.music();
        roundStartTime = System.currentTimeMillis();
    }

    private void stopTimers()
    {
        frameTimer.stop();
        enemyMoveTimer.stop();
        enemyHealthTimer.stop();
    }

    private void onEnemyDefeated()
    {
        score = score+(roundNumber+1)*100;

        long took = System.currentTimeMillis() - roundStartTime;
        if(took<20000)
        {
            score = score+(int)((20000-took)/100);
        }
        if(roundNumber>=rounds.length-1)
        {
            rushOver = true;
            didWin = true;
            score = score+playerHealth*3;
            stopTimers();
            SoundManager.win();
            saveScore();
            repaint();
            return;
        }

        playerHealth = playerHealth+30;
        if(playerHealth > PLAYER_MAX_HEALTH)
        {
            playerHealth = PLAYER_MAX_HEALTH;
        }
    }




}



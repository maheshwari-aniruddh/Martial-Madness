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

import javax.swing.JProgressBar;
import javax.swing.Timer;
import java.awt.Image;
import java.awt.Desktop.Action;
import java.awt.BorderLayout;
import java.awt.CardLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;

class GamePanel extends JPanel implements ActionListener, KeyListener, FocusListener, ChangeListener 
{

    private static final int ARCH_BALANCED = 0;
    private static final int ARCH_AGGRESSIVE = 1;
    private static final int ARCH_DEFENSIVE = 2;

    private int enemyArchetype;
    private long lastTimePlayerAttacked = 0;

    private boolean isPaused = false;

    private JProgressBar myHealth;
    private JProgressBar enemyHeatlh;


    private int imageX;
    private int imageY;
    private int enemyImageX;
    private int enemyImageY;



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
    private ComboQueue comboQueue;
    
    private Image[][] animationFrames;
    private Image[][] enemyFrames;
    private int currentFrame;
    private int animationPlaying;
    private int imageX, imageY;
    private int enemyImageX, enemyImageY;
    private boolean firstTime;
    private Image defaultImage;
    private Image enemyDefaultImage;

    // Fields for dynamic character dynamic switching
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

    private int PLAYER_SPEED;
    private int PLAYER_MAX_HEALTH;
    private int FRAME_DELAY_MS;
    private double COMBO_MULT;

    private String comboDisplayName = null;
    private Timer comboDisplayTimer;

    private boolean tookDamageThisLevel = false;
    private int blockStamina = 100;
    private final int MAX_BLOCK_STAMINA = 100;
    private long blockStartTime = 0;
    private boolean parryFlash = false;

    private long lastLeftTap = 0;
    private long lastRgihtTap = 0;
    private long dodgeUtil  = 0;

    private int shakeFrames = 0;

    private int hitsLanded = 0;
    private int blocksLanded = 0;
    private int combosLanded = 0;

    private int levelNumber = 0 ;

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


        if(ENEMY_FOLLOW_SPEED<=7)
        {
            enemyAnimationPlaying = ARCH_BALANCED;
        }
        else if(ENEMY_FOLLOW_SPEED<=13)
        {
            enemyAnimationPlaying = ARCH_AGGRESSIVE;
        }
        else{
            enemyAnimationPlaying = ARCH_DEFENSIVE;
        }

        String charType = info.getCharacterType();

        if(charType.equals("ninja"));
        {
            PLAYER_SPEED = 25;
            PLAYER_MAX_HEALTH = 80;
            FRAME_DELAY_MS = 85;
            COMBO_MULT = 1.2;
        }
        else if(charType.equals("sumo"))
        {
            PLAYER_SPEED = 20;
            PLAYER_MAX_HEALTH = 100;
            FRAME_DELAY_MS = 105;
            COMBO_MULT = 1.0;
        }
        else{
            PLAYER_SPEED = 20;
            PLAYER_MAX_HEALTH = 100;
            FRAME_DELAY_MS = 105;
            COMBO_MULT = 1.0;
        }




        myProgressColor = Color.GREEN;
        enemyProgressColor = Color.GREEN;


        myHealth = new JProgressBar(0,PLAYER_MAX_HEALTH);


        myHealth = new JProgressBar(0,100);
        enemyHealth = new JProgressBar(0,100);
        defaultPlayerStance = info.getMyImage("animations/default/default.png");
        ryuPlayerStance = info.getMyImage("animations/ryu/default.png");
        zangiefPlayerStance = info.getMyImage("animations/zangief/default.png");

        defaultEnemyStance = info.getMyImage("enemy_animations/default/default.png");
        ryuEnemyStance = info.getMyImage("enemy_animations/ryu/default.png");
        zangiefEnemyStance = info.getMyImage("enemy_animations/zangief/default.png");

        defaultPlayerFrames = loadCharacterAnimations("animations/default", defaultPlayerStance, false);
        ryuPlayerFrames = loadCharacterAnimations("animations/ryu", ryuPlayerStance, false);
        zangiefPlayerFrames = loadCharacterAnimations("animations/zangief", zangiefPlayerStance, false);
        //start with default, initializeeLevel() will sawp to the right one
        defaultEnemyFrames = loadCharacterAnimations("enemy_animations/default", defaultEnemyStance, true);
        ryuEnemyFrames = loadCharacterAnimations("enemy_animations/ryu", ryuEnemyStance, true);
        zangiefEnemyFrames = loadCharacterAnimations("enemy_animations/zangief", zangiefEnemyStance, true);

        // Point active references to default character initially
        defaultImage = defaultPlayerStance;
        animationFrames = defaultPlayerFrames;
        enemyDefaultImage = defaultEnemyStance;
        enemyFrames = defaultEnemyFrames;

        comboQueue = new ComboQueue();
        

        setLayout(new BorderLayout());
        JPanel healthPanel = new JPanel();
        healthPanel.setLayout(null);
        healthPanel.setOpaque(false);
        healthPanel.setPreferredSize(new Dimension(800,70));

        myHealth.setValue(PLAYER_MAX_HEALTH);
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

        FixedPanelHolder fph = new FixedPanelHolder(mmh, cards, true, lph);

        backPic = info.getMyImage(pictName);

        bottom = info.getMyImage("bottom copy.png");
        add(fph, BorderLayout.SOUTH);


        addKeyListener(this);
        addFocusListener(this);
        frameTimer = new Timer(FRAME_DELAY_MS,this);
        TimerHandler th = new TimerHandler();
        countDownTimer = new Timer(1000, th);
        enemyAttackTimer = new Timer(ENEMY_ATTACK_INTERVAL, th);
        enemyHealthTimer = new Timer(1000,th);
        enemyHealthTimer.start();
        comboDisplayTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                comboDisplayName = null;
                repaint();
            }
        })
        comboDisplayTimer.setRepeats(false);
        hasFocus = true;
        initializeLevel();

    }

    public void stateChanged(ChangeEvent e)
    {
        JProgressBar source = (JProgressBar) e.getSource();

        if (source == myHealth)
        {
            int value = myHealth.getValue();
            if(value>70)
            {
                myProgressColor= Color.GREEN;
            }
            else if(value>30)
            {
                myProgressColor = Color.YELLOW;
            }
            else
            {
                myProgressColor = Color.RED;
            }
        }
        else if(source == enemyHealth)
        {
            int value = enemyHealth.getValue();
            if(value>70)
            {
                enemyProgressColor = Color.GREEN;
            }
            else if(value>30)
            {
                enemyProgressColor = Color.YELLOW;
            }
            else{
                enemyProgressColor = Color.RED;
            }
        }
    }

    public void focusGained(FocusEvent evt)
    {
        hasFocus = true;
    }

    public void focusLost(FocusEvent evt)
    {
        hasFocus = false;
    }

    class TimerHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            if(isPaused == true)
            {
                return;
            }

                Timer timerName = (Timer) evt.getSource();
            if (timerName == countDownTimer)
            {
                if(timeRemaining>0)
                {
                    timeRemaining--;
                }
                else
                {
                    countDownTimer.stop();
                }
                repaint();
            }

            if(timerName == enemyAttackTimer)
            {
                if (countDownStarted== true)
                {
                    makeEnemyMoves();
                }
            }
            if(timerName == enemyHealthTimer)
            {
                int currentH = enemyHealth.getValue();
                int newH = currentH - (int)ENEMY_HEALTH_DEPLETION_RATE;
                if(newH<0)
                {
                    newH = 0
                }
                enemyHealth.setValue(newH);
            }

        }

    }

    public Image[][] loadCharacterAnimations(String inputDir, Image defaultFrame, boolean isEnemy)
    {
        Image[][] target = new Image[TOTAL_ANIMATIONS][];

        target[PUNCH] = new Image[4];
        target[PUNCH][0] = defaultFrame;
        target[PUNCH][1] = info.getMyImage(inputDir + "/punch_animation copy/frame1.png");
        target[PUNCH][2] = info.getMyImage(inputDir+ "/punch_animation copy/frame2.png");
        target[PUNCH][3] = info.getImage(inputDir+ "/punch_animation copy/frame3.png");
        
        target[BLOCK] = new Image[4];
        target[BLOCK][0] = defaultFrame;
        target[BLOCK][1] = info.getMyImage(inputDir+"/block_animation/frame1.png");
        target[BLOCK][2] = info.getMyImage(inputDir+"/block_animation/frame2.png");
        target[BLOCK][3] = info.getMyImage(inputDir+"/block_animation/frame3.png");
        
        target[KICK] = new Image[4];
        target[KICK][0] = defaultFrame;
        target[KICK][1] = info.getMyImage(inputDir+"/kick_animation/frame1.png");
        target[KICK][2] = info.getMyImage(inputDir+"/kick_animation/frame2.png");
        target[KICK][3] = info.getMyImage(inputDir+"/kick_animation/frame3.png");
        
        target[UPPERCUT] = new Image[5];
        target[UPPERCUT][0] = defaultFrame;
        target[UPPERCUT][1] = info.getMyImage(inputDir+"/uppercut_animation/frame1.png");
        target[UPPERCUT][2] = info.getMyImage(inputDir+"/uppercut_animation/frame2.png");
        target[UPPERCUT][3] = info.getMyImage(inputDir+"/uppercut_animation/frame3.png");
        target[UPPERCUT][4] = info.getMyImage(inputDir+"/uppercut_animation/frame4.png");
    
        target[ROUNDHOUSE] = new Image[10];
        target[ROUNDHOUSE][0] = defaultFrame;
        target[ROUNDHOUSE][1] = info.getMyImage(inputDir+"/roundhouse_animations/frame1.png");
        target[ROUNDHOUSE][2] = info.getMyImage(inputDir+"/roundhouse_animations/frame2.png");
        target[ROUNDHOUSE][3] = info.getMyImage(inputDir+"/roundhouse_animations/frame3.png");
        target[ROUNDHOUSE][4] = info.getMyImage(inputDir+"/roundhouse_animations/frame4.png");
        target[ROUNDHOUSE][5] = info.getMyImage(inputDir+"/roundhouse_animations/frame5.png");
        target[ROUNDHOUSE][6] = info.getMyImage(inputDir+"/roundhouse_animations/frame6.png");
        target[ROUNDHOUSE][7] = info.getMyImage(inputDir+"/roundhouse_animations/frame7.png");
        target[ROUNDHOUSE][8] = info.getMyImage(inputDir+"/roundhouse_animations/frame8.png");
        target[ROUNDHOUSE][9] = info.getMyImage(inputDir+"/roundhouse_animations/frame9.png");
        // enemy have forward/backward because they face the other direction
        if (isEnemy==true)
        {
            target[FORWARD] = new Image[6];
            target[FORWARD][0] = defaultFrame;
            target[FORWARD][1] = info.getMyImage(inputDir+"/backward_animation/frame1.png");
            target[FORWARD][2] = info.getMyImage(inputDir+"/backward_animation/frame2.png");
            target[FORWARD][3] = info.getMyImage(inputDir+"/backward_animation/frame3.png");
            target[FORWARD][4] = info.getMyImage(inputDir+"/backward_animation/frame4.png");
            target[FORWARD][5] = info.getMyImage(inputDir+"/backward_animation/frame5.png");
            
            target[BACKWARD] = new Image[6];
            target[BACKWARD][0] = defaultFrame;
            target[BACKWARD][1] = info.getMyImage(inputDir+"/forward_animation/frame1.png");
            target[BACKWARD][2] = info.getMyImage(inputDir+"/forward_animation/frame2.png");
            target[BACKWARD][3] = info.getMyImage(inputDir+"/forward_animation/frame3.png");
            target[BACKWARD][4] = info.getMyImage(inputDir+"/forward_animation/frame4.png");
            target[BACKWARD][5] = info.getMyImage(inputDir+"/forward_animation/frame5.png");
        }
        else
        {
            target[FORWARD] = new Image[6];
            target[FORWARD][0] = defaultFrame;
            target[FORWARD][1] = info.getMyImage(inputDir+"/forward_animation/frame1.png");
            target[FORWARD][2] = info.getMyImage(inputDir+"/forward_animation/frame2.png");
            target[FORWARD][3] = info.getMyImage(inputDir+"/forward_animation/frame3.png");
            target[FORWARD][4] = info.getMyImage(inputDir+"/forward_animation/frame4.png");
            target[FORWARD][5] = info.getMyImage(inputDir+"/forward_animation/frame5.png");
            
            target[BACKWARD] = new Image[6];
            target[BACKWARD][0] = defaultFrame;
            target[BACKWARD][1] = info.getMyImage(inputDir+"/backward_animation/frame1.png");
            target[BACKWARD][2] = info.getMyImage(inputDir+"/backward_animation/frame2.png");
            target[BACKWARD][3] = info.getMyImage(inputDir+"/backward_animation/frame3.png");
            target[BACKWARD][4] = info.getMyImage(inputDir+"/backward_animation/frame4.png");
            target[BACKWARD][5] = info.getMyImage(inputDir+"/backward_animation/frame5.png");
        }
        return target;
    }

    public Image getDefaultImage()
    {
        return defaultImage;
    }

    public Image[][] getAnimationArray()
    {
        return animationFrames;
    }
    public void setLevelNumber(int n)
    {
        levelNumber = n;
    }

    public void setAnimation(int typeIn)
    {
        animationPlaying = typeIn;
        currentFrame = 0;
        delayCounter = 0;
        frameTimer.start();
    }

    public void setEnemyAnimation(int typeIn)
    {
        enemyAnimationPlaying = typeIn;
        enemyCurrentFrame = 0;
        enemyDelayCounter = 0;
        frameTimer.start();
    }

    public void makeEnemyMoves()
    {
        if(enemyArchetype == ARCH_BALANCED)
        {
            int[] attacks = {PUNCH,KICK,UPPERCUT,ROUNDHOUSE};
            int pick = (int)(Math.random()*attacks.length);
            setEnemyAnimation(attacks[pick]);
        }
        else if(enemyArchetype == ARCH_AGGRESSIVE)
        {
            double r = Math.random();
            if(r<0.4)
            {
                setEnemyAnimation(ROUNDHOUSE);
            }
            else if(r<0.7)
            {
                setEnemyAnimation(KICK);
            }
            else if(r<0.9)
            {
                setEnemyAnimation(PUNCH);
            }
            else
            {
                setEnemyAnimation(UPPERCUT);
            }
            
        }
        else
        {
            long timeSince = System.currentTimeMillis() - lastTimePlayerAttacked;

            if(lastTimePlayerAttacked > 0 && timeSince<800)
            {
                if(Math.random()<0.5)
                {
                    setEnemyAnimation(ROUNDHOUSE);
                }
                else{
                    setEnemyAnimation(UPPERCUT);
                }
            }
        }
    }

    public void keyPressed(KeyEvent evt)
    {
        firstTime = false;
        int keyCode = evt.getKeyCode();
        if(keyCode == KeyEvent.VK_ESCAPE)
        {
            isPaused = !isPaused;
            if(isPaused == true)
            {
                frameTimer.stop();
                countDownTimer.stop();
                enemyAttackTimer.stop();
                enemyHealthTimer.stop();


            }
            else
            {
                if(countDownStarted == true)
                {
                    frameTimer.start();
                    countDownTimer.start();
                    enemyAttackTimer.start();
                    enemyHealthTimer.start();
                }
            }
            repaint();
            return;
        }
        if(isPaused ==true)
        {
            return;
        }
        if(!haveWon)
        {
            if(keyCode == KeyEvent.VK_F)
            {
                comboQueue.addMove("Punch");
                currentAnimation = PUNCH;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_D)
            {
                currentAnimation = BLOCK;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_V)
            {
                comboQueue.addMove("Kick");
                currentAnimation = KICK;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_R)
            {
                comboQueue.addMove("Uppercut");
                currentAnimation = UPPERCUT;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_E)
            {
                comboQueue.addMove("Roundhouse");
                currentAnimation = ROUNDHOUSE;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_LEFT)
            {
                currentAnimation = BACKWARD;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_RIGHT)
            {
                currentAnimation = FORWARD;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_SPACE && !countDownStarted)
            {
                stopFight();
                countDownStarted = true;
                countDownTimer.start();
                enemyAttackTimer.start();
                enemyHealthTimer.start();
            }
        }
    }

    public void stopFight()
    {
        myHealth.setValue(PLAYER_MAX_HEALTH);
        enemyHealth.setValue(100);
        timeRemaining = 60;
        imageX = 200;
        enemyImageX = 450;
        blockStamina = MAX_BLOCK_STAMINA;
        if (firstTime == false)
        {
            info.makeIt();
        }
        frameTimer.stop();
        countDownTimer.stop();
        enemyAttackTimer.stop();
        comboQueue.clear();
        enemyHealthTimer.stop();
        countDownStarted = false;
        SoundManager.stopMusic();
    }


    public void keyReleased(KeyEvent evt)
    {
    }

    public void keyTyped(KeyEvent evt)
    {
    }

    public void handleCombat()
    {
        int distance = Math.abs(imageX - enemyImageX);
        if(distance>MAX_ATTACK_RANGE)
        {
            return;
        }

        String combo = comboQueue.checkCombo();
        if(combo!= null)
        {
            comboDisplayName = combo;
            comboDisplayTimer.restart();
            AchievementManager.check("combo: "+combo);
            SoundManager.combo();
            combosLanded++;

            int baseDmg = 0;
            int push = 0;
            int pts = 0;

            if(combo.equals("Shadow Kick"))
            {
                baseDmg = 25; push = 250; pts = 5;
            }
            else if(combo.equals("Hurricane Kick"))
            {
                baseDmg =35;push = 300; pts = 8;
            }
            else if(combo.equals("Dragon Sweep"))
            {
                baseDmg = 30; push = 250; pts = 6;
            }
            else if(combo.equals("Counter Burst"))
            {
                baseDmg = 40; push = 400; pts = 15;
            }

            int finalDmg = (int)(baseDmg*COMBO_MULT);
            info.setPoints(pts);


            showPlayerAttackIndicator = true;
            attackIndicatorX = enemyImageX + 100;
            attackIndicatorY = enemyImageY+100;

            if(enemyImageX<imageX)
            {
                enemyImageX = enemyImageX -push;
                if(enemyImageX<0) enemyImageX = 0;
            }
            else
            {
                enemyImageX = enemyImageX+push;
                if(enemyImageX>600)enemyImageX = 600;
            }
            int currentH = enemyHealth.getValue();
            enemyHealth.setValue(Math.max(0,currentH-finalDmg));

            return;
        }

        if(animationPlaying == PUNCH|| animationPlaying == KICK||
            animationPlaying == UPPERCUT || animationPlaying == ROUNDHOUSE)
        {
            int pushDist = 0;

            if(animationPlaying == PUNCH)
            {
                info.setPoints(1);
                pushDist = PUNCH_REBOUND;
                attackIndicatorY = enemyImageY+100;
            }
            else if(animationPlaying == KICK)
            {
                info.setPoints(1);
                pushDist = KICK_REBOUND;
                attackIndicatorY = enemyImageY+150;
            }
            else if(animationPlaying == UPPERCUT)
            {
                info.setPoints(1);
                pushDist = UPPERCUT_REBOUND;
                attackIndicatorY = enemyImageY+50;
            }
            else if(animationPlaying == ROUNDHOUSE)
            {
                info.setPoints(1);
                pushDist = ROUNDHOUSE_REBOUND;
                attackIndicatorY = enemyImageY+100;
            }
            hitsLanded++;
            showPlayerAttackIndicator = true;
            attackIndicatorX = enemyImageX+100;
            if(enemyImageX<imageX)
            {
                enemyImageX = Math.max(0, enemyImageX - pushDist);   
            }
            else
            {
                enemyImageX = Math.min(600,enemyImageX+pushDist);
            }

        }
    }

    public void initializeLevel()
    {
        String type = info.getCharacterType();
        if ("ninja".equals(type)) {
            animationFrames = ryuPlayerFrames;
            defaultImage = ryuPlayerStance;
            enemyFrames = ryuEnemyFrames;
            enemyDefaultImage = ryuEnemyStance;
        } else if ("sumo".equals(type)) {
            animationFrames = zangiefPlayerFrames;
            defaultImage = zangiefPlayerStance;
            enemyFrames = zangiefEnemyFrames;
            enemyDefaultImage = zangiefEnemyStance;
        } else {
            animationFrames = defaultPlayerFrames;
            defaultImage = defaultPlayerStance;
            enemyFrames = defaultEnemyFrames;
            enemyDefaultImage = defaultEnemyStance;
        }

        haveWon = false;
        pointGiven = true;
        firstTime = true;
        gameOver = -1;
        countDownStarted = false;

        isPaused = false;
        comboDisplayName = null;
        tookDamageThisLevel = null;
        lastTimePlayerAttacked = 0;

        blockStamina = MAX_BLOCK_STAMINA;
        blockStartTime = 0;
        dodgeUtil = 0;
        parryFlash = false;
        shakeFrames = 0;

        hitsLanded = 0;
        blocksLanded = 0;
        combosLanded = 0;

        comboQueue.clear();

        imageX = 200;
        imageY = 215;
        enemyImageX = 450;
        enemyImageY= 215;

        animationPlaying = -1;
        currentAnimation = -1;
        currentFrame = 0;
        delayCounter = 0;

        enemyCurrentFrame = 0;
        enemyAnimationPlaying = -1;
        enemyDelayCounter =0;
        
        myHealth.setValue(PLAYER_MAX_HEALTH);
        enemyHealth.setValue(100);

        timeRemaining = 60;

        frameTimer.stop();
        countDownTimer.stop();
        enemyAttackTimer.stop();
        enemyHealthTimer.stop();

        requestFocusInWindow();
        repaint();
    }

    public void handleEnemyAttack()
    {
        int distance = Math.abs(imageX - enemyImageX);

        if(distance>MAX_ATTACK_RANGE)
        {
            return;
        }
        if(System.currentTimeMillis()<dodgeUtil)
        {
            return;
        }
        if(animationPlaying == BLOCK)
        {
            long sinceBlock = System.currentTimeMillis() - blockStartTime;

            if(sinceBlock<=150)
            {
                parryFlash = true;
                blocksLanded++;
                info.setPoints(5);
                SoundManager.combo();
                enemyAnimationPlaying  = -1;
                enemyCurrentFrame = 0;
                return;
            }

            if(blockStamina>0)
            {
                blockStamina = blockStamina -25;
                if(blockStamina<0) blockStamina = 0;
                blocksLanded++;
                return;
            }

            //normal block - works as lonmg as you still ahve stamina
            if(blockStamina>0)
            {
                blockStamina = blockStamina-25;
                if(blockStamina<0) blockStamina = 0;
                blocksLanded++;
                return;
            }
        }


        int damage = 0;
        int pushDist = 0;

        if(enemyAnimationPlaying == PUNCH)
        {
            damage = PUNCH_DAMAGE;
            pushDist = PUNCH_REBOUND;
            attackIndicatorY = imageY+100;
        }
        else if(enemyAnimationPlaying == KICK)
        {
            damage = KICK_DAMAGE;
            pushDist = KICK_REBOUND;
            attackIndicatorY = imageY+150;
        }
        else if(enemyAnimationPlaying == UPPERCUT)
        {
            damage = UPPERCUT_DAMAGE;
            pushDist = UPPERCUT_REBOUND;
            attackIndicatorY = imageY+50;
        }
        else if(enemyAnimationPlaying == ROUNDHOUSE)
        {
            damage = ROUNDHOUSE_DAMAGE;
            pushDist = ROUNDHOUSE_REBOUND;
            attackIndicatorY = imageY+100;
        }
        if(damage>0)
        {
            tookDamageThisLevel = true;
            shakeFrames = 6;
        }

        showEnemyAttackIndicator = true;
        attackIndicatorX = imageX+100;

        if(imageX<enemyImageX)
        {
            imageX = Math.max(0, imageX-pushDist);
        }
        else
        {
            imageX = Math.min(600, o,imageX+pushDist);
        }

        int currentH = myHealth.getValue();
        myHealth.setValue(Math.max(0,currentH-damage));
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        requestFocusInWindow();

        //screen shake and jitter everything for a few fgrames after a hit
        int shakeX = 0;
        int shakeY = 0;
        if(shakeFrames>0)
        {
            shakeX = (int)(Math.random()*10)-5;
            shakeY = (int)(Math.random()*10)-5; 
            shakeFrames--;
        }
        g.translate(shakeX, shakeY);


        g.drawImage(backPic, 0,-20,800,600,this);
        g.drawImage(bottom, 0, 400, this);

        if (animationPlaying>=0 && animationPlaying < TOTAL_ANIMATIONS)
        {
            g.drawImage(animationFrames[animationPlaying][currentFrame], imageX, imageY, 200, 200, this);
        }
        else
        {
            g.drawImage(defaultImage, imageX, imageY, 200,200,this);   
        }
        if(enemyAnimationPlaying>= 0 && enemyAnimationPlaying< TOTAL_ANIMATIONS)
        {
            g.drawImage(enemyFrames[enemyAnimationPlaying][enemyCurrentFrame], enemyImageX, enemyImageY, 200, 200, this);
        }
        else
        {
            g.drawImage(enemyDefaultImage,enemyImageX,enemyImageY, 200,200, this);
        }

        if (showPlayerAttackIndicator)
        {
            g.setColor(new Color(255,0,0,100));
            g.fillOval(attackIndicatorX-25, attackIndicatorY-25,50,50);
            showPlayerAttackIndicator = false;
        }
        if(showEnemyAttackIndicator)
        {
            g.setColor(new Color(255,0,0,100));
            g.fillOval(attackIndicatorX-25,attackIndicatorY-25,50,50);
            showEnemyAttackIndicator = false; 
        }

        g.translate(-shakeX, -shakeY);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD, 16));
        
        g.drawString("Player Health",150,25);
        g.setColor(myProgressColor);
        g.fillRect(150,30,myHealth.getValue()*2,30);
        int barWidth = (myHealth.getValue()*200);
        g.fillRect(150,30,barWidth,30);
        g.setColor(Color.WHITE);
        g.drawString("Enemy Health",450,25);
        g.setColor(enemyProgressColor);
        g.fillRect(450,30,enemyHealth.getValue()*2,30);
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD,12));
        g.drawString("BLOCK",108,75);
        g.setColor(Color.DARK_GRAY);
        g.fillRect(150,66,100,10);
        g.setColor(new Color(80,160,255));
        g.fillRect(150,66,blockStamina,10);
        if(countDownStarted)
        {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial",Font.BOLD, 24));
            g.drawString("Time: "+ timeRemaining,350,50);
        }

        if(enemyHealth.getValue()<=0)
        {
            gameOver = 0;
            g.setColor(new Color(245,245,220));
            g.fillRect(0,0,800,410);
            g.setColor(Color.GREEN);
            g.setFont(new Font("Arial", Font.BOLD,48));
            g.drawString("YOU WIN! ", 250, 200);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("(press back to go to the next level) ", 70, 240);
            countDownStarted = false;
            setLevel();
        }

        if(myHealth.getValue()<=0)
        {
            gameOver = 1;
            g.setColor(new Color(245,245,220));
            g.fillRect(0,0,800,410);
            g.setColor(Color.RED);
            g.setFont(new Font("Arial", Font.BOLD, 48));
            g.drawString( "YOU LOSE!", 250,200);
            g.setFont(new Font("Arial", Font.BOLD, 36));
            g.drawString("(press space to retry level)",70 ,240 );
            setLevel();
        }

        if(firstTime)
        {
            g.setColor(new Color(245,245, 200));
            g.fillRect(0,0,800,410);
            g.setColor(new Color(250,49,100));
            g.setFont(new Font("Monteserrat", Font.BOLD, 60));
            g.drawString("PRESS SPACE TO BEGIN",10,200);
        }
        else if(timeRemaining<=0)
        {
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial", Font.BOLD,48));
            g.drawString("TIME'S UP!", 250,200);
        }
    }

    public void setLevel()
    {
        if(gameOver == 0 && pointGiven)
        {
            haveWon = true;
            stopFight();
        }
        if(gameOver == 1)
        {
            stopFight();
        }
        gameOver = -1;
        pointGiven = false;
    }

    public void actionPerformed(ActionEvent evt)
    {
        Timer timerName = (Timer) evt.getSource();
        
        if(timerName == frameTimer)
        {
            if(animationPlaying>= 0)
            {
                int positionAdjustment = 0;
                if(animationPlaying==FORWARD)
                {
                    positionAdjustment = 20;
                }
                else if(animationPlaying == BACKWARD)
                {
                    positionAdjustment = -20;
                }

                int newX = imageX+positionAdjustment;
                int distanceToEnemy = Math.abs(newX - enemyImageX);
                if(distanceToEnemy < 150)
                {
                    if(newX < enemyImageX)
                    {
                        enemyImageX = Math.min(600,enemyImageX+positionAdjustment);
                    }
                    else
                    {
                        enemyImageX = Math.max(0,enemyImageX+positionAdjustment);
                    }
                }

                if(newX>=0 && newX<=600)
                {
                    imageX = newX;
                }
                if(currentFrame < animationFrames[animationPlaying].length-1)
                {
                    currentFrame++;
                    if(currentFrame == animationFrames[animationPlaying].length-2)
                    {
                        handleCombat();
                    }
                }
                else if (delayCounter< DELAY_FRAMES)
                {
                    delayCounter++;
                }
                else
                {
                    currentFrame = 0;
                    animationPlaying = -1;
                    delayCounter = 0;
                }
            }

            if (enemyAnimationPlaying>=0)
            {
                if(enemyCurrentFrame<enemyFrames[enemyAnimationPlaying].length -1)
                {
                    enemyCurrentFrame++;
                    if (enemyCurrentFrame == enemyFrames[enemyAnimationPlaying].length-2)
                    {
                        handleEnemyAttack();
                    }
                }
                else if(enemyDelayCounter< DELAY_FRAMES)
                {
                    enemyDelayCounter++;
                }
                else
                {
                    enemyCurrentFrame = 0;
                    enemyAnimationPlaying = -1;
                    enemyDelayCounter = 0;
                }
            }
            else
            {
                int distanceToPlayer = Math.abs(imageX - enemyImageX);
                if(distanceToPlayer>= 150)
                {
                    if(enemyImageX<imageX)
                    {
                        enemyImageX = Math.min(600,enemyImageX+(int)ENEMY_FOLLOW_SPEED);
                    }
                    else
                    {
                        enemyImageX = Math.max(0, enemyImageX - (int)ENEMY_FOLLOW_SPEED);
                    }
                }
            }
            repaint();
        }
        else if(timerName == countDownTimer)
        {
            if (countDownStarted)
            {
                makeEnemyMoves();
            }
        }

        if (timerName == enemyHealthTimer)
        {
            int currentHealth = enemyHealth.getValue();
            enemyHealth.setValue(Math.max(0,currentHealth - (int)ENEMY_HEALTH_DEPLETION_RATE));
        }
    }

}

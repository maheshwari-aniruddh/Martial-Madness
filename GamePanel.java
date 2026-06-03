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

import java.awt.BorderLayout;
import java.awt.CardLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;

class GamePanel extends JPanel implements ActionListener, KeyListener, FocusListener, ChangeListener 
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
        defaultImage = info.getMyImage("default.png");
        enemyDefaultImage = info.getMyImage("enemy_default.png");

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

        FixedPanelHolder fph = new FixedPanelHolder(mmh, cards, true, lph);

        backPic = info.getMyImage(pictName);

        bottom = info.getMyImage("bottom copy.png");
        add(fph, BorderLayout.SOUTH);


        addKeyListener(this);
        addFocusListener(this);
        frameTimer = new Timer(105, this); 
        TimerHandler th = new TimerHandler();
        countDownTimer = new Timer(1000, th);
        enemyAttackTimer = new Timer(ENEMY_ATTACK_INTERVAL, th);
        enemyHealthTimer = new Timer(1000,th);
        enemyHealthTimer.start();

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
                if (countDownStarted)
                {
                    makeEnemyMoves();
                }
            }
            if(timerName == enemyHealthTimer)
            {
                int currentHealth = enemyHealth.getValue();
                enemyHealth.setValue(Math.max(0,currentHealth-(int)ENEMY_HEALTH_DEPLETION_RATE));
            }

        }

    }

    public void loadAnimations(String inputDir)
    {
        boolean isEnemy = inputDir.equals("enemy_animations");
        Image defaultFrame;
        Image [][] target;

        if(isEnemy)
        {
            enemyFrames = new Image[TOTAL_ANIMATIONS][];
            defaultFrame = info.getMyImage("enemy_default.png");
            target = enemyFrames;
        }
        else
        {
            animationFrames = new Image[TOTAL_ANIMATIONS][];
            defaultFrame = info.getMyImage("default.png");
            target = animationFrames;
        }

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
    }

    public Image[][] getAnimationArray()
    {
        return animationFrames;
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
        int[] attackMoves = {PUNCH, KICK, UPPERCUT, ROUNDHOUSE};
        int randomMove = attackMoves[(int)(Math.random() * attackMoves.length)];
        setEnemyAnimation(randomMove);
    }

    public void keyPressed(KeyEvent evt)
    {
        firstTime = false;
        int keyCode = evt.getKeyCode();
        if(!haveWon)
        {
            if(keyCode == KeyEvent.VK_F)
            {
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
                currentAnimation = KICK;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_R)
            {
                currentAnimation = UPPERCUT;
                setAnimation(currentAnimation);
            }
            else if(keyCode == KeyEvent.VK_E)
            {
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
        myHealth.setValue(100);
        enemyHealth.setValue(100);
        timeRemaining = 60;
        imageX = 200;
        enemyImageX = 450;
        if (firstTime == false)
        {
            info.makeIt();
        }
        frameTimer.stop();
        countDownTimer.stop();
        enemyAttackTimer.stop();
        enemyHealthTimer.stop();
        countDownStarted = false;
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

        if (distance<=MAX_ATTACK_RANGE)
        {
            if (animationPlaying == PUNCH || animationPlaying==KICK || animationPlaying == UPPERCUT
                    || animationPlaying == ROUNDHOUSE)
                    {
                        int pushDistance = 0;
                        if (animationPlaying == PUNCH)
                        {
                            info.setPoints(1);
                            pushDistance = PUNCH_REBOUND;

                            attackIndicatorY = enemyImageY + 100;
                        }
                        else if(animationPlaying == KICK)
                        {
                            info.setPoints(1);
                            pushDistance = KICK_REBOUND;
                            attackIndicatorY = enemyImageY+150;
                        }
                        else if(animationPlaying == UPPERCUT)
                        {
                            info.setPoints(1);
                            pushDistance = UPPERCUT_REBOUND;
                            attackIndicatorY = enemyImageY+50;
                        }
                        else if(animationPlaying == ROUNDHOUSE)
                        {
                            info.setPoints(1);
                            pushDistance = ROUNDHOUSE_REBOUND;
                            attackIndicatorY = enemyImageY+100;
                        }
                        showPlayerAttackIndicator = true;

                        attackIndicatorX = enemyImageX+100;
                        if(enemyImageX<imageX)
                        {
                            enemyImageX = Math.max(0,enemyImageX - pushDistance);
                        }
                        else
                        {
                            enemyImageX = Math.min(600,enemyImageX+ pushDistance);
                        }
                    }
        }
    }

    public void initializeLevel()
    {
        haveWon = false;
        pointGiven = true;
        firstTime = true;
        gameOver = -1;
        countDownStarted = false;

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
        
        myHealth.setValue(100);
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
        if (distance<=MAX_ATTACK_RANGE)
        {
            if(animationPlaying!=BLOCK)
            {
                int damage = 0;
            int pushDistance = 0;
            if (enemyAnimationPlaying == PUNCH)
            {
                damage = PUNCH_DAMAGE;
                pushDistance = PUNCH_REBOUND;
                attackIndicatorY = imageY+100;
            }
            else if(enemyAnimationPlaying == KICK)
            {
                damage = KICK_DAMAGE;
                pushDistance = KICK_REBOUND;
                attackIndicatorY = imageY+150;
            }
            else if(enemyAnimationPlaying == UPPERCUT)
            {
                damage = UPPERCUT_DAMAGE;
                pushDistance = UPPERCUT_REBOUND;
                attackIndicatorY = imageY+50;
            }
            else if(enemyAnimationPlaying == ROUNDHOUSE)
            {
                damage = ROUNDHOUSE_DAMAGE;
                pushDistance = ROUNDHOUSE_REBOUND;
                attackIndicatorY = imageY+100;
            }

            showEnemyAttackIndicator = true;
            attackIndicatorX = imageX + 100;
            
            if (imageX<enemyImageX)
            {
                 imageX = Math.max(0, imageX-pushDistance);
            }
            else
            {
                imageX = Math.min(600, imageX+pushDistance);
            }

            int currentHealth = myHealth.getValue();
            myHealth.setValue(Math.max(0,currentHealth-damage));
            }
            
        }

    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);

        requestFocusInWindow();
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

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial",Font.BOLD, 16));
        
        g.drawString("Player Health",150,25);
        g.setColor(myProgressColor);
        g.fillRect(150,30,myHealth.getValue()*2,30);

        g.setColor(Color.WHITE);
        g.drawString("Enemy Health",450,25);
        g.setColor(enemyProgressColor);
        g.fillRect(450,30,enemyHealth.getValue()*2,30);

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

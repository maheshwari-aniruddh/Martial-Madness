import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;


public class TwoPlayerGamePanel extends JPanel implements ActionListener, KeyListener, ChangeListener
{
    private static final int PUNCH =0;
    private static final int BLOCK = 1;
    private static final int KICK = 2;
    private static final int UPPERCUT = 3;
    private static final int ROUNDHOUSE = 4;
    private static final int FORWARD = 5;
    private static final int BACKWARD = 6;
    private static final int TOTAL_ANIMS = 7;
    private static final int DELAY_FRAMES = 3;

    private int p1X = 150;
    private int p1Y = 215;
    private int p1AnimPlaying = -1;
    private int p1Frame = 0;
    private int p1Delay = 0;
    private Image[][] p1Frames;
    private Image p1DefaultImg;
    private JProgressBar p1Health;
    private Color p1Color = Color.GREEN;
    private ComboQueue p1Combo;
    private String p1ComboName = null;

    private int p2X = 490;
    private int p2Y = 215;
    private int p2AnimPlaying = -1;
    private int p2Frame = 0;
    private int p2Delay = 0;
    private Image[][] p2Frames;
    private Image p2DefaultImg;
    private JProgressBar p2Health;
    private Color p2Color = Color.GREEN;
    private ComboQueue p2Combo;
    private String p2ComboName = null;

    private Timer frameTimer;
    private Timer countDownTimer;
    private Timer p1ComboTimer;
    private Timer p2ComboTimer;

    private int timeLeft = 90;
    private boolean gameStarted = false;
    private boolean isGameOver = false;
    private String winnerText = "";

    private boolean showHitOnP1 = false;
    private boolean showHitOnP2 = false;
    private int hitFlashX =0;
    private int hitFlashY = 0;

    private Information info;
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private LevelPanelHolder lph;
    private Image background;
    private Image floorImg;


    private final int PUNCH_DMG = 18;
    private final int KICK_DMG = 23;
    private final int UPPER_DMG = 28;
    private final int RH_DMG = 38;

    private final int PUNCH_PUSH = 75;
    private final int KICK_PUSH =100;
    private final int UPPER_PUSH = 125;
    private final int RH_PUSH = 500;

    private final int HIT_RANGE = 200;
    public TwoPlayerGamePanel(MartialMadnessHolder mmhIn, CardLayout cardsIn, LevelPanelHolder lphIn, Information infoIn)
    {
        mmh = mmhIn;
        cards = cardsIn;
        lph = lphIn;
        info = infoIn;

        String charType = info.getCharacterType();
        String pDir = "animations/default";
        String eDir = "enemy_animations/default";
        if(charType.equals("ninja"))
        {
            pDir = "animations/ryu";
            eDir = "enemy_animations/ryu";
        }
        else if(charType.equals("sumo"))
        {
            pDir = "animations/zangief";
            eDir = "enemy_animations/zangief";
        }

        p1DefaultImg = info.getMyImage(pDir+"/default.png");
        p2DefaultImg = info.getMyImage(eDir+"/default.png");
        p1Frames = loadAnimFrames(pDir,p1DefaultImg,false);
        p2Frames = loadAnimFrames(eDir,p2DefaultImg,true);

        p1Health = new JProgressBar(0,100);
        p1Health.setValue(100);
        p1Health.addChangeListener(this);
        
        p2Health = new JProgressBar(0,100);
        p2Health.setValue(100);
        p2Health.addChangeListener(this);

        p1Combo = new ComboQueue();
        p2Combo = new ComboQueue();

        background = info.getMyImage("tents.jpg");
        floorImg = info.getMyImage("bottom copy.png");

        p1ComboTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                p1ComboName = null;
                repaint();
            }
        });
        p1ComboTimer.setRepeats(false);

        p2ComboTimer = new Timer(1500,new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                p2ComboName = null;
                repaint();
            }
        });
        p2ComboTimer.setRepeats(false);

        frameTimer = new Timer(105,this);

        countDownTimer = new Timer(1000,new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                if(timeLeft>0)
                {
                    timeLeft--;
                }
                else{
                    countDownTimer.stop();
                    if(!isGameOver)
                    {
                        isGameOver = true;
                        frameTimer.stop();

                        int p1hp = p1Health.getValue();
                        int p2hp = p2Health.getValue();
                        
                        if(p1hp>p2hp)
                        {
                            winnerText = "Player 1 wins!";
                        }
                        else if(p2hp>p1hp)
                        {
                            winnerText = "Player 2 wins!";
                        }
                        else
                        {
                            winnerText = "Draw!";
                        }

                    }
                }
                repaint();
            }
        });

        setLayout(new BorderLayout());
        FixedPanelHolder fph = new FixedPanelHolder(mmh, cards, true, lph);
        add(fph,BorderLayout.SOUTH);

        setFocusable(true);
        addKeyListener(this);
    }

    private Image[][] loadAnimFrames(String dir, Image defaultFrame, boolean isEnemy)
    {
        Image[][] frames = new Image[TOTAL_ANIMS][];

        frames[PUNCH] = new Image[4];
        frames[PUNCH][0] = defaultFrame;
        frames[PUNCH][1] = info.getMyImage(dir+"/punch_animation copy/frame1.png");
        frames[PUNCH][2] = info.getMyImage(dir+"/punch_animation copy/frame2.png");
        frames[PUNCH][3]= info.getMyImage(dir+"/punch_animation copy/frame3.png");
        
        frames[BLOCK] = new Image[4];
        frames[BLOCK][0] = defaultFrame;
        frames[BLOCK][1] = info.getMyImage(dir+"/block_animation copy/frame1.png");
        frames[BLOCK][2] = info.getMyImage(dir+"/block_animation copy/frame2.png");
        frames[BLOCK][3] = info.getMyImage(dir+"/block_animation copy/frame3.png");
        
        frames[KICK] = new Image[4];
        frames[KICK][0] = defaultFrame;
        frames[KICK][1] = info.getMyImage(dir+"/kick_animation copy/frame1.png");
        frames[KICK][2] = info.getMyImage(dir+"/kick_animation copy/frame2.png");
        frames[KICK][3]=info.getMyImage(dir+"/kick_animation copy/frame3.png");
        
        frames[UPPERCUT]=new Image[5];
        frames[UPPERCUT][0]=defaultFrame;
        frames[UPPERCUT][1]=info.getMyImage(dir+"/uppercut_animatiom copy/frame1.png");
        frames[UPPERCUT][2]= info.getMyImage(dir+"/uppercut_animation copy/frame2.png");
        frames[UPPERCUT][3] = info.getMyImage(dir+"/uppercut_animation copy/frame3.png");
        frames[UPPERCUT][4] = info.getMyImage(dir+"/uppercut_animation copy/frame4.png");

        frames[ROUNDHOUSE] = new Image[10];
        frames[ROUNDHOUSE][0] = defaultFrame;
        frames[ROUNDHOUSE][1] = info.getMyImage(dir+"/roundhouse_animation/frame1.png");
        frames[ROUNDHOUSE][2] = info.getMyImage(dir+"/roundhouse_animtation copy/frame2.png");
        frames[ROUNDHOUSE][3] = info.getMyImage(dir+"/roundhouse_animation/frame3.png");
        frames[ROUNDHOUSE][4] = info.getMyImage(dir+"/roundhouse_animation/frame4.png");
        frames[ROUNDHOUSE][5] = info.getMyImage(dir+"/roundhouse_animation/frame5.png");
        frames[ROUNDHOUSE][6] = info.getMyImage(dir+"/roundhouse_animation copy/frame6.png");
        frames[ROUNDHOUSE][7] = info.getMyImage(dir+"/roundhouse_animation/frame7.png");
        frames[ROUNDHOUSE][8] = info.getMyImage(dir+"/roundhouse_animation/frame8.png");
        frames[ROUNDHOUSE][9] = info.getMyImage(dir+"/roundhouse)animation/frame9.png");

        if(isEnemy == true)
        {
            frames[FORWARD] = new Image[6];
            frames[FORWARD][0] = defaultFrame;
            frames[FORWARD][1] = info.getMyImage(dir+"/backward_animation/frame1.png");
            frames[FORWARD][2]=info.getMyImage(dir+"backward_animation/frame2.png");
            frames[FORWARD][3] = info.getMyImage(dir+"backward_animation/frame3.png");
            frames[FORWARD][4] = info.getMyImage(dir+"/backward_animation/frame4.png");
            frames[FORWARD][5] = info.getMyImage(dir+"/backward_animation/frame5.png");

            frames[BACKWARD] = new Image[6];
            frames[BACKWARD][0] = defaultFrame;
            frames[BACKWARD][1] = info.getMyImage(dir+"/forward_animation/frame1.png");
            frames[BACKWARD][2] = info.getMyImage(dir+"/forward_animation/frame2.png");
            frames[BACKWARD][3] = info.getMyImage(dir+"forward_animation/frame3.png");
            frames[BACKWARD][4] = info.getMyImage(dir+"/forward_animatiion/frame4.png");
            frames[BACKWARD][5] = info.getMyImage(dir+"/forward_animation/frame5.png");

        }
        else 
        {
            frames[FORWARD]= new Image[6];
            frames[FORWARD][0] = defaultFrame;
            frames[FORWARD][1] = info.getMyImage(dir+"/forward_animation/frame1.png");
            frames[FORWARD][2] = info.getMyImage(dir+"/forward_animation/frame2.png");
            frames[FORWARD][3] = info.getMyImage(dir+"/forward_animation/frame3.png");
            frames[FORWARD][4] = info.getImage(dir+"/forward_animation/frame4.png");
            frames[FORWARD][5] = info.getMyImage(dir+"/forward_animation/frame5.png");

            frames[BACKWARD] = new Image[6];
            frames[BACKWARD][0] = defaultFrame;
            frames[BACKWARD][1] = info.getMyImage(dir+"/backward_animation/frame1.png");
            frames[BACKWARD][2] = info.getMyImage(dir+"/backward_animation/frame2.png");
            frames[BACKWARD][3] = info.getMyImage(dir+"/backward_animation/frame3.png");
            frames[BACKWARD][4] = info.getMyImage(dir+"/backward_animation/frame4.png");
            frames[BACKWARD][5] = defaultFrame; // Fill missing frame
        }

        return frames;
    }

    public void initalizeMatch()
    {
        String charType = info.getCharacterType();
        String pDir = "animations/default";
        String eDir = "enemy_animations/default";

        if(charType.equals("ninja"))
        {
            pDir = "animations/ryu";
            eDir = "enemy_animations/ryu";
        }
        else if(charType.equals("sumo"))
        {
            pDir = "animations/zangief";
            eDir = "enemy_animations/zangief";
        }
        p1DefaultImg = info.getMyImage(pDir+"/default.png");
        p2DefaultImg = info.getMyImage(eDir+"/default.png");
        p1Frames = loadAnimFrames(pDir,p1DefaultImg,false);
        p2Frames = loadAnimFrames(eDir,p2DefaultImg,true);

        p1X = 150;
        p1Y = 215;
        p2X = 490;
        p2Y = 215;

        p1AnimPlaying = -1;
        p1Frame   = 0;
        p1Delay = 0;
        p2AnimPlaying = -1;
        p2Frame = 0;
        p2Delay = 0;

        p1Health.setValue(100);
        p2Health.setValue(100);

        timeLeft = 99;
        gameStarted = false;
        isGameOver = false;
        winnerText = "";
        p1ComboName = null;
        p2ComboName = null;
        showHitOnP1 = false;
        showHitOnP2 = false;

        p1Combo.clear();
        p2Combo.clear();

        frameTimer.stop();
        countDownTimer.stop(); 
        
        requestFocusInWindow();
        repaint();

    }

    private void resolveP1Attack()
    {
        int dist = Math.abs(p1X-p2X);
        if(dist>HIT_RANGE)
        {
            return;
        }
        String combo = p1Combo.checkCombo();

        if(combo!=null)
        {
            p1ComboName = combo;
            p1ComboTimer.restart();
            SoundManager.combo();
            AchievementManager.check("combo: "+combo);
            info.setPoints(getComboPoints(combo));
            hurtP2(getComboDamage(combo),getComboPush(combo));
            return;
        }

        if(p1AnimPlaying == PUNCH)
        {
            info.setPoints(1);
            hurtP2(PUNCH_DMG,PUNCH_PUSH);
            SoundManager.punch();
        }
        else if(p1AnimPlaying==KICK)
        {
            info.setPoints(1);
            hurtP2(KICK_DMG,KICK_PUSH);
            SoundManager.kick();   
        }
        else if(p1AnimPlaying == UPPERCUT)
        {
            info.setPoints(1);
            hurtP2(RH_DMG,RH_PUSH);
            SoundManager.roundhouse();
        }
    }

    private void hurtP2(int damage, int push)
    {
        if(p2AnimPlaying==BLOCK)
        {
            return;
        }

        showHitOnP2 = true;
        hitFlashX = p2X +100;
        hitFlashY = p2Y+100;

        int newHp = p2Health.getValue()-damage;
        if(newHp<0)
        {
            newHp = 0;
        }
        p2Health.setValue(newHp);

        if (p2X>p1X)
        {
            p2x = p2x +push;
            if(p2X>600)
            {
                p2X = 600;
            }
        }  
        else
        {
            p2X = p2X - push;
            if(p2X<0)
            {
                p2X = 0;
            }
        }  
        
    }

    private void resolveP2Attack()
    {
        int dist = Math.abs(p1X-p2X);
        if(dist>HIT_RANGE)
        {
            return;
        }
        String combo = p2Combo.checkCombo();
        if(combo!=null)
        {
            p2ComboName = combo;
            p2ComboTimer.restart();
            SoundMananger.combo();
            hurtP1(getComboDamage(combo),getComboPush(combo));
            return;
        }
        if(p2AnimPlaying == PUNCH)
        {
            hurtP1(PUNCH_DMG,PUNCH_PUSH);
            SoundManager.punch();
        }
        else if(p2AnimPlaying==KICK)
        {
            hurtP1(KICK_DMG,KICK_PUSH);
            SoundManager.kick();
        }
        else if(p2AnimPlaying == UPPERCUT)
        {
            hurtP1(UPPER_DMG,UPPER_PUSH);
            SoundDamager.punch();
        }
        else if(p2AnimPlaying == ROUNDHOUSE)
        {
            hurtP1(RH_DMG,RH_PUSH);
            SoundManager.roundhouse();
        }
    }
    private void hurtP1(int damage,int push)
    {
        if(p1AnimPlaying == BLOCK)
        {
            return;
        }
        showHitOnP1 = true;
        hitFlashX  = p1X+100;
        hitFlashY = p1Y +100;

        int newHp = p1Health.getValue()-damage;
        if(newHp<0)
        {
            newHp = 0;
    
        }
        p1Health.setValue(newHp);
        
        if(p1X<p2X)
        {
            p1X = p1X-push;
            if(p1X<0)
            {
                p1X = 0;
            }
        }
        else
        {
            p1X = p1X +push;
            if(p1X>600)
            {
                p1X = 600;
            }
        }
    }

    private int getComboDamage(String combo)
    {
        if(combo.equals("Death Blossom")) return 50;
        if(combo.equals("Counter Burst")) return 40;
        if(combo.equals("Hurricane Kick")) return 35;
        if(combo.equals("Dragon Sweep")) return 30;
        if(combo.equals("Shadow Kick")) return 25;
        return 20;
    }

    private int getComboPush(String combo)
    {
        if(combo.equals("Death Blossom")) return 400;
        if (combo.equals("Counter Burst")) return 350;
        if (combo.equals("Hurricane Kick")) return 300;
        if(combo.equals("Dragon Sweep")) return 250;
        if(combo.equals("Shadow Kick")) return 200;
        return 150;
    }
    private int getComboPoints(String combo)
    {
        if(combo.equals("Death Blossom")) return 15;
        if(combo.equals("Counter Burst")) return 10;
        if(combo.equals("Hurricane Kick")) return 8;
        if(combo.equals("Dragon Sweep")) return 6;
        if(combo.equals("Shadow Kick")) return 5;
        return 3;
    }

    public void stateChanged(ChangeEvent e)
    {
        JProgressBar bar = (JProgressBar)e.getSource();
        int val = bar.getValue();
        Color newColor;
        if(val>70)
        {
            newColor = Color.GREEN;
        }
        else if(val>30)
        {
            newColor = Color.YELLOW;
        }
        else
        {
            newColor = Color.RED;
        }

        if(bar == p1Health)
        {
            p1Color = newColor;
        }
        else
        {
            p2Color = newColor;
        }

        if(!isGameOver)
        {
            if(p2Health.getValue()<=0)
            {
                isGameOver = true;
                winnerText = "Player 1 Wins";
                frameTimer.stop();
                countdownTimer.stop();
                SoundManager.win();
            }
            if(p1Health.getValue()<=0)
            {
                isGameOver = true;
                winnerText = "Player 2 Wins!";
                frameTimer.stop();
                countDownTimer.stop();
                SoundManager.win();
            }
        }
    }

    public void keyPressed(KeyEvent e)
    {
        int k = e.getKeyCode();

        if(k==KeyEvent.VK_SPACE && !gameStarted)
        {
            gameStarted = true;
            frameTimer.start();
        }
    }

}
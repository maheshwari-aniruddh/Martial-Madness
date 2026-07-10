import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PracticeArenaPanel extends JPanel implements ActionListener, KeyListener
{
    private final JPanel parentHolder;
    private final CardLayout cardLayout;
    private final Information gameInfo;

    private static final int PUNCH = 0;
    private static final int BLOCK = 1;
    private static final int KICK = 2;
    private static final int UPPERCUT = 3;
    private static final int ROUNDHOUSE = 4;
    private static final int TOTAL_ANIMS = 5;
    private static final int DELAY_FRAMES = 3;

    private Image dojoImg;

    private int playerX = 150;
    private int playerAnim = -1;
    private int playerFrame = 0;
    private int playerDelay = 0;
    private Image[][] playerFrames;
    private Image playerDefault;

    private int enemyX = 490;
    private int enemyAnim = -1;
    private int enemyFrame = 0;
    private int enemyDelay = 0;
    private Image[][] enemyFrames;
    private Image enemyDefault;

    private ComboQueue comboQueue;
    private String comboName = null;
    private Timer comboTimer;

    private Timer frameTimer;
    private int tickCount = 0;

    private boolean showHitFlash = false;
    private boolean showBlockedText = false;

    public PracticeArenaPanel(JPanel parentHolderIn, CardLayout cardLayoutIn, Information gameInfoIn)
    {
        parentHolder = parentHolderIn;
        cardLayout = cardLayoutIn;
        gameInfo = gameInfoIn;

        comboQueue = new ComboQueue();

        setLayout(new BorderLayout());

        JPanel southPanel = new JPanel();
        JButton backButton = new JButton("Back to Levels");
        backButton.setPreferredSize(new Dimension(160,40));
        backButton.setFocusable(false);
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt)
            {
                stopArena();
                cardLayout.show(parentHolder, "Levels");
            }
        });
        southPanel.add(backButton);
        add(southPanel, BorderLayout.SOUTH);

        comboTimer = new Timer(1500, new ActionListener() {
            public void actionPerformed(ActionEvent evt)
            {
                comboName = null;
                repaint();
            }
        });
        comboTimer.setRepeats(false);

        frameTimer = new Timer(105, this);

        setFocusable(true);
        addKeyListener(this);
    }

    public void startArena()
    {
        dojoImg = gameInfo.getMyImage("dojo.png");

        String pDir = "animations/default";
        String type = gameInfo.getCharacterType();
        if(type.equals("ninja"))
        {
            pDir = "animations/ryu";
        }
        else if(type.equals("sumo"))
        {
            pDir = "animations/zangief";
        }
        playerDefault = gameInfo.getMyImage(pDir + "/default.png");
        playerFrames = loadAnimFrames(pDir, playerDefault);
        enemyDefault = gameInfo.getMyImage("enemy_animations/default/default.png");
        enemyFrames = loadAnimFrames("enemy_animations/default", enemyDefault);

        playerX = 150;
        enemyX = 490;
        playerAnim = -1;
        playerFrame = 0;
        playerDelay = 0;
        enemyAnim = -1;
        enemyFrame = 0;
        enemyDelay = 0;
        comboName = null;
        tickCount = 0;
        comboQueue.clear();

        frameTimer.start();
        requestFocusInWindow();
        repaint();
    }

    public void stopArena()
    {
        frameTimer.stop();
    }

    private Image[][] loadAnimFrames(String dir, Image defaultFrame)
    {
        Image[][] target = new Image[TOTAL_ANIMS][];

        target[PUNCH] = new Image[4];
        target[PUNCH][0] = defaultFrame;
        target[PUNCH][1] = gameInfo.getMyImage(dir + "/punch_animation copy/frame1.png");
        target[PUNCH][2] = gameInfo.getMyImage(dir + "/punch_animation copy/frame2.png");
        target[PUNCH][3] = gameInfo.getMyImage(dir + "/punch_animation copy/frame3.png");

        target[BLOCK] = new Image[4];
        target[BLOCK][0] = defaultFrame;
        target[BLOCK][1] = gameInfo.getMyImage(dir + "/block_animation/frame1.png");
        target[BLOCK][2] = gameInfo.getMyImage(dir + "/block_animation/frame2.png");
        target[BLOCK][3] = gameInfo.getMyImage(dir + "/block_animation/frame3.png");

        target[KICK] = new Image[4];
        target[KICK][0] = defaultFrame;
        target[KICK][1] = gameInfo.getMyImage(dir + "/kick_animation/frame1.png");
        target[KICK][2] = gameInfo.getMyImage(dir + "/kick_animation/frame2.png");
        target[KICK][3] = gameInfo.getMyImage(dir + "/kick_animation/frame3.png");

        target[UPPERCUT] = new Image[5];
        target[UPPERCUT][0] = defaultFrame;
        target[UPPERCUT][1] = gameInfo.getMyImage(dir + "/uppercut_animation/frame1.png");
        target[UPPERCUT][2] = gameInfo.getMyImage(dir + "/uppercut_animation/frame2.png");
        target[UPPERCUT][3] = gameInfo.getMyImage(dir + "/uppercut_animation/frame3.png");
        target[UPPERCUT][4] = gameInfo.getMyImage(dir + "/uppercut_animation/frame4.png");

        target[ROUNDHOUSE] = new Image[10];
        target[ROUNDHOUSE][0] = defaultFrame;
        target[ROUNDHOUSE][1] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame1.png");
        target[ROUNDHOUSE][2] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame2.png");
        target[ROUNDHOUSE][3] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame3.png");
        target[ROUNDHOUSE][4] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame4.png");
        target[ROUNDHOUSE][5] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame5.png");
        target[ROUNDHOUSE][6] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame6.png");
        target[ROUNDHOUSE][7] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame7.png");
        target[ROUNDHOUSE][8] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame8.png");
        target[ROUNDHOUSE][9] = gameInfo.getMyImage(dir + "/roundhouse_animations/frame9.png");

        return target;
    }

    public void actionPerformed(ActionEvent evt)
    {
        tickCount++;

        if(tickCount % 28 == 0 && enemyAnim < 0)
        {
            double r = Math.random();
            if(r < 0.5)
            {
                enemyAnim = PUNCH;
            }
            else
            {
                enemyAnim = KICK;
            }
            enemyFrame = 0;
            enemyDelay = 0;
        }

        if(playerAnim >= 0)
        {
            int len = playerFrames[playerAnim].length;
            if(playerFrame < len - 1)
            {
                playerFrame++;
            }
            else if(playerDelay < DELAY_FRAMES)
            {
                playerDelay++;
            }
            else
            {
                playerFrame = 0;
                playerAnim = -1;
                playerDelay = 0;
            }
        }

        if(enemyAnim >= 0)
        {
            int len = enemyFrames[enemyAnim].length;
            if(enemyFrame < len - 1)
            {
                enemyFrame++;
                if(enemyFrame == len - 2 && Math.abs(enemyX - playerX) < 220)
                {
                    if(playerAnim == BLOCK)
                    {
                        showBlockedText = true;
                        SoundManager.block();
                    }
                    else
                    {
                        showHitFlash = true;
                        playerX = Math.max(0, playerX - 40);
                        SoundManager.punch();
                    }
                }
            }
            else if(enemyDelay < DELAY_FRAMES)
            {
                enemyDelay++;
            }
            else
            {
                enemyFrame = 0;
                enemyAnim = -1;
                enemyDelay = 0;
            }
        }

        repaint();
    }

    public void keyPressed(KeyEvent e)
    {
        int k = e.getKeyCode();
        String move = null;

        if(k == KeyEvent.VK_F)
        {
            move = "Punch";
            playerAnim = PUNCH;
        }
        else if(k == KeyEvent.VK_D)
        {
            move = "Block";
            playerAnim = BLOCK;
        }
        else if(k == KeyEvent.VK_V)
        {
            move = "Kick";
            playerAnim = KICK;
        }
        else if(k == KeyEvent.VK_R)
        {
            move = "Uppercut";
            playerAnim = UPPERCUT;
        }
        else if(k == KeyEvent.VK_E)
        {
            move = "Roundhouse";
            playerAnim = ROUNDHOUSE;
        }
        else if(k == KeyEvent.VK_LEFT)
        {
            playerX = Math.max(0, playerX - 20);
        }
        else if(k == KeyEvent.VK_RIGHT)
        {
            playerX = Math.min(600, playerX + 20);
        }

        if(move != null)
        {
            playerFrame = 0;
            playerDelay = 0;
            comboQueue.addMove(move);

            String detected = comboQueue.checkCombo();
            if(detected != null)
            {
                comboName = detected;
                comboTimer.restart();
                SoundManager.combo();
            }
        }
        repaint();
    }

    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        requestFocusInWindow();

        g.drawImage(dojoImg, 0, 0, 800, 600, this);

        g.setColor(new Color(255,255,255,220));
        g.setFont(new Font("Arial", Font.BOLD, 26));
        g.drawString("PRACTICE ARENA", 290, 50);
        g.setFont(new Font("Arial", Font.BOLD, 15));
        g.drawString("Free practice: F punch  D block  V kick  R uppercut  E roundhouse  |  arrows to move", 90, 80);

        Image pImg = playerDefault;
        if(playerAnim >= 0)
        {
            pImg = playerFrames[playerAnim][playerFrame];
        }
        g.drawImage(pImg, playerX, 215, 200, 290, this);

        Image eImg = enemyDefault;
        if(enemyAnim >= 0)
        {
            eImg = enemyFrames[enemyAnim][enemyFrame];
        }
        g.drawImage(eImg, enemyX, 215, 200, 290, this);

        if(comboName != null)
        {
            g.setColor(new Color(255,215,0));
            g.setFont(new Font("Arial", Font.BOLD, 32));
            g.drawString(comboName + "!", 60, 130);
        }

        if(showHitFlash)
        {
            g.setColor(new Color(255,0,0,120));
            g.fillOval(playerX + 70, 280, 60, 60);
            showHitFlash = false;
        }

        if(showBlockedText)
        {
            g.setColor(new Color(120,220,255));
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.drawString("Blocked!", playerX + 30, 190);
            showBlockedText = false;
        }
    }
}

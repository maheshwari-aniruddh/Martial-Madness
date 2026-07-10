//ComboLabPanel..java
//pick a combo, it shows you the inputs and you pull it off as fast you can

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.PrintWriter;
import java.net.CookieManager;
import java.util.Scanner;

public class ComboLabPanel extends JPanel implements KeyListener
{


    private static final String[] COMBO_NAMES = {
        "Shadow Kick","Dragon Sweep","Hurricane Kick", "Counter Burst","Death Blossom"

    };
   
   
    private static final String[][] COMBO_MOVES = {
        {"Punch", "Punch","Kick"},
        {"Kick","Kick","Uppercut"},
        {"Punch","Uppercut","Roundhouse"},
        {"Block","Punch","Roundhouse"},
        {"Uppercut","Kick","Punch","Kick"}
    };

    private static int[] bestGrade = new int[COMBO_NAMES.length];
    private static boolean loaded = false;

    private LevelPanelHolder lph;
    private CardLayout levelCards;
    private Information info;
    private Image backPic;

    private final int PUNCH = 0;
    private final int BLOCK = 1;
    private final int KICK = 2;
    private final int UPPERCUT = 3;
    private final int ROUNDHOUSE = 4;
    private final int TOTAL_ANIMATIONS = 5;
    private final int DELAY_FRAMES = 3;
    private Image[][] defaultPlayerFrames;
    private Image defaultPlayerStance;
    private Image[][] ryuPlayerFrames;
    private Image ryuPlayerStance;
    private Image[][] zangiefPlayerFrames;
    private Image zangiefPlayerStance;
    private Image[][] animationFrames;
    private Image defaultImage;
    private int animationPlaying = -1;
    private int currentFrame = 0;
    private int delayCounter = 0;

    private int currentCombo = -1;
    private ComboQueue queue;
    private long drillStart = 0;
    private String resultText = null;
    private Color resultColor = Color.WHITE;
    private Timer drillTimer;

    public ComboLabPanel(LevelPanelHolder lphIn, CardLayout levelCardsIn, Information infoIn)
    {
        lph = lphIn;
        levelCards = levelCardsIn;
        info = infoIn;

        load();
        queue = new ComboQueue();
        backPic = info.getMyImage("dojo.png");

        defaultPlayerStance = info.getMyImage("animations/default/default.png");
        ryuPlayerStance = info.getMyImage("animations/ryu/default.png");
        zangiefPlayerStance = info.getMyImage("animations/zangief/default.png");
        defaultPlayerFrames = loadCharacterAnimations("animations/default", defaultPlayerStance);
        ryuPlayerFrames = loadCharacterAnimations("animations/ryu", ryuPlayerStance);
        zangiefPlayerFrames = loadCharacterAnimations("animations/zangief", zangiefPlayerStance);
        animationFrames = defaultPlayerFrames;
        defaultImage = defaultPlayerStance;

        setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.setOpaque(false);
        JButton title = new JButton("COMBO LAB");
        title.setEnabled(false);
        top.add(title);
        add(top,BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new GridLayout(2,3,15,10));

        class ComboButtonHandler implements ActionListener
        {
            private int comboId;

            public ComboButtonHandler(int id)
            {
                comboId = id;
            }

            public void actionPerformed(ActionEvent evt)
            {
                startDrill(comboId);
            }
        }

        for(int i = 0; i<COMBO_NAMES.length; i++)
        {
            JButton b = new JButton(COMBO_NAMES[i]);
            b.setPreferredSize(new Dimension(200,50));
            b.addActionListener(new ComboButtonHandler(i));
            buttonPanel.add(b);
        }

        JButton backButton = new JButton("Back to Levels");
        backButton.setPreferredSize(new Dimension(200,50));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt)
            {
                currentCombo = -1;
                levelCards.show(lph,"Levels");
            }
        }); 
        buttonPanel.add(backButton);
        add(buttonPanel, BorderLayout.SOUTH);

        addKeyListener(this);
        setFocusable(true);

        drillTimer = new Timer(100,new ActionListener() {
            public void actionPerformed(ActionEvent evt)
            {
                if(currentCombo>=0)
                {
                    advanceAnimation();
                    if(System.currentTimeMillis() - drillStart>4000)
                    {
                        finishDrill(-1);
                    }
                    repaint();
                }
            }
        });
    }

    public void openLab()
    {
        String type = info.getCharacterType();
        if(type.equals("ninja"))
        {
            animationFrames = ryuPlayerFrames;
            defaultImage = ryuPlayerStance;
        }
        else if(type.equals("sumo"))
        {
            animationFrames = zangiefPlayerFrames;
            defaultImage = zangiefPlayerStance;
        }
        else
        {
            animationFrames = defaultPlayerFrames;
            defaultImage = defaultPlayerStance;
        }

        currentCombo = -1;
        resultText = null;
        animationPlaying = -1;
        currentFrame = 0;
        requestFocusInWindow();
        repaint();
    }

    private void startDrill(int comboId)
    {
        currentCombo = comboId;
        queue.clear();
        drillStart = System.currentTimeMillis();
        resultText = null;
        animationPlaying = -1;
        currentFrame = 0;
        drillTimer.start();
        requestFocusInWindow();
        repaint();
    }

    private int animForMove(String move)
    {
        if(move.equals("Punch")) return PUNCH;
        if(move.equals("Block")) return BLOCK;
        if(move.equals("Kick")) return KICK;
        if(move.equals("Uppercut")) return UPPERCUT;
        if(move.equals("Roundhouse")) return ROUNDHOUSE;
        return -1;
    }

    private void advanceAnimation()
    {
        if(animationPlaying < 0)
        {
            return;
        }

        int frameLen = animationFrames[animationPlaying].length;
        if(currentFrame < frameLen - 1)
        {
            currentFrame++;
        }
        else if(delayCounter < DELAY_FRAMES)
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

    private Image[][] loadCharacterAnimations(String inputDir, Image defaultFrame)
    {
        Image[][] target = new Image[TOTAL_ANIMATIONS][];

        target[PUNCH] = new Image[4];
        target[PUNCH][0] = defaultFrame;
        target[PUNCH][1] = info.getMyImage(inputDir + "/punch_animation copy/frame1.png");
        target[PUNCH][2] = info.getMyImage(inputDir + "/punch_animation copy/frame2.png");
        target[PUNCH][3] = info.getMyImage(inputDir + "/punch_animation copy/frame3.png");

        target[BLOCK] = new Image[4];
        target[BLOCK][0] = defaultFrame;
        target[BLOCK][1] = info.getMyImage(inputDir + "/block_animation/frame1.png");
        target[BLOCK][2] = info.getMyImage(inputDir + "/block_animation/frame2.png");
        target[BLOCK][3] = info.getMyImage(inputDir + "/block_animation/frame3.png");

        target[KICK] = new Image[4];
        target[KICK][0] = defaultFrame;
        target[KICK][1] = info.getMyImage(inputDir + "/kick_animation/frame1.png");
        target[KICK][2] = info.getMyImage(inputDir + "/kick_animation/frame2.png");
        target[KICK][3] = info.getMyImage(inputDir + "/kick_animation/frame3.png");

        target[UPPERCUT] = new Image[5];
        target[UPPERCUT][0] = defaultFrame;
        target[UPPERCUT][1] = info.getMyImage(inputDir + "/uppercut_animation/frame1.png");
        target[UPPERCUT][2] = info.getMyImage(inputDir + "/uppercut_animation/frame2.png");
        target[UPPERCUT][3] = info.getMyImage(inputDir + "/uppercut_animation/frame3.png");
        target[UPPERCUT][4] = info.getMyImage(inputDir + "/uppercut_animation/frame4.png");

        target[ROUNDHOUSE] = new Image[10];
        target[ROUNDHOUSE][0] = defaultFrame;
        target[ROUNDHOUSE][1] = info.getMyImage(inputDir + "/roundhouse_animations/frame1.png");
        target[ROUNDHOUSE][2] = info.getMyImage(inputDir + "/roundhouse_animations/frame2.png");
        target[ROUNDHOUSE][3] = info.getMyImage(inputDir + "/roundhouse_animations/frame3.png");
        target[ROUNDHOUSE][4] = info.getMyImage(inputDir + "/roundhouse_animations/frame4.png");
        target[ROUNDHOUSE][5] = info.getMyImage(inputDir + "/roundhouse_animations/frame5.png");
        target[ROUNDHOUSE][6] = info.getMyImage(inputDir + "/roundhouse_animations/frame6.png");
        target[ROUNDHOUSE][7] = info.getMyImage(inputDir + "/roundhouse_animations/frame7.png");
        target[ROUNDHOUSE][8] = info.getMyImage(inputDir + "/roundhouse_animations/frame8.png");
        target[ROUNDHOUSE][9] = info.getMyImage(inputDir + "/roundhouse_animations/frame9.png");

        return target;
    }

    private void finishDrill(int grade)
    {
        drillTimer.stop();

        if(grade<0)
        {
            resultText = "Too slow! Try again";
            resultColor = new Color(255,90,90);
        }
        else
        {
            String letter = gradeLetter(grade);
            resultText = "Grade: "+letter+"!";
            resultColor = new Color(255,215,0);


            int reward = grade*5;
            ShopPanel.addCoins(reward);
            SoundManager.combo();
        
            if(grade>bestGrade[currentCombo])
            {
                bestGrade[currentCombo] = grade;
                save();
            }
        }
        currentCombo = -1;
        repaint();
    }

    private String gradeLetter(int grade)
    {
        if(grade == 4) return "S";
        if(grade==3) return "A";
        if(grade==2) return "B";
        return "C";
    }

    private int gradeForTime(long ms)
    {
        if(ms<900) return 4;
        if(ms<1400) return 3;
        if(ms<2000) return 2;
        return 1;   
    }

    public void keyPressed(KeyEvent evt)
    {
        if(currentCombo<0)
        {
           return;
        }

        String move = null;
        int code = evt.getKeyCode();

        if(code == KeyEvent.VK_F)
            move = "Punch";
        else if(code == KeyEvent.VK_D)
            move = "Block";
        else if(code==KeyEvent.VK_V)
            move = "Kick";
        else if(code == KeyEvent.VK_R)
        {
            move ="Uppercut";
        }
        else if(code == KeyEvent.VK_E)
            move = "Roundhouse";

        if(move == null)
        {
            return;
        }

        animationPlaying = animForMove(move);
        currentFrame = 0;
        delayCounter = 0;

        queue.addMove(move);

        String detected = queue.checkCombo();
        if(detected!=null)
        {
            if(detected.equals(COMBO_NAMES[currentCombo]))
            {
                long taken = System.currentTimeMillis() - drillStart;
                finishDrill(gradeForTime(taken));
            }
            else
            {
                resultText = "Wrong Combo, that was "+detected;
                resultColor = new Color(255,160,60);
                currentCombo = -1;
                drillTimer.stop();
                repaint();
            }
        }
        else
        {
            repaint();
        }
    }

        public void keyReleased(KeyEvent evt){}
        public void keyTyped(KeyEvent evt){}

        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            g.drawImage(backPic,0,0,800,600,this);
            g.setColor(new Color(0,0,0,140));
            g.fillRect(0,0,800,600);
            
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial",Font.BOLD, 30));
            g.drawString("COMBO LAB", 300, 60);

            if(animationPlaying >= 0 && animationPlaying < TOTAL_ANIMATIONS)
            {
                g.drawImage(animationFrames[animationPlaying][currentFrame], 300, 360, 200, 200, this);
            }
            else
            {
                g.drawImage(defaultImage, 300, 360, 200, 200, this);
            }

            if(currentCombo>=0)
            {
                drawDrill(g);
            }
            else
            {
                drawMenu(g);
            }
        }

        private void drawMenu(Graphics g)
        {
            g.setFont(new Font("Arial", Font.PLAIN,18));
            int y= 130;

            for(int i = 0;i<COMBO_NAMES.length;i++)
            {
                g.setColor(Color.WHITE);
                g.drawString(COMBO_NAMES[i], 90, y);

                g.setColor(new Color(150,210,255));
                g.drawString(inputHint(i), 300, y);

                if(bestGrade[i]>0)
                {
                    g.setColor(new Color(150,210,255));
                    g.drawString("Best: "+gradeLetter(bestGrade[i]), 620, y);

                }
                else
                {
                    g.setColor(Color.GRAY);
                    g.drawString("Best: -",620,y);
                }
                y+=45;

            }

            if(resultText!=null)
            {
                g.setColor(resultColor);
                g.setFont(new Font("Arial",Font.BOLD,26));
                g.drawString(resultText,260,360);
            }

            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Arial",Font.PLAIN,16));
            g.drawString("pick a combo below to start a drill",260,400);
        }

        private void drawDrill(Graphics g)
        {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial",Font.BOLD,26));
            g.drawString("DO THIS:   " + COMBO_NAMES[currentCombo], 200,160);

            g.setColor(new Color(150,210,255));
            g.setFont(new Font("Arial", Font.BOLD, 40));
            g.drawString(inputHint(currentCombo), 160, 230);

            long elapsed = System.currentTimeMillis() - drillStart;
            g.setColor(Color.YELLOW);
            g.setFont(new Font("Arial",Font.BOLD,22));
            g.drawString("Time: "+elapsed+ " ms",320,300);

            g.setColor(Color.LIGHT_GRAY);
            g.setFont(new Font("Arial",Font.PLAIN,16));
            g.drawString("faster = better grade (S under 900ms)",250,340);

        }

        private String inputHint(int comboId)
        {
            String s = "";
            for(int i = 0; i<COMBO_MOVES[comboId].length;i++)
            {
                s = s+keyFor(COMBO_MOVES[comboId][i]);
                if(i<COMBO_MOVES[comboId].length-1)
                {
                    s = s+"  ";
                }
            }
            return s;
        }

        private String keyFor(String move)
        {
            if(move.equals("Punch"))
                return "F";
            if(move.equals("Block"))
                return "D";
            if(move.equals("Kick"))
                return "V";
            if(move.equals("Uppercut"))
                return "R";
            if(move.equals("Roundhouse"))
                return "E";
            return "?";
        }
        private static void load()
        {
            if(loaded == true)
            {
                return;
            }
            loaded = true;

            try
            {
                File f = new File("combolab.txt");
                if(f.exists() == false)
                {
                    return;
                }
                Scanner sc = new Scanner(f);
                for(int i = 0; i<COMBO_NAMES.length; i++)
                {
                    bestGrade[i] = sc.nextInt();
                }
                sc.close();
            }
            catch(Exception e)
            {
                for(int i = 0;i<COMBO_NAMES.length; i++)
                {
                    bestGrade[i] = 0;
                }
            }
        }

        private static void save()
        {
            try
            {
                PrintWriter pw = new PrintWriter("combolab.txt");
                for(int i = 0; i<COMBO_NAMES.length;  i++)
                {
                    pw.println(bestGrade[i]);
                }
                pw.close();
            }
            catch(Exception e)
            {
                // oh shit , i couldnt save lmaooo
            }
        }
    }
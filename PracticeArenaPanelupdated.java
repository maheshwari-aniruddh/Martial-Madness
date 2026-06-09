import javax.swing.*;
import java.awt.*;
import java.awt.RenderingHints.Key;
import java.awt.event.*;
import java.sql.Blob;

public class PracticeArenaPanelupdated extends JPanel implements ActionListener, KeyListener
{
    private final JPanel parentHolder;
    private final CardLayout cardLayout;
    private final Information info;

    private Timer frameTimer;
    private Image bgImage;
    private Image defaultImage;
    private Image enemyDefaultImage;

    private Image[][] animationFrames;
    private int currentFrame=0;
    private int animationPlaying = -1;
    private int delayCounter = 0;

    private int imageX = 200;
    private int imageY = 215;
    private int enemyImageX = 450;
    private int enemyImageY = 215;

    private final int PUNCH = 0;
    private final int BLOCK = 1;
    private final int KICK = 2;
    private final int UPPERCUT = 3;
    private final int ROUNDHOUSE =4;
    private final int FORWARD = 5;
    private final int BACKWARD = 6;
    private final int TOTAL_ANIMATIONS = 7;
    private final int DELAY_FRAMES = 3;

    private static final int HIT_RANGE = 180;
    private boolean showHitEffect = false;
    private int hitEffectX = 0;
    private int hitEffectY = 0;

    private Image[][] defaultPlayerFrames;
    private Image defaultPlaterStance;
    private Image[][] ryuPlayerFrames;
    private Image ryuPlayerStance;
    private Image[][] zangiefPlayerStance;

    public PracticeArenaPanelupdated(JPanel parentHolderIn, CardLayout cardLayoutIn, Information gameInfoIn)
    {
        this.parentHolder = parentHolderIn;
        this.cardLayout=cardLayoutIn;
        this.info=gameInfoIn;
        setPreferredSize(new Dimension(800,600));
        setLayout(null);

        bgImage = info.getMyImage("dojo.png");
        defaultPlayerStance = info.getMyImage("animations/default/default.png");
        ryuPlayerStance=info.getMyImage("animations/ryu/default.png");
        zangiefPlayerStance=info.getMyImage("animations/zangief/default.png");
        enemyDefaultImage = info.getMyImage("enemy_animations/default/default.png");

        defaultPlayerFrame = loadCharacterAnimations("animations/default",defaultPlaterStance,false);
        ryuPlayerFrames = loadCharacterAnimations("animations/ryu",ryuPlayerStance,false);
        zangiefPlayerStance = loadCharacterAnimations("animations/zangief",zangiefPlayerStance,false);

        JButton backBtn = new JButton("Exit Training");
        backBtn.setBounds(310,20,180,35);
        backBtn.setFont(new Font("Monospaced",Font.BOLD,14));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusPainted(false);
        backBtn.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,60)));
        backBtn.addActionListener(e -> {
            stopArena();
            cardLayout.show(parentHolder,"First");
        });
        add(backBtn);
        frameTimer = new Timer(33, this);
        setFocusable(true);
        addKeyListener(this);
    
    }

    public void stopArena()
    {
        frameTimer.stop();
    }
    
    public void actionPerformed(ActionEvent e)
    {
        if (animationPlaying>=0 && animationPlaying<TOTAL_ANIMATIONS)
        {
            delayCounter=0;
            currentFrame++;

            if((animationPlaying==PUNCH || animationPlaying==KICK || animationPlaying==UPPERCUT)
                && currentFrame==2)
            {
                checkStrikeConnection();
            }
            if(currentFrame >= animationFrames[animationPlaying].length)
            {
                currentFrame = 0;
                animationPlaying = -1;
            }
        }
        if (simulationTimeTick%30 == 0)
        {
            requestFocusInWindow();
        }
        simulationTimeTick++;
        repaint();
    }
    private int simulationTimeTick = 0;

    private void checkStrikeConnection()
    {
        int distance = Math.abs((imageX+100)-enemyImageX);
        if (distance <=HIT_RANGE)
        {
            int push = 40;
            if (animationPlaying == UPPERCUT)
            {
                push = 60;
            }
            if(animationPlaying == KICK)
            {
                push = 50;
            }

            enemyImageX = Math.min(600, enemyImageX+push);

            showHitEffect = true;
            hitEffectX = enemyImageX+30;
            hitEffectY = enemyImageY+80;
        }
    }

    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();
        if (animationPlaying>=0 && animationPlaying<=ROUNDHOUSE){
            return;
        }
        if(key==KeyEvent.VK_A || key == KeyEvent.VK_LEFT)
        {
            imageX = Math.max(50.imageX - 8);
            animationPlaying = BACKWARD;
        }
        else if(key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT)
        {
            imageX = Math.min(enemyImageX-100,imageX+8);
            animationPlaying = FORWARD;
        }
        else if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
        {
            animationPlaying = BLOCK;
            currentFrame = 1;
        }
        else if(key==KeyEvent.VK_J)
        {
            animationPlaying = KICK;
            currentFrame = 0;
            delayCounter = 0;
        }
        else if(key == KeyEvent.VK_K)
        {
            animationPlaying = PUNCH;
            currentFrame = 0;
            delayCounter = 0;
        }
        else if(key == KeyEvent.VK_L)
        {
            animationPlaying = UPPERCUT;
            currentFrame = 0;
            delayCounter = 0;
        }
    }

    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();

        if(key==KeyEvent.VK_A || key==KeyEvent.VK_LEFT ||
            key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT ||
            key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN)
            {
                if (animationPlaying == FORWARD || animationPlaying == BACKWARD || animationPlaying == BLOCK)
                {
                    animationPlaying = -1;
                    currentFrame = 0;
                }

            } 
    }

    public void keyTyped(KeyEvent e){}

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(Graphics g);

        if(bgImage!=null)
        {
            g.drawImage(bgImage,0,-20,800,600,this);
        }
        else{
            g.setColor(new Color(25,30,40));
            g.fillRect(0,0,getWidth(),getHeight());
        }

        if(animationPlaying>=0 && animationPlaying<TOTAL_ANIMATIONS)
        {
            g.drawImage(animationFrames[animationPlaying][currentFrame],imageX,imageY,200,200,this);
        }
        else{
            g.drawImage(defaultImage,imageX,imageY,200,200,this);
        }

        g.drawImage(enemyDefaultImage, enemyImageX,enemyImageY,200,200,this);
        if(showHitEffect)
        {
            g.setColor(new Color(255,0,0,150));
            g.fillOval(hitEffectX-25,hitEffectY-25,50,50);
            showHitEffect = false;
        }
    }

    public Image[][] loadCharacterAnimations(String inputDir,Image defaultFrame,boolean isEnemy)
    {
        Image[][] target = new Image[TOTAL_ANIMATIONS][];

        target[PUNCH] = new Image[4];
        target[PUNCH][0] = defaultFrame;
        target[PUNCH][1] = info.getMyImage(inputDir + "/punch_animation copy/frame1.png");
        target[PUNCH][2] = info.getMyImage(inputDir + "/punch_animation copy/frame2.png");
        target[PUNCH][3] = info.getMyImage(inputDir + "/punch_animation copy/frame3.png");

        target[BLOCK] = new Image[4];
        target[BLOCK][0] = defaultFrame;
        target[BLOCK][1] = info.getMyImage(inputDir + "/block_animation copy/frame1.png");
        target[BLOCK][2] = info.getMyImage(inputDir + "/block_animation copy/frame2.png");
        target[BLOCK][3] = info.getMyImage(inputDir + "/block_animation copy/frame3.png");

        target[KICK] = new Image[4];
        target[KICK][0] = defaultFrame;
        target[KICK][1] = info.getMyImage(inputDir + "/kick_animation copy/frame1.png");
        target[KICK][2] = info.getMyImage(inputDir + "/kick_animation copy/frame2.png");
        target[KICK][3] = info.getMyImage(inputDir + "/kick_animation copy/frame3.png");

        target[UPPERCUT] = new Image[5];
        target[UPPERCUT][0] = defaultFrame;
        target[UPPERCUT][1] = info.getMyImage(inputDir + "/uppercut_animation copy/frame1.png");
        target[UPPERCUT][2] = info.getMyImage(inputDir + "/uppercut_animation copy/frame2.png");
        target[UPPERCUT][3] = info.getMyImage(inputDir + "/uppercut_animation copy/frame3.png");
        target[UPPERCUT][4] = info.getMyImage(inputDir + "/uppercut_animation copy/frame1.png");

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

        if(isEnemy)
        {
            target[FORWARD] = Image[6];
            target[FORWARD][0] = defaultFrame;
            target[FORWARD][1] = info.getMyImage(inputDir+"/backward_animation/frame1.png");
            target[FORWARD][2] = info.getMyImage(inputDir+"/backward_animation/frame2.png");
            target[FORWARD][3] = info.getMyImage(inputDir+"/backward_animation/frame3.png");
            target[FORWARD][4] = info.getMyImage(inputDir+"/backward_animation/frame4.png");
            target[FORWARD][5] = info.getMyImage(inputDir+"/backward_animation/frame5.png");

            target[BACKWARD] = Image[6];
            target[BACKWARD][0] = defaultFrame;
            target[BACKWARD][1] = info.getMyImage(inputDir+"/forward_animation/frame1.png");
            target[BACKWARD][2] = info.getMyImage(inputDir+"/forward_animation/frame2.png");
            target[BACKWARD][3] = info.getMyImage(inputDir+"/forward_animation/frame3.png");
            target[BACKWARD][4] = info.getMyImage(inputDir+"/forward_animation/frame4.png");
            target[BACKWARD][5] = info.getMyImage(inputDir+"/forward_animation/frame5.png");

        }
        return target;
    }

}
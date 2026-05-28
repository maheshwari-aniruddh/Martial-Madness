import java.awt.Graphics;
import javax.swing.JPanel;
import javax.swing.JFrame;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JCheckBox;
import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;

import java.awt.Insets;

import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import javax.imageio.ImageIO;
import java.awt.Image;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.CardLayout;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Scanner;


public class MartialMadness
{
    public MartialMadness()
    {
    }

    /*
    Main method to start the application
    
    */
   
    public static void main(String[] args)
    {
        MartialMadness mm = new MartialMadness();
        mm.run();
    }

    public void run()
    {
        JFrame frame = new JFrame("Martial Madness");
        frame.setSize(800,600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocation(0,0);
        frame.setResizable(false);
        MartialMadnessHolder mmh = new MartialMadnessHolder();
        frame.getContentPane().add(mmh);
        frame.setVisible(true);

    }
}
/*
Main panel that holds and manages the card layout
*/
class MartialMadnessHolder extends JPanel
{
    public MartialMadnessHolder()
    {
        CardLayout cards = new CardLayout();
        setLayout(cards);

        Information info = new Information();
        InstructionPanel ip = new InstructionPanel(this, cards, info);

        FirstPagePanel fpp = new FirstPagePanel(this, cards, info);
        HighScorePanel hsp = new HighScorePanel(this, cards, lph, info);

        add(fpp, "First");
        add(hsp, "HighScores");
        add(ip, "Instructions");
        add(lph, "LevelHolder");

    }

}


class FirstPagePanel extends JPanel
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private Information info;
    private JTextField tfName;
    private Image backPic;

    public FirstPagePanel(MartialMadnessHolder mmhIn, CardLayout cardsIn, Information infoIn)
    {

        mmh = mmhIn;
        cards = cardsIn;
        info = infoIn;
        setLayout(null);

        class StartButtonHandler implements ActionListener
        {   
            public void actionPerformed(ActionEvent evt)
            {
                String command = evt.getActionCommand();
                if(command.equals("Play"))
                    cards.show(mmh, "Instructions");
                else if(command.equals("High Scores"))
                    cards.show(mmh, "HighScores");
                else if(command.equals("Quit"))
                    System.exit(1);

            }

        }
        
        StartButtonHandler sbh = new StartButtonHandler();
        JButton instructionButton = new JButton("Play");
        instructionButton.setEnabled(false);
        JButton highScore = new JButton("High Scores");
        highScore.setEnabled(false);
        JButton quitButton = new JButton("Quit");
        quitButton.setEnabled(false);
        instructionButton.setBounds(125, 300, 120, 50);
        instructionButton.addActionListener(sbh);
        add(instructionButton);

        highScore.setBounds(325, 300, 120, 50);
        highScore.addActionListener(sbh);
        add(highScore);

        quitButton.setBounds(525, 300, 120, 50);
        quitButton.addActionListener(sbh);
        add(quitButton);

        
        class TextFieldHandler implements ActionListener
        {
            public void actionPerformed(ActionEvent e)
            {
                String enteredText = e.getActionCommand();
                if(enteredText != null && !enteredText.trim().isEmpty())
                {
                    instructionButton.setEnabled(true);
                    highScore.setEnabled(true);
                    quitButton.setEnabled(true);
                }
                info.setName(enteredText);
            }

        }

        tfName = new JTextField("Enter your name here", 20);
        TextFieldHandler tfHandler = new TextFieldHandler();
        tfName.addActionListener(tfHandler);
        tfName.setEditable(true);
        tfName.setBounds(270, 415, 275, 25);
        add(tfName);

        backPic = info.getMyImage("start_page.jpeg");
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(backPic, 0, 0, 800, 600, this);
    }

}



class FixedPanelHolder extends JPanel
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private boolean inGame;
    private LevelPanelHolder lph;
    
    public FixedPanelHolder(MartialMadnessHolder mmhIn, CardLayout cardsIn, boolean inGameIn, LevelPanelHolder lphIn)
    {   
        mmh = mmhIn;
        cards = cardsIn;
        inGame = inGameIn;
        lph = lphIn;


        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(100, 50));

        JButton homeButton = new JButton("Home");
        homeButton.setPreferredSize(new Dimension(100, 50));

        JButton quitButton = new JButton("Quit");
        quitButton.setPreferredSize(new Dimension(100, 50));

        class ButtonHandler implements ActionListener
        {
            public void actionPerformed(ActionEvent evt)
            {
                String command = evt.getActionCommand();
                if (command.equals("Back") && inGame && lph != null)
                {
                    CardLayout levelCards = (CardLayout)lph.getLayout();
                    levelCards.show(lph, "Levels");
                }
                else if (command.equals("Back") && inGame)
                {
                    cards.show(mmh, "LevelHolder");                
                }
                else if (command.equals("Quit"))
                    System.exit(1);
                else if (command.equals("Home"))
                    cards.show(mmh, "First");
            }

        }
        
        ButtonHandler bHandler = new ButtonHandler();
        homeButton.addActionListener(bHandler);
        quitButton.addActionListener(bHandler);
        backButton.addActionListener(bHandler);


        JPanel bPanel = new JPanel();
        bPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 100, 20));
        bPanel.add(backButton);
        bPanel.add(homeButton);
        bPanel.add(quitButton);
        add(bPanel);

    }
}
class InstructionPanel extends JPanel
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private Information info;

    public InstructionPanel(MartialMadnessHolder mmhIn, CardLayout cardsIn, Information infoIn)
    {
        info = infoIn;
        mmh = mmhIn;
        cards = cardsIn;
        setLayout(new BorderLayout());

        JButton continueButton = new JButton("Continue");
        continueButton.setPreferredSize(new Dimension(100, 40));
        continueButton.setEnabled(false);

        class InstructionButtonHandler implements ActionListener
        {
            public void actionPerformed(ActionEvent evt)
            {
                String command = evt.getActionCommand();
                if (command.equals("Back"))
                    cards.show(mmh, "First");
                else if ((command.equals("Continue")))
                    cards.show(mmh, "LevelHolder");
                else if (command.equals("Quit"))
                    System.exit(1);

            }
        }
            
        JPanel instructionLabelPanel = new JPanel();
        Color instructionLabelColor = new Color(24, 196, 132);
        instructionLabelPanel.setBackground(instructionLabelColor);
        JLabel instructionLabel = new JLabel("Instructions");
        instructionLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        instructionLabelPanel.add(instructionLabel);
        add(instructionLabelPanel, BorderLayout.NORTH);
        JCheckBox understood = new JCheckBox("I understand how to play the game");

        JPanel eastKeyPanel = new JPanel();

        Color eastColor = new Color(154, 224, 104);
        eastKeyPanel.setBackground(eastColor);
        eastKeyPanel.setLayout(new GridLayout(5, 1, 50, 20));
        eastKeyPanel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        JLabel rightLabel = new JLabel("Right Arrow to move right ");
        JLabel leftLabel = new JLabel("Left Arrow to move left");
        JLabel punchLabel = new JLabel("F key to punch");
        JLabel blockLabel = new JLabel("D key to block");
        JLabel kickLabel = new JLabel("V key to kick");
        eastKeyPanel.add(rightLabel);
        eastKeyPanel.add(leftLabel);
        eastKeyPanel.add(punchLabel);
        eastKeyPanel.add(kickLabel);
        eastKeyPanel.add(blockLabel);
        add(eastKeyPanel, BorderLayout.WEST);

        // this class changes the boolean to true or false depending on if the check box has been clicked
        
        JPanel centerPanel = new JPanel();
        centerPanel.setBorder(BorderFactory.createEmptyBorder(30, 20, 20, 20));
        JTextArea howToPlayTxt = new JTextArea(
            "This is a java game to teach self defense, you will learn defense moves to save yourself in combat. There will be a tutorial so you can learn how to play and then you will play in the level. You can block, kick, punch and move left and right. As you progress you will learn stronger and better moves. In the gameplay, you both have health bars and the enemy health bar will deplete at a set rate and you have to defend yourself until the time runs out. The enemy's attacks are random and their speed and complexity will also increase as you progress. Once you defeat the enemy, you can move on to the next level", 15, 0
        );

        howToPlayTxt.setLineWrap(true);
        howToPlayTxt.setMargin(new Insets(10, 10, 10, 10));
        howToPlayTxt.setWrapStyleWord(true);
        howToPlayTxt.setEditable(false);
        centerPanel.setLayout(new BorderLayout());
        centerPanel.add(howToPlayTxt, BorderLayout.NORTH);

        class CheckBoxListener implements ActionListener
        {
            public void actionPerformed(ActionEvent evt)
            {
                String cmd = evt.getActionCommand();
                if (cmd.equals("I understand how to play the game"))
                {
                    understood.setSelected(true);
                    continueButton.setEnabled(true);
                }
                else
                {
                    understood.setSelected(false);
                }
            }
        }
        CheckBoxListener cbl = new CheckBoxListener();
        understood.addActionListener(cbl);
        centerPanel.add(understood, BorderLayout.SOUTH);
        add(centerPanel, BorderLayout.CENTER);

        JPanel westPanel = new JPanel();
        Color westColor = new Color(152, 194, 152);
        westPanel.setBackground(westColor);
        westPanel.setBorder(BorderFactory.createEmptyBorder(0,0,0,20));
        westPanel.setLayout(new BorderLayout());
        JLabel westPanelLabel = new JLabel("Game Play Pictures:");
        westPanelLabel.setFont(new Font("Dialog", Font.BOLD,15));
        westPanel.add(westPanelLabel,BorderLayout.EAST);

        ImagePanel ip = new ImagePanel(mmh, info);
        westPanel.add(ip,BorderLayout.CENTER);
        add(westPanel, BorderLayout.EAST);

        InstructionButtonHandler ibh = new InstructionButtonHandler();
        JPanel southPanel = new JPanel();
        Color southColor = new Color(50,205,50);
        southPanel.setBackground(southColor);
        southPanel.setLayout(new FlowLayout(FlowLayout.LEFT,100,30));
        JButton goBack = new JButton("Back");
        goBack.addActionListener(ibh);
        goBack.setPreferredSize(new Dimension(100,40));
        continueButton.addActionListener(ibh);

        JButton quitButton = new JButton("Quit");
        quitButton.addActionListener(ibh);
        quitButton.setPreferredSize(new Dimension(100,40));
        southPanel.add(goBack);
        southPanel.add(continueButton);
        southPanel.add(quitButton);
        add(southPanel,BorderLayout.SOUTH);
    }

    class ImagePanel extends JPanel
    {
        private MartialMadnessHolder mmh;
        private Information info;

        public ImagePanel(MartialMadnessHolder mmhIn, Information infoIn)
        {
            info = infoIn;
            setBackground(new Color(152,194,152));
            mmh = mmhIn;
        }

        //drawing the three pictures
        public void paintComponent(Graphics g)        
        {
            super.paintComponent(g);
            Image pic1 = info.getMyImage("pic1.png");
            Image pic2 = info.getMyImage("pic2.png");
            Image pic3 = info.getMyImage("pic3.png");
            g.drawImage(pic1, 0,0,160,140,this);
            g.drawImage(pic2,0,150,160,140,this);
            g.drawImage(pic3,0,300,160,130,this);

        }

    }
}

class LevelPanelHolder extends JPanel
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private Information info;
    private LevelPanel lp;

    public LevelPanelHolder(MartialMadnessHolder mmhIn, CardLayout cardsIn, Information infoIn)
    {
        info = infoIn;
        double[] level0 = {5.0,6,5000.0};
        double[] level1 = {7.0,5,4500.0};
        double[] level2 = {10.0,4,3500.0};
        double[] level3 = {13,3,2500.0};
        double[] level4 = {15.0,2,2000.0};
        double[] level5 = {20.0,1,1500.0};
        double[][] levels = {level0,level1,level2,level3,level4,level5};

        mmh = mmhIn;
        cards = cardsIn;

        CardLayout levelCards = new CardLayout();
        setLayout(levelCards);

        lp = new LevelPanel(this,levelCards,mmh,cards,info);
        add(lp,"Levels");
        GamePanel gp1 = new GamePanel(mmh, cards,this,info,"clouds.jpg",level1);
        Tutorial tp = new Tutorial(mmh,cards,this,gp1,info);
        add(tp,"Tutorial");
        ExtraCredit ec = new ExtraCredit(mmh,cards,this,info);
        add(ec,"ExtraCredit");

        //for loop to initialize the game panels with their respective images and adding them
        String[] backGroundPicNames = {"canyon.jpeg","desert.jpg","forest.jpg","mystical.jpg","rural_pasture.jpg","tents.jpg"};
        for(int i=0;i<6;i++)
        {
            GamePanel gp = new GamePanel(mmh,cards,this,info,backGroundPicNames[i],levels[i]);
            add(gp,"GamePanel" + (i));
    
        }

    }
}

class LevelPanel extends JPanel
{
    private LevelPanelHolder lph;
    private CardLayout levelCards;
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private Information info;
    private JButton[] levelButtons;

    public LevelPanel(LevelPanelHolder lphIn, CardLayout levelCardsIn, MartialMadnessHolder mmhIn, CardLayout cardsIn, Information infoIn)
    {
        info = infoIn;
        lph = lphIn;
        levelCards = levelCardsIn;
        mmh = mmhIn;
        cards = cardsIn;
        setLayout(new BorderLayout());

        class ButtonHandler implements ActionListener
        {
            public void actionPerformed(ActionEvent evt)
            {
                String command = evt.getActionCommand();
                if(command.equals("Tutorial"))
                {
                    levelCards.show(lph,"Tutorial");
                }
                else if(command.equals("Level 1"))
                {
                    levelCards.show(lph,"Level 1");
                }
                else if(command.equals("Level 2"))
                {
                    levelCards.show(lph,"Level 2");
                }
                else if (command.equals("Level 3"))
                {
                    levelCards.show(lph,"Level 3");
                }
                else if(command.equals("Level 4"))
                {
                    levelCards.show(lph,"Level 4");
                }
                else if(command.equals("Level 5"))
                {
                    levelCards.show(lph,"Level 5");
                }
                else if(command.equals("Level 6"))
                {
                    levelCards.show(lph,"Level 6");
                }
                else if(command.equals("Extra Points"))
                {
                    levelCards.show(lph,"Extra Credit");
                }
            }


        }

        JPanel levelPanel = new JPanel();
        JLabel levelLabel = new JLabel("Choose Level");
        levelLabel.setFont(new Font("Dialog",Font.BOLD,20));
        levelPanel.add(levelLabel);
        add(levelPanel,BorderLayout.NORTH);

        JPanel centerPanel = new JPanel();
        Color centerColor = new Color(143,145,218);
        centerPanel.setBackground(centerColor);
        centerPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 100,30));

        ButtonHandler bh = new ButtonHandler();
        levelButtons = new JButton[7];

        JButton level0 = new JButton("Tutorial");
        level0.setPreferredSize(new Dimension(150,100));
        level0.addActionListener(bh);
        levelButtons[0] = level0;

        JButton level1 = new JButton("Level 1");
        level1.setPreferredSize(new Dimension(150,100));
        level1.addActionListener(bh);
        levelButtons[1] = level1;

        JButton level2 = new JButton("Level 2");
        level2.setPreferredSize(new Dimension(150,100));
        level2.addActionListener(bh);
        levelButtons[2] = level2;

        JButton level3 = new JButton("Level 3");
        level3.setPreferredSize(new Dimension(150,100));
        level3.addActionListener(bh);
        levelButtons[3] = level3;

        JButton level4 = new JButton("Level 4");
        level4.setPreferredSize(new Dimension(150,100));
        level4.addActionListener(bh);
        levelButtons[4] = level4;

        JButton level5 = new JButton("Level 5");
        level5.setPreferredSize(new Dimension(150,100));
        level5.addActionListener(bh);
        levelButtons[5] = level5;

        JButton level6 = new JButton("Level 6");
        level6.setPreferredSize(new Dimension(150,100));
        level6.addActionListener(bh);
        levelButtons[6] = level6;

        JButton level7 = new JButton("Extra Points");
        level7.setPreferredSize(new Dimension(150,100));
        level7.addActionListener(bh);

        centerPanel.add(level0);
        centerPanel.add(level1);
        centerPanel.add(level2);
        centerPanel.add(level3);
        centerPanel.add(level4);
        centerPanel.add(level5);
        centerPanel.add(level6);
        centerPanel.add(level7);
        add(centerPanel,BorderLayout.CENTER);

        FixedPanelHolder fph = new FixedPanelHolder(mmh,cards,false,lph);
        add(fph,BorderLayout.SOUTH);
    }
}


class Information
{
    private int points;
    private int levelComplete;
    private String name;
    private String outFileName;
    private PrintWriter pw;

    public Information()
    {
        points = 0;
        levelComplete = 0;
        outFileName = "highscores.txt";
        
    }
}
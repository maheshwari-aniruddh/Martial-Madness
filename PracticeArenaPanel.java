import javax.smartcardio.Card;
import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class PracticeArenaPanel extends JPanel implements ActionListener, KeyListener
{
    private final JPanel parentHolder;
    private final CardLayout cardLayout;
    private final Infomation gameInfo;

    private Timer arenaTimer;
    private int simulationTime;
    
    private final SinglyLinkedList<String> inputBuffer = new SinglyLinkedList<>();
    private final SinglyLinkedList<String> loggedInputs = new SinglyLinkedList<>();
    private final SinglyLinkedList<String> detectedCombos = new SinglyLinkedList<>();

    private long enemyAttackStartTime = 0;
    private boolean isEnemyAttacking = false;
    private long succesfulBlockTime;

    private int totalEnemyAttacks = 0;
    private int succesfulBlocks = 0;
    private long totalReactionTimeMs = 0;
    private double blockSuccessRate = 0.0;
    
    private JSlider enemySpeedSlider;
    private JSLider attackRateSlider;
    private JComboBox<String> opponentStyleSelector;

    private JTextArea inputLogArea;
    private JTextArea comboConsole;
    private JLabel statsLabel;
    

    private int dummyPlayerX = 150;
    private int dummyEnemyX = 550;
    private String dummyPlayerAction = "STANCE";
    private String dummyEnemyAction = "STANCE";

    public PracticeArenaPanel(JPanel parentHolderIn, CardLayout cardLayoutIn, Information gameInfoIn)
    {
        this.parentHolder = parentHolderIn;
        this.cardLayout = cardLayoutIn;
        this.gameInfo = gameInfoIn;
        
        setPreferredSize(new Dimension(800,600));
        setBackground(new Color(15,20,30));
        setLayout(null);

        initializeSimulationControls();
        initializeDashboard();

        arenaTimer= new Timer(33, this);

        setFocusable(true);
        addKeyListener(this);

    }

    private void initializeSimulationControls()
    {
        JPanel controlPanel= new JPanel();
        controlPanel.setLayout(null);
        controlPanel.setBounds(540,20,240,340);
        controlPanel.setBackground(new Color(25,30,45,200));
        controlPanel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(255,255,255,6)),
            "Simulator Settings",0,0,
            new Font("Monospaced", Font.BOLD,14),Color.YELLOW
        ));
        JLabel styleLabel = new JLabel("Oppenent Archetype:");
        styleLabel.setForeground(Color.WHITE);
        styleLabel.setFont(new Font("Monospaced",Font.PLAIN.,12);
        styleLabel.setBounds(15,30,210,20);
        controlPanel.add(styleLabel);

        String[] styles = {"Balanced AI","Aggresive Rush","Defensive Counters"};
        opponentStyleSelector = new JComboBox<>(styles);
        opponentStyleSelector.setBounds(15,55,210,25);
        opponentStyleSelector.setBackground(new Color(40,45,60));
        opponentStyleSelector.setForeground(Color.WHITE);
        controlPanel.add(opponentStyleSelector);

        JLabel speedLabel = new JLabel("Enemy Speed Multiplier");
        speedLabel.setForeground(Color.WHITE);
        speedLabel.setFont(new Font("Monospaced",Font.PLAIN,12));
        speedLabel.setBounds(15,100,210,20);
        controlPanel.add(speedLabel);

        enemySpeedSlider = new JSlider(1,5,2);
        enemySpeedSlider.setBounds(15,125,210,40);
        enemySpeedSlider.setBackground(new Color(25,30,45));
        enemySpeedSlider.setForeground(Color.WHITE);
        enemySpeedSlider.setMajorTickSpacing(1);
        enemySpeedSlider.setPaintTicks(true);
        enemySpeedSlider.setPaintLabels(true);
        controlPanel.add(rateLabel);

        JLabel rateLabel = new JLabel("Attack Interval (sec)");
        rateLabel.setForeground(Color.WHITE);
        rateLabel.setFont(new Font("Monospaced",Font.PLAIN,12));
        rateLabel.setBounds(15,185,210,20);
        controlPanel.add(rateLabel);

        JButton triggerAttackBtn = new JButton("Froced Enemy Strike");
        triggerAttackBtn.setBounds(15,270,210,30);
        triggerAttackBtn.setFont(new Font("Monospaced",Font.BOLD,12));
        triggerAttackBtn.setBackground(new Color(255,75,75));
        triggerAttackBtn.setForeground(Color.WHITE);
        triggerAttackBtn.setFocusable(false);
        triggerAttackBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e)
            {
                simulateEnemyAttack();
            }
        });
        controlPanel.add(triggerAttackBtn);
        add(controlPanel);
    }

    private void initializeDashboard(){
        JLabel logLabel - new JLabel("Live Input Buffer:");
        logLabel.setForeground(Color.CYAN);
        logLabel.setFont(new Font("Monospaced", Font.BOLD,14));
        logLabel.setBounds(20,20,200,20);
        add(logLabel);

        inputLogArea = new JTextArea();
        inputLogArea.setFont("Monospaced",Font.PLAIN,13);
        inputLogArea.setForeground(Color.WHITE);
        inputLogArea.setBackground(Color.GREEN);
        inputLogArea.setEditable(false);
        inputLogArea.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,30)));

        JScrollPane logScroll = new JScrollPane(inputLogArea);
        logScroll.setBounds(20,45,230,120);
        add(logScroll);

        JLabel comboLabel = new JLabel("Detected Combos");
        comboLabel.setForeground(Color.ORANGE);
        comboLabel.setFont(new Font("Monospaced",Font.BOLD,14));
        comboLabel.setBounds(270,20,200,20);
        add(comboLabel);

        comboConsole = new JTextArea();
        comboConsole.setFont(new Font("Monospaced",Font.PLAIN,13));
        comboConsole.setForeground(Color.ORANGE);
        comboConsole.setBackground(new Color(10,15,20));
        comboConsole.setEditable(false);
        comboConsole.setBorder(BorderFactory.createLineBorder(new Color(255,255,255,30)));

        JScrollPane comboScroll = new JScrollPane(comboConsole);
        comboScroll.setBounds(270,45,250,120);
        add(comboScroll);

        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(null);
        statsPanel.setBounds(20,185,500,100);
        statsPanel.setBackground(new Color(25,30,40));
        statsPanel.setBorder(BorderFactory.createLineBorder(new Color (255,255,255,30)));

        statsLabel = new JLabel("<html><b>TRAINING PERFORMANCE LOGS:</b<>br/>" +
            "Total Attacks: 0 | Succesful Blocks: 0<br/>" +
            "Block Success Rate: 0.0%<br/>" +
            "Average Reflex Response Time: 0ms</html>");
        statsLabel.setFont(new Font("Monospaced",Font.PLAIN,13));
        statsLabel.setForeground(Color.WHITE);
        statsLabel.setBounds(15,10,470,80);
        statsPanel.add(statsLabel);
        add(statsPanel);

        JButton resetStatsBtn = new JButton("Reset Statistics");
        resetStatsBtn.setBounds(20,300,160,30);
        resetStatsBtn.setFont(new Font("Monospaced",Font.PLAIN,13));
        resetStatsBtn.setBackground(new Color(40,50,65));
        resetStatsBtn.setForeground(Color.WHITE);
        resetStatsBtn.setFocusable(false);
        resetStatsBtn.addActionListener(e -> resetStats());
        add(resetStatsBtn);

        JButton backBtn = new JButton("Back to Menu");
        backBtn.setBounds(360,300,160,30);
        backBtn.setFont(new Font("Monospaced",Font.BOLD,12));
        backBtn.setBackground(new Color(40,50,65));
        backBtn.setForeground(Color.WHITE);
        backBtn.setFocusable(false);
        backBtn.addActionListener(e ->{
            stopArena();
            cardLayout.show(parentHolder,"First");
        });
        add(backBtn);

    }

    public void startArena()
    {
        arenaTimer.start();
        resetStats();
        requestFocusInWindow();
    }

     public void stopArena()
     {
        arenaTimer.stop();
     }

     private void resetStats()
     {
        totalEnemyAttacks = 0;
        succesfulBlocks =0;
        totalReactionTimeMs = 0;
        blockSuccessRate = 0.0;
        loggedInputs.clear();
        detectedCombos.clear();
        inputBuffer.clear();
        updateDashboardTexts();
     }

     private void updateDashboardTexts()
     {
        long avgReflext;
     }
    
   

}   
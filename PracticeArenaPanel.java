import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;


public class PracticeArenaPanel extends JPanel implements ActionListener, KeyListener
{
    private final JPanel parentHolder;
    private final CardLayout cardLayout;
    private final Information gameInfo;

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
    private JSlider attackRateSlider;
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
        styleLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
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
        controlPanel.add(enemySpeedSlider);

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
        JLabel logLabel = new JLabel("Live Input Buffer:");
        logLabel.setForeground(Color.CYAN);
        logLabel.setFont(new Font("Monospaced", Font.BOLD,14));
        logLabel.setBounds(20,20,200,20);
        add(logLabel);

        inputLogArea = new JTextArea();
        inputLogArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
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

        statsLabel = new JLabel("<html><b>TRAINING PERFORMANCE LOGS:</b><br/>" +
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
        long avgReflex = succesfulBlocks > 0 ? (totalReactionTimeMs / succesfulBlocks) : 0;
        blockSuccessRate = totalEnemyAttacks > 0 ? (((double) succesfulBlocks / totalEnemyAttacks) * 100) : 0.0;
        
        statsLabel.setText(String.format(
            "<html><b>TRAINING PERFORMANCE LOGS:</b><br/>" +
            "Total Attacks: %d | Successful Blocks: %d<br/>" +
            "Block Success Rate: %.1f%%<br/>" +
            "Average Reflex Response Time: %d ms</html>",
            totalEnemyAttacks, succesfulBlocks, blockSuccessRate, avgReflex
        ));

        // Update Log Area (display last 8 logged items using your SinglyLinkedList nodes)
        StringBuilder logBuilder = new StringBuilder();
        int logSize = loggedInputs.size();
        int logStartIndex = Math.max(0, logSize - 8);
        ListNode<String> currentLog = loggedInputs.getHead();
        int idx = 0;
        while (currentLog != null) {
            if (idx >= logStartIndex) {
                logBuilder.append(currentLog.getValue()).append("\n");
            }
            currentLog = currentLog.getNext();
            idx++;
        }
        inputLogArea.setText(logBuilder.toString());

        // Update Combos Area
        StringBuilder comboBuilder = new StringBuilder();
        int comboSize = detectedCombos.size();
        int comboStartIndex = Math.max(0, comboSize - 8);
        ListNode<String> currentCombo = detectedCombos.getHead();
        idx = 0;
        while (currentCombo != null) {
            if (idx >= comboStartIndex) {
                comboBuilder.append(currentCombo.getValue()).append("\n");
            }
            currentCombo = currentCombo.getNext();
            idx++;
        }
        comboConsole.setText(comboBuilder.toString());
    }

    private void simulateEnemyAttack()
    {
        if (isEnemyAttacking) return;
        
        isEnemyAttacking = true;
        totalEnemyAttacks++;
        dummyEnemyAction = "ATTACK";
        enemyAttackStartTime = System.currentTimeMillis();
        
        // Auto-reset enemy pose after 400ms if not blocked
        Timer windDown = new Timer(400, e -> {
            if (isEnemyAttacking) {
                isEnemyAttacking = false;
                dummyEnemyAction = "STANCE";
                updateDashboardTexts();
            }
        });
        windDown.setRepeats(false);
        windDown.start();
        
        updateDashboardTexts();
    }

    // Central loop tick running at 30 FPS
    @Override
    public void actionPerformed(ActionEvent e)
    {
        simulationTime++;
        
        // Dynamic automatic attacks based on slider frequency settings
        int intervalFrames = attackRateSlider.getValue() * 30;
        if (simulationTime % intervalFrames == 0) {
            simulateEnemyAttack();
        }

        // Keep focus
        if (simulationTime % 30 == 0) {
            requestFocusInWindow();
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e)
    {
        int key = e.getKeyCode();
        String inputKey = "";

        if (key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN) {
            inputKey = "BLOCK";
            dummyPlayerAction = "BLOCKING";
        } else if (key == KeyEvent.VK_A || key == KeyEvent.VK_LEFT) {
            inputKey = "BACKWARD";
            dummyPlayerX = Math.max(50, dummyPlayerX - 8);
        } else if (key == KeyEvent.VK_D || key == KeyEvent.VK_RIGHT) {
            inputKey = "FORWARD";
            dummyPlayerX = Math.min(300, dummyPlayerX + 8);
        } else if (key == KeyEvent.VK_J) {
            inputKey = "KICK";
            dummyPlayerAction = "KICKING";
        } else if (key == KeyEvent.VK_K) {
            inputKey = "PUNCH";
            dummyPlayerAction = "PUNCHING";
        }

        if (!inputKey.isEmpty()) {
            registerPlayerInput(inputKey);
        }
    }

    private void registerPlayerInput(String inputKey)
    {
        long now = System.currentTimeMillis();
        loggedInputs.addLast(inputKey + " @ " + now % 100000 + "ms");

        // Add to active combo parsing buffer
        inputBuffer.addLast(inputKey);
        if (inputBuffer.size() > 4) {
            inputBuffer.remove(0); // Pop first item to maintain size of 4
        }

        // 1. Evaluate Reaction Reflex
        if (inputKey.equals("BLOCK") && isEnemyAttacking) {
            isEnemyAttacking = false;
            dummyEnemyAction = "STANCE";
            succesfulBlocks++;
            long reflexMs = now - enemyAttackStartTime;
            totalReactionTimeMs += reflexMs;
            detectedCombos.addLast("REFLEX BLOCK: " + reflexMs + "ms!");
        }

        // 2. Evaluate Combo Patterns
        checkComboSequences();
        updateDashboardTexts();
    }

    private void checkComboSequences()
    {
        // Construct string representing key sequence in the SinglyLinkedList
        StringBuilder sequence = new StringBuilder();
        ListNode<String> current = inputBuffer.getHead();
        while (current != null) {
            sequence.append(current.getValue()).append("->");
            current = current.getNext();
        }
        String segStr = sequence.toString();
        if(segStr.contains("BACKWARD->BLOCK->PUNCH")){
            detectedCombos.addLast("COMBO: Perfect Parry Counter!");
            inputBuffer.clear();
        }
        else if(segStr.contains("BLOCK->KICK->PUNCH"))
        {
            detectedCombos.addLast("COMBO: Shield Smash Combo!");
            inputBuffer.clear();
        }
    }

    public void keyReleased(KeyEvent e)
    {
        int key = e.getKeyCode();
        if(key == KeyEvent.VK_S || key == KeyEvent.VK_DOWN || key == KeyEvent.VK_J || key == KeyEvent.VK_K)
        {
            dummyPlayerAction = "STANCE";
        }

    }

    public void keyTyped(KeyEvent e){}

    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D)g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);

        g2d.setColor(new Color(40,45,60));
        g2d.fillRect(0,360,getWidth(),getHeight()-360);
        g2d.setColor(new Color(60, 70, 90));
        g2d.setStroke(new BasicStroke(3));
        g2d.drawLine(0, 360, getWidth(), 360);

        // Grid lines on floor for depth visual
        g2d.setColor(new Color(50, 55, 75));
        for (int i = 0; i < getWidth(); i += 80) {
            g2d.drawLine(i, 360, i - 100, getHeight());
        }

        // Render dummy characters (simplified silhouettes for practice representation)
        // Draw Player Dummy
        g2d.setColor(Color.CYAN);
        g2d.drawString("PLAYER (Active Keys: A,D,S,J,K)", dummyPlayerX - 40, 405);
        if (dummyPlayerAction.equals("BLOCKING")) {
            g2d.fillRoundRect(dummyPlayerX - 25, 460, 50, 50, 10, 10); // Squat block stance
        } else if (dummyPlayerAction.equals("KICKING")) {
            g2d.fillRoundRect(dummyPlayerX - 25, 420, 50, 90, 10, 10);
            g2d.fillRect(dummyPlayerX + 25, 460, 45, 15); // Extended leg
        } else if (dummyPlayerAction.equals("PUNCHING")) {
            g2d.fillRoundRect(dummyPlayerX - 25, 420, 50, 90, 10, 10);
            g2d.fillRect(dummyPlayerX + 25, 435, 40, 15); // Extended fist
        } else {
            g2d.fillRoundRect(dummyPlayerX - 25, 420, 50, 90, 10, 10); // Idle standing
        }

        // Draw Enemy Dummy
        g2d.setColor(Color.RED);
        g2d.drawString("SIMULATOR DUMMY", dummyEnemyX - 35, 405);
        if (dummyEnemyAction.equals("ATTACK")) {
            g2d.fillRoundRect(dummyEnemyX - 25, 420, 50, 90, 10, 10);
            g2d.setColor(Color.ORANGE);
            g2d.fillRect(dummyEnemyX - 70, 435, 45, 15); // Striking forward
        } else {
            g2d.fillRoundRect(dummyEnemyX - 25, 420, 50, 90, 10, 10); // Idle
        }
    
    }
}   
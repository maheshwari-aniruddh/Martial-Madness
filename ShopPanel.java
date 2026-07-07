//Shop Panel.java - the upgrade shop
// win fights to earn coins, spend them here on permananet upgrade
// saves to shop.txt

import javax.swing.*;
import javax.swing.border.Border;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;


public class ShopPanel extends JPanel
{
    private static final int IRON_BODY = 0;
    private static final int COMBO_MASTER = 1;
    private static final int TOUGH_GUARD = 2;
    private static final int QUICK_FEET  = 3;
    private static final int SHADOW_STEP = 4;
    private static final int NUM_UPGRADES = 5; 

    private static final String[] NAMES = {
        "Iron Body", "Combo Master","Tough. Guard","Quick Feet","Shadow Step"

    };

    private Static final String[] DESCRIPTIONS = {
        "+20 max health per level",
        "combos do +15% damage per level",
        "block stamina regens faster",
        "+$ movement speed per level",
        "dodge lasts 150ms longer per level"
        
    };

    private static final int[] MAX_LEVELS = {3,2,2,2,2};
    private static final int[] BASE_COSTS = {50,80,60,60,40};

    private static int coins = 0;
    private static int[] levels = new int[NUM_UPGRADES];
    private static boolean loaded = false;

    private LevelPanelHolder lph;
    private CardLayout levelCards;
    private Information info;
    private Image backPic; 

    private JLabel coinLabel;
    private JLabel[] levLabels = new JLabel[NUM_UPGRADES];
    private JButton[] buyButtons = new JButton[NUM_UPGRADES];

    public ShopPanel(LevelPanelHolder lphIn, CardLayout levelCardsIn, Information infoIn)
    {
        lph = lphIn;
        levelCards = levelCardsIn;
        info = infoIn;

        load();

        backPic = info.getMyImage("dojo.png");

        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        JLabel title = new JLabel("UPGRADE SHOP");
        title.setFont(new Font("Arial", Font.BOLD, 32));
        title.setForeground(Color.WHITE);
        topPanel.add(title);

        coinLabel = new JLabel();
        coinLabel.setFont(new File("Arial",Font.BOLD,24));
        coinLabel.setForeground(new Color(255,215,0));
        topPanel.add(cointLabel);
        add(topPanel,BorderLayout.NORTH);

        JPanel shopRows = new JPanel();
        shopRows.setOpaque(false);
        shopRows.setLayout(new GridLayout(NUM_UPGRADES,1,0,8));

        class BuyHandler implements ActionListener
        {
            private int upgradeId;

            public BuyHandler(int id)
            {
                upgradeId = id;
            }

            public void actionPerformed(ActionEvent evt)
            {
                if(buy(upgradeId) == true)
                {
                    SoundManager.combo();
                }
                refreshLabels();
            }
        }

        for(int i = 0; i<NUM_UPGRADES;i++)
        {
            JPanel row = new JPanel();
            row.setLayout(new FlowLayout(FlowLayout.LEFT,15,5));
            row.setBackground(new Color(0,0,0,140));

            JLabel nameLabel = new JLabel(NAMES[i]);
            nameLabel.setFont(new Font("Arial",Font.BOLD,20));
            nameLabel.setForeground(Color.WHITE);
            nameLabel.setPreferredSize(new Dimension(170,30));
            row.add(nameLabel);

            JLabel descLabel = new JLabel(DESCRIPTIONS[i]);
            descLabel.setFont(new Font("Arial",Font.PLAIN,15));
            descLabel.setForeground(Color.LIGHT_GRAY);
            descLabel.setPreferredSize(new Dimension(280,30));
            row.add(descLabel);

            levLabels[i] = new JLabel();
            levLabels[i].setFont(new Font("Arial",Font.BOLD,16));
            levLabels[i].setForeground(new Color(120,220,120));
            levLabels[i].setPreferredSize(new Dimension(90,30));
            row.add(levLabels[i]);

            buyButtons[i] = new JButton();
            buyButtons[i].setPreferredSize(new Dimension(110,35));
            buyButtons[i].addActionListener(new BuyHandler(i));
            row.add(buyButtons[i]);

            shopRows.add(row);


        }
        JPanel centerWrap = new JPanel();
        centerWrap.setOpaque(false);
        centerWrap.setLayout(new FlowLayout(FlowLayout.CENTER,0,25));
        centerWrap.add(shopRows);
        add(centerWrap,BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);

        JButton backButton = new JButton("Back to Levels");
        backButton.setPreferredSize(new Dimension(160,48));
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt)
            {
                levelCards.show(lph,"Levels");
            }
        });

        bottomPanel.add(backButton);
        add(bottomPanel,BorderLayout.SOUTH);

        refreshLabels();
    
    
    }

    public void refreshLabels()
    {
        coinLabel.setText("        Coins: "+coins);

        for(int i = 0;i<NUM_UPGRADES;i++)
        {
            levLabels[i].setText("Lv "+levels[i]+"/"+MAX_LEVELS[i]);
            if(levels[i]>= MAX_LEVELS[i])
            {
                buyButtons[i].setText("MAXED");
                buyButtons[i].setEnabled(false);
            }
            else
            {
                int cost = costFor(i);
                buyButtons[i].setText("Buy: "+cost);
                buyButtons[i].setEnabled(coins>=cost);
            }

            
        }
        repaint();
    }

    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        g.drawImage(backPic,0,0,800,600,this);
        g.setColor(new Color(0,0,0,120));
        g.fillRect(0,0,800,600);
    }  
    
    private static int costFor(int id)
    {
        return BASE_COSTS[id]*(levels[id]+1);
    }

    private static void load()
    {
        if(loaded==true)
        {
            return;
        }
        loaded = true;

        try
        {
            File f = new File("shop.txt");
            if(f.exists() == false)
            {
                return;
            }
            Scanner sc = new Scanner(f);
            coins = sc.nextInt();
            for(int i = 0; i<NUM_UPGRADES; i++)
            {
                levels[i] = sc.nextInt();
            }
            sc.close();
        }
        catch(Exception e)
        {
            coins = 0;
            for(int i = 0; i<NUM_UPGRADES; i++)
            {
                levels[i] = 0;
            }
            
        }

        for(int i = 0; i<NUM_UPGRADES;i++)
        {
            if(levels[i]>MAX_LEVELS[i])
            {
                levels[i] = MAX_LEVELS[i];
            }
            if(levels[i]<0)
            {
                levels[i] = 0;
            }
        }
        if(coins<0)
        {
            coins = 0;
        }

    }

    private static void save()
    {
        try
        {
            PrintWriter pw = new PrintWriter("shop.txt");
            pw.println(coins);
            for(int i = 0; i< NUM_UPGRADES; i++)
            {
                pw.println(levels[i]);
            }
            pw.close();


        }
        catch(Exception e)
        {
            // count save but not fatal
        }
    }

    public static void addCoins(int amount)
    {
        load();
        coins = coins+amount;
        save();
    }

    public static int getCoins()
    {
        load();
        return coins;
    }

    public static boolean buy(int id)
    {
        load();

        if(id<0 || id>=NUM_UPGRADES)
        {
            return false;
        }
        if(levels[id]>= MAX_LEVELS[id])
        {
            return false;
        }

        int cost = costFor(id);
        if(coins < cost)
        {
            return false;
        }

        coins = coins - cost;
        levels[id]++;
        save();
        return true;
    }

    public static int healthBonus()
    {
        load();
        return levels
    }


}
//Shop Panel.java - the upgrade shop
// win fights to earn coins, spend them here on permananet upgrade
// saves to shop.txt

import javax.swing.*;
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
                refreshLabel();
            }
        }
    }
}
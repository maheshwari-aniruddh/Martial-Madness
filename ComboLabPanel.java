//ComboLabPanel..java
//pick a combo, it shows you the inputs and you pull it off as fast you can

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.PrintWriter;
import java.util.Scanner;

public class ComboLabPanel extends JPanel implements KeyListener
{


    private static final String[] COMBO_NAMES = {
        "Shadow Kick","Dragon Sweep","Hurricane Kick", "Counter Burst","Death Blossom"

    };
   
   
    private static final String[] COMBO_MOVES = {
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

    private int currentCombo = -1;
    private ComboQueue queue;
    private long drillStart = 0;
    private String resultText = null;
    private Color resultColor = Color.WHITE;
    private Timer drilTimer;

    public ComboLabPanel(LevelPanelHolder lphIn, CardLayout levelCardsIn, Information infoIn)
    {
        lph = lphIn;
        levelCards = levelCardsIn;
        info = infoIn;

        load();
        queue = new ComboQueue();
        backPic = info.getMyImage("dojo.png");

        setLayout(new BorderLayout());
        JPanel top = new JPanel();
        top.setOpaque(false);
        JButton title = new JButton("COMBO LAB");
        title.setEnabled(false);
        top.add(title);
        add(top,BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,20,20));

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

        for(int i = 0; i,COMBO_NAMES.length; i++)
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

        add()
    }
}
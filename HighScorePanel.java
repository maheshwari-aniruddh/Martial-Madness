import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.border.Border;
import javax.swing.BorderFactory;
import java.awt.Insets;

import java.awt.BorderLayout;
import java.awt.CardLayout;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Scanner;

public class HighScorePanel extends JPanel
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private LevelPanelHolder lph;
    private Information info;
    private JTextArea highScoresArea;

    public HighScorePanel(MartialMadnessHolder mmhIn, CardLayout cardsIn, LevelPanelHolder lphIn, Information infoIn)
    {
        mmh = mmhIn;
        cards = cardsIn;
        lph = lphIn;
        info = infoIn;

        setLayout(new BorderLayout());
        setBackground(new Color(240,240,245));

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(25,25,112));
        titlePanel.setBorder(BorderFactory.createEmptyBorder(40,40,40,40));

        JLabel titleLabel = new JLabel("High Scores");
        titleLabel.setFont(new Font("Times New Roman", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        add(titlePanel, BorderLayout.NORTH);

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color((240,240,245)));
        add(contentPanel, BorderLayout.CENTER);

        JPanel scoresContainer = new JPanel(new BorderLayout());
        scoresContainer.setBackground(Color.WHITE);
        scoresContainer.setBorder(BorderFactory.createLineBorder(new Color(200,200,200),1));
        
        highScoresArea = new JTextArea(22,50);
        highScoresArea.setFont(new Font("Courier New", Font.PLAIN,14));
        highScoresArea.setLineWrap(false);
        highScoresArea.setEditable(false);
        highScoresArea.setForeground(new Color(50,50,50));
        highScoresArea.setMargin(new Insets(10,10,10,10));

        JScrollPane scrollPane = new JScrollPane(highScoresArea);
        scrollPane.setOpaque(false);
        scrollPane.setPreferredSize(new Dimension(600,450));

        scoresContainer.add(scrollPane,BorderLayout.CENTER);
        contentPanel.add(scoresContainer, BorderLayout.CENTER);

        FixedPanelHolder fph = new FixedPanelHolder(mmh, cards, false, lph);
        add(fph,BorderLayout.SOUTH);

        loadAndDisplayHighScores();
    }

    public void loadAndDisplayHighScores()
    {
        String[] names = new String[100];
        int[] points = new int[100];
        int count = 0;

        Scanner scanner = info.loadFile("highscores.txt");

        while(scanner.hasNextLine()&& count<100)
        {
            String line = scanner.nextLine();
            String[] parsed = parseLine(line);
            names[count] = parsed[0];
            points[count] = Integer.parseInt(parsed[1]);
            count++;

        }
        scanner.close();

        if(count==0)
        {
            String emptyMessage = "\n\n\n" +
                    "                 No scores recorded yet.\n\n";
            highScoresArea.setText(emptyMessage);
        }

        for(int i =0;i<count-1;i++)
        {
            for(int j = 0; j<count-1-i;j++)
            {
                if(points[j]<points[j+1])
                {
                    int tempPoints = points[j];
                    points[j] = points[j+1];
                    points[j+1] = tempPoints;

                    String tempName = names[j];
                    names[j] = names[j+1];
                    names[j+1] = tempName;
                }
            }
        }

        String displayText = "\n\n";

        String headerLine = String.format("   %-6s   %-" + 15 + "s   %8s",
                "RANK", "PLAYER NAME", "SCORE");
        displayText+= headerLine +"\n";

        String seperator = "   ";
        for(int i = 0; i<displayText;i++)
        {
            String rank = String.format("#%d", i + 1);
            String formattedScore = String.format("%,d", points[i]);

            isplayText += String.format("   %-6s   %-" + 15 + "s   %8s\n",
                    rank, names[i], formattedScore);
        }
        displayText += "\n";

        highScoresArea.setText(displayText);
    }

    public String[] parseLine(String line)
    {
        int sepIndex = line.indexOf("@");
        String name = line.substring(0, sepIndex);
        String pointStr = line.substring(sepIndex+1);
        return new String[]{name,pointStr};
    }
}
import javax.swing.JPanel;
import javax.swing.border.Border;
import javax.swing.JButton;
import javax.swing.JLabel;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Dimension;
import java.awt.Color;
import java.awt.Font;
import java.awt.CardLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class CharacterSelectPanel extends JPanel
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private Information info;

    public CharacterSelectPanel(MartialMadnessHolder mmhIn, CardLayout cardsIn, Information infoIn)
    {
        mmh = mmhIn;
        cards  = cardsIn;
        info = infoIn;
        setLayout(new BorderLayout());

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(new Color(24,196,132));
        JLabel titleLabel = new JLabel("Choose your character");
        titleLabel.setFont(new Font("Dialog",Font.BOLD,24));
        titlePanel.add(titleLabel);
        add(titleLabel,BorderLayout.NORTH);

        class CharButtonHandler implements ActionListener
        {
            public void actionPerformed(ActionEvent evt)
            {
                String command = evt.getActionCommand();
                if(command.equals("Balanced Fighter"))
                {
                    info.setCharacterType("default");
                }
                else if(command.equals("Fast Ninja"))
                {
                    info.setCharacterType("ninja");
                }
                else if(command.equals("Heavy Sumo"))
                {
                    info.setCharacterType("sumo");
                }
                else if(command.equals("Back"))
                {
                    cards.show(mmh, "Instructions");
                    return;
                }
                cards.show(mmh, "LevelHolder");
            }
        }

        CharButtonHandler cbh = new CharButtonHandler();

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(new Color(143,145,218));
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER,50,150));

        JButton defaultChar = new JButton("Balanced Fighter");
        defaultChar.setPreferredSize(new Dimension(180,100));
        defaultChar.addActionListener(cbh);

        JButton ninjaButton = new JButton("Fast Ninja");
        ninjaButton.setPreferredSize(new Dimension(180,100));
        ninjaButton.addActionListener(cbh);

        JButton sumoChar = new JButton("Heavy Sumo");
        sumoChar.setPreferredSize(new Dimension(180,100));
        sumoChar.addActionListener(cbh);

        buttonPanel.add(defaultChar);
        buttonPanel.add(ninjaButton);
        buttonPanel.add(sumoChar);

        add(buttonPanel, BorderLayout.CENTER);

        JPanel southPanel = new JPanel();
        southPanel.setBackground(new Color(143,145,218));
        JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(120,40));
        backButton.addActionListener(cbh);
        southPanel.add(backButton);
        add(southPanel, BorderLayout.SOUTH);
    }
}
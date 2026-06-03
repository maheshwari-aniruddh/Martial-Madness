import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.border.Border;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.BorderFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.QuadCurve2D;
import java.util.Scanner;
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.BorderLayout;
import java.awt.Graphics;
import java.awt.Image;

public class ExtraCredit extends JPanel
{
    private MartialMadnessHolder mmh;
    private CardLayout cards;
    private LevelPanelHolder lph;
    private JLabel questionLabel;
    private JRadioButton[] options;
    private ButtonGroup group;
    private JLabel feedbackLabel;
    private int score;
    private String[][] allQuestions;
    private String[][] selectedQuestions;
    private boolean[] usedQuestions;
    private int totalQuestionsInFile;
    private int currentQuestionIndex;
    private int totalQuestions;
    private JButton submitButton, nextButton;
    private boolean quizCompleted;
    private Information info;


    public ExtraCredit(MartialMadnessHolder mmhIn, CardLayout cardsIn, LevelPanelHolder lphIn, Information infoIn)
    {
        info = infoIn;
        mmh = mmhIn;
        cards  = cardsIn;
        lph = lphIn;
        score = 0;
        totalQuestions = 5;
        currentQuestionIndex = 0;
        selectedQuestions = new String[totalQuestions][6];
        quizCompleted = false;

        setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(null);
        mainPanel.setPreferredSize(new Dimension(750,350));

        questionLabel = new JLabel();
        questionLabel.setBounds(25,20,700,60);
        questionLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK,1));
        questionLabel.setFont(new Font("Arial",Font.ITALIC,14));
        mainPanel.add(questionLabel);

        options = new JRadioButton[4];
        group = new ButtonGroup();

        for (int i = 0; i<options.length;i++)
        {
            options[i] = new JRadioButton();
            options[i].setBounds(35,100+(i*40),680,30);
            options[i].setFont(new Font("Arial", Font.ITALIC, 14));
            group.add(options[i]);
            mainPanel.add(options[i]);
        }

        feedbackLabel = new JLabel("Choose the correct answer and press Submit");
        feedbackLabel.setBounds(25,280,500,30);
        feedbackLabel.setFont(new Font("Arial", Font.ITALIC,14));
        mainPanel.add(feedbackLabel);
        ButtonHandler bh = new ButtonHandler();
        submitButton = new JButton("Submit");
        submitButton.setBounds(575,280,75,40);
        submitButton.setActionCommand("Submit");
        submitButton.addActionListener(bh);
        mainPanel.add(submitButton);

        nextButton = new JButton("Next");
        nextButton.setBounds(655,280,75,40);
        nextButton.setActionCommand("Next");
        nextButton.setEnabled(false);
        nextButton.addActionListener(bh);
        mainPanel.add(nextButton);

        FixedPanelHolder fph = new FixedPanelHolder(mmh, cards, true, lph);
        
        JPanel mainQuizPanel = new JPanel(new BorderLayout());
        mainQuizPanel.add(mainPanel, BorderLayout.CENTER);
        mainQuizPanel.add(fph,BorderLayout.SOUTH);

        FighterPanel fp = new FighterPanel(mmh);
        fp.setPreferredSize(new Dimension(750,150));
    
        add(mainQuizPanel,BorderLayout.CENTER);
        add(fp,BorderLayout.SOUTH);

        Scanner questions = info.loadFile("quiz.txt");
        parseAllQuestions(questions);
        selectRandomQuestions();
        loadNextQuestion();

    }

    class FighterPanel extends JPanel
    {
        private MartialMadnessHolder mmh;
        public FighterPanel(MartialMadnessHolder mmhIn)
        {
            mmh = mmhIn;
        }
        public void paintComponent(Graphics g)
        {
            super.paintComponent(g);
            Image fight1 = info.getMyImage("fighter1.png");
            Image fight2 = info.getMyImage("fighter2.png");
            Image fight3 = info.getMyImage("fighter3.png");

            g.drawImage(fight1, 0,0,160,140,this);
            g.drawImage(fight2, 350, 0 , 160, 140,this);
            g.drawImage(fight3, 650,0,160,130,this);
        }
    }

    public class ButtonHandler implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            String command = evt.getActionCommand();

            if (quizCompleted)
            {
                feedbackLabel.setText("Quiz Already Completed!");
            }
            if(command.equals("Submit"))
            {
                checkAnswer();
                submitButton.setEnabled(false);
                nextButton.setEnabled(true);
            }
            else if (command.equals("Next"))
            {
                loadNextQuestion();
                submitButton.setEnabled(true);
                nextButton.setEnabled(false);
            }
        }
    }

    public void parseAllQuestions(Scanner scanner)
    {
        totalQuestionsInFile = 0;
        while(scanner.hasNext())
        {
            scanner.nextLine();

            for(int i =0; i<4;i++)
            {
                if (scanner.hasNextLine())
                {
                    scanner.nextLine();
                }
            }

            totalQuestionsInFile++;
        }

        allQuestions = new String[totalQuestionsInFile][6];
        usedQuestions = new boolean[totalQuestionsInFile];

        Scanner questions2 = info.loadFile("quiz.txt");
        int index = 0;

        while(questions2.hasNextLine() && index<totalQuestionsInFile)
        {
            allQuestions[index][0] = questions2.nextLine();
            for(int i = 0; i<4;i++)
            {
                if(questions2.hasNextLine())
                {
                    String option = questions2.nextLine();
                    if (option.startsWith("*"))
                    {
                        allQuestions[index][5] = String.valueOf(i);
                        option = option.substring(1);
                    }

                    allQuestions[index][i+1] = option;
                }
            }
            index++;
        }
    }

    public void selectRandomQuestions()
    {
        for(int i =0;i<totalQuestions;i++)
        {
            int randomIndex;

            do
            {
                randomIndex = (int)(Math.random() * totalQuestionsInFile);
            }
            while(usedQuestions[randomIndex]);

            usedQuestions[randomIndex] = true;

            for(int j = 0; j<6;j++)
            {
                selectedQuestions[i][j] = allQuestions[randomIndex][j];
            }
        }
    }

    public void checkAnswer()
    {
        boolean answerSelected = false; 

        int correctAnswerIndex = Integer.parseInt(selectedQuestions[currentQuestionIndex-1][5]);

        for(int i =0;i<options.length;i++)
        {
            if(options[i].isSelected())
            {
                answerSelected = true;
                if(i==correctAnswerIndex)
                {
                    score+=2;
                    feedbackLabel.setText("Correct! Your score: "+score);
                }
                else
                {
                    feedbackLabel.setText("Wrong. The correct answer was: " + 
                        selectedQuestions[currentQuestionIndex-1][correctAnswerIndex+1]
                    );
                }
            }
        }
        if(!answerSelected)
        {
            feedbackLabel.setText("Please select an answer to finish");
            submitButton.setEnabled(true);
            nextButton.setEnabled(false);
        }
    }

    public void loadNextQuestion()
    {
        if(currentQuestionIndex<totalQuestions)
        {
            String[] currentQuestion = selectedQuestions[currentQuestionIndex];
            questionLabel.setText(currentQuestion[0]);

            for(int i = 0; i< options.length;i++)
            {
                options[i].setText(currentQuestion[i+1]);
                options[i].setEnabled(true);
            }

            group.clearSelection();
            feedbackLabel.setText("Choose the correct answer and press Submit");

            currentQuestionIndex++;
        }
        else
        {
            quizCompleted = true;
            questionLabel.setText("Quiz Completed! Your Score:" + score + "out of "+ (totalQuestions*2));
            feedbackLabel.setText("");
            submitButton.setEnabled(false);
            nextButton.setEnabled(false);
            info.setPoints(score*3);
        }
    }
}
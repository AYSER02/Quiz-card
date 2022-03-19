package quizcard;

/**
 *
 * @author ayser
 */
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.swing.*;

public class QuizCardPlayer {
    private static final Dimension FRAME_SIZE = new Dimension(300, 300);
    private static final Dimension MINIMUM_FRAME_SIZE = new Dimension(200, 200);

    private int deckIndex;
    private boolean isAnswerShown;
    private Deck deck;
    private JButton correctButton, showAnswerButton, wrongButton;
    private JFrame frame;
    private JLabel label;
    private JPanel contentPane;
    private JTextArea textArea;

    private QuizCardBuilder quizCardBuilder;


    public QuizCardPlayer(Deck deck){
        this.deck = deck;
    }

    void build(){
        SwingUtilities.invokeLater(
                () -> {
                    buildFrame();
                    buildContentPane();
                    buildLabel();
                    buildTextArea();
                    buildButtonPanel();
                    displayFrame();
                    showAnswerButton.requestFocusInWindow();
                }
        );
    }

    private void buildButtonPanel(){
            showAnswerButton = new JButton("Show answer");
            showAnswerButton.addActionListener(new ButtonListener());
            correctButton = new JButton("Right");
            correctButton.addActionListener(new CorrectButtonListener());
            correctButton.setVisible(false);
            wrongButton = new JButton("Wrong");
            wrongButton.addActionListener(new WrongButtonListener());
            wrongButton.setVisible(false);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(showAnswerButton);
            buttonPanel.add(correctButton);
            buttonPanel.add(wrongButton);
            buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            contentPane.add(BorderLayout.SOUTH, buttonPanel);
    }

    private void buildContentPane(){
        contentPane = new JPanel();
        contentPane.setLayout(new BorderLayout());
        contentPane.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 15));
        frame.setContentPane(contentPane);
    }

    private void buildFrame(){
        frame = new JFrame("Quiz card Player - " + deck.getFileName());
        frame.setMinimumSize(MINIMUM_FRAME_SIZE);
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        closeFrame();
                    }
                }
        );
    }

    private void buildLabel(){
        label = new JLabel("Question:");
        label.setFont(FontConstants.labelFont);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPane.add(BorderLayout.NORTH, label);
    }

    private void buildTextArea(){
        textArea = new JTextArea();
        textArea.setEditable(false);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setText(deck.getQuizCardList().get(0).getQuestion());
        textArea.setFont(FontConstants.textAreaFont);
        JScrollPane jsp = new JScrollPane(textArea);
        jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPane.add(BorderLayout.CENTER, jsp);

    }

    private void closeFrame(){
        SwingUtilities.invokeLater(frame::dispose);
        deck.setIsTestRunning(false);
        deck.setNumCorrect(0);
        deck.setNumWrong(0);
        quizCardBuilder.setTextAreaEditability(true);
        quizCardBuilder.getQuestionText().requestFocusInWindow();
    }

    private void displayFrame(){
        frame.setSize(FRAME_SIZE);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    void registerQuizCardBuilder(QuizCardBuilder newQuizCardBuilder){
        quizCardBuilder = newQuizCardBuilder;
    }

    void toFront(){
        SwingUtilities.invokeLater(frame::toFront);
    }

    private class CorrectButtonListener extends ButtonListener {
        @Override
        public void actionPerformed(ActionEvent ev){
            deck.setNumCorrect(deck.getNumCorrect() + 1);
            super.actionPerformed(ev);
        }
    }

    private class WrongButtonListener extends ButtonListener {

        @Override
        public void actionPerformed(ActionEvent ev){
            deck.setNumWrong(deck.getNumWrong() + 1);
            super.actionPerformed(ev);
        }
    }

    private class ButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent ev){
            if(deckIndex < deck.getQuizCardList().size()) {
                if (isAnswerShown) {
                    showNextCard();
                } else {
                    showAnswer();
                }
            }else if(deckIndex == deck.getQuizCardList().size()) {
                showResults();
            }else{
                closeFrame();
            }
        }

        private void showAnswer(){
            SwingUtilities.invokeLater(
                    () -> {
                        label.setText("Answer:");
                        textArea.setText(deck.getQuizCardList().get(deckIndex).getAnswer());
                        isAnswerShown = true;
                        showAnswerButton.setVisible(false);
                        correctButton.setVisible(true);
                        correctButton.requestFocusInWindow();
                        deckIndex++;
                    }
            );
        }

        private void showNextCard(){
            SwingUtilities.invokeLater(
                    () -> {
                        label.setText("Question:");
                        textArea.setText(deck.getQuizCardList().get(deckIndex).getQuestion());
                        isAnswerShown = false;
                        showAnswerButton.setText("Show answer");
                        showAnswerButton.setVisible(true);
                        showAnswerButton.requestFocusInWindow();
                        correctButton.setVisible(false);
                    }
            );
        }

        private void showResults(){
            SwingUtilities.invokeLater(
                    () -> {
                        label.setText("Results:");
                        textArea.setText("There are no more questions");
                        showAnswerButton.setText("End");
                        showAnswerButton.setVisible(true);
                        showAnswerButton.requestFocusInWindow();
                        correctButton.setVisible(false);
                        wrongButton.setVisible(false);
                        deckIndex++;
                    }
            );
        }
    }
}

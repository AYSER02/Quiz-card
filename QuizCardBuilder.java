
package quizcard;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

/** QuizCardBuilder - This class allows the user to create, edit and save a Deck of QuizCards. */
public class QuizCardBuilder {
    private Deck deck;
    private JButton button;
    private JFileChooser fileChooser = new JFileChooser();
    private JFrame frame;
    private JTextArea answerText = new JTextArea();
    private JTextArea questionText = new JTextArea();
    private JPanel panel;

    private QuizCardPlayer quizCardPlayer;


    public QuizCardBuilder(Deck deck) {
        this.deck = deck;
        createQuizCardPlayer();
    }

    /** addCard - adds a QuizCard to the current Deck. */
    private void addCard(){
        deck.addQuizCard(getQuestionText().getText(), getAnswerText().getText());
        setQuestionText(null);
        setAnswerText(null);
    }

    void build() {
        SwingUtilities.invokeLater(
                () -> {
                        buildFrame();
                        buildContentPane();
                        buildMenuBar();
                        buildLabel(new JLabel("Question:"));
                        buildTextArea(questionText);
                        buildLabel(new JLabel("Answer:"));
                        buildTextArea(answerText);
                        buildButtonPanel();
                        displayFrame();
                        questionText.requestFocusInWindow();
                }
        );

    }

    private void buildButtonPanel() {
        button = new JButton("Add");
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        button.addActionListener(ev -> addCard());
        panel.add(button);
    }

    private void buildContentPane() {
        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 15));
        frame.setContentPane(panel);
    }

    private void buildFrame() {
        frame = new JFrame("Quiz card builder - " + deck.getFileName());
        frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        frame.setMinimumSize(new Dimension(400, 400));
        frame.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        close();
                    }
                }
        );
    }

    private void buildLabel(JLabel label) {
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setFont(FontConstants.labelFont);
        panel.add(label);
    }

    private void buildMenuBar() {
        JMenuBar jMenuBar = new JMenuBar();
        JMenu file = new JMenu("File");
        file.add(Open);
        file.add(Save);
        file.add(SaveAs);
        file.add(Exit);

        JMenu card = new JMenu("Deck");
        card.add(ShuffleDeck);
        card.add(Play);

        jMenuBar.add(file);
        jMenuBar.add(card);
        frame.setJMenuBar(jMenuBar);
    }

    private void buildTextArea(JTextArea jTextArea) {
        jTextArea.setWrapStyleWord(true);
        jTextArea.setLineWrap(true);
        jTextArea.setFont(FontConstants.textAreaFont);
        jTextArea.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                deck.setIsModified(true);
            }
        });
        JScrollPane jsp = new JScrollPane(jTextArea);
        jsp.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        jsp.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(jsp);
    }

    private void close(){
        if (deck.getIsModified()) {
            // Automatically closes the program if there's nothing to be saved.
            if(deck.getQuizCardList().size() == 0 && getQuestionText().getText().length() == 0
                    && getAnswerText().getText().length() == 0) {
                System.exit(0);
            }else {
                int optionChosen = JOptionPane.showConfirmDialog(frame, "Do you want to save this deck?", "Save",
                        JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
                if (optionChosen == JOptionPane.YES_OPTION) {
                    save();
                }
                if (optionChosen != JOptionPane.CANCEL_OPTION) {
                    System.exit(0);
                }
            }
        }else{
            System.exit(0);
        }
    }

    /** createQuizCardPlayer - safely creates an instance of QuizCardPlayer, whilst allowing QuizCardPlayer to
     * have a callback */
    private void createQuizCardPlayer(){
        quizCardPlayer = new QuizCardPlayer(deck);
        quizCardPlayer.registerQuizCardBuilder(this);   // registers the callback
    }

    private void displayFrame() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** openFile - opens a saved Deck */
    private void openFile(){
        int optionChosen = JOptionPane.YES_OPTION;
        if(deck.getIsModified()){
            optionChosen = JOptionPane.showConfirmDialog(frame, "Do you want to save this deck before " +
                            "opening another?", "Save", JOptionPane.YES_NO_CANCEL_OPTION,JOptionPane.QUESTION_MESSAGE);
            if(optionChosen == JOptionPane.YES_OPTION){
                save();
            }
        }

        if(optionChosen != JOptionPane.CANCEL_OPTION && fileChooser.showOpenDialog(frame) == JFileChooser.APPROVE_OPTION){
            deck = new Deck();
            deck.readFile(fileChooser.getSelectedFile().getAbsolutePath());
            setTitle(deck.getFileName());
            setQuestionText(null);
            setAnswerText(null);
        }
    }

    /** save - Saves the current Deck under the same name, if previously saved. If the Deck is new,
     * then saveAs is invoked */
    private void save(){
        if(deck.getFileName().equals("Untitled")){
            saveAs();
        }else{
            if(getQuestionText().getText().length() > 0){
                addCard();
            }
            deck.save(deck.getFileLocation());
            deck.setIsModified(false);
        }
    }

    /** saveAs - User gets to choose the filename that stores the current Deck */
    private void saveAs(){
        if(fileChooser.showSaveDialog(frame) == JFileChooser.APPROVE_OPTION) {
            if(getQuestionText().getText().length() > 0){
                addCard();
            }
            deck.save(fileChooser.getSelectedFile().getAbsolutePath());
            deck.setFileName(fileChooser.getSelectedFile().getName());
            setTitle(deck.getFileName());
            deck.setIsModified(false);
        }
    }


    // GETTERS
    private JTextArea getAnswerText() {
        return answerText;
    }

    JTextArea getQuestionText() {
        return questionText;
    }


    // SETTERS
    private void setAnswerText(String text) {
        SwingUtilities.invokeLater(() -> answerText.setText(text));
    }

    void setTextAreaEditability(boolean isEditable){
        questionText.setEditable(isEditable);
        answerText.setEditable(isEditable);
        button.setEnabled(isEditable);
    }

    private void setTitle(String newTitle){
        SwingUtilities.invokeLater(() -> frame.setTitle("Quiz Card Builder - " + newTitle));
    }

    private void setQuestionText(String text) {
        SwingUtilities.invokeLater(() -> questionText.setText(text));
    }




    // ACTIONS
    private Action Exit = new AbstractAction("Quit"){
        @Override
        public void actionPerformed(ActionEvent ev){
            close();
        }
    };

    private Action Open = new AbstractAction("Open"){
        @Override
        public void actionPerformed(ActionEvent ev){
            openFile();
        }
    };

    private Action Play = new AbstractAction("Begin test"){
        @Override
        public void actionPerformed(ActionEvent ev){
            // Allows the user to open a file if no file is already open
            if(deck.getQuizCardList().size() == 0) {
                openFile();
            }

            // Prevents window from popping up if there's no QuizCards to use
            if(deck.getQuizCardList().size() > 0) {
                if (deck.getIsTestRunning()) {
                    Toolkit.getDefaultToolkit().beep();
                    quizCardPlayer.toFront();
                } else {
                    deck.setIsTestRunning(true);
                    setTextAreaEditability(false);
                    createQuizCardPlayer();
                    quizCardPlayer.build();
                }
            }
        }
    };

    private Action Save = new AbstractAction("Save"){
        @Override
        public void actionPerformed(ActionEvent ev){
            save();
        }
    };

    private Action SaveAs = new AbstractAction("Save as...") {
        @Override
        public void actionPerformed(ActionEvent e) {
            saveAs();
        }
    };

    private Action ShuffleDeck = new AbstractAction("Shuffle deck"){
        @Override
        public void actionPerformed(ActionEvent ev){
            deck.shuffle();
        }
    };
}
package CloneLTaps.me.SQLPracticeDatabase.gui;

import CloneLTaps.me.SQLPracticeDatabase.FileManager;
import CloneLTaps.me.SQLPracticeDatabase.UpdateSettingsRunnable;
import CloneLTaps.me.SQLPracticeDatabase.database.Database;
import CloneLTaps.me.SQLPracticeDatabase.objects.QueryObject;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.uiDesigner.core.GridLayoutManager;
import com.intellij.uiDesigner.core.Spacer;
import org.yaml.snakeyaml.Yaml;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.Map;
import javax.swing.*;
import java.awt.*;
import java.io.*;

public class ScreenGui extends JFrame {
    private final Database db;
    private final FileManager fileManager;

    private JPanel mainPanel, topPanel, bottomPanel, middlePanel;
    private JScrollPane topScrollPane, middleScrollPane;

    private JSpinner fontSize;
    private JComboBox<?> fontType;

    private JLabel topLabelBox, correctnessText;
    private JTextArea middleTextArea;

    private JButton descButton, answerButton, queryButton, skipButton;

    final UpdateSettingsRunnable runnable;
    final Thread fileSaverThread;

    private volatile String fontName;
    private String usersQuery = "";
    private volatile int sizeOfFont;
    private boolean randomizeProblems;

    private DataBeingShown dataBeingShown = DataBeingShown.NULL;
    private int currentObjectIndex;
    private QueryObject queryObject;

    public ScreenGui(final Database db, final FileManager fileManager) {
        super("SQL Practice Database");
        this.db = db;
        this.fileManager = fileManager;
        pullConfigAndSettings();

        this.setContentPane(this.mainPanel);
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(700, 700));
        this.pack();
        this.setVisible(true);

        onWindowClose();
        correctnessText.setText("");

        // This handles font color
        final Font font = new Font(this.fontName, Font.PLAIN, this.sizeOfFont);
        middleTextArea.setFont(font);
        topLabelBox.setFont(font);

        // This handles text color
        middleTextArea.setSelectedTextColor(Color.BLACK);
        middleTextArea.setForeground(Color.BLACK);

        // This handles border colors
        mainPanel.setBorder(BorderFactory.createMatteBorder(6, 6, 6, 6, Color.GRAY));
        topPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));
        middlePanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));
        descButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));
        answerButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));
        queryButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));
        skipButton.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, Color.DARK_GRAY));

        // This handles background color
        mainPanel.setForeground(Color.GRAY);
        mainPanel.setBackground(Color.GRAY);
        bottomPanel.setBackground(Color.GRAY);
        middleTextArea.setBackground(Color.LIGHT_GRAY);
        middlePanel.setBackground(Color.LIGHT_GRAY);
        middleScrollPane.setBackground(Color.LIGHT_GRAY);
        topScrollPane.setBackground(Color.LIGHT_GRAY);
        topLabelBox.setOpaque(true);
        topLabelBox.setBackground(Color.LIGHT_GRAY);

        handleResize();
        handleFontSize();
        handleFontType();

        handleDescriptionButton();
        handleQueryButton();
        handleAnswerButton();
        handleSkipButton();

        runnable = new UpdateSettingsRunnable();
        fileSaverThread = new Thread(runnable);
        fileSaverThread.setDaemon(true);
        fileSaverThread.start();

        currentObjectIndex = randomizeProblems ? ThreadLocalRandom.current().nextInt(this.db.getFinalQuerySize()) : 0;
        queryObject = this.db.getFinalQueryIndex(currentObjectIndex);
        updateDescription();
    }

    /**
     * This ensures that the data being shown at the top is always correctly resized.
     */
    private void handleResize() {
        mainPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                if (dataBeingShown == DataBeingShown.DESCRIPTION) updateDescription();
                else if (dataBeingShown == DataBeingShown.EXPLANATION) updateAnswer();
            }
        });
    }

    private void onWindowClose() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                runnable.shutDownThread();
            }
        });
    }

    private void handleFontSize() {
        fontSize.setValue(sizeOfFont);

        fontSize.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                final int newFontSize = (int) fontSize.getValue();
                final String nameOfFont = middleTextArea.getFont().getFontName();
                final Font font = new Font(middleTextArea.getFont().getFamily(), Font.PLAIN, newFontSize);
                fontName = nameOfFont;
                sizeOfFont = newFontSize;
                middleTextArea.setFont(font);
                topLabelBox.setFont(font);
                saveFile();
            }
        });
    }

    private void handleFontType() {
        final String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontType.setModel(new DefaultComboBoxModel(fonts));
        fontType.setSelectedItem(fontName);

        fontType.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String nameOfFont = (String) fontType.getSelectedItem();
                final int newFontSize = middleTextArea.getFont().getSize();
                final Font font = new Font(nameOfFont, Font.PLAIN, newFontSize);
                fontName = nameOfFont;
                sizeOfFont = newFontSize;
                middleTextArea.setFont(font);
                topLabelBox.setFont(font);
                saveFile();
            }
        });
    }

    private void handleDescriptionButton() {
        descButton.addActionListener(e -> updateDescription());
    }

    private void updateDescription() {
        final String desc = getSpacedText("<u>Description</u>: " + queryObject.getProblemDescription()).replace("</html>", "");
        final String queriedData = fileManager.showDataQueried() ? "<br></br><br></br>" + getSpacedText("<u>Data Queried</u>: " +
                queryObject.getQueriedAnswer(queryObject.getCorrectQuery())).replace("</html>", "") : "";
        final String usersQuery = !this.usersQuery.equals("") ? "<br></br><br></br>" +
                getSpacedText("<u>Your Query</u>: " + queryObject.getQueriedAnswer(this.usersQuery)).replace("</html>", "") : "";
        topLabelBox.setText(desc + queriedData + usersQuery);
        dataBeingShown = DataBeingShown.DESCRIPTION;
    }

    private void handleQueryButton() {
        queryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                final String enteredQuery = middleTextArea.getText() == null ? "" : middleTextArea.getText();

                usersQuery = enteredQuery;

                if (!enteredQuery.equals("") && queryObject.isQueryValid(enteredQuery)) {
                    correctnessText.setText("Correct!");
                    correctnessText.setForeground(Color.GREEN);

                    if (dataBeingShown == DataBeingShown.DESCRIPTION) updateDescription();
                    else if (dataBeingShown == DataBeingShown.EXPLANATION) updateAnswer();
                } else {
                    correctnessText.setText("Incorrect!");
                    correctnessText.setForeground(Color.RED);
                    if (dataBeingShown == DataBeingShown.DESCRIPTION) updateDescription();
                    else if (dataBeingShown == DataBeingShown.EXPLANATION) updateAnswer();
                }
            }
        });
    }

    private void handleSkipButton() {
        skipButton.addActionListener(e -> {
            correctnessText.setText("");

            if (fileManager.discardAnswers()) {
                db.addUsedQuery(queryObject);
                db.removeFinalQuery(queryObject);
            }

            if (db.getFinalQuerySize() <= 0) { // This will add all of the old queryObjects back
                db.setFinalQueryObjectsList();
                currentObjectIndex = 0;
            } else
                currentObjectIndex = randomizeProblems ? ThreadLocalRandom.current().nextInt(db.getFinalQuerySize()) : ++currentObjectIndex;

            if (currentObjectIndex > db.getFinalQuerySize()) { // This will reset the index back to the start
                currentObjectIndex = 0;
            }
            queryObject = db.getFinalQueryIndex(currentObjectIndex);

            topLabelBox.setText("");
            middleTextArea.setText("");
            usersQuery = "";
            updateDescription();
        });
    }

    private void handleAnswerButton() {
        answerButton.addActionListener(e ->
                updateAnswer()
        );
    }

    private void updateAnswer() {
        final String correctAnswer = getSpacedText("<u>Answer</u>: " + queryObject.getCorrectQuery()).replace("</html>", "");
        final String explanation = !queryObject.getExplanation().equals("") ? "<br></br><br></br>" + getSpacedText("<u>Explanation</u>: " +
                queryObject.getExplanation()).replace("</html>", "") : "";
        final String queriedData = getSpacedText("<u>Data Queried</u>: " + queryObject.getQueriedAnswer(queryObject.getCorrectQuery())).replace("</html>", "");
        final String userQuery = getSpacedText("<u>Your Query</u>: " + queryObject.getQueriedAnswer(usersQuery)).replace("</html>", "");
        final String str = correctAnswer + explanation + "<br></br><br></br>" + queriedData + (!userQuery.equals("") ?
                "<br></br><br></br>" + userQuery : "");
        topLabelBox.setText(str);
        dataBeingShown = DataBeingShown.EXPLANATION;
    }

    /**
     * For some reason labels don't seem to support auto adding text past the box length to a new line. Also the
     * way to actually make a new line is by using html break points. So the following code takes in
     *
     * @param originalText then correctly adds break points to make all of the text fit on the screen no matter its size.
     */
    private String getSpacedText(final String originalText) {
        final Font font = new Font(middleTextArea.getFont().getFamily(), Font.PLAIN, middleTextArea.getFont().getSize());
        final int width = topPanel.getWidth() - 30;

        final String breakStatement = "<br></br>";
        final StringBuilder finalString = new StringBuilder("<html>");

        int beginIndex = 0;
        for (int i = 0; i < originalText.length(); i++) {
            String selectedString = originalText.substring(beginIndex, i);
            final int fontWidth = topLabelBox.getFontMetrics(font).stringWidth(selectedString);
            final int difference = width - fontWidth;

            if (difference == 0) {
                i++;
                selectedString = originalText.substring(beginIndex, i);
                finalString.append(selectedString).append(breakStatement);
                beginIndex = i;
            } else if (difference < 0) {  // This means I need to go back through the string and make the previous word the last word on that line
                for (int j = i; j > 0; j--) {
                    selectedString = originalText.substring(beginIndex, j);
                    final char character = originalText.charAt(j);

                    if (character == ' ') {
                        finalString.append(selectedString).append(breakStatement);
                        beginIndex = j + 1;
                        i = j + 1;
                        break;
                    }
                }
            }

            if (i == originalText.length() - 1) { // This just adds the last bit that did not go over to our final string
                finalString.append(originalText, beginIndex, i + 1);
            }
        }
        finalString.append("</html>");
        return finalString.toString();
    }

    /**
     * This reads the saved font, font size, and randomized boolean that is saved in our config file.
     */
    private void pullConfigAndSettings() {
        try {
            final Yaml settingYaml = new Yaml();
            final Map<String, Object> settingData = settingYaml.load(new FileInputStream(fileManager.getSettingsFile()));

            final String fontName = (String) settingData.getOrDefault("font", "Arial");
            final int sizeOfFont = (int) settingData.getOrDefault("font-size", 20);
            this.fontName = fontName;
            this.sizeOfFont = sizeOfFont;
            this.randomizeProblems = fileManager.pickRandomly();
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
        }
    }

    /**
     * This will add our current instance of this class to a blockingQueue so that another thread
     * can handle all write operations asynchronously.
     */
    public void saveFile() {
        runnable.insertElement(this);
    }

    public File getSettingsFile() {
        return fileManager.getSettingsFile();
    }

    public String getFontName() {
        return fontName;
    }

    public int getSizeOfFont() {
        return sizeOfFont;
    }

    {
// GUI initializer generated by IntelliJ IDEA GUI Designer
// >>> IMPORTANT!! <<<
// DO NOT EDIT OR ADD ANY CODE HERE!
        $$$setupUI$$$();
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayoutManager(6, 9, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.setBackground(new Color(-12828863));
        mainPanel.putClientProperty("html.disable", Boolean.FALSE);
        topPanel = new JPanel();
        topPanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        topPanel.setBackground(new Color(-12828863));
        topPanel.putClientProperty("html.disable", Boolean.FALSE);
        mainPanel.add(topPanel, new GridConstraints(1, 0, 3, 9, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(500, 250), new Dimension(1000, 400), null, 0, false));
        topScrollPane = new JScrollPane();
        topScrollPane.setEnabled(false);
        topScrollPane.setHorizontalScrollBarPolicy(31);
        topScrollPane.setVerticalScrollBarPolicy(22);
        topPanel.add(topScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, new Dimension(1000, 375), null, 0, false));
        topLabelBox = new JLabel();
        topLabelBox.setHorizontalAlignment(2);
        topLabelBox.setHorizontalTextPosition(0);
        topLabelBox.setText("");
        topLabelBox.setVerticalAlignment(1);
        topScrollPane.setViewportView(topLabelBox);
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new GridLayoutManager(1, 4, new Insets(0, 0, 0, 0), -1, -1));
        mainPanel.add(bottomPanel, new GridConstraints(5, 0, 1, 9, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(-1, 40), null, null, 0, false));
        descButton = new JButton();
        descButton.setHorizontalTextPosition(0);
        descButton.setText("Description");
        bottomPanel.add(descButton, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        answerButton = new JButton();
        answerButton.setText("Get Answer");
        bottomPanel.add(answerButton, new GridConstraints(0, 2, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        queryButton = new JButton();
        queryButton.setText("Query");
        bottomPanel.add(queryButton, new GridConstraints(0, 1, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        skipButton = new JButton();
        skipButton.setText("Next");
        bottomPanel.add(skipButton, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        middlePanel = new JPanel();
        middlePanel.setLayout(new GridLayoutManager(1, 1, new Insets(0, 0, 0, 0), -1, -1));
        middlePanel.setEnabled(true);
        mainPanel.add(middlePanel, new GridConstraints(4, 0, 1, 9, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, new Dimension(500, 250), new Dimension(500, 250), null, 0, false));
        middleScrollPane = new JScrollPane();
        middleScrollPane.setHorizontalScrollBarPolicy(31);
        middleScrollPane.setVerticalScrollBarPolicy(22);
        middlePanel.add(middleScrollPane, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_WANT_GROW, null, null, null, 0, false));
        middleTextArea = new JTextArea();
        middleTextArea.setForeground(new Color(-10658467));
        middleTextArea.setLineWrap(true);
        middleTextArea.setWrapStyleWord(true);
        middleTextArea.putClientProperty("html.disable", Boolean.TRUE);
        middleScrollPane.setViewportView(middleTextArea);
        fontType = new JComboBox();
        mainPanel.add(fontType, new GridConstraints(0, 4, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label1 = new JLabel();
        label1.setBackground(new Color(-16777216));
        label1.setEnabled(true);
        label1.setForeground(new Color(-16777216));
        label1.setHorizontalAlignment(2);
        label1.setHorizontalTextPosition(2);
        label1.setText("Font Size:");
        mainPanel.add(label1, new GridConstraints(0, 0, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_BOTH, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        fontSize = new JSpinner();
        fontSize.setForeground(new Color(-16777216));
        mainPanel.add(fontSize, new GridConstraints(0, 1, 1, 2, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        final JLabel label2 = new JLabel();
        label2.setBackground(new Color(-16777216));
        label2.setForeground(new Color(-16777216));
        label2.setHorizontalAlignment(2);
        label2.setHorizontalTextPosition(2);
        label2.setText("   Font:");
        mainPanel.add(label2, new GridConstraints(0, 3, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_VERTICAL, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, null, null, null, 0, false));
        correctnessText = new JLabel();
        correctnessText.setBackground(new Color(-16777216));
        correctnessText.setForeground(new Color(-16777216));
        correctnessText.setText("Label");
        correctnessText.setVerticalAlignment(0);
        mainPanel.add(correctnessText, new GridConstraints(0, 6, 1, 1, GridConstraints.ANCHOR_WEST, GridConstraints.FILL_NONE, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, GridConstraints.SIZEPOLICY_CAN_SHRINK | GridConstraints.SIZEPOLICY_CAN_GROW, new Dimension(100, -1), new Dimension(100, -1), null, 0, false));
        final Spacer spacer1 = new Spacer();
        mainPanel.add(spacer1, new GridConstraints(0, 7, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
        final Spacer spacer2 = new Spacer();
        mainPanel.add(spacer2, new GridConstraints(0, 5, 1, 1, GridConstraints.ANCHOR_CENTER, GridConstraints.FILL_HORIZONTAL, GridConstraints.SIZEPOLICY_WANT_GROW, 1, null, null, null, 0, false));
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }

    public enum DataBeingShown {
        DESCRIPTION, EXPLANATION, NULL
    }
}

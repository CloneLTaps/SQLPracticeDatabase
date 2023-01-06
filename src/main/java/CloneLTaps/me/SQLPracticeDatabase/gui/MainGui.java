package CloneLTaps.me.SQLPracticeDatabase.gui;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class MainGui extends JFrame implements ActionListener {
    private final JTextArea textArea;
    private final JSpinner fontSizeSpinner;

    private final JComboBox<String[]> fontBox;

    public MainGui() {
        this.getRootPane().addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                super.componentResized(e);
                final Component component = e.getComponent();
                //System.out.println("RootSize:" + component.getSize());
            }
        });

        // This creates the general gui pane
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setMinimumSize(new Dimension(500, 500));
        this.setTitle("Test");
        this.setSize(500, 500);
        this.setLocationRelativeTo(null);
        this.setLayout(new GridBagLayout());

        // This creates the text box
        textArea = new JTextArea();
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setFont(new Font("Arial", Font.PLAIN, 20));

        // This sets the text box's dimensions and adds a scroll bar
        final JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(450, 450));
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        // The bellow portion handles the font and text info
        JLabel fontLabel = new JLabel("Font");

        fontSizeSpinner = new JSpinner();
        fontSizeSpinner.setPreferredSize(new Dimension(50, 25));
        fontSizeSpinner.setValue(20);
        fontSizeSpinner.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                textArea.setFont(new Font(textArea.getFont().getFamily(), Font.PLAIN, (int)fontSizeSpinner.getValue()));
            }
        });

        final String[] fonts = GraphicsEnvironment.getLocalGraphicsEnvironment().getAvailableFontFamilyNames();
        fontBox = new JComboBox(fonts);
        fontBox.addActionListener(this);
        fontBox.setSelectedItem("Arial");

        final JButton queryButton = new JButton("Query");
        queryButton.setLayout(new FlowLayout(FlowLayout.TRAILING, 5, 5));

        this.add(fontLabel);
        this.add(fontSizeSpinner);
        this.add(fontBox);
        this.add(scrollPane);
        this.add(queryButton);
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == fontBox) {
            final String selectedFont = (String) fontBox.getSelectedItem();
            textArea.setFont(new Font(selectedFont, Font.PLAIN, textArea.getFont().getSize()));
        }
    }
}

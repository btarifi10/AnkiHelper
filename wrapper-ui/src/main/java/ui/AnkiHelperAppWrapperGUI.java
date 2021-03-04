package ui;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AnkiHelperAppWrapperGUI extends JFrame {
    private JPanel pnlMain;
    private JButton btnStopApp;
    private JPanel pnlLogo;
    private JLabel lblInfo;
    private JTextPane txtPnlAppName;

    public static void main(String[] args) {
        String title = "Anki Helper App";
        JFrame frame = new AnkiHelperAppWrapperGUI(title);
    }

    public AnkiHelperAppWrapperGUI(String title) {
        super(title);

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setContentPane(pnlMain);
        this.pack();

        btnStopApp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

            }
        });
    }

    private void createUIComponents() {
    }
}

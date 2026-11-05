package gui.dashboard;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MachineStatus extends JPanel implements ActionListener {
    public ImageIcon appIcon;
    private JButton backButton;
    private Runnable onBack;

    public MachineStatus() {
        appIcon = new ImageIcon(
                getClass().getResource("/Icons/washer.png")
        );

        backButton = new JButton("Back to Dashboard");
        add(backButton);
        backButton.addActionListener(this);
    }

    public void setOnBack(Runnable action) {
        this.onBack = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == backButton) {
            if (onBack != null) {
                onBack.run();
            }
        }
    }
}

package gui.dashboard;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class DashboardPage extends JPanel implements ActionListener {

    private Runnable onChangePage;
    public ImageIcon appIcon;

    private JButton machineButton;
    private JButton registerButton;
    private JButton recordsButton;

    private JPanel menuPanel;
    private JPanel topPanel;
    private JLabel headerLabel;

    public DashboardPage() {
        appIcon = new ImageIcon(
                getClass().getResource("/Icons/washer.png")
        );

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(30, 50, 30, 50));

        // Header
        headerLabel = new JLabel("WELCOME");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 32));

        topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(headerLabel);

        add(topPanel, BorderLayout.NORTH);

        // Buttons
        machineButton = new JButton("Check Machines");
        registerButton = new JButton("Register");
        recordsButton = new JButton("Records");

        Dimension buttonSize = new Dimension(220, 220);

        machineButton.setPreferredSize(buttonSize);
        registerButton.setPreferredSize(buttonSize);
        recordsButton.setPreferredSize(buttonSize);

        // Menu
        menuPanel = new JPanel(new GridLayout(1, 3, 30, 0));

        menuPanel.add(machineButton);
        menuPanel.add(registerButton);
        menuPanel.add(recordsButton);

        // Center menu without stretching buttons
        JPanel menuWrapper = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 0, 60)
        );

        menuWrapper.add(menuPanel);

        add(menuWrapper, BorderLayout.CENTER);

        // Event listeners
        machineButton.addActionListener(this);
        registerButton.addActionListener(this);
        recordsButton.addActionListener(this);
    }

    public void setOnChangePage(Runnable action) {
        this.onChangePage = action;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == machineButton) {
            if (onChangePage != null) {
                onChangePage.run();
            }
        }
    }
}

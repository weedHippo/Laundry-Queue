package gui.pages;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MachineStatus extends JPanel implements ActionListener {
    public ImageIcon appIcon;
    private JButton backButton;
    private JPanel machineAssTab;
    private JPanel machineStatsTab;
    private Runnable onBack;
    private JTabbedPane monitorMachines;

    public MachineStatus() {
        appIcon = new ImageIcon(
                getClass().getResource("/Icons/washer.png")
        );
        backButton = new JButton("Back to Dashboard");

        setLayout(new BorderLayout(10,10));

        //tab pane
        monitorMachines = new JTabbedPane();

        machineAssTab = new JPanel();
        machineAssTab.add(new JLabel("Machine Assignment"));

        machineStatsTab = new JPanel();
        machineStatsTab.add(new JLabel("Machine Status"));

        monitorMachines.addTab("Status", machineStatsTab);
        monitorMachines.addTab("Assign Machine", machineAssTab);



        add(backButton, BorderLayout.NORTH);
        add(monitorMachines, BorderLayout.CENTER);
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
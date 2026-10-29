package gui;

import gui.dashboard.DashboardPage;
import gui.dashboard.MachineStatus;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout layout;
    private JPanel pages;

    private DashboardPage dashboard;
    private MachineStatus machineStatus;

    public MainFrame() {
        super("Laundry Queueing System");

        layout = new CardLayout();
        pages = new JPanel(layout);

        dashboard = new DashboardPage();
        machineStatus = new MachineStatus();

        pages.add(dashboard, "Dashboard");
        pages.add(machineStatus, "Machines");

        dashboard.setOnChangePage(() -> {
            layout.show(pages, "Machines");
        });

        machineStatus.setOnBack(() -> {
            layout.show(pages, "Dashboard");
        });

        add(pages);

        setIconImage(dashboard.appIcon.getImage());
        setSize(854, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void start() {
        setVisible(true);
    }
}
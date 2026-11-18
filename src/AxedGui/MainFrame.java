
package AxedGui;

import AxedGui.pages.DashboardPage;
import AxedGui.pages.MachineStatus;
import AxedGui.pages.RegistrationPage;
import AxedGui.pages.RecordsPage;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private CardLayout layout;
    private JPanel pages;

    private DashboardPage dashboard;
    private MachineStatus machineStatus;
    private RegistrationPage registration;
    private RecordsPage records;

    public MainFrame() {
        super("Laundry Queueing System");

        layout = new CardLayout();
        pages = new JPanel(layout);

        dashboard = new DashboardPage();
        machineStatus = new MachineStatus();
        registration = new RegistrationPage();
        records = new RecordsPage();

        pages.add(dashboard, "Dashboard");
        pages.add(machineStatus, "Machines");
        pages.add(registration, "Registration");
        pages.add(records, "Records");

        dashboard.setOnChangePage(() ->
                layout.show(pages, "Machines")
        );

        // Connect these after updating DashboardPage.java
        dashboard.setOnRegister(() -> {
            registration.refreshCustomers();
            layout.show(pages, "Registration");
        });

        dashboard.setOnRecords(() -> {
            records.refreshRecords();
            layout.show(pages, "Records");
        });

        machineStatus.setOnBack(() ->
                layout.show(pages, "Dashboard")
        );

        registration.setOnBack(() ->
                layout.show(pages, "Dashboard")
        );

        records.setOnBack(() ->
                layout.show(pages, "Dashboard")
        );

        add(pages);

        if (dashboard.appIcon != null) {
            setIconImage(dashboard.appIcon.getImage());
        }

        setSize(1000, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    public void start() {
        setVisible(true);
    }
}

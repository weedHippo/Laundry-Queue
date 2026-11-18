
package AxedGui.pages;

import Model.Machine;
import Model.MachineState;
import Model.MachineType;
import Service.Managers.MachineManager;

import javax.swing.*;
import java.awt.*;

public class MachineStatus extends JPanel {

    private final MachineManager manager = new MachineManager();
    private final JTextArea machineList = new JTextArea();
    private Runnable onBack;

    public MachineStatus() {
        setLayout(new BorderLayout(10, 10));

        JButton back = new JButton("Back to Dashboard");
        back.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });
        add(back, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();

        JPanel statusPanel = new JPanel(new BorderLayout());
        machineList.setEditable(false);
        statusPanel.add(new JScrollPane(machineList));

        JButton refresh = new JButton("Refresh Machines");
        refresh.addActionListener(e -> refreshMachines());
        statusPanel.add(refresh, BorderLayout.SOUTH);

        JPanel managePanel = new JPanel(new GridLayout(0, 2, 10, 10));

        JComboBox<MachineType> type =
                new JComboBox<>(MachineType.values());
        JTextField capacity = new JTextField();
        JComboBox<MachineState> state =
                new JComboBox<>(MachineState.values());

        JTextField machineId = new JTextField();
        JComboBox<MachineState> newState =
                new JComboBox<>(MachineState.values());

        JButton register = new JButton("Register Machine");
        JButton update = new JButton("Update Status");

        managePanel.add(new JLabel("Machine Type:"));
        managePanel.add(type);
        managePanel.add(new JLabel("Capacity (KG):"));
        managePanel.add(capacity);
        managePanel.add(new JLabel("Initial Status:"));
        managePanel.add(state);
        managePanel.add(new JLabel(""));
        managePanel.add(register);

        managePanel.add(new JLabel("Machine ID:"));
        managePanel.add(machineId);
        managePanel.add(new JLabel("New Status:"));
        managePanel.add(newState);
        managePanel.add(new JLabel(""));
        managePanel.add(update);

        register.addActionListener(e -> {
            try {
                int kg = Integer.parseInt(capacity.getText().trim());
                if (kg <= 0) throw new IllegalArgumentException(
                        "Capacity must be greater than zero.");

                manager.registerMachine(
                        (MachineType) type.getSelectedItem(),
                        kg,
                        (MachineState) state.getSelectedItem()
                );
                JOptionPane.showMessageDialog(this, "Machine registered!");
                capacity.setText("");
                refreshMachines();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        update.addActionListener(e -> {
            try {
                int id = Integer.parseInt(machineId.getText().trim());
                manager.updateMachineState(
                        id,
                        (MachineState) newState.getSelectedItem()
                );
                JOptionPane.showMessageDialog(this, "Status updated!");
                machineId.setText("");
                refreshMachines();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        tabs.addTab("Status", statusPanel);
        tabs.addTab("Manage Machines", new JScrollPane(managePanel));
        add(tabs, BorderLayout.CENTER);

        refreshMachines();
    }

    private void refreshMachines() {
        try {
            StringBuilder text = new StringBuilder();
            for (Machine m : manager.getAllMachine()) {
                text.append("ID: ").append(m.getMachineId())
                    .append(" | Type: ").append(m.getMachineType())
                    .append(" | Capacity: ").append(m.getMachineCapacity())
                    .append(" KG | Status: ").append(m.getMachineState())
                    .append("\n");
            }
            machineList.setText(text.toString());
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this, ex.getMessage(), "Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void setOnBack(Runnable action) {
        onBack = action;
    }
}

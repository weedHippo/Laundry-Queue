
package AxedGui.pages;

import Model.Load;
import Model.ServiceStage;
import Service.Managers.LaundryAssignmentManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class RecordsPage extends JPanel {

    private final LaundryAssignmentManager manager =
            new LaundryAssignmentManager();

    private final DefaultTableModel model = new DefaultTableModel(
            new String[]{"Load ID", "Customer ID", "Weight",
                    "Service", "Status", "Date"}, 0) {
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    };

    private final JTable table = new JTable(model);
    private Runnable onBack;

    public RecordsPage() {
        setLayout(new BorderLayout(10, 10));

        JButton back = new JButton("Back to Dashboard");
        back.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshRecords());

        JPanel header = new JPanel();
        header.add(new JLabel("Laundry Records"));
        header.add(refresh);
        header.add(back);
        add(header, BorderLayout.NORTH);

        table.setRowHeight(28);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel actions = new JPanel(new GridLayout(2, 3, 10, 10));

        JTextField loadId = new JTextField();
        JTextField weight = new JTextField();
        JComboBox<ServiceStage> status =
                new JComboBox<>(ServiceStage.values());

        JButton updateWeight = new JButton("Update Weight");
        JButton updateStatus = new JButton("Update Status");

        actions.add(new JLabel("Load ID:"));
        actions.add(loadId);
        actions.add(new JLabel("New Weight (KG):"));
        actions.add(weight);
        actions.add(new JLabel("New Status:"));
        actions.add(status);

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(actions, BorderLayout.CENTER);

        JPanel buttons = new JPanel();
        buttons.add(updateWeight);
        buttons.add(updateStatus);
        bottom.add(buttons, BorderLayout.SOUTH);
        add(bottom, BorderLayout.SOUTH);

        table.getSelectionModel().addListSelectionListener(e -> {
            int row = table.getSelectedRow();
            if (!e.getValueIsAdjusting() && row >= 0) {
                loadId.setText(table.getValueAt(row, 0).toString());
            }
        });

        updateWeight.addActionListener(e -> {
            try {
                int id = Integer.parseInt(loadId.getText().trim());
                double kg = Double.parseDouble(weight.getText().trim());
                if (!Double.isFinite(kg) || kg <= 0)
                    throw new IllegalArgumentException("Invalid weight.");

                manager.updateLoadWeight(id, kg);
                JOptionPane.showMessageDialog(this, "Weight updated!");
                refreshRecords();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        updateStatus.addActionListener(e -> {
            try {
                int id = Integer.parseInt(loadId.getText().trim());
                manager.updateLoadState(
                        id, (ServiceStage) status.getSelectedItem()
                );
                JOptionPane.showMessageDialog(this, "Status updated!");
                refreshRecords();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        refreshRecords();
    }

    public void refreshRecords() {
        try {
            java.util.List<Load> loads = manager.getAllLoadAss();
            model.setRowCount(0);

            for (Load load : loads) {
                model.addRow(new Object[]{
                        load.getLoadId(),
                        load.getCstIdFk(),
                        load.getLoadWeight(),
                        load.getServiceType(),
                        load.getLoadStat(),
                        load.getTimestamp()
                });
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(
                this, ex.getMessage(), "Records Error",
                JOptionPane.ERROR_MESSAGE
        );
    }

    public void setOnBack(Runnable action) {
        onBack = action;
    }
}

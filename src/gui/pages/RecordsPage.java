
package gui.pages;

import Model.Load;
import Service.Managers.LaundryAssignmentManager;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

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
    private final TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
    private static final DateTimeFormatter RECORD_TIME =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private Runnable onBack;

    public RecordsPage() {
        setLayout(new BorderLayout(10, 10));

        JButton back = new JButton("Back to Dashboard");
        back.addActionListener(e -> {
            if (onBack != null) onBack.run();
        });

        JButton refresh = new JButton("Refresh");
        refresh.addActionListener(e -> refreshRecords());

        JTextField search = new JTextField(20);
        JPanel header = new JPanel(new BorderLayout(8, 8));
        header.add(new JLabel("Laundry Records"), BorderLayout.WEST);
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        searchPanel.add(new JLabel("Search:"));
        searchPanel.add(search);
        header.add(searchPanel, BorderLayout.CENTER);
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        headerButtons.add(refresh);
        headerButtons.add(back);
        header.add(headerButtons, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        table.setRowHeight(28);
        table.setRowSorter(sorter);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JTextField weight = new JTextField(10);
        JButton updateWeight = new JButton("Update Weight");
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottom.add(new JLabel("Select a record, then new weight (KG):"));
        bottom.add(weight);
        bottom.add(updateWeight);
        bottom.add(new JLabel("Status is changed by machine actions."));
        add(bottom, BorderLayout.SOUTH);

        search.getDocument().addDocumentListener(new DocumentListener() {
            private void filter() {
                String text = search.getText().trim();
                sorter.setRowFilter(text.isEmpty()
                        ? null
                        : RowFilter.regexFilter("(?i)" + Pattern.quote(text)));
            }
            @Override public void insertUpdate(DocumentEvent e) { filter(); }
            @Override public void removeUpdate(DocumentEvent e) { filter(); }
            @Override public void changedUpdate(DocumentEvent e) { filter(); }
        });

        updateWeight.addActionListener(e -> {
            try {
                int viewRow = table.getSelectedRow();
                if (viewRow < 0) throw new IllegalArgumentException("Select a record first.");
                int modelRow = table.convertRowIndexToModel(viewRow);
                int id = ((Number) model.getValueAt(modelRow, 0)).intValue();
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
                        load.getTimestamp() == null
                                ? ""
                                : load.getTimestamp().format(RECORD_TIME)
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

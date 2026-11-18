package gui.pages;

import Model.Load;
import Model.Machine;
import Model.MachineAssignment;
import Model.MachineState;
import Model.MachineType;
import Model.ServiceStage;
import Model.ServiceType;
import Service.Managers.LaundryAssignmentManager;
import Service.Managers.MachineAssignmentManager;
import Service.Managers.MachineManager;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/** Machine queue screen: assign, manually start, finish, cancel, or interrupt. */
public class MachineStatus extends JPanel {
    private final MachineManager machineManager = new MachineManager();
    private final LaundryAssignmentManager laundryManager = new LaundryAssignmentManager();
    private final MachineAssignmentManager assignmentManager = new MachineAssignmentManager();

    private final DefaultTableModel loadsModel = readOnlyModel("Load ID", "Customer", "Service", "Status");
    private final DefaultTableModel machinesModel = readOnlyModel("Machine ID", "Type", "Status");
    private final DefaultTableModel allMachinesModel = readOnlyModel("Machine ID", "Type", "Status", "Capacity");
    private final JTable loadsTable = new JTable(loadsModel);
    private final JTable machinesTable = new JTable(machinesModel);
    private final JTable allMachinesTable = new JTable(allMachinesModel);
    private final JComboBox<AssignmentOption> assignmentCombo = new JComboBox<>();
    private final JComboBox<AssignmentOption> reassignCombo = new JComboBox<>();
    private final JComboBox<MachineState> newMachineState = new JComboBox<>(MachineState.values());
    private final JTextField durationField = new JTextField("40", 6);
    private final JLabel selectedLoadLabel = new JLabel("No load selected");
    private final JLabel selectedServiceLabel = new JLabel(" ");
    private final JLabel timerLabel = new JLabel("Not started");
    private final Timer timer = new Timer(1000, e -> refreshTimer());
    private Runnable onBack;

    public MachineStatus() {
        setLayout(new BorderLayout(10, 10));

        JButton back = new JButton("Back to Dashboard");
        back.addActionListener(e -> { if (onBack != null) onBack.run(); });
        add(back, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Assignments", buildAssignmentPanel());
        tabs.addTab("Machines", buildMachinePanel());
        add(tabs, BorderLayout.CENTER);
        timer.start();
        refreshAll();
    }

    private JPanel buildAssignmentPanel() {
        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        loadsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        machinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        loadsTable.setRowHeight(26);
        machinesTable.setRowHeight(26);

        JPanel loadsPanel = titledTable("Loads", loadsTable);
        JPanel machinesPanel = titledTable("Available Machines", machinesTable);
        JSplitPane tables = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, loadsPanel, machinesPanel);
        tables.setResizeWeight(0.5);
        root.add(tables, BorderLayout.CENTER);

        JPanel bottom = new JPanel(new BorderLayout(8, 8));
        JPanel assignmentForm = new JPanel(new FlowLayout(FlowLayout.LEFT));
        assignmentForm.setBorder(BorderFactory.createTitledBorder("Assign machine"));
        assignmentForm.add(new JLabel("Duration (minutes):"));
        assignmentForm.add(durationField);
        JButton assignButton = new JButton("Assign");
        assignmentForm.add(assignButton);
        JButton refreshButton = new JButton("Refresh");
        assignmentForm.add(refreshButton);
        bottom.add(assignmentForm, BorderLayout.NORTH);

        JPanel current = new JPanel(new GridLayout(0, 2, 8, 5));
        current.setBorder(BorderFactory.createTitledBorder("Selected load / session"));
        current.add(new JLabel("Load:")); current.add(selectedLoadLabel);
        current.add(new JLabel("Service:")); current.add(selectedServiceLabel);
        current.add(new JLabel("Assignment:")); current.add(assignmentCombo);
        current.add(new JLabel("Timer:")); current.add(timerLabel);
        current.add(new JLabel("Interrupted assignment:")); current.add(reassignCombo);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton startButton = new JButton("Start");
        JButton completeButton = new JButton("Complete");
        JButton cancelButton = new JButton("Cancel Assignment");
        JButton cancelInterruptedButton = new JButton("Cancel Interrupted");
        JButton interruptButton = new JButton("Simulate Interruption");
        JButton reassignButton = new JButton("Reassign Interrupted Load");
        actions.add(startButton); actions.add(completeButton);
        actions.add(cancelButton); actions.add(cancelInterruptedButton);
        actions.add(interruptButton); actions.add(reassignButton);
        current.add(new JLabel()); current.add(actions);
        bottom.add(current, BorderLayout.CENTER);
        root.add(bottom, BorderLayout.SOUTH);

        loadsTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) refreshSelectedLoad();
        });
        assignmentCombo.addActionListener(e -> refreshTimer());
        refreshButton.addActionListener(e -> refreshAll());
        assignButton.addActionListener(e -> assignSelected());
        startButton.addActionListener(e -> run(() -> {
            assignmentManager.startSession(selectedAssignment());
            refreshAll();
        }, "Session started."));
        completeButton.addActionListener(e -> completeSelected());
        cancelButton.addActionListener(e -> run(() -> {
            assignmentManager.cancelAssignment(selectedAssignment());
            refreshAll();
        }, "Assignment canceled."));
        cancelInterruptedButton.addActionListener(e -> run(() -> {
            AssignmentOption selected = (AssignmentOption) reassignCombo.getSelectedItem();
            if (selected == null) throw new IllegalArgumentException("Select an interrupted assignment first.");
            assignmentManager.cancelAssignment(selected.id);
            refreshAll();
        }, "Interrupted load canceled."));
        interruptButton.addActionListener(e -> run(() -> {
            assignmentManager.interruptSession(selectedAssignment());
            refreshAll();
        }, "Interruption simulated. Machine is out of service."));
        reassignButton.addActionListener(e -> reassignInterrupted());
        return root;
    }

    private JPanel buildMachinePanel() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));
        allMachinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        allMachinesTable.setRowHeight(26);
        panel.add(titledTable("All Machines", allMachinesTable), BorderLayout.CENTER);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controls.add(new JLabel("New state:"));
        controls.add(newMachineState);
        JButton update = new JButton("Update Selected Machine");
        JButton refresh = new JButton("Refresh");
        controls.add(update);
        controls.add(refresh);
        panel.add(controls, BorderLayout.SOUTH);

        update.addActionListener(e -> run(() -> {
            int row = allMachinesTable.getSelectedRow();
            if (row < 0) throw new IllegalArgumentException("Select a machine first.");
            int id = ((Number) allMachinesModel.getValueAt(row, 0)).intValue();
            machineManager.updateMachineState(id, (MachineState) newMachineState.getSelectedItem());
            refreshAll();
        }, "Machine state updated."));
        refresh.addActionListener(e -> refreshAll());
        return panel;
    }

    private static JPanel titledTable(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(title));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private static DefaultTableModel readOnlyModel(String... columns) {
        return new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
    }

    private void refreshAll() {
        refreshLoads();
        refreshMachines();
        refreshAssignments();
        refreshSelectedLoad();
        refreshTimer();
    }

    private void refreshLoads() {
        try {
            loadsModel.setRowCount(0);
            for (Load load : laundryManager.getAllLoadAss()) {
                ServiceStage stage = load.getLoadStat();
                if (stage == ServiceStage.QUEUED_WASHER || stage == ServiceStage.QUEUED_DRYER
                        || stage == ServiceStage.TRANSFER || stage == ServiceStage.INTERRUPTION) {
                    loadsModel.addRow(new Object[]{load.getLoadId(), load.getCstIdFk(),
                            load.getServiceType(), stage});
                }
            }
        } catch (Exception ex) { showError(ex); }
    }

    private void refreshMachines() {
        try {
            machinesModel.setRowCount(0);
            allMachinesModel.setRowCount(0);
            for (Machine machine : machineManager.getAllMachine()) {
                allMachinesModel.addRow(new Object[]{machine.getMachineId(), machine.getMachineType(),
                        machine.getMachineState(), machine.getMachineCapacity()});
                if (machine.getMachineState() == MachineState.AVAILABLE) {
                    machinesModel.addRow(new Object[]{machine.getMachineId(), machine.getMachineType(), machine.getMachineState()});
                }
            }
        } catch (Exception ex) { showError(ex); }
    }

    private void refreshAssignments() {
        try {
            AssignmentOption previous = (AssignmentOption) assignmentCombo.getSelectedItem();
            int previousId = previous == null ? -1 : previous.id;
            assignmentCombo.removeAllItems();
            reassignCombo.removeAllItems();
            for (MachineAssignment assignment : assignmentManager.mar.findAll()) {
                if (assignment.getEndedAt() == null) {
                    assignmentCombo.addItem(new AssignmentOption(assignment, false));
                } else {
                    try {
                        if (findLoad(assignment.getLoadId()).getLoadStat() == ServiceStage.INTERRUPTION) {
                            reassignCombo.addItem(new AssignmentOption(assignment, true));
                        }
                    } catch (Exception ignored) {
                        // Ignore stale assignment rows while refreshing the screen.
                    }
                }
            }
            for (int i = 0; i < assignmentCombo.getItemCount(); i++) {
                if (assignmentCombo.getItemAt(i).id == previousId) {
                    assignmentCombo.setSelectedIndex(i);
                    break;
                }
            }
        } catch (Exception ex) { showError(ex); }
    }

    private void refreshSelectedLoad() {
        int row = loadsTable.getSelectedRow();
        if (row < 0) { selectedLoadLabel.setText("No load selected"); selectedServiceLabel.setText(" "); return; }
        selectedLoadLabel.setText(String.valueOf(loadsModel.getValueAt(row, 0)));
        selectedServiceLabel.setText(String.valueOf(loadsModel.getValueAt(row, 2)));
    }

    private void refreshTimer() {
        AssignmentOption selected = (AssignmentOption) assignmentCombo.getSelectedItem();
        if (selected == null) { timerLabel.setText("No assignment selected"); return; }
        int id = selected.id;
        try {
            MachineAssignment assignment = assignmentManager.mar.findById(id);
            if (assignment == null || assignment.getStartedAt() == null) {
                timerLabel.setText("Not started");
            } else {
                long seconds = assignmentManager.getRemainingSeconds(id);
                timerLabel.setText((seconds / 60) + "m " + (seconds % 60) + "s remaining");
            }
        } catch (Exception ex) { timerLabel.setText("Timer unavailable"); }
    }

    private void assignSelected() {
        run(() -> {
            int loadId = selectedLoadId();
            int machineId = selectedMachineId();
            int duration = Integer.parseInt(durationField.getText().trim());
            if (duration <= 0) throw new IllegalArgumentException("Duration must be greater than zero.");
            assignmentManager.assignMachine(loadId, machineId, duration);
            refreshAll();
        }, "Machine assigned. Press Start when ready.");
    }

    private void reassignInterrupted() {
        run(() -> {
            AssignmentOption selected = (AssignmentOption) reassignCombo.getSelectedItem();
            if (selected == null) throw new IllegalArgumentException("Select an interrupted assignment first.");
            int assignmentId = selected.id;
            int machineRow = machinesTable.getSelectedRow();
            if (machineRow < 0) throw new IllegalArgumentException("Select an available replacement machine first.");

            int replacementId = ((Number) machinesModel.getValueAt(machineRow, 0)).intValue();
            MachineAssignment oldAssignment = assignmentManager.mar.findById(assignmentId);
            Machine oldMachine = findMachine(oldAssignment.getMachineId());
            if (oldMachine == null) throw new IllegalArgumentException("Original machine no longer exists.");

            assignmentManager.reassignLoad(oldAssignment.getLoadId(), replacementId,
                    assignmentId, oldMachine.getMachineType());
            refreshAll();
        }, "Interrupted load reassigned. Press Start when ready.");
    }

    private void completeSelected() {
        run(() -> {
            int assignmentId = selectedAssignment();
            MachineAssignment assignment = assignmentManager.mar.findById(assignmentId);
            Machine machine = findMachine(assignment.getMachineId());
            assignmentManager.completeSession(assignmentId);
            refreshAll();

            if (machine != null && machine.getMachineType() == MachineType.WASHER
                    && findLoad(assignment.getLoadId()).getServiceType() == ServiceType.FULL_WASH) {
                promptDryer(assignment.getLoadId());
            }
        }, "Session completed.");
    }

    private void promptDryer(int loadId) {
        List<Integer> dryers = new ArrayList<>();
        for (Machine machine : machineManager.getAllMachine()) {
            if (machine.getMachineType() == MachineType.DRYER && machine.getMachineState() == MachineState.AVAILABLE) dryers.add(machine.getMachineId());
        }
        if (dryers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No available dryers. The load remains in transfer status.");
            return;
        }
        Integer chosen = (Integer) JOptionPane.showInputDialog(this, "Choose an available dryer:",
                "Transfer Load", JOptionPane.QUESTION_MESSAGE, null, dryers.toArray(), dryers.get(0));
        if (chosen != null) run(() -> { assignmentManager.transferLoad(loadId, chosen); refreshAll(); }, "Load transferred to dryer queue.");
    }

    private int selectedLoadId() {
        int row = loadsTable.getSelectedRow();
        if (row < 0) throw new IllegalArgumentException("Select a load first.");
        return ((Number) loadsModel.getValueAt(row, 0)).intValue();
    }

    private int selectedMachineId() {
        int row = machinesTable.getSelectedRow();
        if (row < 0) throw new IllegalArgumentException("Select an available machine first.");
        return ((Number) machinesModel.getValueAt(row, 0)).intValue();
    }

    private int selectedAssignment() {
        AssignmentOption selected = (AssignmentOption) assignmentCombo.getSelectedItem();
        if (selected == null) throw new IllegalArgumentException("Select an assignment first.");
        return selected.id;
    }

    private Load findLoad(int id) {
        for (Load load : laundryManager.getAllLoadAss()) if (load.getLoadId() == id) return load;
        throw new IllegalArgumentException("Load no longer exists.");
    }

    private Machine findMachine(int id) {
        for (Machine machine : machineManager.getAllMachine()) if (machine.getMachineId() == id) return machine;
        return null;
    }

    private void run(Runnable action, String success) {
        try { action.run(); JOptionPane.showMessageDialog(this, success); }
        catch (Exception ex) { showError(ex); }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, ex.getMessage(), "Machine Operation Error", JOptionPane.ERROR_MESSAGE);
    }

    private final class AssignmentOption {
        private final int id;
        private final String label;

        private AssignmentOption(MachineAssignment assignment, boolean interrupted) {
            this.id = assignment.getAssignmentId();
            Machine machine = findMachine(assignment.getMachineId());
            String machineText = machine == null
                    ? "Machine " + assignment.getMachineId()
                    : machine.getMachineType() + " " + assignment.getMachineId();
            String state = interrupted ? "Interrupted" :
                    (assignment.getStartedAt() == null ? "Not started" : "Running");
            String remaining = "";
            if (interrupted && assignment.getStartedAt() != null && assignment.getEndedAt() != null) {
                long seconds = Math.max(0,
                        assignment.getDurationMinutes() * 60L
                                - java.time.Duration.between(assignment.getStartedAt(), assignment.getEndedAt()).getSeconds());
                remaining = " — " + ((seconds + 59) / 60) + " min remaining";
            }
            this.label = "Assignment " + id + " — Load " + assignment.getLoadId()
                    + " — " + machineText + " — " + state + remaining;
        }

        @Override public String toString() { return label; }
    }

    public void setOnBack(Runnable action) { onBack = action; }
}

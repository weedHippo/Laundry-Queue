package App;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class GuiCheatSheet {

    public static void main(String[] args) throws Exception {

        // Print installed Look and Feels
        for (UIManager.LookAndFeelInfo laf :
                UIManager.getInstalledLookAndFeels()) {
            System.out.println(laf.getClassName());
        }

        // Change this to test different themes
        UIManager.setLookAndFeel(
                "javax.swing.plaf.nimbus.NimbusLookAndFeel"
        );

        SwingUtilities.invokeLater(GuiCheatSheet::createUI);
    }

    private static void createUI() {
        JFrame frame = new JFrame("Swing UI Demo");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(750, 550);
        frame.setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();

        // TAB 1: Basic Components
        JPanel basic = new JPanel(new GridLayout(0, 2, 10, 10));
        basic.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JTextField textField = new JTextField("Sample Text");
        JPasswordField password = new JPasswordField("password");

        basic.add(new JLabel("Text Field:"));
        basic.add(textField);

        basic.add(new JLabel("Password Field:"));
        basic.add(password);

        basic.add(new JLabel("Combo Box:"));
        basic.add(new JComboBox<>(new String[]{
                "Option 1", "Option 2", "Option 3"
        }));

        basic.add(new JLabel("Spinner:"));
        basic.add(new JSpinner(
                new SpinnerNumberModel(5, 0, 100, 1)
        ));

        basic.add(new JLabel("Slider:"));
        basic.add(new JSlider(0, 100, 50));

        basic.add(new JLabel("Progress Bar:"));
        JProgressBar progress = new JProgressBar(0, 100);
        progress.setValue(65);
        progress.setStringPainted(true);
        basic.add(progress);

        basic.add(new JLabel("Checkbox:"));
        basic.add(new JCheckBox("Enable Feature", true));

        basic.add(new JLabel("Radio Buttons:"));
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JRadioButton radio1 = new JRadioButton("A", true);
        JRadioButton radio2 = new JRadioButton("B");
        ButtonGroup group = new ButtonGroup();
        group.add(radio1);
        group.add(radio2);
        radios.add(radio1);
        radios.add(radio2);
        basic.add(radios);

        JButton button = new JButton("Click Me");
        button.addActionListener(e ->
                JOptionPane.showMessageDialog(frame,
                        "Hello from Swing!")
        );

        basic.add(new JLabel("Button:"));
        basic.add(button);

        tabs.addTab("Components", new JScrollPane(basic));

        // TAB 2: Table
        String[] columns = {"ID", "Name", "Status"};
        Object[][] data = {
                {1, "Alice", "Active"},
                {2, "Bob", "Pending"},
                {3, "Charlie", "Inactive"},
                {4, "Diana", "Active"}
        };

        DefaultTableModel model =
                new DefaultTableModel(data, columns);

        JTable table = new JTable(model);
        table.setFillsViewportHeight(true);

        tabs.addTab("Table", new JScrollPane(table));

        // TAB 3: Text Area
        JTextArea textArea = new JTextArea();
        textArea.setText(
                "Welcome to the Swing UI Demo!\n\n" +
                        "This area supports multiple lines.\n" +
                        "Try typing something here."
        );

        tabs.addTab("Text Area", new JScrollPane(textArea));

        // TAB 4: Dialogs
        JPanel dialogs = new JPanel(new FlowLayout());

        JButton message = new JButton("Message");
        message.addActionListener(e ->
                JOptionPane.showMessageDialog(frame,
                        "This is a message dialog.")
        );

        JButton confirm = new JButton("Confirm");
        confirm.addActionListener(e ->
                JOptionPane.showConfirmDialog(frame,
                        "Do you want to continue?")
        );

        JButton input = new JButton("Input");
        input.addActionListener(e ->
                JOptionPane.showInputDialog(frame,
                        "Enter your name:")
        );

        JButton fileChooser = new JButton("File Chooser");
        fileChooser.addActionListener(e ->
                new JFileChooser().showOpenDialog(frame)
        );

        dialogs.add(message);
        dialogs.add(confirm);
        dialogs.add(input);
        dialogs.add(fileChooser);

        tabs.addTab("Dialogs", dialogs);

        // Menu Bar
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu("File");
        JMenuItem exit = new JMenuItem("Exit");
        exit.addActionListener(e -> frame.dispose());
        fileMenu.add(exit);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem about = new JMenuItem("About");
        about.addActionListener(e ->
                JOptionPane.showMessageDialog(frame,
                        "Java Swing UI Demo")
        );
        helpMenu.add(about);

        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);
        frame.add(tabs);
        frame.setVisible(true);
    }
}

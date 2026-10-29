package gui;

import javax.swing.*;

import Service.LaundryManager;

public class GuiTestMethodCalling extends JFrame {

    // Fields
    private JTextField nameField;
    private JButton registerButton;
    private JLabel resultLabel;
    private LaundryManager service;

    // Constructor
    public GuiTestMethodCalling() {

        super("Laundry System");

        service = new LaundryManager();
        nameField = new JTextField(15);
        registerButton = new JButton("Register");
        resultLabel = new JLabel("Waiting...");

        // Button event


        // Layout
        JPanel panel = new JPanel();
        panel.add(nameField);
        panel.add(registerButton);
        panel.add(resultLabel);

        add(panel);

        setSize(350, 150);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // Method called by ActionListener
    private void registerCustomer() {

        String name = nameField.getText();

        String result = service.printName(name);

        resultLabel.setText(result);
    }

    public static void main(String[] args){
        GuiTestMethodCalling lg = new GuiTestMethodCalling();
        lg.registerCustomer();
    }
}

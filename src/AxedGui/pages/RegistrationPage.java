
package AxedGui.pages;

import Model.Customer;
import Model.ServiceType;
import Service.Managers.CustomerManager;
import Service.Managers.LaundryAssignmentManager;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class RegistrationPage extends JPanel {

    private final CustomerManager customerManager = new CustomerManager();
    private final LaundryAssignmentManager laundryManager =
            new LaundryAssignmentManager();

    private final JTextField nameField = new JTextField(20);
    private final JTextField contactField = new JTextField(20);
    private final JTextField weightField = new JTextField(20);

    private final JComboBox<Customer> customerCombo =
            new JComboBox<>();

    private final JComboBox<ServiceType> serviceCombo =
            new JComboBox<>(ServiceType.values());

    private Runnable onBack;

    public RegistrationPage() {
        setLayout(new BorderLayout(10, 10));

        JLabel title = new JLabel("Customer Registration");
        title.setFont(new Font("SansSerif", Font.BOLD, 24));

        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(e -> {
            if (onBack != null) {
                onBack.run();
            }
        });

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(backButton, BorderLayout.WEST);
        topPanel.add(title, BorderLayout.CENTER);

        add(topPanel, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridLayout(0, 2, 10, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        form.add(new JLabel("Customer Name:"));
        form.add(nameField);

        form.add(new JLabel("Contact Number:"));
        form.add(contactField);

        JButton addCustomerButton = new JButton("Add Customer");
        form.add(new JLabel());
        form.add(addCustomerButton);

        form.add(new JLabel("Select Customer:"));
        form.add(customerCombo);

        form.add(new JLabel("Laundry Weight (kg):"));
        form.add(weightField);

        form.add(new JLabel("Service Type:"));
        form.add(serviceCombo);

        JButton registerButton = new JButton("Register Laundry");
        form.add(new JLabel());
        form.add(registerButton);

        add(form, BorderLayout.CENTER);

        addCustomerButton.addActionListener(e -> addCustomer());
        registerButton.addActionListener(e -> registerLaundry());

        refreshCustomers();
    }

    private void addCustomer() {
        try {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            customerManager.addCustomer(name, contact);

            JOptionPane.showMessageDialog(
                    this,
                    "Customer added successfully!"
            );

            nameField.setText("");
            contactField.setText("");

            refreshCustomers();

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Registration Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void registerLaundry() {
        try {
            Customer customer =
                    (Customer) customerCombo.getSelectedItem();

            if (customer == null) {
                throw new IllegalArgumentException(
                        "Please select a customer."
                );
            }

            double weight = Double.parseDouble(
                    weightField.getText().trim()
            );

            ServiceType service =
                    (ServiceType) serviceCombo.getSelectedItem();

            laundryManager.registerLaundry(
                    customer.getCustomerId(),
                    weight,
                    service
            );

            JOptionPane.showMessageDialog(
                    this,
                    "Laundry registered successfully!"
            );

            weightField.setText("");

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    ex.getMessage(),
                    "Laundry Registration Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void refreshCustomers() {
        customerCombo.removeAllItems();

        try {
            List<Customer> customers =
                    customerManager.getAllCustomers();

            for (Customer customer : customers) {
                customerCombo.addItem(customer);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Unable to load customers: " + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    public void setOnBack(Runnable action) {
        this.onBack = action;
    }
}

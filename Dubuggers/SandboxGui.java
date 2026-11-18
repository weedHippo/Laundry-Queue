
package App.Dubuggers;

import Model.Customer;
import Service.DataManager.Repo.CustomerRepo;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class SandboxGui {

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Customer Update Test");
            frame.setSize(450, 250);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setLayout(new GridLayout(4, 2, 10, 10));

            CustomerRepo repo = new CustomerRepo();

            // Fetch customers
            List<Customer> customers = repo.findAll();

            JComboBox<Customer> customerBox = new JComboBox<>();

            for (Customer customer : customers) {
                customerBox.addItem(customer);
            }

            // Editable fields
            JTextField nameField = new JTextField();
            JTextField contactField = new JTextField();

            JButton updateButton = new JButton("Update Customer");
            JButton selectButton = new JButton("Show Customer");

            // Load selected customer into fields
            customerBox.addActionListener(e -> {

                Customer selected = (Customer) customerBox.getSelectedItem();

                if (selected != null) {
                    nameField.setText(selected.getCustomerName());
                    contactField.setText(selected.getCustomerContact());
                }
            });

            // Update customer
            updateButton.addActionListener(e -> {

                Customer selected =
                        (Customer) customerBox.getSelectedItem();

                if (selected == null) {
                    return;
                }

                String newName = nameField.getText().trim();
                String newContact = contactField.getText().trim();

                if (newName.isEmpty() || newContact.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "Fields cannot be empty!"
                    );
                    return;
                }

                Customer updated = new Customer(
                        selected.getCustomerId(),
                        newName,
                        newContact
                );

                repo.updateEntry(updated);

                JOptionPane.showMessageDialog(
                        frame,
                        "Update request completed."
                );

                // Reload customers from database
                customerBox.removeAllItems();

                for (Customer customer : repo.findAll()) {
                    customerBox.addItem(customer);
                }
            });

            // Show selected customer information
            selectButton.addActionListener(e -> {

                Customer selected = (Customer) customerBox.getSelectedItem();

                if (selected != null) {
                    JOptionPane.showMessageDialog(
                            frame,
                            "ID: " + selected.getCustomerId() +
                                    "\nName: " + selected.getCustomerName() +
                                    "\nContact: " + selected.getCustomerContact()
                    );
                }
            });

            frame.add(new JLabel("Select Customer:"));
            frame.add(customerBox);

            frame.add(new JLabel("Customer Name:"));
            frame.add(nameField);

            frame.add(new JLabel("Contact Number:"));
            frame.add(contactField);

            frame.add(selectButton);
            frame.add(updateButton);

            // Initialize fields for first customer
            if (customerBox.getItemCount() > 0) {
                Customer first = customerBox.getItemAt(0);
                nameField.setText(first.getCustomerName());
                contactField.setText(first.getCustomerContact());
            }

            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}

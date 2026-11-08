package Service.DataManager.Repo;

import Model.Customer;
import Service.DataManager.*;
import java.sql.*;

public class CustomerRepo implements Repository<Customer> {

    @Override
    public void addEntry(Customer item) {

        String sql = "INSERT INTO CUSTOMER (CUSTOMER_NAME, CONTACT_NUMBER) VALUES (?, ?)";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement prep = conn.prepareStatement(sql)
        ) {

            // Set values from the model
            prep.setString(1, item.getCustomerName());
            prep.setString(2, item.getContactNumber());

            prep.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void updateEntry(Customer item) {

        String sql = "UPDATE CUSTOMER SET CUSTOMER_NAME = ?, CONTACT_NUMBER = ? WHERE CUSTOMER_ID = ?";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement prep = conn.prepareStatement(sql)
        ) {

            prep.setString(1, item.getCustomerName());
            prep.setString(2, item.getContactNumber());
            prep.setInt(3, item.getCustomerId());

            prep.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntry(int id) {

        String sql = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement prep = conn.prepareStatement(sql)
        ) {

            prep.setInt(1, id);

            prep.executeUpdate();

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public Customer findById(int id) {

        String sql = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement prep = conn.prepareStatement(sql)
        ) {

            prep.setInt(1, id);

            try (ResultSet rs = prep.executeQuery()) {

                if (rs.next()) {

                    // Convert database row into an object
                    return new Customer(
                            rs.getInt("CUSTOMER_ID"),
                            rs.getString("CUSTOMER_NAME"),
                            rs.getString("CONTACT_NUMBER")
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}

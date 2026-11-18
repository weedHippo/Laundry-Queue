
package Service.DataManager.Repo;

import Model.Customer;
import Service.DataManager.DataConnections;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepo implements RepoButWithFindByID<Customer> {

    @Override
    public void addEntry(Customer item) {
        String sql = "INSERT INTO CUSTOMER (CUSTOMER_NAME, CONTACT_NUMBER) VALUES (?,?)";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement prep = conn.prepareStatement(
                        sql, new String[]{"CUSTOMER_ID"})
        ) {
            prep.setString(1, item.getCustomerName());
            prep.setString(2, item.getCustomerContact());

            int rows = prep.executeUpdate();
            System.out.println("Rows inserted: " + rows);

            try (ResultSet rs = prep.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setCustomerId(rs.getInt(1));
                }
            }

        } catch (SQLException se) {
            throw new RuntimeException(
                    "Failed to insert customer: " + se.getMessage(), se);
        }
    }

    @Override
    public void updateEntry(Customer item) {
        String sql = "UPDATE CUSTOMER SET CUSTOMER_NAME = ?, CONTACT_NUMBER = ? WHERE CUSTOMER_ID = ?";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setString(1, item.getCustomerName());
            ps.setString(2, item.getCustomerContact());
            ps.setInt(3, item.getCustomerId());

            int rows = ps.executeUpdate();
            System.out.println("Rows affected: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to update customer: " + e.getMessage(), e);
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

            int rows = prep.executeUpdate();
            System.out.println("Rows affected: " + rows);

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to delete customer: " + e.getMessage(), e);
        }
    }

    @Override
    public Customer findById(int id) {
        String sql = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Customer(
                            rs.getInt("CUSTOMER_ID"),
                            rs.getString("CUSTOMER_NAME"),
                            rs.getString("CONTACT_NUMBER")
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to find customer: " + e.getMessage(), e);
        }

        return null;
    }

    @Override
    public List<Customer> findAll() {
        List<Customer> customers = new ArrayList<>();

        String sql = "SELECT CUSTOMER_ID, CUSTOMER_NAME, CONTACT_NUMBER FROM CUSTOMER ORDER BY CUSTOMER_NAME";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement prep = conn.prepareStatement(sql);
                ResultSet rs = prep.executeQuery()
        ) {
            while (rs.next()) {
                customers.add(new Customer(
                        rs.getInt("CUSTOMER_ID"),
                        rs.getString("CUSTOMER_NAME"),
                        rs.getString("CONTACT_NUMBER")
                ));
            }

        } catch (SQLException e) {
            throw new RuntimeException(
                    "Failed to load customers: " + e.getMessage(), e);
        }

        return customers;
    }
}

package Service.DataManager.Repo;

import Model.Customer;
import Service.DataManager.DataConnections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerRepo implements Repository<Customer> {


    @Override
    public void addEntry(Customer item) {
        String sql = "INSERT INTO CUSTOMER (CUSTOMER_NAME, CONTACT_NUMBER) VALUES (?,?)";

        try(
                Connection conn = DataConnections.getConnection();
                PreparedStatement prep = conn.prepareStatement(sql, new String[]{"CUSTOMER_ID"})
                ){
            prep.setString(1,item.getCustomerName());
            prep.setString(2, item.getCustomerContact());
            int rows = prep.executeUpdate();
            System.out.println("Rows inserted: " + rows);

            try(ResultSet rs = prep.getGeneratedKeys()){
                if(rs.next()){
                    item.setCustomerId(rs.getInt(1));
                }
            }

        }catch(SQLException se){
            System.out.println(se.getMessage());
        }
    }

    @Override
    public void updateEntry(Customer item) {

    }

    @Override
    public void deleteEntry(int id) {

    }

    @Override
    public Customer findById(int id) {
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
            System.out.println(e.getMessage());
        }

        return customers;
    }
}

package App;

import Service.DataManager.DataConnections;
import java.sql.*;

public class BackendTester {

    // Reserved ID for testing only


    private static final String TEST_NAME = "TEST CUSTOMER";
    private static final String TEST_CONTACT = "09999999999";
    private static final int TEST_ID = 8;


    public static void main(String[] args) {

        System.out.println("===== BACKEND TEST STARTED =====");

        try (Connection conn = DataConnections.getConnection()) {

            // All test changes stay in one transaction
            conn.setAutoCommit(false);

            try {
               // testInsert(conn);
                testFind(conn);
                testUpdate(conn);
                testDelete(conn);

                System.out.println("\nALL TESTS PASSED!");

            } finally {
                // Undo every database change made during testing
                conn.rollback();
                System.out.println("\nTest transaction rolled back.");
            }

        } catch (SQLException | AssertionError e) {
            System.out.println("\nTEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }

        System.out.println("===== BACKEND TEST FINISHED =====");
    }

    // TEST 1: INSERT
    private static void testInsert(Connection conn)
            throws SQLException {

        System.out.println("\n[TEST 1] INSERT CUSTOMER");

        String sql = """
            INSERT INTO CUSTOMER
            (CUSTOMER_NAME, CONTACT_NUMBER)
            VALUES (?, ?)
            """;

        try (PreparedStatement prep = conn.prepareStatement(
                sql, new String[]{"CUSTOMER_ID"})) {

            prep.setString(1, TEST_NAME);
            prep.setString(2, TEST_CONTACT);

            int rows = prep.executeUpdate();

            check(rows == 1, "Insert failed");

            int testId;
            try (ResultSet keys = prep.getGeneratedKeys()) {

                check(keys.next(), "No generated ID returned");

                testId = keys.getInt(1);
            }

            System.out.println("PASS: Customer inserted.");
            System.out.println("Generated Customer ID: " + testId);
        }
    }

    // TEST 2: SELECT
    private static void testFind(Connection conn)
            throws SQLException {

        System.out.println("\n[TEST 2] FIND CUSTOMER");

        String sql = "SELECT * FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setInt(1, TEST_ID);

            try (ResultSet rs = prep.executeQuery()) {

                check(rs.next(), "Customer not found");

                String name = rs.getString("CUSTOMER_NAME");
                String contact = rs.getString("CONTACT_NUMBER");

                check(TEST_NAME.equals(name), "Incorrect customer name");
                check(TEST_CONTACT.equals(contact), "Incorrect contact");

                System.out.println("PASS: Customer found.");
                System.out.println("Name: " + name);
                System.out.println("Contact: " + contact);
            }
        }
    }

    // TEST 3: UPDATE
    private static void testUpdate(Connection conn)
            throws SQLException {

        System.out.println("\n[TEST 3] UPDATE CUSTOMER");

        String sql = """
                UPDATE CUSTOMER
                SET CUSTOMER_NAME = ?
                WHERE CUSTOMER_ID = ?
                """;

        try (PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setString(1, "UPDATED TEST CUSTOMER");
            prep.setInt(2, TEST_ID);

            int rows = prep.executeUpdate();

            check(rows == 1, "Update failed");

            System.out.println("PASS: Customer updated.");
        }

        String verifySql =
                "SELECT CUSTOMER_NAME FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (PreparedStatement prep = conn.prepareStatement(verifySql)) {

            prep.setInt(1, TEST_ID);

            try (ResultSet rs = prep.executeQuery()) {

                check(rs.next(), "Updated customer not found");

                check(
                        "UPDATED TEST CUSTOMER".equals(rs.getString(1)),
                        "Updated name was not saved"
                );

                System.out.println("PASS: Update verified.");
            }
        }
    }

    // TEST 4: DELETE
    private static void testDelete(Connection conn)
            throws SQLException {

        System.out.println("\n[TEST 4] DELETE CUSTOMER");

        String sql = "DELETE FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (PreparedStatement prep = conn.prepareStatement(sql)) {

            prep.setInt(1, TEST_ID);

            int rows = prep.executeUpdate();

            check(rows == 1, "Delete failed");

            System.out.println("PASS: Customer deleted.");
        }

        String verifySql =
                "SELECT 1 FROM CUSTOMER WHERE CUSTOMER_ID = ?";

        try (PreparedStatement prep = conn.prepareStatement(verifySql)) {

            prep.setInt(1, TEST_ID);

            try (ResultSet rs = prep.executeQuery()) {

                check(!rs.next(), "Customer still exists");

                System.out.println("PASS: Deletion verified.");
            }
        }
    }

    // SIMPLE ASSERTION HELPER
    private static void check(boolean condition, String message) {

        if (!condition) {
             throw new AssertionError(message);
        }
    }
}

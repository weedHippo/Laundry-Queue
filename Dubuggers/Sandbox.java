package App.Dubuggers;

import Service.DataManager.DataConnections;
import Service.Managers.CustomerManager;
import Service.Managers.LaundryAssignmentManager;

import java.sql.*;

/**
 * Console smoke tester for the manager layer and database tables.
 *
 * The tests are intentionally small and independent: one failure is reported
 * and does not prevent the remaining checks or the table debugger from running.
 */
public class Sandbox {

    public static void main(String[] args) {
        System.out.println("========== MANAGER TESTS ==========");

        testCustomerManager();
        //testLaundryAssignmentManager();

        // TODO: MachineManager currently has no implemented methods to test.
        // testMachineManager();

        // TODO: MachineAssignmentManager currently has no implemented methods to test.
        // testMachineAssignmentManager();

        // TODO: CustomerManager.removeCustomer(int) is package-private, so it
        // cannot be called from App.Dubuggers without changing the manager API.
        // customerManager.removeCustomer(0);

        System.out.println("\n========== TABLE DEBUGGER ==========");
        debugTable("CUSTOMER");
        debugTable("LAUNDRY_LOADS");
        debugTable("MACHINES");
    }

    private static void testCustomerManager() {
        CustomerManager customerManager = new CustomerManager();

        runTest("CustomerManager.addCustomer(valid)", () ->
                customerManager.addCustomer("Sandbox Tester", "0900000000"));

        runTest("CustomerManager.updateCustomer(valid)", () ->
                customerManager.updateCustomer(53, "Updated Sandbox Tester", "0911111111"));

        runExpectedException(
                "CustomerManager.addCustomer(blank name)",
                IllegalArgumentException.class,
                () -> customerManager.addCustomer("  ", "0922222222")
        );

        runExpectedException(
                "CustomerManager.addCustomer(invalid name)",
                IllegalArgumentException.class,
                () -> customerManager.addCustomer("Sandbox123", "0933333333")
        );

        runExpectedException(
                "CustomerManager.addCustomer(blank contact)",
                IllegalArgumentException.class,
                () -> customerManager.addCustomer("Valid Name", "  ")
        );

        runExpectedException(
                "CustomerManager.updateCustomer(missing customer)",
                RuntimeException.class,
                () -> customerManager.updateCustomer(-1, "Missing Customer", "0944444444")
        );
    }
    /*
    private static void testLaundryAssignmentManager() {
        LaundryAssignmentManager laundryManager = new LaundryAssignmentManager();

        runTest("LaundryAssignmentManager.printName", () -> {
            String actual = laundryManager.printName("Sandbox Tester");
            String expected = "the name is: Sandbox Tester";

            if (!expected.equals(actual)) {
                throw new AssertionError(
                        "Expected '" + expected + "' but received '" + actual + "'."
                );
            }
        });
    }

     */

    private static void runTest(String testName, Runnable test) {
        try {
            test.run();
            System.out.println("[PASS] " + testName);
        } catch (Throwable e) {
            System.err.println("[FAIL] " + testName + " -> " + messageFor(e));
        }
    }

    private static void runExpectedException(
            String testName,
            Class<? extends Throwable> expectedType,
            Runnable test
    ) {
        try {
            test.run();
            System.err.println(
                    "[FAIL] " + testName + " -> expected " + expectedType.getSimpleName()
            );
        } catch (Throwable e) {
            if (expectedType.isInstance(e)) {
                System.out.println("[PASS] " + testName + " -> " + e.getMessage());
            } else {
                System.err.println(
                        "[FAIL] " + testName + " -> expected "
                                + expectedType.getSimpleName() + " but received "
                                + e.getClass().getSimpleName() + ": " + messageFor(e)
                );
            }
        }
    }

    private static String messageFor(Throwable error) {
        return error.getMessage() == null ? error.getClass().getSimpleName() : error.getMessage();
    }

    private static void debugTable(String tableName) {

        // Only allow known tables
        if (!tableName.equals("CUSTOMER") &&
                !tableName.equals("LAUNDRY_LOADS") &&
                !tableName.equals("MACHINES")) {
            throw new IllegalArgumentException("Unknown table");
        }

        String sql = "SELECT * FROM " + tableName;

        System.out.println("\n========== " + tableName + " ==========");

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            ResultSetMetaData meta = rs.getMetaData();
            int columns = meta.getColumnCount();
            int count = 0;

            // Print column names
            for (int i = 1; i <= columns; i++) {
                System.out.printf("%-22s", meta.getColumnName(i));
            }
            System.out.println();

            System.out.println("-".repeat(columns * 22));

            // Print records
            while (rs.next()) {
                count++;

                for (int i = 1; i <= columns; i++) {
                    Object value = rs.getObject(i);

                    System.out.printf(
                            "%-22s",
                            value == null ? "NULL" : value.toString()
                    );
                }

                System.out.println();
            }

            System.out.println("\nTotal records: " + count);

        } catch (SQLException e) {
            System.err.println(
                    "Database error: " + e.getMessage()
            );
        }
    }
}

package Service.DataManager;

import java.sql.*;

public class DataConnections {
    private static final String URL = "jdbc:oracle:thin:@//192.168.56.1:1521/XEPDB1";
    private static final String USER = "SYSTEM";
    private static final String PASSWORD = "DB12345";

    public static Connection getConnection() throws SQLException{
        return DriverManager.getConnection(URL,USER,PASSWORD);
    }
 }

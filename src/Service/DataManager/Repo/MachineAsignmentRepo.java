package Service.DataManager.Repo;

import Model.MachineAssignment;
import Service.DataManager.DataConnections;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class MachineAsignmentRepo implements MachAssRepoInt<MachineAssignment>{

    @Override
    public void addEntry(MachineAssignment item) {
        String sql = "INSERT INTO MACHINE_ASSIGNMENTS (LOAD_ID, MACHINE_ID, STARTED_AT, DURATION_MINUTES, ENDED_AT) " +
                "VALUES (?, ?, ?, ?, ?)";

        try(
                Connection maConn = DataConnections.getConnection();
                PreparedStatement maPs = maConn.prepareStatement(sql);
                ){

        }catch(SQLException e){
            System.out.println(e.getMessage());
        }
        
    }

    @Override
    public void updateEntry(MachineAssignment item) {
        String sql = "UPDATE MACHINE_ASSIGNMENTS SET DURATION_MINUTES = ? WHERE ASSIGNMENT_ID = ?";
            try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
            ) {
                // Set parameters here
                ps.setInt(1, item.getDurationMinutes());

                int rows = ps.executeUpdate();
                System.out.println("Rows affected: " + rows);

            } catch (SQLException e) {
                System.out.println(e.getMessage());
            }

    }


    @Override
    public void deleteEntry(int id) {
        String sql = "INSERT INTO MACHINE_ASSIGNMENTS (LOAD_ID, MACHINE_ID, STARTED_AT, DURATION_MINUTES, ENDED_AT) VALUES (?,?,?,?,?)";
        try (
            Connection conn = DataConnections.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            // Set parameters here
            ps.setInt(1, id);

            int rows = ps.executeUpdate();
            System.out.println("Rows affected: " + rows);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    //For records
    @Override
    public MachineAssignment findById(int id) {

        String sql = "SELECT * FROM MACHINE_ASSIGNMENTS WHERE ASSIGNMENT_ID = ?";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            // IMPORTANT: Bind the ID before executing
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    // Retrieve timestamps
                    Timestamp ts = rs.getTimestamp("STARTED_AT");
                    Timestamp es = rs.getTimestamp("ENDED_AT");

                    // Convert to LocalDateTime
                    LocalDateTime startedAt = ts != null
                            ? ts.toLocalDateTime()
                            : null;

                    LocalDateTime endedAt = es != null
                            ? es.toLocalDateTime()
                            : null;

                    // Return the object directly
                    return new MachineAssignment(
                            rs.getInt("ASSIGNMENT_ID"),
                            rs.getInt("LOAD_ID"),
                            rs.getInt("MACHINE_ID"),
                            startedAt,
                            rs.getInt("DURATION_MINUTES"),
                            endedAt
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find machine assignment", e);
        }

        return null;
    }

    @Override
    public List<MachineAssignment> findAll() {
        List<MachineAssignment> ma = new ArrayList<>();

        String sql = "SELECT * FROM MACHINE_ASSIGNMENTS";

        try (
            Connection conn = DataConnections.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    while (rs.next()) {

                        // Retrieve timestamps
                        Timestamp ts = rs.getTimestamp("STARTED_AT");
                        Timestamp es = rs.getTimestamp("ENDED_AT");

                        // Convert to LocalDateTime
                        LocalDateTime startedAt = ts != null
                                ? ts.toLocalDateTime()
                                : null;

                        LocalDateTime endedAt = es != null
                                ? es.toLocalDateTime()
                                : null;

                        // Create and add MachineAssignment
                        ma.add(new MachineAssignment(
                                rs.getInt("ASSIGNMENT_ID"),
                                rs.getInt("LOAD_ID"),
                                rs.getInt("MACHINE_ID"),
                                startedAt,
                                rs.getInt("DURATION_MINUTES"),
                                endedAt
                        ));
                    }


                }
            }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return ma;
    }
}

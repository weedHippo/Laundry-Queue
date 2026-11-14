package Service.DataManager.Repo;


import Model.Load;
import Model.Machine;
import Model.ServiceStage;
import Service.DataManager.DataConnections;
import oracle.sql.TIMESTAMP;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class LaundryStatRepo implements AssRepo<Load> {
    MachineRepo mr = new MachineRepo();
    List<Machine> lm = mr.findAll();
    Machine selected = lm.get(0);
    private final int machineId = selected.getMachineId();

    @Override
    public void addEntry(Load item) {
        String sql = "INSERT INTO LAUNDRY_LOADS(CUSTOMER_ID, WEIGHT_KG, QUEUE_STATUS, RECEIVED_AT) VALUES (?,?,?,?)";
        try(
                Connection conn = DataConnections.getConnection();
                PreparedStatement  ps = conn.prepareStatement(sql);
                ){
            ps.setInt(1, machineId);
            ps.setDouble(2,item.getLoadWeight());
            ps.setString(3,item.getLoadStat().toString());
            ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
            int row = ps.executeUpdate();
            System.out.println("Rows affected: " + row );

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void updateEntry(Load item) {
        String sql = "UPDATE LAUNDRY_LOADS SET WEIGHT_KG = ?, QUEUE_STATUS = ? WHERE LOAD_ID = ?";

        try (
            Connection conn = DataConnections.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            // Set parameters here
            ps.setDouble(1, item.getLoadWeight());
            ps.setString(2, item.getLoadStat().toString());

            int rows = ps.executeUpdate();
            System.out.println("Rows affected: " + rows);

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void updateEntryState(Load item) {
        String sql = "UPDATE LAUNDRY_LOADS SET QUEUE_STATUS = ? WHERE LOAD_ID = ?";
        
        try (
            Connection conn = DataConnections.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            ps.setString(1, item.getLoadStat().toString());
            ps.setInt(2, item.getLoadId());
        
            int rows = ps.executeUpdate();
            System.out.println("Rows affected: " + rows);
        
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
    }

    @Override
    public void deleteEntry(int id) {

    }

    @Override
    public List<Load> findAll() {

        String sql = "SELECT LOAD_ID,CUSTOMER_ID,WEIGHT_KG,QUEUE_STATUS,RECEIVED_AT FROM LAUNDRY_LOADS ORDER BY RECEIVED_AT";
        List<Load> loadAss = new ArrayList<>();
        try (
            Connection conn = DataConnections.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
        ) {

                while (rs.next()) {
                    Object recieveAt;
                    loadAss.add(new Load(
                            rs.getInt("LOAD_ID"),
                            rs.getInt("CUSTOMER_ID"),
                            rs.getDouble("WEIGHT_KG"),
                            ServiceStage.valueOf(rs.getString("QUEUE_STATUS")),
                            rs.getTimestamp("RECEIVE_AT").toLocalDateTime()
                    ));
                }


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return loadAss;
    }

    @Override
    public Load findById(int id) {
        String sql = "SELECT * FROM LAUNDRY_LOADS WHERE LOAD_ID = ?";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Load(
                            rs.getInt("LOAD_ID"),
                            rs.getInt("CUSTOMER_ID"),
                            rs.getDouble("WEIGHT_KG"),
                            ServiceStage.valueOf(rs.getString("QUEUE_STATUS")),
                            rs.getTimestamp("RECEIVED_AT").toLocalDateTime()
                    );
                }
            }

        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        return null;
    }
}

package Service.DataManager.Repo;

import Model.Machine;
import Model.MachineState;
import Model.MachineType;
import Service.DataManager.DataConnections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MachineRepo implements MachAssRepoInt<Machine>{

    @Override
    public void addEntry(Machine item) {
        String sql = "INSERT INTO MACHINES (MACHINE_TYPE, CAPACITY_KG, OPERATIONAL_STATUS) VALUES (?,?,?)";
        try(
                Connection macConn = DataConnections.getConnection();
                PreparedStatement macPrep = macConn.prepareStatement(sql);
                ){
            macPrep.setString(1,item.getMachineType().name());
            macPrep.setInt(2, (int) item.getMachineCapacity());
            macPrep.setString(3, item.getMachineState().toString());
            int rows = macPrep.executeUpdate();
            System.out.println("Rows Inserted" + rows);

            try(ResultSet rs = macPrep.getGeneratedKeys()){
                if(rs.next()){
                    item.setMachineId(rs.getInt(1));
                }
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void updateEntry(Machine item) {
        String sql = "UPDATE MACHINES SET OPERATIONAL_STATUS = ? WHERE MACHINE_ID = ?";

        try(
            Connection con = DataConnections.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
                ){

                ps.setString(1,item.getMachineState().name());
                ps.setInt(2,item.getMachineId());
                int rows = ps.executeUpdate();

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void deleteEntry(int id) {
        String sql = "DELETE MACHINES WHERE MACHINE_ID = ?";
        try(
                Connection con = DataConnections.getConnection();
                PreparedStatement prep = con.prepareStatement(sql);
                ){

            prep.setInt(1, id);
            int rows = prep.executeUpdate();
            System.out.println(rows);

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }
    @Override
    public Machine findById(int id) {

        String sql = "SELECT * FROM MACHINE_ASSIGNMENTS WHERE ASSIGNMENT_ID = ?";

        try (
                Connection conn = DataConnections.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql)
        ) {

            // IMPORTANT: Bind the ID before executing
            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Machine(
                            rs.getInt("MACHINE_ID"),
                            rs.getDouble("CAPACITY_KG"),
                            MachineType.valueOf(rs.getString("MACHINE_TYPE").trim().toUpperCase()),
                            MachineState.valueOf(rs.getString("OPERATIONAL_STATUS").trim().toUpperCase())
                    );
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to find machine assignment", e);
        }

        return null; // Assignment not found
    }

    @Override
    public List findAll() {
        List<Machine> machines = new ArrayList<>();
        String sql = "SELECT MACHINE_ID, MACHINE_TYPE, CAPACITY_KG, OPERATIONAL_STATUS FROM MACHINES ORDER BY MACHINE_TYPE";
        try(
                Connection conn = DataConnections.getConnection();
                PreparedStatement ps = conn.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();
                ){

            while (rs.next()){
               machines.add(new Machine(
                       rs.getInt("MACHINE_ID"),
                       rs.getDouble("CAPACITY_KG"),
                       MachineType.valueOf(rs.getString("MACHINE_TYPE")),
                       MachineState.valueOf(rs.getString("OPERATIONAL_STATUS").trim().toUpperCase())
               ));
            }

        }catch (SQLException e){
            System.out.println(e.getMessage());
        }
        return machines;
    }


}

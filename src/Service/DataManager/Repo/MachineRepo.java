package Service.DataManager.Repo;

import Model.Machine;
import Model.MachineState;
import Service.DataManager.DataConnections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

public class MachineRepo implements Repository<Machine>{

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

    }

    @Override
    public void deleteEntry(int id) {

    }

    @Override
    public List findAll() {
        return List.of();
    }
}

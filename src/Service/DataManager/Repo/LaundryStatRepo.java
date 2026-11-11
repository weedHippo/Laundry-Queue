package Service.DataManager.Repo;


import Model.Load;
import Model.Machine;
import Service.DataManager.DataConnections;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LaundryStatRepo implements Repository<Load> {
    MachineRepo mr = new MachineRepo();
    List<Machine> lm = mr.findAll();
    Machine selected = lm.get(0);
    private final int machineId = selected.getMachineId();

    @Override
    public void addEntry(Load item) {
        String sql = "INSERT INTO LAUNDRY_LOADS (CUSTOMER_ID, WEIGHT_KG, QUEUE_STATUS, RECEIVED_AT) VALUES (?,?,?,?,?)";
        try(
                Connection conn = DataConnections.getConnection();
                PreparedStatement  ps = conn.prepareStatement(sql);
                ){
            ps.setInt(1, machineId);
            //ps.setInt(2,item.getL);




        }catch (SQLException e){
            System.out.println(e.getMessage());
        }

    }

    @Override
    public void updateEntry(Load item) {

    }

    @Override
    public void deleteEntry(int id) {

    }

    @Override
    public List<Load> findAll() {
        return List.of();
    }
}

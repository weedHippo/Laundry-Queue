package Service.DataManager.Repo;

import Service.DataManager.DataConnections;
import oracle.jdbc.proxy.annotation.Pre;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class MachineAsignmentRepo implements Repository{

    @Override
    public void addEntry(Object item) {
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
    public void updateEntry(Object item) {

    }

    @Override
    public void deleteEntry(int id) {

    }

    @Override
    public List findAll() {
        return List.of();
    }
}

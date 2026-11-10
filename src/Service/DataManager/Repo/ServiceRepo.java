package Service.DataManager.Repo;

import Model.Customer;

import java.util.List;

public class ServiceRepo implements Repository<Customer> {


    @Override
    public void addEntry(Customer item) {

    }

    @Override
    public void updateEntry(Customer item) {

    }

    @Override
    public void deleteEntry(int id) {

    }


    @Override
    public List<Customer> findAll() {
        return List.of();
    }


}

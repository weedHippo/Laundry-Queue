package Service.DataManager.Repo;

import Model.Customer;

import java.util.List;

public class LaundryStatRepo implements Repository<Customer> {


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
    public Customer findById(int id) {
        return null;
    }

    @Override
    public List<Customer> findAll() {
        return List.of();
    }
}

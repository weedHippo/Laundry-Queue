package App;
import Model.Customer;
import Service.CustomerManager;
import Service.CustomerManager.*;
import Service.DataManager.Repo.CustomerRepo;

public class Sandbox {
    public static void main(String[] args) throws IllegalAccessException {

        CustomerRepo repo = new CustomerRepo();

        Customer c = new Customer(0, "Test", "09999999997");

        /*
        // 1. add, then check the ID got overwritten
        repo.addEntry(c);
        System.out.println("New ID: " + c.getCustomerId());
         */

        for (Customer cust : repo.findAll()) {
            System.out.println(cust.getCustomerId() + " | " + cust.getCustomerName() + " | " + cust.getCustomerContact());
        }

    }
}

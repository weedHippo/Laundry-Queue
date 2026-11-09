package Service;

import Model.Customer;
import Service.DataManager.Repo.CustomerRepo;

public class CustomerManager {
    private final CustomerRepo repo = new CustomerRepo();
    private int tempId = 0;

    public void addCustomer(String cstName, String cstContact) {
        if (cstName == null || cstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter the customer's name.");
        }
        if (cstContact == null || cstContact.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a contact number.");
        }
        if(!cstName.matches("[A-Za-z ]+")){
            throw new IllegalArgumentException("No symbold and numbers 1-9 /*-+.");
        }

        repo.addEntry(new Customer(tempId,cstName,cstContact));
    }
}
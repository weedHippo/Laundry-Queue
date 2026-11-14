package Service.Managers;

import Model.Customer;
import Service.DataManager.Repo.CustomerRepo;
import Service.Exception.InsufficientCostumerInformationException;

import java.util.List;

public class CustomerManager {
    private final CustomerRepo repo = new CustomerRepo();
    private final int TEMP_ID = 0;

    public void addCustomer(String cstName, String cstContact) {
        if (cstName == null || cstName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter the customer's name.");
        }

        if (cstContact == null || cstContact.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a contact number.");
        }
        //Validate String input, check if it has a symbol or numbers
        if(!cstName.matches("[A-Za-z ]+")){
            throw new IllegalArgumentException("No symbold and numbers 1-9 /*-+.");
        }

        repo.addEntry(new Customer(TEMP_ID,cstName,cstContact));
    }


    //not working?
    public void updateCustomer(int cstId, String newName, String newContact){
        CustomerRepo cstRepo = new CustomerRepo();
        List<Customer> customers = repo.findAll();

        boolean notFound = customers.stream().noneMatch(cst ->cst.getCustomerId() == cstId);


        //triggering
        if(notFound){
            throw new InsufficientCostumerInformationException("The Customer doesn't Exist");
        }

        if (newName == null || newName.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter the customer's name.");
        }
        if (newContact == null || newContact.trim().isEmpty()) {
            throw new IllegalArgumentException("Please enter a contact number.");
        }
        if(!newName.matches("[A-Za-z ]+")){
            throw new IllegalArgumentException("No symbold and numbers 1-9 /*-+.");
        }

        repo.updateEntry(new Customer(cstId,newName,newContact));
    }

    void removeCustomer(int cstId){
        CustomerRepo cstRepo = new CustomerRepo();
        List<Customer> customers = repo.findAll();

        boolean notFound = customers.stream().noneMatch(cst ->cst.getCustomerId() == cstId);

        if(notFound){
            throw new InsufficientCostumerInformationException("The Customer doesn't Exist");
        }

        cstRepo.deleteEntry(cstId);

    }

}
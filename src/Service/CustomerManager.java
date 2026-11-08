package Service;
import Model.Customer;

public class CustomerManager {

    public void registerCustomer(String cstName, String cstContacts) throws IllegalAccessException {
       if(cstName == null || cstName.isEmpty()){
           throw new IllegalArgumentException(
                   "customer Entry cannot be empty"
           );
       }

       if(cstContacts == null || cstContacts.isEmpty()){
           throw new IllegalArgumentException(
                   "Costumer Contact Information Cannot be empty"
           );
       }

       if(cstName.matches("[a-zA-Z]+")){
           throw new IllegalArgumentException(
                   "Customer Name Can't Contain Symbols or Numbers (/*-+, or 12345)"
           );
       }

        System.out.println("All passed");

    }


}

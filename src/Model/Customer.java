package Model;

public class Customer {
   private String customerName;
   private String customerContact;
   private int customerId;

    public Customer(int customerId, String customerName, String customerContact) {
        this.customerName = customerName;
        this.customerContact = customerContact;
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getCustomerContact() {
        return customerContact;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    @Override
    public String toString() {
        return customerName;
    }
}

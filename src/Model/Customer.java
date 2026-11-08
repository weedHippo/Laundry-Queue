package Model;

public class Customer {

    private int customerId;
    private String customerName;
    private String contactNumber;

    public Customer(int customerId, String customerName, String contactNumber) {
        this.contactNumber = contactNumber;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public String getContactNumber() {
        return contactNumber;
    }


}
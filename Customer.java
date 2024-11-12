/**
 * Represents a bank customer with all their personal information and accounts.
 * Handles customer-specific data and operations.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class Customer extends Person {
    /** Unique identifier for each customer */
    private String customerID;
    /** Customer's date of birth */
    private String dateOfBirth;
    /** Customer's street address */
    private String address;
    /** Customer's city */
    private String city;
    /** Customer's state */
    private String state;
    /** Customer's ZIP code */
    private String zipCode;
    /** Customer's phone number */
    private String phoneNumber;
    /** Customer's credit score */
    private int creditScore;
    
    /**
     * Creates a new customer with basic information.
     * Additional fields can be set using setters.
     * 
     * @param name the customer's full name
     * @param customerID unique identifier for the customer
     */
    public Customer(String name, String customerID) {
        super(name);
        this.customerID = customerID;
    }
    
    // Getters and setters for all fields
    public String getCustomerID() {
        return customerID;
    }
    
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
    
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public int getCreditScore() {
        return creditScore;
    }
    
    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }
    
    /**
     * Creates a formatted string of customer information.
     * Useful for displays and reports.
     * 
     * @return formatted string of customer details
     */
    @Override
    public String toString() {
        return String.format(
            "Customer ID: %s%nName: %s%nDOB: %s%nAddress: %s%nCity: %s, %s %s%nPhone: %s",
            customerID, getName(), dateOfBirth, address, city, state, zipCode, phoneNumber
        );
    }
}
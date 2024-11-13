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
     * 
     * @param name the customer's full name
     * @param customerID unique identifier for the customer
     */
    public Customer(String name, String customerID) {
        super(name);
        this.customerID = customerID;
    }
    
    /**
     * Retrieves the unique identifier for the customer.
     * @return the customer's unique id
     */
    public String getCustomerID() {
        return customerID;
    }
    /**
     * Sets the customer's unique identifier.
     * 
     * @param customerID the new unique identifier for the customer
     */
    public void setCustomerID(String customerID) {
        this.customerID = customerID;
    }
    
    /**
     * Retrieves the customer's date of birth.
     * 
     * @return the customer's date of birth
     */
    public String getDateOfBirth() {
        return dateOfBirth;
    }
    
    /**
     * Sets the customer's date of birth.
     * 
     * @param dateOfBirth the customer's date of birth
     */
    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }
    
    /**
     * Retrieves the customer's street address.
     * 
     * @return the customer's street address
     */
    public String getAddress() {
        return address;
    }
    
    /**
     * Sets the customer's street address.
     * 
     * @param address the customer's street address
     */
    public void setAddress(String address) {
        this.address = address;
    }
    
    /**
     * Retrieves the customer's city of residence.
     * 
     * @return the customer's city
     */
    public String getCity() {
        return city;
    }
    
    /**
     * Sets the customer's city of residence.
     * 
     * @param city the customer's city
     */
    public void setCity(String city) {
        this.city = city;
    }
    
    /**
     * Retrieves the customer's state of residence.
     * 
     * @return the customer's state
     */
    public String getState() {
        return state;
    }
    
    /**
     * Sets the customer's state of residence.
     * 
     * @param state the customer's state
     */
    public void setState(String state) {
        this.state = state;
    }
    
    /**
     * Retrieves the customer's ZIP code.
     * 
     * @return the customer's ZIP code
     */
    public String getZipCode() {
        return zipCode;
    }
    
    /**
     * Sets the customer's ZIP code.
     * 
     * @param zipCode the customer's ZIP code
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    /**
     * Retrieves the customer's phone number.
     * 
     * @return the customer's phone number
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    /**
     * Sets the customer's phone number.
     * 
     * @param phoneNumber the customer's phone number
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    /**
     * Retrieves the customer's credit score.
     * 
     * @return the customer's credit score
     */
    public int getCreditScore() {
        return creditScore;
    }
    
    /**
     * Sets the customer's credit score.
     * 
     * @param creditScore the customer's credit score
     */
    public void setCreditScore(int creditScore) {
        this.creditScore = creditScore;
    }
}
package com.banksystem;
import java.util.*;

/**
 * Handles the creation of new bank users and their accounts.
 * This class manages:
 * - Generating unique IDs
 * - Creating accounts
 * - Setting credit limits
 * - Validating new user names
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class NewUsers {

    /** The last used user ID to be able to increase it for a new customer */
    private int lastUserId;

    /** A Map to store existing customers */
    private Map<String, Customer> existingCustomers;

    /** A random number generator for generating credit limits. */
    private Random random;

    /**
     * Creates a new instance of the `NewUsers` class.
     * 
     * @param existingCustomers a map of existing customers by their full names
     */
    public NewUsers(Map<String, Customer> existingCustomers) {
        this.existingCustomers = existingCustomers;
        this.random = new Random();
        initializeLastUserId();
    }

    /**
     * Initializes the last user ID based on existing customers.
     */
    private void initializeLastUserId() {
        lastUserId = existingCustomers.values().stream()
            .mapToInt(c -> Integer.parseInt(c.getCustomerID()))
            .max()
            .orElse(0);
    }

    /**
     * Checks whether the provided first and last name combination can be used for a new customer.
     * 
     * @param firstName the first name of the customer
     * @param lastName the last name of the customer
     * @return true if the name is valid for a new customer, false otherwise
     */
    public boolean isValidNewCustomerName(String firstName, String lastName) {
        //count customers with matching first name and last name
        String fullName = firstName + " " + lastName;
        int matchingBothNames = 0;

        //check if exact full name already exists
        if (existingCustomers.containsKey(fullName)) {
            return false;
        }

        for (String existingName : existingCustomers.keySet()) {
            String[] nameParts = existingName.split(" ");
            String existingFirstName = nameParts[0];
            String existingLastName = nameParts[1];


            if(existingFirstName.equals(firstName) && existingLastName.equals(lastName)) {
                matchingBothNames++;
            }
        }

        return matchingBothNames == 0;
    }



    /**
     * Creates a new customer using the provided user data.
     * 
     * @param userData a map containing the required fields for creating a new customer
     * @return a `Customer` object representing the newly created customer
     * @throws IllegalArgumentException if required fields are missing or invalid
     */
    public Customer createUser(Map<String, String> userData) {
        //check if we have the required fields
        validateUserData(userData);
        
        //generate customer ID
        String customerId = String.valueOf(++lastUserId);
        
        String firstName = userData.get("firstName");
        String lastName = userData.get("lastName");
        String fullName = firstName + " " + lastName;
        
        //create new customer object
        Customer newCustomer = new Customer(fullName, customerId);
        
        //set customer details
        newCustomer.setDateOfBirth(userData.get("dob"));
        newCustomer.setAddress(userData.get("address"));
        newCustomer.setCity(userData.get("city"));
        newCustomer.setState(userData.get("state"));
        newCustomer.setZipCode(userData.get("zip"));
        newCustomer.setPhoneNumber(userData.get("phone"));
        newCustomer.setCreditScore(Integer.parseInt(userData.get("creditScore")));
        
        //generate account numbers
        List<Account> accounts = new ArrayList<>();

        // Create checking account
        Account checking = AccountFactory.createAccount(
            AccountFactory.CHECKING,
            AccountFactory.generateAccountNumber(AccountFactory.CHECKING, customerId),
            0.0,
            0.0
        );
        checking.setOwner(newCustomer);
        accounts.add(checking);

        // Create savings account
        Account savings = AccountFactory.createAccount(
            AccountFactory.SAVINGS,
            AccountFactory.generateAccountNumber(AccountFactory.SAVINGS, customerId),
            0.0,
            0.0
        );
        savings.setOwner(newCustomer);
        accounts.add(savings);

        // Create credit account with limit
        double creditLimit = getCreditLimit(newCustomer.getCreditScore());
        Account credit = AccountFactory.createAccount(
            AccountFactory.CREDIT,
            AccountFactory.generateAccountNumber(AccountFactory.CREDIT, customerId),
            0.0,
            creditLimit
        );
        credit.setOwner(newCustomer);
        accounts.add(credit);

        newCustomer.setAccounts(accounts);
        return newCustomer;
    }

    /**
     * Validates the provided user data to ensure all required fields are present and correctly formatted.
     * 
     * @param userData a map containing user data fields
     * @throws IllegalArgumentException if any required fields are missing or invalid
     */
    private void validateUserData(Map<String, String> userData) {
        String[] requiredFields = {
            "firstName", "lastName", "dob", "address", "city", 
            "state", "zip", "phone", "creditScore"
        };
        
        List<String> missingFields = new ArrayList<>();
        for (String field : requiredFields) {
            if (!userData.containsKey(field) || userData.get(field).trim().isEmpty()) {
                missingFields.add(field);
            }
        }
        
        if (!missingFields.isEmpty()) {
            throw new IllegalArgumentException("Missing required fields: " + 
                String.join(", ", missingFields));
        }
        
        //check credit score
        try {
            int creditScore = Integer.parseInt(userData.get("creditScore"));
            if (creditScore < 300 || creditScore > 850) {
                throw new IllegalArgumentException("Credit score must be between 300 and 850");
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid credit score format");
        }
    }

    /**
     * Determines the credit limit for a customer based on their credit score.
     * 
     * @param creditScore the credit score of the customer
     * @return the calculated credit limit
     */
    private double getCreditLimit(int creditScore) {
        if (creditScore <= 580) {
            return 100 + random.nextDouble() * (699 - 100);
        } else if (creditScore <= 669) {
            return 700 + random.nextDouble() * (4999 - 700);
        } else if (creditScore <= 739) {
            return 5000 + random.nextDouble() * (7499 - 5000);
        } else if (creditScore <= 799) {
            return 7500 + random.nextDouble() * (15999 - 7500);
        } else {
            return 16000 + random.nextDouble() * (25000 - 16000);
        }
    }
}
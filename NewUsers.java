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
 * @version 2.0
 */
public class NewUsers {
    /** Tracks the last used user ID for generating new IDs */
    private int lastUserId;
    
    /** Map of existing customers for duplicate checking */
    private Map<String, Customer> existingCustomers;
    
    /** Random number generator for credit limits */
    private Random random;

    /**
     * Initializes the NewUsers handler with existing customer data.
     * @param existingCustomers map of existing customers
     */
    public NewUsers(Map<String, Customer> existingCustomers) {
        this.existingCustomers = existingCustomers;
        this.random = new Random();
        initializeLastUserId();
    }

    /**
     * Finds the highest existing user ID to initialize the counter.
     */
    private void initializeLastUserId() {
        lastUserId = existingCustomers.values().stream()
            .mapToInt(c -> Integer.parseInt(c.getCustomerID()))
            .max()
            .orElse(0);
    }

    /**
     * Validates if a new customer name would create an invalid duplicate scenario.
     * Invalid scenario: Customer sharing both first and last name with existing customer.
     * 
     * @param firstName proposed first name
     * @param lastName proposed last name
     * @return true if name is valid, false if would create invalid duplicate
     */
    public boolean isValidNewCustomerName(String firstName, String lastName) {
        // Check if exact full name already exists
        String fullName = firstName + " " + lastName;
        if (existingCustomers.containsKey(fullName)) {
            return false;
        }

        // Count matches for first name and last name
        boolean hasFirstNameMatch = false;
        boolean hasLastNameMatch = false;

        for (String existingName : existingCustomers.keySet()) {
            String[] nameParts = existingName.split(" ");
            if (nameParts[0].equalsIgnoreCase(firstName)) {
                hasFirstNameMatch = true;
            }
            if (nameParts[1].equalsIgnoreCase(lastName)) {
                hasLastNameMatch = true;
            }

            // If both match, this would create an invalid duplicate
            if (hasFirstNameMatch && hasLastNameMatch) {
                return false;
            }
        }

        return true;
    }

    /**
     * Creates a new customer with all necessary accounts.
     * Assumes name has already been validated with isValidNewCustomerName().
     * 
     * @param userData map containing all required customer information
     * @return the newly created Customer object
     * @throws IllegalArgumentException if required data is missing or invalid
     */
    public Customer createUser(Map<String, String> userData) {
        // Validate required fields
        validateUserData(userData);
        
        // Generate customer ID
        String customerId = String.valueOf(++lastUserId);
        
        String firstName = userData.get("firstName");
        String lastName = userData.get("lastName");
        String fullName = firstName + " " + lastName;
        
        // Create new customer object with additional fields
        Customer newCustomer = new Customer(fullName, customerId);
        newCustomer.setDateOfBirth(userData.get("dob"));
        newCustomer.setAddress(userData.get("address"));
        newCustomer.setCity(userData.get("city"));
        newCustomer.setState(userData.get("state"));
        newCustomer.setZipCode(userData.get("zip"));
        newCustomer.setPhoneNumber(userData.get("phone"));
        newCustomer.setCreditScore(Integer.parseInt(userData.get("creditScore")));
        
        // Generate accounts
        List<String> accountNumbers = generateAccountNumbers(customerId);
        List<Account> accounts = new ArrayList<>();
        
        // Create checking account
        accounts.add(new Checkings(accountNumbers.get(0), 0.0));
        
        // Create savings account
        accounts.add(new Savings(accountNumbers.get(1), 0.0));
        
        // Create credit account with appropriate limit
        double creditLimit = getCreditLimit(newCustomer.getCreditScore());
        accounts.add(new Credit(accountNumbers.get(2), 0.0, creditLimit));
        
        newCustomer.setAccounts(accounts);
        return newCustomer;
    }

    /**
     * Validates that all required user data is present and valid.
     * 
     * @param userData map of user data to validate
     * @throws IllegalArgumentException if data is missing or invalid
     */
    private void validateUserData(Map<String, String> userData) {
        String[] requiredFields = {
            "firstName", "lastName", "dob", "address", "city", 
            "state", "zip", "phone", "creditScore"
        };
        
        for (String field : requiredFields) {
            if (!userData.containsKey(field) || userData.get(field).trim().isEmpty()) {
                throw new IllegalArgumentException("Missing required field: " + field);
            }
        }
        
        // Validate credit score
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
     * Generates account numbers for all account types.
     * Format: [type prefix][customer id padded to 3 digits]
     * 
     * @param customerId the customer's ID
     * @return list of account numbers [checking, savings, credit]
     */
    private List<String> generateAccountNumbers(String customerId) {
        String paddedId = String.format("%03d", Integer.parseInt(customerId));
        return Arrays.asList(
            "1" + paddedId,  // Checking
            "2" + paddedId,  // Savings
            "3" + paddedId   // Credit
        );
    }

    /**
     * Calculates credit limit based on credit score ranges.
     * Uses random number within appropriate range.
     * 
     * @param creditScore customer's credit score
     * @return calculated credit limit
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
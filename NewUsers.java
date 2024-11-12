import java.util.*;

/**
 * Handles the creation of new bank users and their accounts.
 * This class manages:
 * - Generating unique IDs
 * - Creating accounts
 * - Setting credit limits
 * - Handling duplicate names
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
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
     * @param customers map of existing customers
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
     * Creates a new customer with all necessary accounts.
     * Handles duplicate names by adding a unique identifier if needed.
     * 
     * @param userData map containing all required customer information
     * @return the newly created Customer object
     */
    public Customer createUser(Map<String, String> userData) {
        // Generate unique customer ID
        String customerId = generateUserId();
        
        // Create customer name, handling duplicates
        String firstName = userData.get("firstName");
        String lastName = userData.get("lastName");
        String fullName = handleDuplicateName(firstName, lastName);
        
        // Create new customer object with additional fields
        Customer newCustomer = new Customer(fullName, customerId);
        newCustomer.setDateOfBirth(userData.get("dob"));
        newCustomer.setAddress(userData.get("address"));
        newCustomer.setCity(userData.get("city"));
        newCustomer.setState(userData.get("state"));
        newCustomer.setZipCode(userData.get("zip"));
        newCustomer.setPhoneNumber(userData.get("phone"));
        newCustomer.setCreditScore(Integer.parseInt(userData.get("creditScore")));
        
        // Generate account numbers and create accounts
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
     * Generates a unique user ID by incrementing the last used ID.
     * @return new user ID as string
     */
    private String generateUserId() {
        return String.valueOf(++lastUserId);
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

    /**
     * Handles duplicate names by adding a unique identifier if needed.
     * Only handles either same first name or same last name, not both.
     * 
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @return unique full name for the customer
     */
    private String handleDuplicateName(String firstName, String lastName) {
        String baseFullName = firstName + " " + lastName;
        
        // If no duplicate exists, return original name
        if (!existingCustomers.containsKey(baseFullName)) {
            return baseFullName;
        }
        
        // Count existing duplicates to generate unique identifier
        int duplicateCount = 1;
        for (String existingName : existingCustomers.keySet()) {
            if (existingName.startsWith(firstName + " ") || existingName.endsWith(" " + lastName)) {
                duplicateCount++;
            }
        }
        
        // Add unique identifier to name
        return baseFullName + " (" + duplicateCount + ")";
    }

    /**
     * Finds customers by partial name match.
     * Can search by either first name or last name.
     * 
     * @param searchName name to search for
     * @param isFirstName true if searching by first name, false for last name
     * @return list of matching customers
     */
    public List<Customer> findCustomersByName(String searchName, boolean isFirstName) {
        List<Customer> matches = new ArrayList<>();
        
        for (Customer customer : existingCustomers.values()) {
            String[] nameParts = customer.getName().split(" ");
            String nameToCheck = isFirstName ? nameParts[0] : nameParts[1];
            
            if (nameToCheck.equalsIgnoreCase(searchName)) {
                matches.add(customer);
            }
        }
        
        return matches;
    }
}
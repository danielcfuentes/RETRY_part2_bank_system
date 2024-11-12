import java.util.*;
import java.io.*;

/**
 * Represents a Bank Manager with special privileges and responsibilities.
 * Bank Managers can:
 * - Look up any customer's accounts
 * - Create new customers
 * - Process transaction files
 * - Generate bank statements
 * - Handle customer searches
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class BankManager extends Person {
    /** Map of all customers in the system */
    private Map<String, Customer> customers;
    
    /** Transaction logger */
    private TransactionLog logger;
    
    /** Scanner for reading user input */
    private Scanner scanner;
    
    /** Handler for creating new users */
    private NewUsers newUsersHandler;

    /**
     * Creates a new Bank Manager with access to all customer data.
     * 
     * @param name the manager's name
     * @param customers map of all customers in the system
     * @param logger transaction logging system
     */
    public BankManager(String name, Map<String, Customer> customers, TransactionLog logger) {
        super(name);
        this.customers = customers;
        this.logger = logger;
        this.scanner = new Scanner(System.in);
        this.newUsersHandler = new NewUsers(customers);
    }

    /**
     * Creates a new customer account with all required information.
     * Validates name for duplicates and creates all necessary accounts.
     * 
     * @param userData map containing all customer information
     * @return Optional containing the new customer if creation successful
     */
    public Optional<Customer> createNewCustomer(Map<String, String> userData) {
        try {
            // Validate name first
            if (!newUsersHandler.isValidNewCustomerName(
                    userData.get("firstName"), userData.get("lastName"))) {
                System.out.println("Error: Invalid name combination. Would create duplicate customer.");
                return Optional.empty();
            }
            
            // Create the customer
            Customer newCustomer = newUsersHandler.createUser(userData);
            
            // Add to customers map
            customers.put(newCustomer.getName(), newCustomer);
            
            // Log the creation
            logger.logTransaction("Bank Manager created new customer: " + newCustomer.getName() + 
                                " (ID: " + newCustomer.getCustomerID() + ")");
            
            return Optional.of(newCustomer);
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating customer: " + e.getMessage());
            return Optional.empty();
        }
    }

    /**
     * Searches for customers by name, handling cases where multiple customers
     * share the same first or last name.
     * 
     * @param searchName the name to search for
     * @param isFirstName true if searching by first name, false for last name
     * @return list of matching customers
     */
    public List<Customer> searchCustomers(String searchName, boolean isFirstName) {
        List<Customer> matches = new ArrayList<>();
        
        for (Customer customer : customers.values()) {
            String[] nameParts = customer.getName().split(" ");
            String nameToCheck = isFirstName ? nameParts[0] : nameParts[1];
            
            if (nameToCheck.equalsIgnoreCase(searchName)) {
                matches.add(customer);
            }
        }
        
        return matches;
    }

    /**
     * Interactive method to help bank manager find specific customer when
     * multiple matches exist.
     * 
     * @param firstName first name to search
     * @param lastName last name to search
     * @return selected customer or empty if none found
     */
    public Optional<Customer> findCustomerInteractive(String firstName, String lastName) {
        // Try exact match first
        Customer exactMatch = customers.get(firstName + " " + lastName);
        if (exactMatch != null) {
            return Optional.of(exactMatch);
        }

        // Search by first name
        List<Customer> firstNameMatches = searchCustomers(firstName, true);
        
        // Search by last name
        List<Customer> lastNameMatches = searchCustomers(lastName, false);
        
        // If we have matches, let manager select
        if (!firstNameMatches.isEmpty()) {
            System.out.println("\nMultiple customers found with first name '" + firstName + "':");
            return selectCustomerFromList(firstNameMatches);
        }
        
        if (!lastNameMatches.isEmpty()) {
            System.out.println("\nMultiple customers found with last name '" + lastName + "':");
            return selectCustomerFromList(lastNameMatches);
        }
        
        return Optional.empty();
    }

    /**
     * Helper method to display list of matching customers and let manager select one.
     * 
     * @param customers list of matching customers
     * @return selected customer or empty if selection failed
     */
    private Optional<Customer> selectCustomerFromList(List<Customer> customers) {
        for (int i = 0; i < customers.size(); i++) {
            Customer c = customers.get(i);
            System.out.printf("%d. %s (ID: %s) - %s, %s%n",
                i + 1,
                c.getName(),
                c.getCustomerID(),
                c.getCity(),
                c.getState()
            );
        }
        
        System.out.println("Enter number to select customer (or 0 to cancel):");
        try {
            int selection = Integer.parseInt(scanner.nextLine().trim());
            if (selection > 0 && selection <= customers.size()) {
                return Optional.of(customers.get(selection - 1));
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid selection.");
        }
        
        return Optional.empty();
    }

    /**
     * Generates a bank statement for a specific customer.
     * 
     * @param customer the customer to generate statement for
     * @return formatted bank statement string
     */
    public String generateBankStatement(Customer customer) {
        StringBuilder statement = new StringBuilder();
        
        statement.append("BANK STATEMENT\n");
        statement.append("Date: ").append(new Date()).append("\n\n");
        statement.append("Customer: ").append(customer.getName()).append("\n");
        statement.append("Customer ID: ").append(customer.getCustomerID()).append("\n");
        statement.append("Address: ").append(customer.getAddress()).append("\n");
        statement.append(customer.getCity()).append(", ");
        statement.append(customer.getState()).append(" ");
        statement.append(customer.getZipCode()).append("\n");
        statement.append("Phone: ").append(customer.getPhoneNumber()).append("\n\n");
        
        statement.append("ACCOUNT SUMMARY\n");
        for (Account account : customer.getAccounts()) {
            statement.append(account.getClass().getSimpleName())
                    .append(" (").append(account.getAccountNumber()).append(")\n");
            statement.append("Current Balance: $")
                    .append(String.format("%.2f", account.getBalance())).append("\n\n");
        }
        
        logger.logTransaction("Bank Manager generated statement for: " + customer.getName());
        
        return statement.toString();
    }

    /**
     * Process a transaction file containing multiple banking operations.
     * 
     * @param filename name of the transaction file
     * @throws IOException if file cannot be read
     */
    public void processTransactionFile(String filename) throws IOException {
        // This will be implemented in the next requirement
        // It will handle reading and processing the Transactions.csv file
    }
}
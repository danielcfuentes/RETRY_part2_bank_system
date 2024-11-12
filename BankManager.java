import java.util.*;
import java.io.*;

/**
 * Represents a Bank Manager with special privileges and responsibilities.
 * This class extends Person and implements specific bank manager operations.
 * Bank Managers can:
 * - Look up any customer's accounts
 * - Create new customers
 * - Process transaction files
 * - Generate bank statements
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class BankManager extends Person {
    /** Map of all customers in the system */
    private Map<String, Customer> customers;
    
    /** Transaction logger */
    private TransactionLog logger;
    
    /** Counter for generating new customer IDs */
    private static int lastCustomerId = 0;

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
        initializeLastCustomerId(); // Set up the customer ID counter
    }

    /**
     * Finds the highest existing customer ID to initialize the counter.
     * This ensures new customer IDs don't conflict with existing ones.
     */
    private void initializeLastCustomerId() {
        for (Customer customer : customers.values()) {
            int currentId = Integer.parseInt(customer.getCustomerID());
            if (currentId > lastCustomerId) {
                lastCustomerId = currentId;
            }
        }
    }

    /**
     * Creates a new customer with all necessary accounts.
     * Automatically generates:
     * - Customer ID
     * - Account numbers
     * - Credit limit based on credit score
     * 
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @param dob date of birth
     * @param address street address
     * @param city city
     * @param state state
     * @param zip zip code
     * @param phone phone number
     * @param creditScore credit score for determining credit limit
     * @return the newly created Customer object
     */
    public Customer createNewCustomer(String firstName, String lastName, String dob, 
                                    String address, String city, String state, 
                                    String zip, String phone, int creditScore) {
        // Generate new customer ID
        String customerId = String.valueOf(++lastCustomerId);
        
        // Create full name
        String fullName = firstName + " " + lastName;
        
        // Create new customer
        Customer newCustomer = new Customer(fullName, customerId);
        
        // Generate account numbers
        String checkingNumber = generateAccountNumber("1");
        String savingsNumber = generateAccountNumber("2");
        String creditNumber = generateAccountNumber("3");
        
        // Determine credit limit based on credit score
        double creditLimit = calculateCreditLimit(creditScore);
        
        // Create accounts with zero initial balance
        List<Account> accounts = new ArrayList<>();
        accounts.add(new Checkings(checkingNumber, 0.0));
        accounts.add(new Savings(savingsNumber, 0.0));
        accounts.add(new Credit(creditNumber, 0.0, creditLimit));
        
        // Set accounts for customer
        newCustomer.setAccounts(accounts);
        
        // Add to customers map
        customers.put(fullName, newCustomer);
        
        // Log the creation
        logger.logTransaction("Bank Manager created new customer: " + fullName + 
                            " (ID: " + customerId + ")");
        
        return newCustomer;
    }

    /**
     * Generates a unique account number based on type prefix.
     * Format: [type prefix][customer id padded to 3 digits]
     * 
     * @param typePrefix 1 for checking, 2 for savings, 3 for credit
     * @return generated account number
     */
    private String generateAccountNumber(String typePrefix) {
        return typePrefix + String.format("%03d", lastCustomerId);
    }

    /**
     * Calculates credit limit based on credit score ranges.
     * 
     * @param creditScore customer's credit score
     * @return calculated credit limit
     */
    private double calculateCreditLimit(int creditScore) {
        Random random = new Random();
        
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
     * Looks up a customer's accounts by their name.
     * 
     * @param firstName customer's first name
     * @param lastName customer's last name
     * @return list of customer's accounts or empty list if not found
     */
    public List<Account> lookupCustomerByName(String firstName, String lastName) {
        String fullName = firstName + " " + lastName;
        Customer customer = customers.get(fullName);
        
        if (customer != null) {
            logger.logTransaction("Bank Manager looked up accounts for: " + fullName);
            return customer.getAccounts();
        }
        
        return new ArrayList<>();
    }

    /**
     * Looks up an account by account number across all customers.
     * 
     * @param accountNumber the account number to search for
     * @return Optional containing the account if found
     */
    public Optional<Account> lookupAccountByNumber(String accountNumber) {
        for (Customer customer : customers.values()) {
            for (Account account : customer.getAccounts()) {
                if (account.getAccountNumber().equals(accountNumber)) {
                    logger.logTransaction("Bank Manager looked up account: " + accountNumber);
                    return Optional.of(account);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Generates a bank statement for a specific customer.
     * 
     * @param customer the customer to generate statement for
     * @return string containing the formatted bank statement
     */
    public String generateBankStatement(Customer customer) {
        StringBuilder statement = new StringBuilder();
        
        statement.append("BANK STATEMENT\n");
        statement.append("Date: ").append(new Date()).append("\n\n");
        statement.append("Customer: ").append(customer.getName()).append("\n");
        statement.append("Customer ID: ").append(customer.getCustomerID()).append("\n\n");
        
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
import java.io.IOException;
import java.util.*;

/**
 * Implements the Menu interface for bank manager operations.
 * This class provides specific menu functionality for bank managers,
 * including options for customer account inquiries, creating new users,
 * and generating reports.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class BankManagerMenu implements Menu {
    /** The bank manager using this menu */
    private BankManager bankManager;
    
    /** Logger for recording transactions */
    private TransactionLog logger;
    
    /** Scanner for reading user input */
    private Scanner scanner;
    
    /**
     * Constructs a new BankManagerMenu.
     * 
     * @param bankManager the bank manager instance
     * @param logger the transaction logger
     */
    public BankManagerMenu(BankManager bankManager, TransactionLog logger) {
        this.bankManager = bankManager;
        this.logger = logger;
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public void displayMenu() {
        System.out.println("\nBank Manager Menu");
        System.out.println("A. Inquire account by name");
        System.out.println("B. Inquire account by account number");
        System.out.println("C. Create new customer");
        System.out.println("D. Process transaction file");
        System.out.println("E. Generate bank statement");
        System.out.println("F. Return to Main Menu");
        System.out.println("__________________");
    }
    
    @Override
    public boolean handleChoice(String choice) {
        try {
            switch (choice.toUpperCase()) {
                case "A":
                    handleNameInquiry();
                    return true;
                case "B":
                    handleAccountInquiry();
                    return true;
                case "C":
                    handleNewCustomer();
                    return true;
                case "D":
                    handleTransactionFile();
                    return true;
                case "E":
                    handleBankStatement();
                    return true;
                case "F":
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return true;
            }
        } catch (Exception e) {
            System.out.println("Error processing request: " + e.getMessage());
            return true;
        }
    }
    
    @Override
    public String getInput() {
        return scanner.nextLine().trim();
    }
    
    /**
     * Handles customer account inquiry by name.
     * Uses bank manager's interactive search when multiple matches exist.
     */
    private void handleNameInquiry() {
        System.out.println("Enter first name:");
        String firstName = getInput();
        
        System.out.println("Enter last name:");
        String lastName = getInput();
        
        Optional<Customer> customer = bankManager.findCustomerInteractive(firstName, lastName);
        if (customer.isPresent()) {
            displayCustomerAccounts(customer.get());
        } else {
            System.out.println("No customer found with that name.");
        }
    }
    
    /**
     * Handles creation of new customer account.
     * Collects all necessary information and validates before creation.
     */
    private void handleNewCustomer() {
        try {
            Map<String, String> userData = new HashMap<>();
            
            System.out.println("\nEnter new customer details:");
            System.out.println("First Name:");
            userData.put("firstName", getInput());
            
            System.out.println("Last Name:");
            userData.put("lastName", getInput());
            
            // Check if name would create invalid duplicate
            if (!bankManager.createNewCustomer(userData).isPresent()) {
                System.out.println("Error: Cannot create user with this name.");
                return;
            }
            
            System.out.println("Date of Birth (DD-MMM-YY):");
            userData.put("dob", getInput());
            
            System.out.println("Address:");
            userData.put("address", getInput());
            
            System.out.println("City:");
            userData.put("city", getInput());
            
            System.out.println("State:");
            userData.put("state", getInput());
            
            System.out.println("ZIP Code:");
            userData.put("zip", getInput());
            
            System.out.println("Phone Number:");
            userData.put("phone", getInput());
            
            System.out.println("Credit Score (300-850):");
            userData.put("creditScore", getInput());
            
            Optional<Customer> newCustomer = bankManager.createNewCustomer(userData);
            if (newCustomer.isPresent()) {
                System.out.println("\nNew customer created successfully!");
                displayCustomerSummary(newCustomer.get());
            }
            
        } catch (IllegalArgumentException e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }
    }
    
    /**
     * Handles account inquiry by account number.
     */
    private void handleAccountInquiry() {
        System.out.println("Enter account number:");
        String accountNumber = getInput();
        
        Optional<Account> account = bankManager.lookupAccountByNumber(accountNumber);
        if (account.isPresent()) {
            System.out.printf("%s: $%.2f%n", 
                accountNumber, 
                account.get().getBalance());
        } else {
            System.out.println("Account not found.");
        }
    }
    
    /**
     * Handles processing of transaction file.
     */
    private void handleTransactionFile() {
        try {
            System.out.println("Enter transaction file name:");
            String filename = getInput();
            bankManager.processTransactionFile(filename);
        } catch (IOException e) {
            System.out.println("Error processing transaction file: " + e.getMessage());
        }
    }
    
    /**
     * Handles generation of bank statement for a customer.
     */
    private void handleBankStatement() {
        System.out.println("Enter customer first name:");
        String firstName = getInput();
        
        System.out.println("Enter customer last name:");
        String lastName = getInput();
        
        Optional<Customer> customer = bankManager.findCustomerInteractive(firstName, lastName);
        if (customer.isPresent()) {
            String statement = bankManager.generateBankStatement(customer.get());
            System.out.println(statement);
        } else {
            System.out.println("Customer not found.");
        }
    }
    
    /**
     * Displays all accounts for a customer.
     */
    private void displayCustomerAccounts(Customer customer) {
        System.out.println("\nAccounts for " + customer.getName() + ":");
        for (Account account : customer.getAccounts()) {
            System.out.printf("%s (%s): $%.2f%n",
                account.getClass().getSimpleName(),
                account.getAccountNumber(),
                account.getBalance()
            );
        }
    }
    
    /**
     * Displays summary of newly created customer information.
     */
    private void displayCustomerSummary(Customer customer) {
        System.out.println("\nCustomer Summary:");
        System.out.println("Name: " + customer.getName());
        System.out.println("ID: " + customer.getCustomerID());
        System.out.println("\nAccounts Created:");
        displayCustomerAccounts(customer);
    }
}
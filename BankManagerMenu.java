import java.util.*;

/**
 * Implements the Menu interface for bank manager operations.
 * This class provides the specific menu functionality for bank managers,
 * allowing them to perform administrative tasks and customer management.
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
     * Gets first and last name from user and displays all accounts.
     */
    private void handleNameInquiry() {
        System.out.println("Enter first name:");
        String firstName = getInput();
        
        System.out.println("Enter last name:");
        String lastName = getInput();
        
        List<Account> accounts = bankManager.lookupCustomerByName(firstName, lastName);
        if (!accounts.isEmpty()) {
            displayAccounts(accounts);
        } else {
            System.out.println("Customer not found.");
        }
    }
    
    /**
     * Handles account inquiry by account number.
     * Searches all customers for matching account number.
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
     * Handles creation of new customer account.
     * Collects all necessary customer information and creates accounts.
     */
    private void handleNewCustomer() {
        try {
            System.out.println("Enter customer details:");
            
            System.out.println("First Name:");
            String firstName = getInput();
            
            System.out.println("Last Name:");
            String lastName = getInput();
            
            System.out.println("Date of Birth (DD-MMM-YY):");
            String dob = getInput();
            
            System.out.println("Address:");
            String address = getInput();
            
            System.out.println("City:");
            String city = getInput();
            
            System.out.println("State:");
            String state = getInput();
            
            System.out.println("ZIP Code:");
            String zip = getInput();
            
            System.out.println("Phone Number:");
            String phone = getInput();
            
            System.out.println("Credit Score (300-850):");
            int creditScore = Integer.parseInt(getInput());
            
            if (creditScore < 300 || creditScore > 850) {
                throw new IllegalArgumentException("Credit score must be between 300 and 850");
            }
            
            Customer newCustomer = bankManager.createNewCustomer(
                firstName, lastName, dob, address, city, state, zip, phone, creditScore
            );
            
            System.out.println("\nNew customer created successfully!");
            displayCustomerSummary(newCustomer);
            
        } catch (Exception e) {
            System.out.println("Error creating customer: " + e.getMessage());
        }
    }
    
    /**
     * Handles processing of transaction file.
     * Reads and processes transactions from CSV file.
     */
    private void handleTransactionFile() {
        try {
            System.out.println("Enter transaction file name (e.g., Transactions.csv):");
            String filename = getInput();
            bankManager.processTransactionFile(filename);
            System.out.println("Transaction file processed successfully.");
        } catch (Exception e) {
            System.out.println("Error processing transaction file: " + e.getMessage());
        }
    }
    
    /**
     * Handles generation of bank statement for a customer.
     * Gets customer name and generates comprehensive statement.
     */
    private void handleBankStatement() {
        System.out.println("Enter customer first name:");
        String firstName = getInput();
        
        System.out.println("Enter customer last name:");
        String lastName = getInput();
        
        List<Account> accounts = bankManager.lookupCustomerByName(firstName, lastName);
        if (!accounts.isEmpty()) {
            String statement = bankManager.generateBankStatement(accounts.get(0).getOwner());
            System.out.println(statement);
        } else {
            System.out.println("Customer not found.");
        }
    }
    
    /**
     * Helper method to display account information.
     * 
     * @param accounts list of accounts to display
     */
    private void displayAccounts(List<Account> accounts) {
        for (Account account : accounts) {
            System.out.printf("%s (%s): $%.2f%n",
                account.getClass().getSimpleName(),
                account.getAccountNumber(),
                account.getBalance()
            );
        }
    }
    
    /**
     * Displays summary of newly created customer information.
     * 
     * @param customer the newly created customer
     */
    private void displayCustomerSummary(Customer customer) {
        System.out.println("\nCustomer Summary:");
        System.out.println("Name: " + customer.getName());
        System.out.println("ID: " + customer.getCustomerID());
        System.out.println("\nAccounts Created:");
        displayAccounts(customer.getAccounts());
    }
}
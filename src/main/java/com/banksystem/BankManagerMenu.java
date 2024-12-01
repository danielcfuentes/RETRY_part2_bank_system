package com.banksystem;
import java.util.*;
import java.io.IOException;
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
        System.out.println("\nBank Manager Options:");
        System.out.println("A. Look Up Customer Account by Name");
        System.out.println("B. Look Up Account by Account Number");
        System.out.println("C. Create New Customer Account");
        System.out.println("D. Process Transaction File");
        System.out.println("E. Generate Customer Bank Statement");
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
        System.out.println("Enter the first name of customer:");
        String firstName = getInput();
        
        System.out.println("Enter the last name of customer:");
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
        
        //get name information first
        System.out.println("First Name:");
        String firstName = getInput();
        System.out.println("Last Name:");
        String lastName = getInput();
        
        //check for name conflicts before proceeding
        if (!bankManager.newUsersHandler.isValidNewCustomerName(firstName, lastName)) {
            System.out.println("Error: A customer with same first AND last name already exists,");
            System.out.println("or multiple customers with same first AND last name would exist.");
            return;
        }
        
        //store name data
        userData.put("firstName", firstName);
        userData.put("lastName", lastName);
        
        //get remaining required information
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
        
        System.out.println("Phone Number (XXX-XXX-XXXX):");
        userData.put("phone", getInput());
        
        //get and validate credit score
        while (true) {
            try {
                System.out.println("Credit Score (300-850):");
                String creditScore = getInput();
                int score = Integer.parseInt(creditScore);
                if (score >= 300 && score <= 850) {
                    userData.put("creditScore", creditScore);
                    break;
                } else {
                    System.out.println("Credit score must be between 300 and 850. Please try again.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number for credit score.");
            }
        }
        
        //create the customer with complete data
        Optional<Customer> newCustomer = bankManager.createNewCustomer(userData);
        if (newCustomer.isPresent()) {
            System.out.println("\nNew customer created successfully!");
            displayCustomerSummary(newCustomer.get());
            
            //log the creation
            logger.logTransaction(
                String.format("Created new customer: %s %s (ID: %s)", 
                    firstName, lastName, newCustomer.get().getCustomerID()),
                "Bank Manager"
            );
        } else {
            System.out.println("Failed to create customer. Please try again.");
        }
        
    } catch (IllegalArgumentException e) {
        System.out.println("Error creating customer: " + e.getMessage());
    }
}
    
    /**
     * Handles account inquiry by account number.
     */
    private void handleAccountInquiry() {
        System.out.println("Enter a customer account number:");
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
            System.out.println("\nProcessing Transactions.csv...");
            bankManager.processTransactionFile();  // No filename parameter needed
            System.out.println("Transaction processing complete.");
        } catch (Exception e) {
            System.out.println("Error processing transactions: " + e.getMessage());
        }
    }
    
    /**
     * Handles generation of bank statement for a customer.
     */
    private void handleBankStatement() {
        try {
            System.out.println("\nSearch Customer by:");
            System.out.println("1. Customer Name");
            System.out.println("2. Customer ID");
            String choice = getInput();
            
            Customer customer = null;
            
            switch (choice) {
                case "1":
                    System.out.println("Enter customer full name:");
                    String name = getInput();
                    String[] nameParts = name.split(" ");
                    if (nameParts.length == 2) {
                        Optional<Customer> found = bankManager.findCustomerInteractive(
                            nameParts[0], nameParts[1]);
                        if (found.isPresent()) {
                            customer = found.get();
                        }
                    } else {
                        System.out.println("Please enter both first and last name.");
                        return;
                    }
                    break;
                    
                case "2":
                    System.out.println("Enter customer ID:");
                    String id = getInput();
                    //search for customer by ID
                    for (Customer c : bankManager.getCustomers().values()) {
                        if (c.getCustomerID().equals(id)) {
                            customer = c;
                            break;
                        }
                    }
                    break;
                    
                default:
                    System.out.println("Invalid choice.");
                    return;
            }
            
            if (customer == null) {
                System.out.println("Customer not found.");
                return;
            }
            
            String filename = bankManager.generateBankStatement(customer);
            System.out.println("\nBank statement generated successfully!");
            System.out.println("File saved as: " + filename);
            
        } catch (Exception e) {
            System.out.println("Error generating bank statement: " + e.getMessage());
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
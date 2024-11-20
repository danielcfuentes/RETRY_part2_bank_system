package com.banksystem;
import java.util.*;
import java.io.*;

/**
 * Handles the processing of transaction files for the banking system.
 * This class reads transactions from a CSV file and executes them using
 * existing bank functionality from other classes.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class TransactionProcessor {

    /** A map of customer names to their respective customer objects. */
    private Map<String, Customer> customers;

    /** A logger to record transaction details. */
    private TransactionLog logger;

    /** The file path for the transaction file. */
    private static final String TRANSACTION_FILE = "Transactions.csv";

    /**
     * Creates a new instance of the `TransactionProcessor` class.
     * 
     * @param customers a map of customer names to their respective customer objects
     * @param logger a `TransactionLog` instance for logging transactions
     */
    public TransactionProcessor(Map<String, Customer> customers, TransactionLog logger) {
        this.customers = customers;
        this.logger = logger;
    }


    /**
     * Reads the transaction file and processes each transaction.
     * Prints the results of the transaction processing to the console.
     */
    public void processTransactionFile() {
        try {
            String fileContent = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(TRANSACTION_FILE)));
            String[] lines = fileContent.split("\n");
            
            System.out.println("Starting transaction processing...\n");
            int processedCount = 0;
            int failedCount = 0;
            
            //skip header
            for (int i = 1; i < lines.length; i++) {
                String line = lines[i].trim();
                if (line.isEmpty()) continue;
                
                try {
                    System.out.printf("\nProcessing line %d: %s%n", i + 1, line);
                    processTransaction(line.split(","), i + 1);
                    processedCount++;
                    System.out.println("Transaction successful!");
                } catch (Exception e) {
                    System.out.printf("Transaction failed at line %d: %s%n", i + 1, e.getMessage());
                    failedCount++;
                }
            }
            
            System.out.printf("\nTransaction processing complete.%n");
            System.out.printf("Successfully processed: %d%n", processedCount);
            System.out.printf("Failed transactions: %d%n", failedCount);
            
        } catch (IOException e) {
            System.out.println("Error reading " + TRANSACTION_FILE + ": " + e.getMessage());
        }
    }

    /**
     * Processes a single transaction based on the parsed CSV fields.
     * 
     * @param parts the array of CSV fields representing the transaction
     * @param lineNumber the line number in the file for error reporting
     * @throws IllegalArgumentException if the transaction action is invalid
     */
    private void processTransaction(String[] parts, int lineNumber) {
        String action = parts[3].trim().toLowerCase();
        
        switch (action) {
            case "pays":
                handlePay(parts, lineNumber);
                break;
            case "transfers":
                handleTransfer(parts, lineNumber);
                break;
            case "inquires":
                handleInquire(parts, lineNumber);
                break;
            case "withdraws":
                handleWithdraw(parts, lineNumber);
                break;
            case "deposits":
                handleDeposit(parts, lineNumber);
                break;
            default:
                throw new IllegalArgumentException("Unknown action: " + action);
        }
    }

    /**
     * Handles "pay" transactions between two customers.
     * 
     * @param parts the array of CSV fields representing the transaction
     * @param lineNumber the line number in the file for error reporting
     * @throws IllegalArgumentException if the transaction is invalid
     */
    private void handlePay(String[] parts, int lineNumber) {
        Customer fromCustomer = findCustomer(parts[0], parts[1]);
        Customer toCustomer = findCustomer(parts[4], parts[5]);
        
        //validate that customers are different
        if (fromCustomer.getName().equals(toCustomer.getName())) {
            throw new IllegalArgumentException("Cannot pay yourself");
        }
        
        Account fromAccount = findAccount(fromCustomer, parts[2]);
        Account toAccount = findAccount(toCustomer, parts[6]);
        double amount = parseAmount(parts[7]);
        
        //validate amount won't cause negative balance
        if (fromAccount.getBalance() - amount < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        //validate credit payment amount
        if (toAccount instanceof Credit) {
            Credit creditAccount = (Credit) toAccount;
            if (Math.abs(creditAccount.getBalance()) < amount) {
                throw new IllegalArgumentException(
                    "Cannot pay more than credit card balance"
                );
            }
        }
        
        fromCustomer.pay(toCustomer, fromAccount, toAccount, amount);
    }

    /**
     * Handles "transfer" transactions within the same customer's accounts.
     * 
     * @param parts the array of CSV fields representing the transaction
     * @param lineNumber the line number in the file for error reporting
     * @throws IllegalArgumentException if the transaction is invalid
     */
    private void handleTransfer(String[] parts, int lineNumber) {
        Customer customer = findCustomer(parts[0], parts[1]);
        Account fromAccount = findAccount(customer, parts[2]);
        Account toAccount = findAccount(customer, parts[6]);
        double amount = parseAmount(parts[7]);
        
        //validate accounts are different
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new IllegalArgumentException(
                "Cannot transfer to the same account"
            );
        }
        
        //validate amount won't cause negative balance
        if (fromAccount.getBalance() - amount < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        //validate credit transfer amount
        if (toAccount instanceof Credit) {
            Credit creditAccount = (Credit) toAccount;
            if (Math.abs(creditAccount.getBalance()) < amount) {
                throw new IllegalArgumentException(
                    "Cannot transfer more than credit card balance"
                );
            }
        }
        
        if (fromAccount instanceof Savings) {
            ((Savings) fromAccount).tansfer(toAccount, amount);
        } else if (fromAccount instanceof Checkings) {
            ((Checkings) fromAccount).tansfer(toAccount, amount);
        } else {
            throw new IllegalArgumentException("Invalid account type for transfer");
        }
    }

    /**
     * Handles "withdraw" transactions from a customer's account.
     * 
     * @param parts the array of CSV fields representing the transaction
     * @param lineNumber the line number in the file for error reporting
     * @throws IllegalArgumentException if the transaction is invalid
     */
    private void handleWithdraw(String[] parts, int lineNumber) {
        Customer customer = findCustomer(parts[0], parts[1]);
        Account account = findAccount(customer, parts[2]);
        double amount = parseAmount(parts[7]);
        
        //validate amount won't cause negative balance
        if (account.getBalance() - amount < 0) {
            throw new IllegalArgumentException(
                String.format("Insufficient funds (balance: $%.2f, withdrawal: $%.2f)",
                    account.getBalance(), amount)
            );
        }
        
        account.withdraw(amount);
    }

    /**
     * Handles "deposit" transactions into a customer's account.
     * 
     * @param parts the array of CSV fields representing the transaction
     * @param lineNumber the line number in the file for error reporting
     * @throws IllegalArgumentException if the transaction is invalid
     */
    private void handleDeposit(String[] parts, int lineNumber) {
        Customer customer = findCustomer(parts[4], parts[5]);
        Account account = findAccount(customer, parts[6]);
        double amount = parseAmount(parts[7]);
        
        //special handling for credit account deposits
        if (account instanceof Credit) {
            Credit creditAccount = (Credit) account;
            if (Math.abs(creditAccount.getBalance()) < amount) {
                throw new IllegalArgumentException(
                    String.format("Cannot deposit more than credit balance (balance: $%.2f, deposit: $%.2f)",
                        Math.abs(creditAccount.getBalance()), amount)
                );
            }
        }
        
        account.deposit(amount);
    }


    /**
     * Handles "inquire" transactions to retrieve account balances.
     * 
     * @param parts the array of CSV fields representing the transaction
     * @param lineNumber the line number in the file for error reporting
     */
    private void handleInquire(String[] parts, int lineNumber) {
        Customer customer = findCustomer(parts[0], parts[1]);
        Account account = findAccount(customer, parts[2]);
        double balance = account.inquireBalance();
        System.out.printf("Balance for %s's %s: $%.2f%n",
            customer.getName(), parts[2], balance);
    }

    /**
     * Finds a customer based on their first and last name.
     * 
     * @param firstName the customer's first name
     * @param lastName the customer's last name
     * @return the `Customer` object associated with the name
     * @throws IllegalArgumentException if the customer does not exist
     */
    private Customer findCustomer(String firstName, String lastName) {
        String fullName = firstName.trim() + " " + lastName.trim();
        Customer customer = customers.get(fullName);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + fullName);
        }
        return customer;
    }

    /**
     * Finds an account for a customer based on the account type.
     * 
     * @param customer the `Customer` object
     * @param accountType the type of account (e.g., "Checking", "Savings", "Credit")
     * @return the `Account` object corresponding to the account type
     * @throws IllegalArgumentException if the account type is invalid or not found
     */
    private Account findAccount(Customer customer, String accountType) {
        String type = accountType.trim().toLowerCase();
        
        for (Account account : customer.getAccounts()) {
            String className = account.getClass().getSimpleName().toLowerCase();
            
            if ((className.equals("checkings") || className.equals("checking")) && 
                (type.equals("checking") || type.equals("checkings"))) {
                return account;
            }
            
            if ((className.equals("savings") || className.equals("saving")) && 
                (type.equals("saving") || type.equals("savings"))) {
                return account;
            }
            
            if (className.equals("credit") && type.equals("credit")) {
                return account;
            }
        }
        
        throw new IllegalArgumentException(
            String.format("Account type %s not found for customer %s (Available accounts: %s)", 
                accountType, 
                customer.getName(),
                customer.getAccounts().stream()
                    .map(acc -> acc.getClass().getSimpleName())
                    .collect(java.util.stream.Collectors.joining(", "))
            )
        );
    }

    /**
     * Parses a string representing a monetary amount into a double.
     * 
     * @param amountStr the string representation of the amount
     * @return the parsed amount as a double
     * @throws IllegalArgumentException if the amount is not valid or is non-positive
     */
    private double parseAmount(String amountStr) {
        try {
            double amount = Double.parseDouble(amountStr.trim());
            if (amount <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            return amount;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid amount format: " + amountStr);
        }
    }
}
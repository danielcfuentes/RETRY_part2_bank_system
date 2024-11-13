import java.util.*;
import java.io.*;

/**
 * Handles the processing of transaction files for the banking system.
 * This class reads transactions from a CSV file and executes them using
 * existing bank functionality from other classes.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
/**
 * Handles processing of transaction files with enhanced validation and error reporting.
 */
public class TransactionProcessor {
    private Map<String, Customer> customers;
    private TransactionLog logger;
    private static final String TRANSACTION_FILE = "Transactions.csv";

    public TransactionProcessor(Map<String, Customer> customers, TransactionLog logger) {
        this.customers = customers;
        this.logger = logger;
    }

    public void processTransactionFile() {
        try {
            String fileContent = new String(java.nio.file.Files.readAllBytes(
                java.nio.file.Paths.get(TRANSACTION_FILE)));
            String[] lines = fileContent.split("\n");
            
            System.out.println("Starting transaction processing...\n");
            int processedCount = 0;
            int failedCount = 0;
            
            // Skip header (i starts at 1 for line numbers to match CSV)
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

    private void handlePay(String[] parts, int lineNumber) {
        Customer fromCustomer = findCustomer(parts[0], parts[1]);
        Customer toCustomer = findCustomer(parts[4], parts[5]);
        
        // Validate that customers are different
        if (fromCustomer.getName().equals(toCustomer.getName())) {
            throw new IllegalArgumentException("Cannot pay yourself");
        }
        
        Account fromAccount = findAccount(fromCustomer, parts[2]);
        Account toAccount = findAccount(toCustomer, parts[6]);
        double amount = parseAmount(parts[7]);
        
        // Validate amount won't cause negative balance
        if (fromAccount.getBalance() - amount < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        // Validate credit payment amount
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

    private void handleTransfer(String[] parts, int lineNumber) {
        Customer customer = findCustomer(parts[0], parts[1]);
        Account fromAccount = findAccount(customer, parts[2]);
        Account toAccount = findAccount(customer, parts[6]);
        double amount = parseAmount(parts[7]);
        
        // Validate accounts are different
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new IllegalArgumentException(
                "Cannot transfer to the same account"
            );
        }
        
        // Validate amount won't cause negative balance
        if (fromAccount.getBalance() - amount < 0) {
            throw new IllegalArgumentException("Insufficient funds");
        }
        
        // Validate credit transfer amount
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

    private void handleWithdraw(String[] parts, int lineNumber) {
        Customer customer = findCustomer(parts[0], parts[1]);
        Account account = findAccount(customer, parts[2]);
        double amount = parseAmount(parts[7]);
        
        // Validate amount won't cause negative balance
        if (account.getBalance() - amount < 0) {
            throw new IllegalArgumentException(
                String.format("Insufficient funds (balance: $%.2f, withdrawal: $%.2f)",
                    account.getBalance(), amount)
            );
        }
        
        account.withdraw(amount);
    }

    private void handleDeposit(String[] parts, int lineNumber) {
        Customer customer = findCustomer(parts[4], parts[5]);
        Account account = findAccount(customer, parts[6]);
        double amount = parseAmount(parts[7]);
        
        // Special handling for credit account deposits
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

    private void handleInquire(String[] parts, int lineNumber) {
        Customer customer = findCustomer(parts[0], parts[1]);
        Account account = findAccount(customer, parts[2]);
        double balance = account.inquireBalance();
        System.out.printf("Balance for %s's %s: $%.2f%n",
            customer.getName(), parts[2], balance);
    }

    private Customer findCustomer(String firstName, String lastName) {
        String fullName = firstName.trim() + " " + lastName.trim();
        Customer customer = customers.get(fullName);
        if (customer == null) {
            throw new IllegalArgumentException("Customer not found: " + fullName);
        }
        return customer;
    }

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
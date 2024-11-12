import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

/**
 * Handles the generation of transaction history files for customers.
 * Creates formatted txt files containing:
 * - Account information
 * - Starting balances (session start)
 * - Current balances
 * - All transactions for the current session
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class TransactionHistory {
    /** Directory where transaction history files are stored */
    private static final String HISTORY_DIR = "transaction_histories/";
    
    /** Customer whose history is being generated */
    private Customer customer;
    
    /** Transaction log containing all transactions */
    private TransactionLog transactionLog;
    
    /** Date/time formatter for the report */
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a new transaction history generator.
     * 
     * @param customer the customer requesting history
     * @param transactionLog the transaction log to get data from
     */
    public TransactionHistory(Customer customer, TransactionLog transactionLog) {
        this.customer = customer;
        this.transactionLog = transactionLog;
        
        // Ensure history directory exists
        new File(HISTORY_DIR).mkdirs();
    }

    /**
     * Generates a transaction history file for the current session.
     * Creates a new file with the format: customerID_YYYYMMDD_HHMMSS.txt
     * 
     * @return the generated file name
     * @throws IOException if file creation fails
     */
    public String generateHistoryFile() throws IOException {
        // Generate filename with timestamp
        String fileName = String.format("%s_%s.txt",
            customer.getCustomerID(),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        );
        
        String fullPath = HISTORY_DIR + fileName;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fullPath))) {
            // Write header
            writeHeader(writer);
            
            // Write account information
            writeAccountInformation(writer);
            
            // Write transactions
            writeTransactions(writer);
            
            writer.flush();
        }
        
        return fileName;
    }

    /**
     * Writes the header section of the history file.
     * Includes customer information and dates.
     */
    private void writeHeader(PrintWriter writer) {
        writer.println("TRANSACTION HISTORY FOR: " + customer.getName());
        writer.println("Customer ID: " + customer.getCustomerID());
        writer.println("Date Generated: " + LocalDateTime.now().format(formatter));
        writer.println("Session Start: " + transactionLog.getSessionStartTime().format(formatter));
        writer.println("\n" + "=".repeat(50) + "\n");
    }

    /**
     * Writes account information section.
     * Shows starting and current balances for all accounts.
     */
    private void writeAccountInformation(PrintWriter writer) {
        writer.println("ACCOUNT INFORMATION:");
        
        for (Account account : customer.getAccounts()) {
            writer.println(account.getClass().getSimpleName() + " (" + 
                         account.getAccountNumber() + "):");
            
            double startingBalance = transactionLog.getStartingBalance(
                customer.getName(), account.getAccountNumber());
            
            writer.printf("  Starting Balance: $%.2f%n", startingBalance);
            writer.printf("  Current Balance:  $%.2f%n", account.getBalance());
            writer.println();
        }
        
        writer.println("=" .repeat(50) + "\n");
    }

    /**
     * Writes all transactions for the current session.
     * Only includes transactions for this customer.
     */
    private void writeTransactions(PrintWriter writer) {
        writer.println("SESSION TRANSACTIONS:");
        
        List<String> transactions = transactionLog.getCustomerSessionTransactions(customer.getName());
        
        if (transactions.isEmpty()) {
            writer.println("No transactions in current session.");
        } else {
            for (String transaction : transactions) {
                writer.println(transaction);
            }
        }
        
        writer.println("\n" + "=".repeat(50));
        writer.println("End of Transaction History");
    }
}
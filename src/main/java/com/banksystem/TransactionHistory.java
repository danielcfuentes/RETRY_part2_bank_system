package com.banksystem;
import java.io.*;
import java.util.*;
import java.time.*;
import java.time.format.*;

/**
 * Handles the generation of transaction history files for customers.
 * Creates formatted txt files containing session-specific information.
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
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd_HHmmss");
    
    /** Date/time formatter for display in the report */
    private DateTimeFormatter displayFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * Creates a new transaction history generator.
     * 
     * @param customer the customer requesting history
     * @param transactionLog the transaction log to get data from
     */
    public TransactionHistory(Customer customer, TransactionLog transactionLog) {
        this.customer = customer;
        this.transactionLog = transactionLog;
        new File(HISTORY_DIR).mkdirs();
    }

    /**
     * Generates a transaction history file for the current session.
     * Creates a new file with the format: FirstName_LastName_Transactions_YYYYMMDD_HHMMSS.txt
     * 
     * @return the generated file name
     * @throws IOException if file creation fails
     */
    public String generateHistoryFile() throws IOException {
        //get customer's first and last name
        String[] nameParts = customer.getName().split(" ");
        String firstName = nameParts[0];
        String lastName = nameParts[1];
        
        //generate filename with timestamp
        String fileName = String.format("%s_%s_Transactions_%s.txt",
            firstName,
            lastName,
            LocalDateTime.now().format(formatter)
        );
        
        String fullPath = HISTORY_DIR + fileName;
        
        try (PrintWriter writer = new PrintWriter(new FileWriter(fullPath))) {
            //write header
            writeHeader(writer);
            
            //write account information
            writeAccountInformation(writer);
            
            //write transactions
            writeTransactions(writer);
            
            writer.flush();
        }
        
        return fileName;
    }

    /**
     * Writes the header section of the history file.
     * @param writer the PrintWriter to write to
     */
    private void writeHeader(PrintWriter writer) {
        writer.println("TRANSACTION HISTORY FOR: " + customer.getName());
        writer.println("Customer ID: " + customer.getCustomerID());
        writer.println("Date Generated: " + LocalDateTime.now().format(displayFormatter));
        writer.println("Session Start: " + transactionLog.getSessionStartTime().format(displayFormatter));
        writer.println("\n" + new String(new char[50]).replace("\0", "=") + "\n");
    }

    /**
     * Writes account information section with starting and current balances.
     * @param writer the PrintWriter to write to
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
        
        writer.println(new String(new char[50]).replace("\0", "=") + "\n");
    }

    /**
     * Writes all transactions for the current session.
     * @param writer the PrintWriter to write to
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
        
        writer.println("\n" + new String(new char[50]).replace("\0", "="));
        writer.println("End of Transaction History");
    }
}
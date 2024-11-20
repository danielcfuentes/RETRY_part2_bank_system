package com.banksystem;
import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates bank statements for customers including personal information,
 * account details, and transaction history.
 * 
 * @author Daniel Camilo Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class BankStatement {
    /** Directory of where we are storing the bank statements */
    private static final String STATEMENT_DIR = "bank_statements/";

    /** Customer for whom the statement is being generated */
    private Customer customer;

    /** Transaction log for the bank */
    private TransactionLog logger;

    /** Map to store starting balances for each account */
    private Map<String, Double> startingBalances;

    /**
     * Constructs a new BankStatement object.
     * @param customer the customer for whom the statement is being generated
     * @param logger the transaction log for the bank
     */
    public BankStatement(Customer customer, TransactionLog logger) {
        this.customer = customer;
        this.logger = logger;
        this.startingBalances = new HashMap<>();
        
        //create directory if it doesn't exist
        new File(STATEMENT_DIR).mkdirs();
    }

    /**
     * Generates a bank statement file for the customer.
     * @return filename of generated statement
     * @throws IOException if an I/O error occurs
     */
    public String generateStatement() throws IOException {
        String filename = String.format("%s%s_statement_%s.txt",
            STATEMENT_DIR,
            customer.getName().replace(" ", "_"),
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"))
        );

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writeHeader(writer);
            writeCustomerInfo(writer);
            writeAccountSummary(writer);
            writeTransactionHistory(writer);
            writeFooter(writer);
        }

        return filename;
    }

    /**
     * Writes the header of the bank statement.
     * @param writer the PrintWriter to write the header to
     */
    private void writeHeader(PrintWriter writer) {
        writer.println("=".repeat(80));
        writer.println("                       EL PASO MINERS BANK");
        writer.println("                        ACCOUNT STATEMENT");
        writer.println("=".repeat(80));
        writer.println();
        writer.printf("Statement Date: %s%n", 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy HH:mm:ss")));
        writer.println();
    }

    /**
     * Writes the customer information to the bank statement.
     * @param writer the PrintWriter to write the customer information to
     */
    private void writeCustomerInfo(PrintWriter writer) {
        writer.println("-".repeat(80));
        writer.println("CUSTOMER INFORMATION");
        writer.println("-".repeat(80));
        writer.printf("Customer ID: %s%n", customer.getCustomerID());
        writer.printf("Name: %s%n", customer.getName());
        writer.printf("Address: %s%n", customer.getAddress());
        writer.printf("Phone: %s%n", customer.getPhoneNumber());
        writer.println();
    }

    /**
     * Writes the account summary to the bank statement.
     * @param writer the PrintWriter to write the account summary to
     */
    private void writeAccountSummary(PrintWriter writer) {
        writer.println("-".repeat(80));
        writer.println("ACCOUNT SUMMARY");
        writer.println("-".repeat(80));
        
        for (Account account : customer.getAccounts()) {
            String accountType = account.getClass().getSimpleName();
            
            writer.printf("%s Account (%s)%n", 
                accountType, account.getAccountNumber());
                
            if (account instanceof Credit) {
                Credit creditAccount = (Credit) account;
                writer.printf("Credit Limit: $%.2f%n", creditAccount.getCreditLimit());
                writer.printf("Current Balance: $%.2f%n", account.getBalance());
                writer.printf("Available Credit: $%.2f%n", 
                    creditAccount.getCreditLimit() + account.getBalance());
            } else {
                writer.printf("Current Balance: $%.2f%n", account.getBalance());
            }
            writer.println();
        }
        writer.println();
    }

    /**
     * Writes the transaction history to the bank statement.
     * @param writer the PrintWriter to write the transaction history to
     */
    private void writeTransactionHistory(PrintWriter writer) {
        writer.println("-".repeat(80));
        writer.println("TRANSACTION HISTORY");
        writer.println("-".repeat(80));
        
        List<String> allTransactions = logger.getCustomerTransactions(customer.getName());
        List<String> relevantTransactions = new ArrayList<>();
        
        //filter transactions to only include ones directly involving this customer
        for (String transaction : allTransactions) {
            //check for the exact customer name (to avoid partial matches)
            if (transaction.matches(".*\\b" + customer.getName() + "\\b.*") &&
                !transaction.contains("Bank Manager") &&
                !transaction.contains("created new customer")) {
                
                relevantTransactions.add(transaction);
            }
        }
        
        if (relevantTransactions.isEmpty()) {
            writer.println("No transactions found for this customer.");
        } else {
            //sort transactions by date if they have timestamps
            relevantTransactions.sort((a, b) -> {
                try {
                    String dateA = a.substring(0, 19);
                    String dateB = b.substring(0, 19);
                    return dateA.compareTo(dateB);
                } catch (Exception e) {
                    return a.compareTo(b);
                }
            });
            
            for (String transaction : relevantTransactions) {
                writer.println(transaction);
            }
        }
        writer.println();
    }

    /**
     * Writes the footer of the bank statement.
     * @param writer the PrintWriter to write the footer to
     */
    private void writeFooter(PrintWriter writer) {
        writer.println("=".repeat(80));
        writer.println("                    End of Statement");
        writer.println("=".repeat(80));
        writer.println();
        writer.println("For questions or concerns:");
        writer.println("Call: 1-800-MINERS-BANK");
        writer.println("Email: support@elpasominersbank.com");
    }
}
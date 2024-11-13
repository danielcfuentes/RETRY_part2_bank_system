import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Generates bank statements for customers including personal information,
 * account details, and transaction history.
 */
public class BankStatement {
    private static final String STATEMENT_DIR = "bank_statements/";
    private Customer customer;
    private TransactionLog logger;
    private Map<String, Double> startingBalances;

    public BankStatement(Customer customer, TransactionLog logger) {
        this.customer = customer;
        this.logger = logger;
        this.startingBalances = new HashMap<>();
        
        // Create directory if it doesn't exist
        new File(STATEMENT_DIR).mkdirs();
    }

    /**
     * Generates a bank statement file for the customer.
     * @return filename of generated statement
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

    private void writeTransactionHistory(PrintWriter writer) {
        writer.println("-".repeat(80));
        writer.println("TRANSACTION HISTORY");
        writer.println("-".repeat(80));
        
        List<String> allTransactions = logger.getCustomerTransactions(customer.getName());
        List<String> relevantTransactions = new ArrayList<>();
        
        // Filter transactions to only include ones directly involving this customer
        for (String transaction : allTransactions) {
            // Check for the exact customer name (to avoid partial matches)
            if (transaction.matches(".*\\b" + customer.getName() + "\\b.*") &&
                !transaction.contains("Bank Manager") &&  // Exclude bank manager logs
                !transaction.contains("created new customer")) {  // Exclude customer creation logs
                
                relevantTransactions.add(transaction);
            }
        }
        
        if (relevantTransactions.isEmpty()) {
            writer.println("No transactions found for this customer.");
        } else {
            // Sort transactions by date if they have timestamps
            relevantTransactions.sort((a, b) -> {
                try {
                    String dateA = a.substring(0, 19); // "yyyy-MM-dd HH:mm:ss"
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

    private void writeFooter(PrintWriter writer) {
        writer.println("=".repeat(80));
        writer.println("                    End of Statement");
        writer.println("=".repeat(80));
        writer.println();
        writer.println("For questions or concerns:");
        writer.println("Call: 1-800-MINERS-BANK");
        writer.println("Visit: www.elpasominersbank.com");
        writer.println("Email: support@elpasominersbank.com");
    }
}
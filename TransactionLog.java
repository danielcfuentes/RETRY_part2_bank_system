import java.io.*;
import java.util.*;
import java.time.*;

/**
 * Handles logging of all banking transactions.
 * Maintains record of activities in the system and tracks session information.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class TransactionLog {
    /** File that stores all activities of the system */
    private static final String LOG_FILE = "transaction_log.txt";
    
    /** List storing all transaction log entries */
    private List<String> currentLogs;
    
    /** Time when the current session started */
    private LocalDateTime sessionStartTime;
    
    /** Map to store starting balances for the session */
    private Map<String, Map<String, Double>> sessionStartBalances;

    /**
     * Creates a new transaction logger.
     * Initializes session tracking and loads existing logs.
     */
    public TransactionLog() {
        this.currentLogs = new ArrayList<>();
        this.sessionStartTime = LocalDateTime.now();
        this.sessionStartBalances = new HashMap<>();
        loadExistingLogs();
    }

    /**
     * Records starting balances for a customer's session.
     * Should be called when customer logs in.
     * 
     * @param customerName the customer's name
     * @param accounts list of customer's accounts
     */
    public void recordSessionStart(String customerName, List<Account> accounts) {
        Map<String, Double> balances = new HashMap<>();
        for (Account account : accounts) {
            balances.put(account.getAccountNumber(), account.getBalance());
        }
        sessionStartBalances.put(customerName, balances);
    }

    /**
     * Gets the starting balance for a specific account in the current session.
     * 
     * @param customerName the customer's name
     * @param accountNumber the account number
     * @return the starting balance or 0 if not found
     */
    public double getStartingBalance(String customerName, String accountNumber) {
        Map<String, Double> balances = sessionStartBalances.get(customerName);
        if (balances != null) {
            return balances.getOrDefault(accountNumber, 0.0);
        }
        return 0.0;
    }

    /**
     * Gets all transactions for a specific customer in the current session.
     * 
     * @param customerName the customer's name
     * @return list of transaction strings
     */
    public List<String> getCustomerSessionTransactions(String customerName) {
        List<String> customerTransactions = new ArrayList<>();
        
        for (String log : currentLogs) {
            // Only include logs after session start that mention the customer
            if (log.contains(customerName)) {
                customerTransactions.add(log);
            }
        }
        
        return customerTransactions;
    }

    /**
     * Gets the time when the current session started.
     * @return session start time
     */
    public LocalDateTime getSessionStartTime() {
        return sessionStartTime;
    }

    // Original methods remain the same
    public void logTransaction(String transaction) {
        String timestamp = LocalDateTime.now().format(
            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String logEntry = timestamp + " - " + transaction;
        currentLogs.add(logEntry);
        
        try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
            writer.write(logEntry + "\n");
        } catch (IOException e) {
            System.err.println("Error writing to transaction log: " + e.getMessage());
        }
    }

    private void loadExistingLogs() {
        try {
            File file = new File(LOG_FILE);
            if (file.exists()) {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while ((line = reader.readLine()) != null) {
                    currentLogs.add(line);
                }
                reader.close();
            } else {
                file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println("Error loading transaction log: " + e.getMessage());
        }
    }

    public void exitUpdate() {
        try (FileWriter writer = new FileWriter(LOG_FILE)) {
            for (String log : currentLogs) {
                writer.write(log + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error updating transaction log on exit: " + e.getMessage());
        }
    }
}
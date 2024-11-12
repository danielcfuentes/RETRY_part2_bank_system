import java.util.*;

/**
 * Main class for the banking application.
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.1
 */
public class RunBank {
    /**
     * Main starting point for the banking application.
     * Initializes the system, presents menu options, and handles user interaction.
     */
    public static void main(String[] args) {
        /** Stores all customer data */
        Map<String, Customer> customers;
        /** Handles user input */
        Scanner scanner = new Scanner(System.in);
        /** Records all transactions */
        TransactionLog logger = new TransactionLog();
        /** Manages CSV file operations */
        CSVHandler csvHandler = new CSVHandler();
        
        try {
            // Load initial data
            customers = csvHandler.loadCustomerData();
            BankOperations operations = new BankOperations(customers, logger);

            // Main menu loop
            boolean exitProgram = false;
            while (!exitProgram) {
                System.out.println("\nWelcome to El Paso Miners Bank");
                System.out.println("1. Individual Customer");
                System.out.println("2. Bank Manager");
                System.out.println("Type 'EXIT' to quit");
                System.out.println("_________________________");
                
                String choice = scanner.nextLine().trim();
                
                if (choice.equalsIgnoreCase("EXIT")) {
                    // Handle program exit
                    System.out.println("\nSaving updated customer data...");
                    csvHandler.saveCustomerData(customers);
                    logger.exitUpdate();
                    System.out.println("____________________");
                    System.out.println("Thank you for using El Paso Miners Bank!");
                    exitProgram = true;
                } else {
                    switch (choice) {
                        case "1":
                            operations.handleCustomer();
                            break;
                        case "2":
                            operations.handleBankManager();
                            break;
                        default:
                            System.out.println("Invalid choice. Please try again.");
                    }
                }
            }
            
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
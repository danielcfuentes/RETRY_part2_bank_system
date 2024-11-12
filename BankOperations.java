import java.util.*;

/**
 * Manages all banking operations and coordinates between users and the system.
 * This class acts as a facade, simplifying the interface between the UI and
 * the business logic of the banking system.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class BankOperations {
    /** Map of customer names to Customer objects */
    private Map<String, Customer> customers;
    
    /** Scanner for reading user input */
    private Scanner scanner;
    
    /** Logger for recording transactions */
    private TransactionLog logger;
    
    /** Bank manager instance */
    private BankManager bankManager;
    
    /** Current active menu */
    private Menu currentMenu;

    /**
     * Initializes bank operations with customer data and transaction logging.
     * 
     * @param customers map of customer names to Customer objects
     * @param logger transaction logging system
     */
    public BankOperations(Map<String, Customer> customers, TransactionLog logger) {
        this.customers = customers;
        this.scanner = new Scanner(System.in);
        this.logger = logger;
        this.bankManager = new BankManager("Bank Manager", customers, logger);
    }

    /**
     * Handles customer login and menu operations.
     * Creates a customer menu and manages the customer session.
     */
    public void handleCustomer() {
        System.out.println("Enter your name:");
        System.out.println("__________________");
        String name = scanner.nextLine();
        
        Customer customer = customers.get(name);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        
        currentMenu = new CustomerMenu(customer, logger);
        runMenuLoop();
    }

    /**
     * Handles bank manager operations.
     * Creates a bank manager menu and manages the manager session.
     */
    public void handleBankManager() {
        // Create menu with access to the bank manager instance
        currentMenu = new BankManagerMenu(bankManager, logger);
        runMenuLoop();
    }

    /**
     * Runs the menu loop for the current user (customer or manager).
     */
    private void runMenuLoop() {
        boolean continueRunning = true;
        
        while (continueRunning) {
            try {
                currentMenu.displayMenu();
                String choice = currentMenu.getInput();
                continueRunning = currentMenu.handleChoice(choice);
            } catch (Exception e) {
                System.out.println("Error in menu operation: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
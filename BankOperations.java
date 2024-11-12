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
     * Sets up bank manager and necessary components.
     * 
     * @param customers map of customer names to Customer objects
     * @param logger transaction logging system
     */
    public BankOperations(Map<String, Customer> customers, TransactionLog logger) {
        this.customers = customers;
        this.scanner = new Scanner(System.in);
        this.logger = logger;
        // Create bank manager with access to all customers
        this.bankManager = new BankManager("Bank Manager", customers, logger);
    }

    /**
     * Handles customer login and menu operations.
     * Verifies customer identity and creates appropriate menu.
     */
    public void handleCustomer() {
        System.out.println("Enter your name:");
        String name = scanner.nextLine();
        
        Customer customer = customers.get(name);
        if (customer == null) {
            System.out.println("Customer not found.");
            return;
        }
        
        currentMenu = new CustomerMenu(customer, logger, bankManager);
        runMenuLoop();
    }

    /**
     * Handles bank manager operations.
     * Creates bank manager menu with full system access.
     */
    public void handleBankManager() {
        currentMenu = new BankManagerMenu(bankManager, logger);
        runMenuLoop();
    }

    /**
     * Updates customer data in the system.
     * Used after creating new customers or updating existing ones.
     * 
     * @param customer the customer to update
     */
    public void updateCustomer(Customer customer) {
        customers.put(customer.getName(), customer);
    }

    /**
     * Runs the menu loop for the current user (customer or manager).
     * Handles menu display, input, and processing until exit.
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

    /**
     * Gets the current bank manager instance.
     * @return the bank manager
     */
    public BankManager getBankManager() {
        return bankManager;
    }

    /**
     * Gets the map of all customers in the system.
     * @return map of customer names to Customer objects
     */
    public Map<String, Customer> getCustomers() {
        return customers;
    }
}
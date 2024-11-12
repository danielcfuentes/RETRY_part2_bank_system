import java.util.Scanner;

/**
 * Implements the Menu interface for customer operations.
 * This class provides the specific menu functionality for bank customers,
 * including options for account inquiries, transfers, deposits, and withdrawals.
 * 
 * The CustomerMenu uses composition by holding references to:
 * - The customer it's serving
 * - The transaction logger
 * - The scanner for input
 * 
 * This is part of the Strategy pattern implementation where CustomerMenu
 * provides concrete behavior for the Menu interface.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class CustomerMenu implements Menu {
    /** The customer using this menu */
    private Customer customer;
    /** Logger for recording transactions */
    private TransactionLog logger;
    /** Scanner for reading user input */
    private Scanner scanner;
    
    /**
     * Constructs a new CustomerMenu for a specific customer.
     * 
     * @param customer the customer who will be using this menu
     * @param logger the transaction logger to record activities
     */
    public CustomerMenu(Customer customer, TransactionLog logger) {
        this.customer = customer;
        this.logger = logger;
        this.scanner = new Scanner(System.in);
    }
    
    @Override
    public void displayMenu() {
        System.out.println("\nWelcome " + customer.getName());
        System.out.println("1. Inquire Balance");
        System.out.println("2. Deposit Money");
        System.out.println("3. Withdraw Money");
        System.out.println("4. Transfer Between Accounts");
        System.out.println("5. Pay Someone");
        System.out.println("6. Return to Main Menu");
        System.out.println("__________________");
    }
    
    @Override
    public boolean handleChoice(String choice) {
        try {
            switch (choice) {
                case "1":
                    handleBalanceInquiry();
                    return true;
                case "2":
                    handleDeposit();
                    return true;
                case "3":
                    handleWithdrawal();
                    return true;
                case "4":
                    handleTransfer();
                    return true;
                case "5":
                    handlePayment();
                    return true;
                case "6":
                    return false;
                default:
                    System.out.println("Invalid choice. Please try again.");
                    return true;
            }
        } catch (Exception e) {
            System.out.println("Error processing request: " + e.getMessage());
            return true;
        }
    }
    
    @Override
    public String getInput() {
        return scanner.nextLine().trim();
    }
    
    /**
     * Handles balance inquiry for all accounts.
     * Displays the balance for each account type and logs the inquiry.
     */
    private void handleBalanceInquiry() {
        System.out.println("\nYour account balances:");
        System.out.println("__________________");
        
        for (Account account : customer.getAccounts()) {
            System.out.printf("%s (%s): $%.2f%n", 
                account.getClass().getSimpleName(), 
                account.getAccountNumber(), 
                account.getBalance()
            );
            System.out.println("__________________");
            
            // Log the balance inquiry
            logger.logTransaction(
                String.format("%s made a balance inquiry on %s. Balance: $%.2f",
                    customer.getName(),
                    account.getAccountNumber(),
                    account.getBalance())
            );
        }
    }
    
    /**
     * Handles deposit operation.
     * Allows customer to select an account and specify amount to deposit.
     */
    private void handleDeposit() {
        // Display available accounts
        displayAccountsForSelection();
        
        try {
            // Get account selection
            System.out.print("Select account (1-" + customer.getAccounts().size() + "): ");
            int accountIndex = Integer.parseInt(getInput()) - 1;
            
            if (accountIndex >= 0 && accountIndex < customer.getAccounts().size()) {
                // Get deposit amount
                System.out.println("Enter amount to deposit:");
                System.out.println("__________________");
                double amount = Double.parseDouble(getInput());
                
                Account selectedAccount = customer.getAccounts().get(accountIndex);
                selectedAccount.deposit(amount);
                
                System.out.printf("Successfully deposited $%.2f%n", amount);
                logger.logTransaction(String.format("%s deposited $%.2f to %s", 
                    customer.getName(), amount, selectedAccount.getAccountNumber()));
            } else {
                System.out.println("Invalid account selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }
    
    // Similar detailed implementations for handleWithdrawal(), handleTransfer(), 
    // and handlePayment() would follow...
    
    /**
     * Helper method to display all accounts for selection.
     * Used by deposit, withdrawal, and transfer operations.
     */
    private void displayAccountsForSelection() {
        System.out.println("\nYour accounts:");
        int index = 1;
        for (Account account : customer.getAccounts()) {
            System.out.printf("%d. %s ($%.2f)%n", 
                index++, 
                account.getAccountNumber(), 
                account.getBalance()
            );
        }
    }
}
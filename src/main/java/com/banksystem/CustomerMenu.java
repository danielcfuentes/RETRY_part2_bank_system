package com.banksystem;
import java.util.*;
import java.io.IOException;

/**
 * Implements the Menu interface for customer operations.
 * Provides functionality for customers to:
 * - View account balances
 * - Make deposits and withdrawals
 * - Transfer between accounts
 * - Pay other customers
 * - Generate transaction history
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

    /** Bank manager for interacting with customers using this to access manager functionality */
    private BankManager bankManager;

    /**
     * Creates a new customer menu for the specified customer.
     * 
     * @param customer the customer using the menu
     * @param logger transaction logging system
     * @param bankManager bank manager which is the instance of the bank manager
     */
    public CustomerMenu(Customer customer, TransactionLog logger, BankManager bankManager) {
        this.customer = customer;
        this.logger = logger;
        this.scanner = new Scanner(System.in);
        this.bankManager = bankManager;
        
        //record starting balances for transaction history
        logger.recordSessionStart(customer.getName(), customer.getAccounts());
    }

    @Override
    public void displayMenu() {
        System.out.println("\nWelcome " + customer.getName());
        System.out.println("\nPlease select the type of transaction you would like to perform:");
        System.out.println("1. View Account Balances");
        System.out.println("2. Make a Deposit");
        System.out.println("3. Make a Withdrawal");
        System.out.println("4. Transfer Between Your Accounts");
        System.out.println("5. Pay Another Customer");
        System.out.println("6. View Transaction History");
        System.out.println("7. Return to Main Menu");
        System.out.println("__________________");
    }

    @Override
    public String getInput() {
        return scanner.nextLine().trim();
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
                    handleTransactionHistory();
                    return true;
                case "7":
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

    /**
     * Handles balance inquiry for all accounts.
     * Shows current balance for each account type.
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

            logger.logTransaction(
                String.format("%s made a balance inquiry on %s. Balance: $%.2f",
                    customer.getName(),
                    account.getAccountNumber(),
                    account.getBalance()),
                customer.getName()
            );
        }
    }

    /**
     * Handles deposit operation.
     * Allows customer to select account and specify amount to deposit.
     */
    private void handleDeposit() {
        //display available accounts
        displayAccountsForSelection();
        
        try {
            //get account selection
            System.out.print("Please select an account by entering a number (1-" + customer.getAccounts().size() + "): ");
            int accountIndex = Integer.parseInt(getInput()) - 1;
            
            if (accountIndex >= 0 && accountIndex < customer.getAccounts().size()) {
                //get deposit amount
                System.out.println("Please enter the amount you wish to deposit: $");
                System.out.println("__________________");
                double amount = Double.parseDouble(getInput());
                
                Account selectedAccount = customer.getAccounts().get(accountIndex);
                selectedAccount.deposit(amount);
                
                System.out.printf("Successfully deposited $%.2f%n", amount);
                logger.logTransaction(
                    String.format("%s deposited $%.2f to %s", 
                        customer.getName(), 
                        amount, 
                        selectedAccount.getAccountNumber()),
                    customer.getName()
                );
            } else {
                System.out.println("Invalid account selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles withdrawal operation.
     * Allows customer to select account and specify amount to withdraw.
     */
    private void handleWithdrawal() {
        displayAccountsForSelection();
        
        try {
            System.out.print("Please select an account by entering a number (1-" + customer.getAccounts().size() + "): ");
            int accountIndex = Integer.parseInt(getInput()) - 1;
            
            if (accountIndex >= 0 && accountIndex < customer.getAccounts().size()) {
                System.out.println("Please enter the amount you wish to withdraw: $");
                System.out.println("__________________");
                double amount = Double.parseDouble(getInput());
                
                Account selectedAccount = customer.getAccounts().get(accountIndex);
                selectedAccount.withdraw(amount);
                
                System.out.printf("Successfully withdrew $%.2f%n", amount);
                logger.logTransaction(
                    String.format("%s withdrew $%.2f from %s", 
                        customer.getName(),
                        amount, 
                        selectedAccount.getAccountNumber()),
                    customer.getName()
                );
            } else {
                System.out.println("Invalid account selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles transfer between accounts.
     * Allows customer to select source and destination accounts and specify amount.
     */
    private void handleTransfer() {
        List<Account> accounts = customer.getAccounts();
        
        System.out.println("\nYour accounts:");
        for (int i = 0; i < accounts.size(); i++) {
            System.out.printf("%d. %s ($%.2f)%n", 
                i + 1, 
                accounts.get(i).getAccountNumber(), 
                accounts.get(i).getBalance()
            );
        }

        try {
            System.out.println("\nPlease select the account you want to transfer money FROM:");
            int fromAccount = Integer.parseInt(getInput()) - 1;
            
            System.out.println("Then select the account you want to transfer money TO:");
            int toAccount = Integer.parseInt(getInput()) - 1;
            
            if (fromAccount == toAccount) {
                System.out.println("Cannot transfer to same account.");
                return;
            }

            if (fromAccount >= 0 && fromAccount < accounts.size() && 
                toAccount >= 0 && toAccount < accounts.size()) {
                    
                System.out.println("Enter amount you wish to transfer:");
                double amount = Double.parseDouble(getInput());
                
                Account source = accounts.get(fromAccount);
                Account destination = accounts.get(toAccount);
                
                source.withdraw(amount);
                destination.deposit(amount);
                
                System.out.printf("Successfully transferred $%.2f%n", amount);
                logger.logTransaction(
                    String.format("%s transferred $%.2f from %s to %s", 
                        customer.getName(), 
                        amount, 
                        source.getAccountNumber(), 
                        destination.getAccountNumber()),
                    customer.getName()
                );
            } else {
                System.out.println("Invalid account selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles payment to another customer.
     * Allows selection of source account and recipient's account.
     */
    private void handlePayment() {
        try {
            System.out.println("Enter recipient's name:");
            System.out.println("__________________");
            String recipientName = getInput();
            
            //get recipient from bank manager
            Optional<Customer> recipientOpt = bankManager
                .findCustomerInteractive(
                    recipientName.split(" ")[0], 
                    recipientName.split(" ")[1]
                );
            
            if (!recipientOpt.isPresent()) {
                System.out.println("Recipient not found.");
                return;
            }
            Customer recipient = recipientOpt.get();

            //check if self payment
            if(customer.getName().equals(recipient.getName())){
                System.out.println("Cannot pay yourself.");
                return;
            }

            //show payer's accounts
            List<Account> payerAccounts = customer.getAccounts();
            System.out.println("\nYour accounts:");
            for (int i = 0; i < payerAccounts.size(); i++) {
                System.out.printf("%d. %s ($%.2f)%n", 
                    i + 1, 
                    payerAccounts.get(i).getAccountNumber(), 
                    payerAccounts.get(i).getBalance()
                );
            }

            System.out.println("\nPlease select the account you want to transfer money FROM:");
            int fromAccount = Integer.parseInt(getInput()) - 1;

            //show recipient's accounts
            List<Account> recipientAccounts = recipient.getAccounts();
            System.out.println("\nRecipient's accounts:");
            for (int i = 0; i < recipientAccounts.size(); i++) {
                System.out.printf("%d. %s%n", 
                    i + 1, 
                    recipientAccounts.get(i).getAccountNumber()
                );
            }

            System.out.println("Then select the account you want to send money to the recipient:");
            int toAccount = Integer.parseInt(getInput()) - 1;

            if (fromAccount >= 0 && fromAccount < payerAccounts.size() && 
                toAccount >= 0 && toAccount < recipientAccounts.size()) {
                
                    System.out.println("Please enter the amount you wish to send: $");
                double amount = Double.parseDouble(getInput());
                
                customer.pay(recipient, 
                           payerAccounts.get(fromAccount), 
                           recipientAccounts.get(toAccount), 
                           amount);
                
                System.out.printf("Successfully paid $%.2f to %s%n", amount, recipientName);
                logger.logTransaction(
                    String.format("%s paid %s $%.2f", 
                        customer.getName(), 
                        recipientName, 
                        amount),
                    customer.getName()
                );
            } else {
                System.out.println("Invalid account selection.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a number.");
        } catch (IllegalArgumentException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Handles creation of transaction history for current session.
     * Creates a txt file with all account and transaction information.
     */
    private void handleTransactionHistory() {
        try {
            TransactionHistory history = new TransactionHistory(customer, logger);
            String fileName = history.generateHistoryFile();
            
            System.out.println("\nTransaction history generated successfully!");
            System.out.println("File saved as: " + fileName);
            System.out.println("You can find it in the transaction_histories folder.");
            
        } catch (IOException e) {
            System.out.println("Error generating transaction history: " + e.getMessage());
        }
    }

    /**
     * Helper method to display accounts for selection.
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
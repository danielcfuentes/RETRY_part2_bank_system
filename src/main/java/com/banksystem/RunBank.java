package com.banksystem;
import java.util.*;

/**
 * Main class for the banking application.
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class RunBank {
    /**
     * Main starting point for the banking application.
     * Initializes the system, presents menu options, and user interaction.
     * @param args Command-line arguments.
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
            // loads data
            customers = csvHandler.loadCustomerData();
            BankOperations operations = new BankOperations(customers, logger);

            // main menu loop
            while (true) {
                System.out.println("\nWelcome to El Paso Miners Bank");
                System.out.println("Please select your role:");
                System.out.println("1. Customer (Access your accounts and perform transactions)");
                System.out.println("2. Bank Manager (Administrative access and customer service)");
                System.out.println("Type 'EXIT' to quit");
                System.out.println("_________________________");
                
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("EXIT")) {
                    break;
                }
                
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

            // save and exit
            System.out.println("\nSaving updated customer data...");
            csvHandler.saveCustomerData(customers);
            logger.exitUpdate();
            System.out.println("Thank you for using El Paso Miners Bank!");
            
        } catch (Exception e) {
            System.out.println("Fatal error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
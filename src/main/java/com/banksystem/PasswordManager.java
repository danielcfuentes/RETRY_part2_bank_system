package com.banksystem;
import java.io.*;
import java.util.*;

/**
 * Handles user authentication and password management for the banking system.
 * Creates passwords on-demand when customers first log in.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class PasswordManager {
    /** File containing user password data */
    private static final String PASSWORD_FILE = "user_passwords.csv";
    
    /** Map storing user IDs and their passwords */
    private Map<String, String> passwords;
    
    /**
     * Initializes the password manager and loads existing password data.
     */
    public PasswordManager() {
        this.passwords = new HashMap<>();
        loadPasswords();
    }
    
    /**
     * Loads existing passwords from the CSV file.
     */
    private void loadPasswords() {
        File file = new File(PASSWORD_FILE);
        
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            //skip header
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    passwords.put(parts[0].trim(), parts[1].trim());
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading passwords: " + e.getMessage());
        }
    }
    
    /**
     * Checks if a customer has a password set.
     * 
     * @param customerID the customer's ID
     * @return true if customer has a password, false otherwise
     */
    public boolean hasPassword(String customerID) {
        return passwords.containsKey(customerID);
    }
    
    /**
     * Verifies a user's password.
     * 
     * @param customerID the customer's ID
     * @param password the password to verify
     * @return true if password is correct, false otherwise
     */
    public boolean verifyPassword(String customerID, String password) {
        String storedPassword = passwords.get(customerID);
        return storedPassword != null && storedPassword.equals(password);
    }
    
    /**
     * Sets up a new password for a customer.
     * Prompts user to enter and confirm password.
     * 
     * @param customerID the customer's ID
     * @param scanner scanner for reading user input
     * @return true if password was set successfully, false otherwise
     */
    public boolean setupNewPassword(String customerID, Scanner scanner) {
        System.out.println("\nFirst time login - Password Setup Required");
        System.out.println("Enter a new password:");
        String password = scanner.nextLine();
        
        System.out.println("Confirm your password:");
        String confirmPassword = scanner.nextLine();
        
        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Please try again.");
            return false;
        }
        
        if (password.trim().isEmpty()) {
            System.out.println("Password cannot be empty. Please try again.");
            return false;
        }
        
        setPassword(customerID, password);
        System.out.println("Password successfully set!");
        return true;
    }
    
    /**
     * Adds or updates a password for a customer.
     * 
     * @param customerID the customer's ID
     * @param password the new password
     */
    public void setPassword(String customerID, String password) {
        passwords.put(customerID, password);
        savePasswords();
    }
    
    /**
     * Saves current passwords to the CSV file.
     */
    private void savePasswords() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PASSWORD_FILE))) {
            writer.println("CustomerID,Password");
            for (Map.Entry<String, String> entry : passwords.entrySet()) {
                writer.printf("%s,%s%n", entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            System.err.println("Error saving passwords: " + e.getMessage());
        }
    }
}
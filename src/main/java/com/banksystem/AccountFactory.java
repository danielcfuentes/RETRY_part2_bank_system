package com.banksystem;

/**
 * Factory class for creating different types of bank accounts.
 * This class implements the Factory Pattern, which provides an interface for creating objects without explicitly specifying their exact classes.
 * 
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class AccountFactory {
    
    /** Constant for checkings account type */
    public static final String CHECKING = "CHECKING";
    /** Constant for savings account type */
    public static final String SAVINGS = "SAVINGS";
    /** Constant for credit account type */
    public static final String CREDIT = "CREDIT";
    
    /**
     * Creates a new bank account of the specified type.
     * 
     * @param type the type of account to create ("CHECKING", "SAVINGS", or "CREDIT")
     * @param accountNumber the unique identifier for the account
     * @param balance the initial balance for the account
     * @param creditLimit the credit limit (only used for credit accounts, ignored for others)
     * @return a new Account object of the specified type
     * @throws IllegalArgumentException if an unknown account type is provided
     */
    public static Account createAccount(String type, String accountNumber, double balance, double creditLimit) {
        if (type.equals(CHECKING)) {
            return new Checkings(accountNumber, balance);
        } else if (type.equals(SAVINGS)) {
            return new Savings(accountNumber, balance);
        } else if (type.equals(CREDIT)) {
            return new Credit(accountNumber, balance, creditLimit);
        } else {
            throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
    
    /**
     * Generates a unique account number based on the account type and customer ID.
     * 
     * @param type the type of account (determines first digit)
     * @param customerId the customer's ID (used for last 3 digits)
     * @return a unique account number string
     * @throws IllegalArgumentException if an unknown account type is provided
     */
    public static String generateAccountNumber(String type, String customerId) {
        // Pad the customer ID with leading zeros to ensure 3 digits
        String paddedId = String.format("%03d", Integer.parseInt(customerId));
        
        // First digit indicates account type
        if (type.equals(CHECKING)) {
            return "1" + paddedId;
        } else if (type.equals(SAVINGS)) {
            return "2" + paddedId;
        } else if (type.equals(CREDIT)) {
            return "3" + paddedId;
        } else {
            throw new IllegalArgumentException("Unknown account type: " + type);
        }
    }
}
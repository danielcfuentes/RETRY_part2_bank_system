package com.banksystem;
import java.util.*;

/**
 * Abstract class for representing people in the banking system.
 * Provides common functions for managing accounts and transactions.
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public abstract class Person {
    /** The person's full name */
    private String name;
    
    /** List of bank accounts owned by this person */
    private List<Account> accounts;
    
    /**
     * Creates a new person with gvien name.
     * @param name the person's full name
     */    
     public Person(String name) {
        this.name = name;
        this.accounts = new ArrayList<>();
    }
    
    /**
     * Gets the person's name.
     * @return the person's full name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the person's name.
     * @param name new full name for the person
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets all acounts owned by this person.
     * @return list of all accounts
     */
    public List<Account> getAccounts() {
        return accounts;
    }
    
    /**
     * Sets the list of accounts for this person.
     * @param accounts new list of accounts to assign
     */
    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }
    
    /**
     * Finds accounts with the same given account number.
     * @param accountNumber account number to search for
     * @return list of matching accounts
     */
    public List<Account> inquireAccount(String accountNumber) {
        //create new list for matching accounts
        List<Account> foundAccounts = new ArrayList<>();
        
        //loop through accounts to find matches
        for (Account account : accounts) {
            if (account.getAccountNumber().equals(accountNumber)) {
                foundAccounts.add(account);
            }
        }
        return foundAccounts;
    }
    
    /**
     * Method for bank managers to find accounts by account number.
     * @param accountNumber account number to search for
     * @param allCustomers map of all customers in the system
     * @return list of same accounts
     */
    public static List<Account> inquireAccount(String accountNumber, Map<String, Customer> allCustomers) {
        //look through all customers
        for (Customer customer : allCustomers.values()) {
            //check each customer's accounts
            for (Account account : customer.getAccounts()) {
                if (account.getAccountNumber().equals(accountNumber)) {
                    //create list for found account
                    List<Account> found = new ArrayList<>();
                    found.add(account);
                    return found;
                }
            }
        }
        //return empty list if not found
        return new ArrayList<>();
    }
    
    /**
     * Method for bank managers to find accounts by customer name.
     * @param firstName customer's first name
     * @param lastName customer's lsat name
     * @param allCustomers map of all customers in the system
     * @return list of customer's accounts
     */
    public static List<Account> inquireAccount(String firstName, String lastName, Map<String, Customer> allCustomers) {
        //combine first and last name
        String fullName = firstName + " " + lastName;
        
        //look up customer in our map
        Customer customer = allCustomers.get(fullName);
        
        if (customer != null) {
            //return copy of customer's accounts
            return new ArrayList<>(customer.getAccounts());
        }
        
        //return empty list if customer not found
        return new ArrayList<>();
    }
    
    /**
     * Gets all accounts owned by this person.
     * @return list of all accounts
     */
    public List<Account> inquireAllAccounts() {
        //return a copy of accounts list
        return new ArrayList<>(accounts);
    }

    /**
     * Makes a payment to another person's account.
     * @param receiver person receiving the payment
     * @param fromAccount account to take payment from
     * @param toAccount account to send money to
     * @param amount amount fo money to pay
     */
    public void pay(Person receiver, Account fromAccount, Account toAccount, double amount) {
        if (this == receiver || this.getName().equals(receiver.getName())) {
            throw new IllegalArgumentException("Error: Cannot make payments to yourself. Please select a different recipient.");
        }
    
        if (fromAccount.getAccountNumber().equals(toAccount.getAccountNumber())) {
            throw new IllegalArgumentException("Cannot transfer between the same account");
        }
    
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }
        
        if (!this.accounts.contains(fromAccount)) {
            throw new IllegalArgumentException("Source account does not belong to you");
        }
        
        if (!receiver.getAccounts().contains(toAccount)) {
            throw new IllegalArgumentException("Destination account does not belong to receiver");
        }
    
        if (toAccount instanceof Credit) {
            Credit creditAccount = (Credit) toAccount;
            if (amount > Math.abs(creditAccount.getBalance())) {
                throw new IllegalArgumentException(
                    String.format("Payment amount ($%.2f) exceeds credit card balance ($%.2f)",
                        amount, Math.abs(creditAccount.getBalance()))
                );
            }
        }
        
        try {
            fromAccount.withdraw(amount);
            toAccount.deposit(amount);
            
            String message = String.format("%s paid %s $%.2f successfully",
                this.getName(), receiver.getName(), amount);
            System.out.println(message);
            
        } catch (InsufficientFundsException e) {
            String errorMessage = "Payment failed: Insufficient funds";
            System.out.println(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
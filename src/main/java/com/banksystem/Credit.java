package com.banksystem;
/**
 * Represents a credit account where the balance is negative, with a set credit limit.
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public class Credit extends Account {

    /** The credit limit for the account. */
    private double creditLimit;

    /** The principle amount of the credit account. */
    private double principle;
    
    /**
     * Constructs a new Credit account with the given account number, balance, and credit limit.
     * @param accountNumber the account number
     * @param balance the initial balance
     * @param creditLimit the credit limit
     */
    public Credit(String accountNumber, double balance, double creditLimit) {
        super(accountNumber, balance);
        this.creditLimit = creditLimit;
        this.principle = Math.abs(balance);
    }

    /**
     * Returns the credit limit for the account.
     * @return the credit limit
     */
    public double getCreditLimit() {
        return creditLimit;
    }

    /**
     * Sets the credit limit for the account.
     * @param creditLimit the new credit limit
     */
    public void setCreditLimit(double creditLimit) {
        this.creditLimit = creditLimit;
    }

    /**
     * Returns the principle amount of the credit account.
     * @return the principle amount
     */
    public double getPrinciple() {
        return principle;
    }

    /**
     * Sets the principle amount of the credit account.
     * @param principle the new principle amount
     */
    public void setPrinciple(double principle) {
        this.principle = principle;
    }

    /**
     * Function to borrow money from the credit account.
     * @param amount the amount to borrow
     */
    public void borrow(double amount) {
        //amount must be positive
        if (amount <= 0) {
            throw new IllegalArgumentException("Borrow amount must be positive");
        }

        //check if borrowing would exceed credit limit
        if (Math.abs(balance) + amount > creditLimit) {
            throw new IllegalArgumentException("Would exceed credit limit of $" + creditLimit);
        }

        //add to balance (negative) and principle
        balance -= amount;  //makes balance more negative
        principle += amount;
    }

    /**
     * Function to pay off the credit account.
     * @param amount the amount to pay off
     */
    public void pay(double amount) {
        //amount must be positive
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        //cannot pay more than what is owed
        if (amount > Math.abs(balance)) {
            throw new IllegalArgumentException("Payment amount cannot exceed balance owed");
        }

        //add to balance (makes it less negative) and reduce principle
        balance += amount;
        principle -= amount;
    }

    @Override
    public void deposit(double amount) {
        //for credit accounts, deposit is same as payment
        pay(amount);
    }

    @Override
    public void withdraw(double amount) {
        //for credit accounts, withdraw is same as borrowing
        borrow(amount);
    }

}
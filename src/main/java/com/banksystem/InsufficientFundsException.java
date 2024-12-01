package com.banksystem;

/**
 * This is a user-defined exception - we create it by extending the Exception class.
 * This is designed for our banking system and can carry specific information about insufficient funds scenarios.
 */
public class InsufficientFundsException extends Exception {
    
    /**How much they tried to withdraw */
    private final double attemptedAmount;
    /**How much they actually had */
    private final double availableBalance;
    /**which account had the problem */
    private final String accountNumber;
    
    /**
     * Constructor for custom exception.
     * Capture all the relevant information when the error occurs.
     */
    public InsufficientFundsException(String message, double attemptedAmount, 
                                    double availableBalance, String accountNumber) {
        super(message);
        this.attemptedAmount = attemptedAmount;
        this.availableBalance = availableBalance;
        this.accountNumber = accountNumber;
    }
    
    /**Getter method to get our attempted amount */
    public double getAttemptedAmount() {
        return attemptedAmount;
    }
    
    /**Getter method to get our attempted amount */
    public double getAvailableBalance() {
        return availableBalance;
    }
    
    /**Getter method to get our accout number */
    public String getAccountNumber() {
        return accountNumber;
    }
    
    /**
     * Customized string representation of our exception.
     * This makes error messages more meaningful to users.
     */
    @Override
    public String toString() {
        return String.format("Insufficient funds in account %s. " +
                           "Attempted amount: $%.2f, Available balance: $%.2f", 
                           accountNumber, attemptedAmount, availableBalance);
    }
}
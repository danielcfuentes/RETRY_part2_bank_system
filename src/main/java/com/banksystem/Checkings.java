package com.banksystem;
/**
 * Represents a checking account with the ability to transfer funds.
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class Checkings extends Account{

    /**
     * Initializes a Checkings account with the given account number and balance.
     * @param accountNumber the account identifier
     * @param balance the initial balance
     */
    public Checkings(String accountNumber, double balance){
        super(accountNumber, balance);
    }
    
    /**
     * Transfers a specified amount to another account and verfies if the amount given is valid and they have enough money in the acocunt.
     * @param to the target account
     * @param amount the amount to transfer
     */
    public void tansfer(Account to, double amount) {
        try {
            if (amount > 0 && amount <= balance) {
                this.withdraw(amount);
                to.deposit(amount);
            } else {
                throw new IllegalArgumentException("Not Valid Transfer Amount");
            }
        } catch (InsufficientFundsException e) {
            throw new IllegalArgumentException("Insufficient funds for transfer: " + e.getMessage());
        }
    }
}

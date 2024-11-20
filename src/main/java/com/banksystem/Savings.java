package com.banksystem;
/**
 * Represents a savings account with the ability to transfer funds to another account.
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public class Savings extends Account {

    /**
     * Initializes a Savings account with an account number and balance.
     * @param accountNumber the account identifier
     * @param balance the initial balance
     */
    public Savings(String accountNumber, double balance){
        super(accountNumber, balance);
    }

    /**
     * Transfers a specified amount to another account.
     * @param to the target account
     * @param amount the amount to transfer
     */
    public void tansfer(Account to, double amount){
        if ( amount > 0 && balance >= amount){
            this.withdraw(amount);
            to.deposit(amount);
        }else{
            throw new IllegalArgumentException("Not Valid Transfer Amount");
        }
    }    
}

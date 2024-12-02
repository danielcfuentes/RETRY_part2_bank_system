package com.banksystem;
/**
 * Abstract class representing a bank account.
 * This class provides basic banking operations like deposits, withdrawals, and transfers.
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 2.0
 */
public abstract class Account {
    /** The current balance in the account */
    protected double balance;
    /** The unique identifier for the account */
    protected String accountNumber;
    /** The transaction log for every inquiry */
    protected static TransactionLog transactionLog;
    /** The owner of the account */
    private Customer owner;

    /**
     * Initializes an account with the given account number and balance.
     * @param accountNumber the unique identifier for the account
     * @param balance the initial balance in the account
     */
    public Account(String accountNumber, double balance) {
        this.accountNumber = accountNumber;
        this.balance = balance;
    }

    /** Default constructor for account. */
    public Account() {}

    /**
     * Sets the owner of the account.
     * @param owner the owner of the account
     */
    public void setOwner(Customer owner) {
        this.owner = owner;
    }

    /**
     * Returns the owner of the account.
     * @return the owner of the account
     */
    public Customer getOwner() {
        return owner;
    }

    /**
     * Sets the transaction log for the account.
     * @param log the transaction log for the account
     */
    public static void setTransactionLog(TransactionLog log) {
        transactionLog = log;
    }

    /**
     * Returns the balance for the account.
     * @return the balance for the account
     */   
    public double getBalance() {
        return balance;
    }
    
    /**
     * Sets the balance for the account.
     * @param balance the new balance for the account
     */
    public void setBalance(double balance) {
        if (!(this instanceof Credit) && balance < 0) {
            throw new IllegalArgumentException("Account balance cannot be negative");
        }
        this.balance = balance;
    }
    
    /**
     * Returns the account number.
     * @return the account number
     */
    public String getAccountNumber() {
        return accountNumber;
    }

    /**
     * Inquires the current balance.
     * @return the current balance in the account
     */
    public double inquireBalance() {
        if (transactionLog != null) {
            String message = String.format("Balance inquiry for %s: $%.2f", 
                accountNumber, balance);
            transactionLog.logTransaction(message, owner.getName());
            System.out.println(message);
        }
        return balance;
    }

    /**
     * Deposits a specified amount into the account.
     * @param amount the amount to deposit
     * @throws IllegalArgumentException if amount is invalid
     */
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        //for credit accounts, validate against outstanding balance
        if (this instanceof Credit) {
            Credit creditAccount = (Credit) this;
            if (Math.abs(balance) < amount) {
                throw new IllegalArgumentException(
                    String.format("Cannot deposit more than the outstanding balance ($%.2f)", 
                        Math.abs(balance))
                );
            }
        }

        this.balance += amount;
        
        if (transactionLog != null) {
            String message = String.format("Deposit of $%.2f to %s successful. New balance: $%.2f", 
                amount, accountNumber, balance);
            transactionLog.logTransaction(message, owner.getName());
            System.out.println(message);
        }
    }
    
    /**
     * Withdraws a specified amount from the account.
     * @param amount the amount to withdraw
     * @throws IllegalArgumentException if amount is invalid or insufficient funds
     */
    public void withdraw(double amount) throws InsufficientFundsException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }
    
        if (!(this instanceof Credit) && amount > balance) {
            throw new InsufficientFundsException(
                "Insufficient funds for withdrawal",
                amount,
                balance,
                accountNumber
            );
        }
    
        this.balance -= amount;
        
        if (transactionLog != null) {
            String message = String.format("Withdrawal of $%.2f from %s successful. New balance: $%.2f", 
                amount, accountNumber, balance);
            transactionLog.logTransaction(message, owner.getName());
            System.out.println(message);
        }
    }
}
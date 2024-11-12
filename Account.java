
/**
 * Abstract class representing a bank account.
 * This class provides basic banking operations like deposits, withdrawals, pay someone, and transfer between accounts.
 * @author Daniel Fuentes, Rogelio Lozano
 * @version 1.0
 */
public abstract class Account {
    /** The current balance in the account */
    protected double balance;
    /** The unique identifier for the account */
    protected String accountNumber;
    /** The transaction log for every inquiry */
    private static TransactionLog transactionLog;
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

    /**
    * Default constructor for account.
    */
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
    * Sets transaction log to log
    * @param log the new transaction log
    */
    public static void setTransactionLog(TransactionLog log) {
        transactionLog = log;
    }

    /**
     * Returns the current account balance.
     * @return the current balance in the account
     */
    public double getBalance() {
        return balance;
    }
    
    /**
     * Sets a new balance for the account.
     * @param balance the new balance to set
     */
    public void setBalance(double balance) {
        this.balance = balance;
    }
    
    /**
     * Returns the account number.
     * @return the unique account identifier
     */
    public String getAccountNumber() {
        return accountNumber;
    }
    
    /**
     * Returns the current balance.
     * @return the current balance in the account
    */
    public double inquireBalance() {
        if (transactionLog != null) {
            transactionLog.logTransaction(
                String.format("Balance inquiry for %s: $%.2f", 
                    accountNumber, balance),
                owner.getName()
            );
        }
        return balance;
    }

    /**
     * Deposits a specified amount into the account.
     * @param amount the amount to deposit
     */
    public void deposit(double amount) {
        if (amount > 0) {
            this.balance += amount;
            if (transactionLog != null) {
                transactionLog.logTransaction(
                    String.format("Deposit of $%.2f to %s. New balance: $%.2f", 
                        amount, accountNumber, balance),
                    owner.getName()
                );
            }
        } else {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }
    }
    
    /**
     * Withdraws a specified amount from the account.
     * @param amount the amount to withdraw
     */
    public void withdraw(double amount) {
        if (amount > 0 && amount <= balance) {
            this.balance -= amount;
            if (transactionLog != null) {
                transactionLog.logTransaction(
                    String.format("Withdrawal of $%.2f from %s. New balance: $%.2f", 
                        amount, accountNumber, balance),
                    owner.getName()
                );
            }
        } else {
            throw new IllegalArgumentException("Invalid withdrawal amount or insufficient funds");
        }
    }
}
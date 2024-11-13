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
     * Makes a payment towards the credit balance.
     * @param amount the amount to pay
     * @throws IllegalArgumentException if payment amount exceeds outstanding balance
     */
    @Override
    public void deposit(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Payment amount must be positive");
        }

        // Check if payment exceeds outstanding balance
        if (amount > Math.abs(balance)) {
            throw new IllegalArgumentException(
                String.format("Payment amount ($%.2f) exceeds outstanding balance ($%.2f)", 
                    amount, Math.abs(balance))
            );
        }

        balance += amount;
        principle -= amount;
        
        String message = String.format(
            "Credit payment of $%.2f successful. New balance: $%.2f, Available credit: $%.2f",
            amount, balance, creditLimit + balance
        );
        System.out.println(message);
        if (transactionLog != null) {
            transactionLog.logTransaction(message, getOwner().getName());
        }
    }

    /**
     * Charges an amount to the credit account.
     * @param amount the amount to charge
     * @throws IllegalArgumentException if amount would exceed credit limit
     */
    public void charge(double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Charge amount must be positive");
        }

        if (Math.abs(balance) + amount > creditLimit) {
            throw new IllegalArgumentException(
                String.format("Charge would exceed credit limit. Available credit: $%.2f, Attempted charge: $%.2f",
                    creditLimit + balance, amount)
            );
        }

        balance -= amount;
        principle += amount;
        
        String message = String.format(
            "Credit charge of $%.2f successful. New balance: $%.2f, Available credit: $%.2f",
            amount, balance, creditLimit + balance
        );
        System.out.println(message);
        if (transactionLog != null) {
            transactionLog.logTransaction(message, getOwner().getName());
        }
    }
}
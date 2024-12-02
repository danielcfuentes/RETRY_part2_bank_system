package com.banksystem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class TransferTest {
    private Account sourceAccount;
    private Account destinationAccount;
    
    @Before
    public void setUp() {
        sourceAccount = AccountFactory.createAccount(
            AccountFactory.CHECKING,
            "SRC001",
            1000.0,
            0.0
        );
        destinationAccount = AccountFactory.createAccount(
            AccountFactory.SAVINGS,
            "DST001",
            500.0,
            0.0
        );
    }
    
    @Test
    public void testValidTransfer() throws InsufficientFundsException {
        ((Checkings)sourceAccount).tansfer(destinationAccount, 500.0);
        assertEquals(500.0, sourceAccount.getBalance(), 0.001);
        assertEquals(1000.0, destinationAccount.getBalance(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeTransfer() {
        ((Checkings)sourceAccount).tansfer(destinationAccount, -100.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInsufficientFundsTransfer() {
        ((Checkings)sourceAccount).tansfer(destinationAccount, 2000.0);
    }
    
    @Test
    public void testZeroBalanceAfterTransfer() throws InsufficientFundsException {
        ((Checkings)sourceAccount).tansfer(destinationAccount, 1000.0);
        assertEquals(0.0, sourceAccount.getBalance(), 0.001);
        assertEquals(1500.0, destinationAccount.getBalance(), 0.001);
    }
    
    @Test
    public void testMultipleTransfers() throws InsufficientFundsException {
        ((Checkings)sourceAccount).tansfer(destinationAccount, 300.0);
        ((Checkings)sourceAccount).tansfer(destinationAccount, 200.0);
        assertEquals(500.0, sourceAccount.getBalance(), 0.001);
        assertEquals(1000.0, destinationAccount.getBalance(), 0.001);
    }
}
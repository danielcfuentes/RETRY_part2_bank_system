package com.banksystem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class WithdrawTest {
    private Account testAccount;
    
    @Before
    public void setUp() {
        testAccount = AccountFactory.createAccount(
            AccountFactory.CHECKING,
            "TEST001",
            1000.0,
            0.0
        );
    }
    
    @Test
    public void testValidWithdraw() throws InsufficientFundsException {
        testAccount.withdraw(500.0);
        assertEquals(500.0, testAccount.getBalance(), 0.001);
    }
    
    @Test(expected = (InsufficientFundsException.class))
    public void testWithdrawMoreThanBalance() throws InsufficientFundsException {
        testAccount.withdraw(2000.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeWithdraw() throws InsufficientFundsException {
        testAccount.withdraw(-100.0);
    }
    
    @Test
    public void testMultipleWithdraws() throws InsufficientFundsException {
        testAccount.withdraw(300.0);
        testAccount.withdraw(200.0);
        assertEquals(500.0, testAccount.getBalance(), 0.001);
    }
}
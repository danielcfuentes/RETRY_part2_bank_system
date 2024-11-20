package com.banksystem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class WithdrawTest {
    private Account testAccount;
    
    @Before
    public void setUp() {
        testAccount = new Account("TEST001", 1000.0) {};
    }
    
    @Test
    public void testValidWithdraw() {
        testAccount.withdraw(500.0);
        assertEquals(500.0, testAccount.getBalance(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testWithdrawMoreThanBalance() {
        testAccount.withdraw(2000.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeWithdraw() {
        testAccount.withdraw(-100.0);
    }
    
    @Test
    public void testMultipleWithdraws() {
        testAccount.withdraw(300.0);
        testAccount.withdraw(200.0);
        assertEquals(500.0, testAccount.getBalance(), 0.001);
    }
}
package com.banksystem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class DepositTest {
    private Account testAccount;
    
    @Before
    public void setUp() {
        // Create a concrete implementation of the abstract Account class for testing
        testAccount = new Account("TEST001", 1000.0) {};
    }
    
    @Test
    public void testValidDeposit() {
        testAccount.deposit(500.0);
        assertEquals(1500.0, testAccount.getBalance(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeDeposit() {
        testAccount.deposit(-100.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testZeroDeposit() {
        testAccount.deposit(0.0);
    }
    
    @Test
    public void testMultipleDeposits() {
        testAccount.deposit(500.0);
        testAccount.deposit(300.0);
        assertEquals(1800.0, testAccount.getBalance(), 0.001);
    }
}
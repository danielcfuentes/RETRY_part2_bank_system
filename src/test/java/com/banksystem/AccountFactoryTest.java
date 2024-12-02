package com.banksystem;

import org.junit.Test;
import static org.junit.Assert.*;

public class AccountFactoryTest {
    
    @Test
    public void testCreateCheckingAccount() {
        Account account = AccountFactory.createAccount(
            AccountFactory.CHECKING,
            "1001",
            1000.0,
            0.0
        );
        
        assertNotNull("Account should not be null", account);
        assertTrue("Should be instance of Checkings", account instanceof Checkings);
        assertEquals("Account number should match", "1001", account.getAccountNumber());
        assertEquals("Balance should match", 1000.0, account.getBalance(), 0.001);
    }
    
    @Test
    public void testCreateSavingsAccount() {
        Account account = AccountFactory.createAccount(
            AccountFactory.SAVINGS,
            "2001",
            2000.0,
            0.0
        );
        
        assertNotNull("Account should not be null", account);
        assertTrue("Should be instance of Savings", account instanceof Savings);
        assertEquals("Account number should match", "2001", account.getAccountNumber());
        assertEquals("Balance should match", 2000.0, account.getBalance(), 0.001);
    }
    
    @Test
    public void testCreateCreditAccount() {
        Account account = AccountFactory.createAccount(
            AccountFactory.CREDIT,
            "3001",
            -500.0,
            5000.0
        );
        
        assertNotNull("Account should not be null", account);
        assertTrue("Should be instance of Credit", account instanceof Credit);
        assertEquals("Account number should match", "3001", account.getAccountNumber());
        assertEquals("Balance should match", -500.0, account.getBalance(), 0.001);
        assertEquals("Credit limit should match", 5000.0, ((Credit)account).getCreditLimit(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testCreateInvalidAccountType() {
        AccountFactory.createAccount(
            "INVALID_TYPE",
            "9999",
            1000.0,
            0.0
        );
    }
    
    @Test
    public void testGenerateCheckingAccountNumber() {
        String accountNumber = AccountFactory.generateAccountNumber(
            AccountFactory.CHECKING,
            "42"
        );
        assertEquals("1042", accountNumber);
    }
    
    @Test
    public void testGenerateSavingsAccountNumber() {
        String accountNumber = AccountFactory.generateAccountNumber(
            AccountFactory.SAVINGS,
            "42"
        );
        assertEquals("2042", accountNumber);
    }
    
    @Test
    public void testGenerateCreditAccountNumber() {
        String accountNumber = AccountFactory.generateAccountNumber(
            AccountFactory.CREDIT,
            "42"
        );
        assertEquals("3042", accountNumber);
    }
    
    @Test
    public void testGenerateAccountNumberWithPadding() {
        String accountNumber = AccountFactory.generateAccountNumber(
            AccountFactory.CHECKING,
            "5"
        );
        assertEquals("1005", accountNumber);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testGenerateAccountNumberInvalidType() {
        AccountFactory.generateAccountNumber(
            "INVALID_TYPE",
            "42"
        );
    }
    
    @Test(expected = NumberFormatException.class)
    public void testGenerateAccountNumberInvalidCustomerId() {
        AccountFactory.generateAccountNumber(
            AccountFactory.CHECKING,
            "abc"  // non-numeric ID
        );
    }
}
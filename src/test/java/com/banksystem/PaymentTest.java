package com.banksystem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class PaymentTest {
    private Person payer;
    private Person receiver;
    private Account payerAccount;
    private Account receiverAccount;
    
    @Before
    public void setUp() {
        payer = new Person("John Doe") {};
        receiver = new Person("Jane Smith") {};
        
        payerAccount = AccountFactory.createAccount(
            AccountFactory.CHECKING,
            "PAY001",
            1000.0,
            0.0
        );
        
        receiverAccount = AccountFactory.createAccount(
            AccountFactory.CHECKING,
            "REC001",
            500.0,
            0.0
        );
        
        List<Account> payerAccounts = new ArrayList<>();
        payerAccounts.add(payerAccount);
        payer.setAccounts(payerAccounts);
        
        List<Account> receiverAccounts = new ArrayList<>();
        receiverAccounts.add(receiverAccount);
        receiver.setAccounts(receiverAccounts);
    }
    
    @Test
    public void testValidPayment() throws InsufficientFundsException {
        payer.pay(receiver, payerAccount, receiverAccount, 500.0);
        assertEquals(500.0, payerAccount.getBalance(), 0.001);
        assertEquals(1000.0, receiverAccount.getBalance(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPaymentExceedingBalance() throws InsufficientFundsException {
        payer.pay(receiver, payerAccount, receiverAccount, 2000.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativePayment() {
        payer.pay(receiver, payerAccount, receiverAccount, -100.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testPaymentWithInvalidAccount() {
        Account invalidAccount = new Account("INV001", 1000.0) {};
        payer.pay(receiver, invalidAccount, receiverAccount, 500.0);
    }
}
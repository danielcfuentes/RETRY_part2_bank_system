package com.banksystem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class CreditLimitTest {
    private NewUsers newUsers;
    private Map<String, Customer> existingCustomers;
    private Map<String, String> validUserData;
    
    @Before
    public void setUp() {
        existingCustomers = new HashMap<>();
        newUsers = new NewUsers(existingCustomers);
        
        validUserData = new HashMap<>();
        validUserData.put("firstName", "John");
        validUserData.put("lastName", "Test");
        validUserData.put("dob", "01-Jan-90");
        validUserData.put("address", "123 Test St");
        validUserData.put("city", "Test City");
        validUserData.put("state", "TX");
        validUserData.put("zip", "12345");
        validUserData.put("phone", "123-456-7890");
    }
    
    @Test
    public void testLowCreditScore() {
        validUserData.put("creditScore", "550");
        Customer customer = newUsers.createUser(validUserData);
        Credit creditAccount = (Credit) customer.getAccounts().get(2);
        assertTrue(creditAccount.getCreditLimit() >= 100.0);
        assertTrue(creditAccount.getCreditLimit() <= 699.0);
    }
    
    @Test
    public void testMediumCreditScore() {
        validUserData.put("creditScore", "650");
        Customer customer = newUsers.createUser(validUserData);
        Credit creditAccount = (Credit) customer.getAccounts().get(2);
        assertTrue(creditAccount.getCreditLimit() >= 700.0);
        assertTrue(creditAccount.getCreditLimit() <= 4999.0);
    }
    
    @Test
    public void testHighCreditScore() {
        validUserData.put("creditScore", "800");
        Customer customer = newUsers.createUser(validUserData);
        Credit creditAccount = (Credit) customer.getAccounts().get(2);
        assertTrue(creditAccount.getCreditLimit() >= 16000.0);
        assertTrue(creditAccount.getCreditLimit() <= 25000.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidLowCreditScore() {
        validUserData.put("creditScore", "299");
        newUsers.createUser(validUserData);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidHighCreditScore() {
        validUserData.put("creditScore", "851");
        newUsers.createUser(validUserData);
    }
}
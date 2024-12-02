package com.banksystem;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class CustomerCreationTest {
    private NewUsers newUsers;
    private Map<String, String> validUserData;
    
    @Before
    public void setUp() {
        newUsers = new NewUsers(new HashMap<>());
        
        validUserData = new HashMap<>();
        validUserData.put("firstName", "John");
        validUserData.put("lastName", "Test");
        validUserData.put("dob", "01-Jan-90");
        validUserData.put("address", "123 Test St");
        validUserData.put("city", "Test City");
        validUserData.put("state", "TX");
        validUserData.put("zip", "12345");
        validUserData.put("phone", "123-456-7890");
        validUserData.put("creditScore", "700");
    }
    
    @Test
    public void testValidCustomerCreation() {
        Customer customer = newUsers.createUser(validUserData);
        assertNotNull(customer);
        assertEquals("John Test", customer.getName());
        assertEquals(3, customer.getAccounts().size());
        assertTrue(customer.getAccounts().get(0) instanceof Checkings);
        assertTrue(customer.getAccounts().get(1) instanceof Savings);
        assertTrue(customer.getAccounts().get(2) instanceof Credit);
    }
    
    @Test
    public void testAccountNumberGeneration() {
        Customer customer = newUsers.createUser(validUserData);
        String checkingNumber = customer.getAccounts().get(0).getAccountNumber();
        String savingsNumber = customer.getAccounts().get(1).getAccountNumber();
        String creditNumber = customer.getAccounts().get(2).getAccountNumber();
        
        assertTrue(checkingNumber.startsWith("1"));
        assertTrue(savingsNumber.startsWith("2"));
        assertTrue(creditNumber.startsWith("3"));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testMissingRequiredField() {
        validUserData.remove("phone");
        newUsers.createUser(validUserData);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testInvalidCreditScore() {
        validUserData.put("creditScore", "invalid");
        newUsers.createUser(validUserData);
    }
    
    @Test
    public void testInitialAccountBalances() {
        Customer customer = newUsers.createUser(validUserData);
        assertEquals(0.0, customer.getAccounts().get(0).getBalance(), 0.001);
        assertEquals(0.0, customer.getAccounts().get(1).getBalance(), 0.001);
        assertEquals(0.0, customer.getAccounts().get(2).getBalance(), 0.001);
    }
}
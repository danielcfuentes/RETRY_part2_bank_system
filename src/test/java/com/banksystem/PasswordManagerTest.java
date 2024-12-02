package com.banksystem;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.io.*;
import java.nio.file.*;

public class PasswordManagerTest {
    private PasswordManager passwordManager;
    private static final String TEST_CUSTOMER_ID = "TEST001";
    private static final String PASSWORD_FILE = "user_passwords.csv";
    
    @Before
    public void setUp() throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(PASSWORD_FILE))) {
            writer.println("CustomerID,Password");
        }
        passwordManager = new PasswordManager();
    }
    
    @After
    public void tearDown() throws IOException {
        Files.deleteIfExists(Paths.get(PASSWORD_FILE));
    }
    
    @Test
    public void testNewCustomerHasNoPassword() {
        assertFalse(passwordManager.hasPassword(TEST_CUSTOMER_ID));
    }
    
    @Test
    public void testSetAndVerifyPassword() {
        passwordManager.setPassword(TEST_CUSTOMER_ID, "testpass123");
        assertTrue(passwordManager.hasPassword(TEST_CUSTOMER_ID));
        assertTrue(passwordManager.verifyPassword(TEST_CUSTOMER_ID, "testpass123"));
    }
    
    @Test
    public void testIncorrectPassword() {
        passwordManager.setPassword(TEST_CUSTOMER_ID, "correctpass");
        assertFalse(passwordManager.verifyPassword(TEST_CUSTOMER_ID, "wrongpass"));
    }
    
    @Test
    public void testPasswordUpdate() {
        passwordManager.setPassword(TEST_CUSTOMER_ID, "oldpass");
        passwordManager.setPassword(TEST_CUSTOMER_ID, "newpass");
        assertFalse(passwordManager.verifyPassword(TEST_CUSTOMER_ID, "oldpass"));
        assertTrue(passwordManager.verifyPassword(TEST_CUSTOMER_ID, "newpass"));
    }
    
    @Test
    public void testNonExistentCustomer() {
        assertFalse(passwordManager.hasPassword("NONEXISTENT"));
        assertFalse(passwordManager.verifyPassword("NONEXISTENT", "anypassword"));
    }
}
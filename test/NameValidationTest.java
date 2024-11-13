package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class NameValidationTest {
    private NewUsers newUsers;
    private Map<String, Customer> existingCustomers;
    
    @Before
    public void setUp() {
        existingCustomers = new HashMap<>();
        existingCustomers.put("John Doe", new Customer("John Doe", "1"));
        existingCustomers.put("Jane Smith", new Customer("Jane Smith", "2"));
        newUsers = new NewUsers(existingCustomers);
    }
    
    @Test
    public void testUniqueNameIsValid() {
        assertTrue(newUsers.isValidNewCustomerName("Alice", "Johnson"));
    }
    
    @Test
    public void testExistingNameIsInvalid() {
        assertFalse(newUsers.isValidNewCustomerName("John", "Doe"));
    }
    
    @Test
    public void testSameFirstNameDifferentLast() {
        assertTrue(newUsers.isValidNewCustomerName("John", "Smith"));
    }
    
    @Test
    public void testDifferentFirstNameSameLast() {
        assertTrue(newUsers.isValidNewCustomerName("Bob", "Smith"));
    }
}
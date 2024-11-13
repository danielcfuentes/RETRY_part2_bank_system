package test;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

public class BorrowTest {
    private Credit creditAccount;
    private static final double CREDIT_LIMIT = 5000.0;
    
    @Before
    public void setUp() {
        creditAccount = new Credit("CRED001", 0.0, CREDIT_LIMIT);
    }
    
    @Test
    public void testValidBorrow() {
        creditAccount.borrow(1000.0);
        assertEquals(-1000.0, creditAccount.getBalance(), 0.001);
        assertEquals(1000.0, creditAccount.getPrinciple(), 0.001);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testBorrowExceedingLimit() {
        creditAccount.borrow(CREDIT_LIMIT + 100.0);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNegativeBorrow() {
        creditAccount.borrow(-100.0);
    }
    
    @Test
    public void testMultipleBorrows() {
        creditAccount.borrow(1000.0);
        creditAccount.borrow(500.0);
        assertEquals(-1500.0, creditAccount.getBalance(), 0.001);
        assertEquals(1500.0, creditAccount.getPrinciple(), 0.001);
    }
}
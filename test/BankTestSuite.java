package test;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    DepositTest.class,
    WithdrawTest.class,
    BorrowTest.class,
    NameValidationTest.class,
    PaymentTest.class
})
public class BankTestSuite {
    // This class remains empty,
    // it is used only as a holder for the above annotations
}
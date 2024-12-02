package com.banksystem;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({
    DepositTest.class,
    WithdrawTest.class,
    BorrowTest.class,
    NameValidationTest.class,
    PaymentTest.class,
    TransferTest.class,
    CreditLimitTest.class,
    CustomerCreationTest.class,
    PasswordManagerTest.class,
    AccountFactoryTest.class
})

public class BankTestSuite {}
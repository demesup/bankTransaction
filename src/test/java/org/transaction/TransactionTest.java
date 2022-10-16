package org.transaction;


import org.junit.jupiter.api.Test;
import org.transaction.enums.Currency;

class TransactionTest {
    static Transaction testTransaction = new Transaction(
            "1234567890123456",
            "65432109876543210",
            600,
            Currency.UAH,
            "test");

    @Test
    void getSenderNumber() {

    }

    @Test
    void getReceiverNumber() {
    }

    @Test
    void testToString() {
    }
}
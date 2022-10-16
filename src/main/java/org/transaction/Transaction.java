package org.transaction;

import lombok.Getter;
import lombok.Setter;
import org.transaction.enums.Currency;
import org.transaction.enums.Status;
import org.transaction.exception.NotCardNumberException;

import java.time.LocalDateTime;

@Getter
@Setter
public class Transaction {
    private long senderNumber;
    private long receiverNumber;
    private int sum;
    private Currency currency;
    private LocalDateTime transactionDateTime;
    private String purpose;
    private Status status;

    public Transaction(String senderNumber, String receiverNumber, int sum, Currency currency, String purpose) {
        this.senderNumber = checkedNumber(senderNumber);
        this.receiverNumber = checkedNumber(receiverNumber);
        this.sum = sum;
        this.currency = currency;
        this.purpose = purpose;
        transactionDateTime = LocalDateTime.now();
        status = Status.REQUEST;
    }

    private long checkedNumber(String number) {
        try {
            if (number.replaceAll("[\\s\\-]", "").length() != 16) throw new RuntimeException();
            return Long.parseLong(number);
        } catch (RuntimeException e) {
            throw new NotCardNumberException(number + " cannot be transformed to card number");
        }
    }

    public String getSenderNumber() {
        return saveNumber(senderNumber);
    }

    public String getReceiverNumber() {
        return saveNumber(receiverNumber);
    }

    public void setSenderNumber(String senderNumber) {
        this.senderNumber = checkedNumber(senderNumber);
    }

    public void setReceiverNumber(String receiverNumber) {
        this.receiverNumber = checkedNumber(receiverNumber);
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "senderNumber=" + saveNumber(senderNumber) +
                ", receiverNumber=" + saveNumber(receiverNumber) +
                ", sum=" + sum +
                ", currency=" + currency +
                ", transactionDateTime=" + transactionDateTime +
                ", purpose='" + purpose + '\'' +
                ", status=" + status +
                '}';
    }

    private String saveNumber(long senderNumber) {
        return String.valueOf(senderNumber).replaceAll("\\b(\\d{4})(\\d{8})(\\d{4})", "$1-XXXX-XXXX-$3");
    }
}

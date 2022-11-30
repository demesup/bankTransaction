package org.transactionmanager.transaction;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.transactionmanager.exception.CardNumberException;
import org.transactionmanager.filemanager.Parsable;
import org.transactionmanager.transaction.enums.Currency;
import org.transactionmanager.transaction.enums.Status;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

import static org.utils.Patterns.cardNumberPattern;
import static org.utils.Patterns.checkMatchToPattern;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder("uuid,senderNumber,receiverNumber,sum,currency,dateTime,purpose,status")
public class Transaction extends Parsable {
    public enum TransactionField {SENDER_NUMBER, RECEIVER_NUMBER, SUM, CURRENCY, DATE_TIME, PURPOSE, STATUS}

    private UUID uuid;

    private String senderNumber;
    private String receiverNumber;
    private int sum;
    private Currency currency;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateTime;
    private String purpose;
    private Status status;

    public Transaction(String senderNumber, String receiverNumber, int sum, Currency currency, String purpose) {
        this.senderNumber = senderNumber;
        this.receiverNumber = receiverNumber;
        if (Objects.equals(senderNumber, receiverNumber)) throw new RuntimeException();
        this.sum = sum;
        this.currency = currency;
        this.purpose = purpose;
        dateTime = LocalDateTime.now();
        status = Status.REQUEST;
        uuid = UUID.randomUUID();
    }

    @Override
    public String toString() {
        return "Transaction: " +
                "\n\tuuid=" + uuid.toString() +
                "\n\tsenderNumber=" +
                saveNumber(senderNumber) +
                ", \n\treceiverNumber=" +
                saveNumber(receiverNumber) +
                ", \n\tsum=" + sum +
                ", \n\tcurrency=" + currency +
                ", \n\tdateTime=" + dateTime +
                ", \n\tpurpose='" + purpose + '\'' +
                ", \n\tstatus=" + status;
    }

    private String saveNumber(String senderNumber) {
        return senderNumber.replaceAll("\\b(\\d{4})(\\d{8})(\\d{4})", "$1-XXXX-XXXX-$3");
    }

    public void setSenderNumber(String senderNumber) {
        checkNumber(senderNumber);
        this.senderNumber = senderNumber;
    }

    public void setReceiverNumber(String receiverNumber) {
        checkNumber(receiverNumber);
        this.receiverNumber = receiverNumber;
    }

    private void checkNumber(String number) {
        if (!checkMatchToPattern(cardNumberPattern(), number)) {
            throw new CardNumberException("[" + number + "] is not card number");
        }
        number = number.replaceAll("-", "");
       try {
           if (number.equals(receiverNumber)) {
               throw new CardNumberException("Sender number and receiver are identical");
           }
       }catch (NullPointerException ignored){}
    }
}

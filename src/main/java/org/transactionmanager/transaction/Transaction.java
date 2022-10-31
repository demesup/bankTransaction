package org.transactionmanager.transaction;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.transactionmanager.filemanager.Parsable;
import org.transactionmanager.transaction.enums.Currency;
import org.transactionmanager.transaction.enums.Status;

import java.time.LocalDateTime;
import java.util.Objects;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder("senderNumber,receiverNumber,sum,currency,dateTime,purpose,status")
public class Transaction extends Parsable {
    enum TransactionField{SENDER_NUMBER,RECEIVER_NUMBER, SUM, CURRENCY, DATE_TIME, PURPOSE,STATUS}
    private String senderNumber;
    private String receiverNumber;
    private int sum;
    private Currency currency;
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
    }

    @Override
    public String toString() {
        return "Transaction: " +
                "\n\t\tsenderNumber=" +
//                saveNumber(
                        senderNumber
//                )
                +
                ", \n\treceiverNumber=" +
//                saveNumber(
                        receiverNumber
//                )
                +
                ", \n\tsum=" + sum +
                ", \n\tcurrency=" + currency +
                ", \n\tdateTime=" + dateTime +
                ", \n\tpurpose='" + purpose + '\'' +
                ", \n\tstatus=" + status ;
    }

    private String saveNumber(String senderNumber) {
        return senderNumber.replaceAll("\\b(\\d{4})(\\d{8})(\\d{4})", "$1-XXXX-XXXX-$3");
    }
}

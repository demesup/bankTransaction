package org.transactionmanager.transaction;

import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.transactionmanager.Action;
import org.transactionmanager.filemanager.Parser;
import org.transactionmanager.filemanager.ParserType;
import org.transactionmanager.filemanager.parser.*;
import org.transactionmanager.transaction.enums.Currency;
import org.transactionmanager.transaction.enums.Status;
import org.utils.exception.ExitException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

import static org.transactionmanager.filemanager.ParserType.*;
import static org.transactionmanager.filemanager.parser.XMLParser.nodeField;
import static org.transactionmanager.transaction.Transaction.TransactionField.*;
import static org.utils.Patterns.*;
import static org.utils.Read.*;
import static org.utils.Utils.numberedArray;

@Slf4j
public class TransactionController implements XMLParsable<Transaction> {
    public static List<Transaction> transactions;
    public static Map<ParserType, Parser<Transaction>> parsers = new HashMap<>();

    static {
        parsers.put(XML, new XMLParser<>(new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.xml"), new TransactionController()));
        parsers.put(CSV, new CSVParser<>(new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.csv"), getCsvSchema()));
        parsers.put(YAML, new YamlParser<>(new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.yaml")));
        parsers.put(JSON, new JsonParser<>(new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.json")));
    }

    public static Map<Action, Runnable> actionRunnableHashMap = new HashMap<>();

    static {
        actionRunnableHashMap.put(Action.ADD, TransactionController::addTransaction);
        actionRunnableHashMap.put(Action.CHANGE, TransactionController::changeTransaction);
        actionRunnableHashMap.put(Action.DELETE, TransactionController::removeTransaction);
        actionRunnableHashMap.put(Action.EXIT, () -> {
            throw new ExitException();
        });
    }

    public static Map<Transaction.TransactionField, Consumer<Transaction>> fieldSetters = new HashMap<>();

    static {
        fieldSetters.put(SUM, transaction -> {
            try {
                transaction.setSum(readPositiveNumber("Enter sum"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fieldSetters.put(STATUS, transaction -> {
            try {
                transaction.setStatus(readEnumValue(Status.values(), "Enter status"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fieldSetters.put(PURPOSE, transaction -> {
            try {
                transaction.setPurpose(read("Enter purpose"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fieldSetters.put(CURRENCY, transaction -> {
            try {
                transaction.setCurrency(readEnumValue(Currency.values(), "Enter currency"));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fieldSetters.put(DATE_TIME, transaction -> transaction.setDateTime(LocalDateTime.now()));
        fieldSetters.put(SENDER_NUMBER, transaction -> {
            try {
                transaction.setSenderNumber(
                        (askStringWhileDoesNotMatchToPattern(cardNumberPattern(), "Enter sender number")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        fieldSetters.put(RECEIVER_NUMBER, transaction -> {
            try {
                transaction.setSenderNumber(
                        (askStringWhileDoesNotMatchToPattern(cardNumberPattern(), "Enter receiver number")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    @SneakyThrows
    public static void start() {
        try {
            while (true) {
                actionRunnableHashMap.get(readEnumValue(Action.values(), "Enter action")).run();
            }
        } catch (ExitException e) {
            if (!inputEqualsYes("Exit? ")) start();
        }
    }

    private static void addTransaction() {
        try {
            transactions.add(createTransaction());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Transaction createTransaction() throws IOException {
        var senderNumber = askStringWhileDoesNotMatchToPattern(cardNumberPattern(), "Enter sender card number");
        var receiverNumber = askStringWhileDoesNotMatchToPattern(cardNumberPattern(), "Enter receiver card number");
        var sum = readNumber("Enter sum");
        var currency = readEnumValue(Currency.values());
        var purpose = read("Enter purpose");
        return new Transaction(senderNumber, receiverNumber, sum, currency, purpose);
    }

    private static void removeTransaction() {
        try {
            transactions.remove(chooseTransaction().orElseThrow());
        } catch (Exception e) {
            log.debug(e.getMessage());
        }
    }

    private static Optional<Transaction> chooseTransaction() throws IOException {
        if (transactions.isEmpty()) {
            log.debug("Transaction list is empty");
            return Optional.empty();
        }

        log.debug(numberedArray(transactions.toArray()));
        return Optional.of(transactions.get(readNumber(transactions.size())));
    }

    private static void changeTransaction() {
        try {
            var transaction = chooseTransaction().orElseThrow();

            var fieldToChange = readEnumValue(
                    Transaction.TransactionField.values(),
                    "Choose parameter to change"
            );
            fieldSetters.get(fieldToChange).accept(transaction);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    private static String checkedCardNumber(String number) {
        if (checkMatchToPattern(cardNumberPattern(), number)) {
            return number;
        } else {
            throw new RuntimeException(number + " cannot be transformed to card number");
        }
    }


    private static CsvSchema getCsvSchema() {
        return CsvSchema.builder().setUseHeader(true)
                .addColumn("senderNumber")
                .addColumn("receiverNumber")
                .addColumn("sum")
                .addColumn("currency")
                .addColumn("dateTime")
                .addColumn("purpose")
                .addColumn("status")
                .build();
    }

    @Override
    public Optional<Transaction> readElement(Node node) {
        Transaction transaction = new Transaction();

        var nodes = node.getChildNodes();
        for (int i = 0; i < nodes.getLength(); i++) {
            var current = nodes.item(i);

            if (current.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }

            final String textContent = current.getTextContent();
            switch (current.getNodeName()) {
                case "senderNumber" -> transaction.setSenderNumber(checkedCardNumber(textContent));
                case "receiverNumber" -> transaction.setReceiverNumber(checkedCardNumber(textContent));
                case "sum" -> transaction.setSum(getAnInt(textContent));
                case "currency" -> transaction.setCurrency(Currency.valueOf(textContent));
                case "dateTime" -> transaction.setDateTime(LocalDateTime.parse(textContent));
                case "purpose" -> transaction.setPurpose(textContent);
                case "status" -> transaction.setStatus(Status.valueOf(textContent));
            }
        }
        return Optional.of(transaction);
    }

    @Override
    public Node nodeFromObject(Transaction transaction, Document document) {
        var transactionNode = document.createElement("transaction");

        transactionNode.appendChild(nodeField("senderNumber", String.valueOf(transaction.getSenderNumber()), document));
        transactionNode.appendChild(nodeField("receiverNumber", String.valueOf(transaction.getReceiverNumber()), document));
        transactionNode.appendChild(nodeField("sum", String.valueOf(transaction.getSum()), document));
        transactionNode.appendChild(nodeField("currency", String.valueOf(transaction.getCurrency()), document));
        transactionNode.appendChild(nodeField("dateTime", String.valueOf(transaction.getDateTime()), document));
        transactionNode.appendChild(nodeField("purpose", transaction.getPurpose(), document));
        transactionNode.appendChild(nodeField("status", String.valueOf(transaction.getStatus()), document));

        return transactionNode;
    }

    private static int getAnInt(String textContent) {
        var i = Integer.parseInt(textContent);
        if (i > 0) return i;
        throw new RuntimeException();
    }
}


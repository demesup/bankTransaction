package org.transactionmanager.transaction;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.transactionmanager.Action;
import org.transactionmanager.Repository;
import org.transactionmanager.filemanager.ParserType;
import org.transactionmanager.filemanager.XMLParsable;
import org.transactionmanager.filemanager.parser.Parser;
import org.transactionmanager.transaction.enums.Currency;
import org.transactionmanager.transaction.enums.Status;
import org.utils.exception.EmptyListException;
import org.utils.exception.ExitException;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.transactionmanager.Repository.*;
import static org.transactionmanager.filemanager.parser.XMLParser.nodeField;
import static org.utils.Patterns.*;
import static org.utils.Read.*;
import static org.utils.Utils.listInSeparatedLines;

@Slf4j
public class TransactionController implements XMLParsable<Transaction> {
    String listName = "transactions";

    public static void run() {
        log.info("Started at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
        log.info(runRepository());
        try {
            if (readList()) {
                log.debug(listInSeparatedLines(transactions));
            }
            new TransactionController().start();
            log.info(writeList());
        } catch (Exception ignored) {
        }
        log.info("Ended at: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    private static String runRepository() {
        Repository repository = new Repository();
        try {
            repository.fillParsers();
            repository.fillActions();
            repository.fillSetters();
            return "All data is loaded";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    @SneakyThrows
    public void start() {
        try {
            workWithTransactions();
        } catch (EmptyListException e) {
            if (!inputEqualsYes("List is empty. Exit? ")) start();
        } catch (ExitException e) {
            if (!inputEqualsYes("Exit? ")) start();
        } catch (Exception e) {
            log.error(e.getMessage());

        }

    }

    @SneakyThrows
    private void workWithTransactions() {
        while (true) {
            actionRunnableHashMap.get(readEnumValue(Action.values(), "Enter action")).run();
        }
    }

    private static String writeList() throws IOException {
        if (transactions.isEmpty()) return "List is empty";
        try {
            if (inputEqualsYes("Save to all files?")) {
                return parsers.values().stream().map(parser -> parser.writeList(transactions)).collect(Collectors.joining("\n"));
            }
            return getParser().writeList(transactions);
        } catch (IndexOutOfBoundsException e) {
            log.debug(e.getMessage());
            return parsers.values().stream().map(p -> p.writeList(transactions)).collect(Collectors.joining("\n"));
        }
    }


    private static boolean readList() throws IOException {
        if (!inputEqualsYes("Do you want to get list form a file?")) {
            transactions = new ArrayList<>();
            return false;
        }
        try {
            transactions = getParser().readList(Transaction.class);
            return true;
        } catch (IllegalArgumentException e) {
            if (inputEqualsYes("Try again?")) return readList();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        transactions = new ArrayList<>();
        return false;
    }

    private static Parser<Transaction> getParser() throws IOException {
        return parsers.get(readEnumValue(ParserType.values(), "Enter type"));
    }

    public void addTransaction() {
        try {
            Transaction transaction = createTransaction();
            transactions.add(transaction);
            log.info("New transaction is created with uuid " + transaction.getUuid().toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Transaction createTransaction() throws IOException {
        var senderNumber = askStringWhileDoesNotMatchToPattern(cardNumberPattern(), "Enter sender card number");
        String receiverNumber;
        do {
            receiverNumber = askStringWhileDoesNotMatchToPattern(cardNumberPattern(), "Enter receiver card number");
        } while (senderNumber.replaceAll("-","")
                .equals(receiverNumber.replaceAll("-","")));
        var sum = readNumber("Enter sum");
        var currency = readEnumValue(Currency.values());
        var purpose = read("Enter purpose");
        return new Transaction(senderNumber, receiverNumber, sum, currency, purpose);
    }

    @SneakyThrows
    public void removeTransaction() {
        Transaction transaction = chooseTransaction();
        transactions.remove(transaction);
        log.info("Transaction " + transaction.getUuid() + " is removed");
    }

    private Transaction chooseTransaction() throws IOException {
        checkList();
        log.debug(listInSeparatedLines(transactions));
        return transactions.get(readNumber(transactions.size()));
    }

    @SneakyThrows
    public void changeTransaction() {
        var transaction = chooseTransaction();

        var fieldToChange = readEnumValue(
                Transaction.TransactionField.values(),
                "Choose parameter to change"
        );
        fieldSetters.get(fieldToChange).accept(transaction);
        log.info("Transaction " + transaction.getUuid() + " is changed");
    }

    private void checkList() {
        if (transactions.isEmpty()) throw new EmptyListException();
    }


    private String checkedCardNumber(String number) {
        if (checkMatchToPattern(cardNumberPattern(), number)) {
            return number;
        } else {
            throw new RuntimeException(number + " cannot be transformed to card number");
        }
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
                case "uuid" -> transaction.setUuid(UUID.fromString(textContent));
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
    public Node nodeFromObject(Transaction transaction, Document document, Node root) {
        Node transactionNode = document.createElement("transaction");
        root.appendChild(transactionNode);
        transactionNode.appendChild(nodeField("uuid", transaction.getUuid().toString(), document));
        transactionNode.appendChild(nodeField("senderNumber", String.valueOf(transaction.getSenderNumber()), document));
        transactionNode.appendChild(nodeField("receiverNumber", String.valueOf(transaction.getReceiverNumber()), document));
        transactionNode.appendChild(nodeField("sum", String.valueOf(transaction.getSum()), document));
        transactionNode.appendChild(nodeField("currency", String.valueOf(transaction.getCurrency()), document));
        transactionNode.appendChild(nodeField("dateTime", String.valueOf(transaction.getDateTime()), document));
        transactionNode.appendChild(nodeField("purpose", transaction.getPurpose(), document));
        transactionNode.appendChild(nodeField("status", String.valueOf(transaction.getStatus()), document));

        return transactionNode;
    }

    private int getAnInt(String textContent) {
        var i = Integer.parseInt(textContent);
        if (i > 0) return i;
        throw new RuntimeException();
    }
}


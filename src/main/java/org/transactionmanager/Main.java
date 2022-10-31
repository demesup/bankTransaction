package org.transactionmanager;


import lombok.extern.slf4j.Slf4j;
import org.transactionmanager.filemanager.Parser;
import org.transactionmanager.filemanager.ParserType;
import org.transactionmanager.transaction.Transaction;
import org.transactionmanager.transaction.TransactionController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import static org.transactionmanager.transaction.TransactionController.parsers;
import static org.transactionmanager.transaction.TransactionController.transactions;
import static org.utils.Read.inputEqualsYes;
import static org.utils.Read.readEnumValue;
import static org.utils.Utils.listInSeparatedLines;

@Slf4j
public class Main {

    public static void main(String[] args) throws IOException {
        try {
            printFileStats();
            if (readList()) {
                log.debug(listInSeparatedLines(transactions));
            }
            TransactionController.start();
            log.info(writeList());
        } catch (IOException e) {
            log.error(e.getMessage());
        }
    }

    private static void printFileStats() {
        parsers.values().stream().map(Parser::getFile).forEach(file -> {
            boolean isEmpty = file.length() == 0;
            log.debug(file.getName() + " is  empty: " + isEmpty);
        });
    }

    private static String writeList() throws IOException {
        try {
            log.debug("Enter number of file type or 4 to save to all of them:");
            return getParser().writeList(transactions);
        } catch (IndexOutOfBoundsException e) {
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
}
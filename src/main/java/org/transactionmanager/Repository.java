package org.transactionmanager;

import lombok.extern.slf4j.Slf4j;
import org.transactionmanager.filemanager.parser.Parser;
import org.transactionmanager.filemanager.ParserType;
import org.transactionmanager.filemanager.parser.CSVParser;
import org.transactionmanager.filemanager.parser.JsonParser;
import org.transactionmanager.filemanager.parser.XMLParser;
import org.transactionmanager.filemanager.parser.YamlParser;
import org.transactionmanager.transaction.Transaction;
import org.transactionmanager.transaction.TransactionController;
import org.transactionmanager.transaction.enums.Currency;
import org.transactionmanager.transaction.enums.Status;
import org.utils.exception.ExitException;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static org.transactionmanager.filemanager.ParserType.*;
import static org.transactionmanager.filemanager.ParserType.JSON;
import static org.transactionmanager.transaction.Transaction.TransactionField.*;
import static org.utils.Patterns.askStringWhileDoesNotMatchToPattern;
import static org.utils.Patterns.cardNumberPattern;
import static org.utils.Read.*;
import static org.utils.Read.readEnumValue;

@Slf4j
public class Repository {
    public static List<Transaction> transactions = new ArrayList<>();
    public static Map<ParserType, Parser<Transaction>> parsers = new HashMap<>();
    public static Map<Action, Runnable> actionRunnableHashMap = new HashMap<>();
    public static Map<Transaction.TransactionField, Consumer<Transaction>> fieldSetters = new HashMap<>();

    public void fillParsers() {
        File xml = new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.xml");
        File csv = new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.csv");
        File yaml = new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.yaml");
        File json = new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.json");
        putParser(XML, new XMLParser<>(xml, new TransactionController()));
        putParser(CSV, new CSVParser<>(csv));
        putParser(YAML, new YamlParser<>(yaml));
        putParser(JSON, new JsonParser<>(json));
    }

    private void putParser(ParserType type, Parser<Transaction> parser) {
        try {
            parsers.put(type, parser);
        } catch (Throwable e) {
            log.warn(e.getMessage());
        }
    }


    public void fillActions() {
        TransactionController controller = new TransactionController();
        actionRunnableHashMap.put(Action.ADD, controller::addTransaction);
        actionRunnableHashMap.put(Action.CHANGE, controller::changeTransaction);
        actionRunnableHashMap.put(Action.DELETE, controller::removeTransaction);
        actionRunnableHashMap.put(Action.PRINT, () -> transactions.forEach(System.out::println));
        actionRunnableHashMap.put(Action.EXIT, () -> {
                    throw new ExitException();
                }
        );
    }


    public void fillSetters() {
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
}
package org.transaction;


import org.transaction.enums.Currency;
import org.transaction.parser.CSVParser;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// TODO: Програма для обробки банківських транзакцій.
//  Опишіть у чотирьох вищенаведених форматах дані:
//  список банківських транзакцій з параметрами
//  номер картки відправника,
//  номер картки отримувача,
//  сума,
//  валюта,
//  дата і час операції,
//  призначення платежу,
//  статус платежу.
//  Програма повинна запитувати на старті у користувача шлях до файлу з транзакціями.
//  По розширенню файла (.xml, .csv, .json, .yaml), визначте яким чином його зчитувати.
//  Зчитайте файл і надрукуйте список транзакцій.
//  Дайте можливість редагувати транзакції.
//  Перед завершенням роботи програми, не забудьте зберегти результат у файл(можна у новий).
//  Не забувайте про ООП, обробку помилок, логування.
//  Програма повинна збиратися в jar з усіма ресурсами (logback config).
//  Коректно працювати через java -jar запуск, не тільки з ідеї.
public class Main {
    static List<Transaction> transactionsTest = new ArrayList<>();
    static File JSON_FILE = new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.json");
    static File XML_FILE = new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.xml");
    static File CSV_FILE = new File("D:\\IdeaProjects\\bankTransaction\\src\\main\\resources\\transactions.csv");

    static {
        transactionsTest.add(new Transaction(
                "1234567890123456",
                "9876543210987654",
                100,
                Currency.USD,
                "test1"
        ));
        transactionsTest.add(new Transaction(
                "1234567890123456",
                "1000000000000000",
                100,
                Currency.USD,
                "test2"
        ));
        transactionsTest.add(new Transaction(
                "9876543210987654",
                "1234567890123456",
                1000,
                Currency.UAH,
                "test3"
        ));
    }

    public static void main(String[] args) throws IOException {
        /**writeList(transactionsTest,
         JSON_FILE,
         Transaction.class);
         System.out.println(listInSeparatedLines(readList(JSON_FILE, Transaction.class)));*/

        //System.out.println(listInSeparatedLines(XMLParser.readList(XML_FILE)));
        // XMLParser.writeList(XML_FILE, transactionsTest);

//        CSVParser.readList(CSV_FILE, Transaction.class, getCsvSchema());
//        writeList(CSV_FILE,transactionsTest, getCsvSchema());


    }
}
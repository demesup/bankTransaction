package org.transaction.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.transaction.Transaction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser extends Parser {
    private CsvSchema schema;

    public CSVParser(File file, CsvSchema schema) {
        super(file);
        this.schema = schema;
    }

    @Override
    public <T> void writeList(List<T> list) throws IOException{
        if (list.isEmpty()) {
            return;
        }
        CsvMapper mapper = new CsvMapper();
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        mapper.registerModule(new JavaTimeModule());


        ObjectWriter writer = mapper.writerFor(list.get(0).getClass()).with(schema);

        writer.writeValues(file).writeAll(list);

        System.out.println("Users saved to csv file under path: ");
        System.out.println(file);

    }

    @Override
    public  <T> List<T> readList(Class<T> tClass) {
        try {
            MappingIterator<T> iterator = new CsvMapper()
                    .registerModule(new JavaTimeModule())
                    .readerFor(tClass)
                    .with(schema)
                    .readValues(file);
            return iterator.readAll();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String[] toStringArray(Transaction item) {
        List<String> list = new ArrayList<>();
        list.add(String.valueOf(item.getSenderNumber()));
        list.add(String.valueOf(item.getReceiverNumber()));
        list.add(String.valueOf(item.getSum()));
        list.add(String.valueOf(item.getCurrency()));
        list.add(String.valueOf(item.getTransactionDateTime()));
        list.add(item.getPurpose());
        list.add(String.valueOf(item.getStatus()));
        return list.toArray(new String[list.size()]);
    }
}

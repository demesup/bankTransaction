package org.transactionmanager.filemanager.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.transactionmanager.filemanager.Parsable;
import org.transactionmanager.filemanager.Parser;
import org.transactionmanager.filemanager.ParserType;
import org.utils.exception.EmptyListException;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser<T extends Parsable> extends Parser<T> {
    private final CsvSchema schema;

    public CSVParser(File file, CsvSchema schema) {
        super(file);
        checkType(file, ParserType.CSV);
        this.schema = schema;
    }

    @Override
    public String writeList(List<T> list) {
        try {
            checkList(list);
            getWriter(list).writeValues(file).writeAll(list);
            return "List was saved under the path: \n" + file.getPath();
        } catch (EmptyListException e) {
            return "List is empty";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private ObjectWriter getWriter(List<T> list) {
        CsvMapper mapper = new CsvMapper();
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        mapper.registerModule(new JavaTimeModule());
        return mapper.writerFor(list.get(0).getClass()).with(schema);
    }

    @Override
    public List<T> readList(Class<T> tClass) {
        try {
            checkFile(file);
            MappingIterator<T> iterator = new CsvMapper()
                    .registerModule(new JavaTimeModule())
                    .readerFor(tClass)
                    .with(schema)
                    .readValues(file);
            return iterator.readAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

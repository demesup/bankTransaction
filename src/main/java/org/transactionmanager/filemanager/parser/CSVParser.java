package org.transactionmanager.filemanager.parser;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.transactionmanager.filemanager.Parsable;
import org.transactionmanager.filemanager.ParserType;
import org.utils.exception.EmptyFileException;
import org.utils.exception.EmptyListException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CSVParser<T extends Parsable> extends Parser<T> {
    final CsvMapper mapper;

    public CSVParser(File file) {
        super(file);
        checkType(file, ParserType.CSV);
        mapper = new CsvMapper();
        mapper.configure(JsonGenerator.Feature.IGNORE_UNKNOWN, true);
        mapper.registerModule(new JavaTimeModule());
    }

    @Override
    public String writeList(List<T> list) {
        try {
            checkList(list);
            mapper.writerWithSchemaFor(list.get(0).getClass()).writeValues(file).writeAll(list);
            return "List was saved under the path: \n" + file.getPath();
        } catch (EmptyListException e) {
            return "List is empty";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> readList(Class<T> tClass) {
        try {
            checkFile(file);
        } catch (EmptyFileException e) {
            return new ArrayList<>();
        }
        return getList(tClass);
    }

    private List<T> getList(Class<T> tClass) {
        try {
            checkFile(file);
            MappingIterator<T> iterator = mapper.readerWithSchemaFor(tClass)
                    .readValues(file);
            return iterator.readAll();
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

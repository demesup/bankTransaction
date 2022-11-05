package org.transactionmanager.filemanager.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.transactionmanager.filemanager.Parsable;
import org.transactionmanager.filemanager.ParserType;
import org.utils.exception.EmptyListException;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class JsonParser<T extends Parsable> extends Parser<T> {
    public JsonParser(File file) {
        super(file);
        checkType(file, ParserType.JSON);
    }

    private static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.registerModule(new JavaTimeModule());
    }

    @Override
    public String writeList(List<T> list) {
        try {
            checkList(list);
            OM.writerFor(
                    OM.getTypeFactory().constructCollectionLikeType(List.class, list.get(0).getClass())
            ).writeValue(file, list);
            return "List was saved under the path: \n" + file.getPath();
        } catch (EmptyListException e) {
            return "List is empty";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> readList(Class<T> cl) throws IOException {
        checkFile(file);
        return OM.readValue(file, OM.getTypeFactory().constructCollectionLikeType(List.class, cl));

    }
}

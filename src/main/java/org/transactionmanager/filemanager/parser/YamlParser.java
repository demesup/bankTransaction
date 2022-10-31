package org.transactionmanager.filemanager.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.transactionmanager.filemanager.Parsable;
import org.transactionmanager.filemanager.Parser;
import org.transactionmanager.filemanager.ParserType;
import org.utils.exception.EmptyListException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class YamlParser<T extends Parsable> extends Parser<T> {
    static ObjectMapper OM;

    static {
        OM = new ObjectMapper(new YAMLFactory());
        OM.findAndRegisterModules();
    }

    public YamlParser(File file) {
        super(file);
        checkType(file, ParserType.YAML);
    }

    @Override
    public String writeList(List<T> list) {
        try {
            checkList(list);
            OM.writerFor(OM.getTypeFactory().constructCollectionLikeType(List.class, list.get(0).getClass()))
                    .writeValue(file, list);
            return "List was saved under the path: \n" + file.getPath();
        } catch (EmptyListException e) {
            return "List is empty";
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<T> readList(Class<T> tClass) {
        CollectionType listType = OM.getTypeFactory().constructCollectionType(List.class, tClass);

        try {
            checkFile(file);
            return OM.readValue(file, listType);
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
}

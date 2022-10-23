package org.transaction.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonParser extends Parser {
    public JsonParser(File file) {
        super(file);
    }

    private static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.registerModule(new JavaTimeModule());
    }

    @Override
    public <T> void writeList(List<T> list) {
        if (list.isEmpty()) {
            System.out.println("List is empty");
            return;
        }

        try {
            OM.writerFor(OM.getTypeFactory().constructCollectionLikeType(List.class, list.get(0).getClass()))
                    .writeValue(file, list);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public <T> List<T> readList(Class<T> cl) {
        if (file.length() == 0) return new ArrayList<>();
        try {
            return OM.readValue(file, OM.getTypeFactory().constructCollectionLikeType(List.class, cl));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }
}

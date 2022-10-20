package org.transaction.parser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {
    private static final ObjectMapper OM = new ObjectMapper();

    static {
        OM.registerModule(new JavaTimeModule());
    }

    public static void writeList(List<?> list, File file, Class<?> cl) {
        if (list.isEmpty()) {
            System.out.println("List is empty");
            return;
        }

        try {
            OM.writerFor(OM.getTypeFactory().constructCollectionLikeType(List.class, cl))
                    .writeValue(file, list);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static List<?> readList(File file, Class<?> cl) {
        if (file.length() == 0) return new ArrayList<>();
        try {
            return OM.readValue(file, OM.getTypeFactory().constructCollectionLikeType(List.class, cl));
        } catch (IOException e) {
            System.out.println(e.getMessage());
            return new ArrayList<>();
        }
    }
}

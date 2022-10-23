package org.transaction.parser;

import lombok.AllArgsConstructor;

import java.io.File;
import java.io.IOException;
import java.util.List;

@AllArgsConstructor
public abstract class Parser {
    protected File file;

    public abstract <T> void writeList(List<T> list) throws IOException;
    public abstract <T> List<T> readList(Class<T> tClass);
}

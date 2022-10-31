package org.transactionmanager.filemanager;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.transactionmanager.exception.TypeMismatchException;
import org.utils.exception.EmptyFileException;
import org.utils.exception.EmptyListException;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Getter
@AllArgsConstructor
public abstract class Parser<T extends Parsable> {
    protected File file;

    public abstract String writeList(List<T> list);

    public abstract List<T> readList(Class<T> tClass) throws IOException;

    protected void checkList(List<T> list) {
        if (list.isEmpty()) throw new EmptyListException();
    }

    protected void checkFile(File file) {
        if (file.length() == 0) throw new EmptyFileException(file);
    }
    protected void checkType(File file, ParserType type){
        if (!file.getPath().endsWith(type.getEnding())) throw new TypeMismatchException(type, file);
    }
}

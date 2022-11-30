package org.transactionmanager.exception;

import org.transactionmanager.filemanager.ParserType;

import java.io.File;

public class TypeMismatchException extends RuntimeException {
    public TypeMismatchException(ParserType type, File file) {
        super("Expected: " + type.getEnding() + ", receied: " + file.getName().substring(file.getName().lastIndexOf(".")));
    }
}

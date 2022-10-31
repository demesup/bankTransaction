package org.transactionmanager.filemanager.parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import java.util.Optional;

public interface XMLParsable<T> {
    Optional<T> readElement(Node node);

    Node nodeFromObject(T object, Document document);
}

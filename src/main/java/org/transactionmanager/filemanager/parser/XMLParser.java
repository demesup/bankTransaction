package org.transactionmanager.filemanager.parser;

import org.transactionmanager.filemanager.Parsable;
import org.transactionmanager.filemanager.Parser;
import org.transactionmanager.filemanager.ParserType;
import org.utils.exception.EmptyListException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.transform.OutputKeys.INDENT;

public class XMLParser<T extends Parsable> extends Parser<T> {
    Document document;
    XMLParsable<T> controller;

    public XMLParser(File file, XMLParsable<T> controller) {
        super(file);
        checkType(file, ParserType.XML);
        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(file);
        } catch (SAXException | IOException | ParserConfigurationException e) {
            e.printStackTrace();
        }
        this.controller = controller;
    }

    @Override
    public String writeList(List<T> list) {
        try {
            checkList(list);

            Element root = document.createElement("transactions");
            document.appendChild(root);

            list.forEach(a -> root.appendChild(controller.nodeFromObject(a, document)));

            transform();
            return "List saved under the path: " + file;
        } catch (EmptyListException e) {
            return "List is empty";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void transform() throws TransformerException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(INDENT, "yes");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(file);

        transformer.transform(source, result);
    }

    public static Node nodeField(String name, String value, Document document) {
        Element node = document.createElement(name);
        node.setTextContent(value);

        return node;
    }

    @Override
    public List<T> readList(Class<T> tClass) {
        try {
            checkFile(file);
        } catch (EmptyListException e) {
            return new ArrayList<>();
        }

        return readList(document.getFirstChild());
    }

    private List<T> readList(Node rootNode) {
        List<T> list = new ArrayList<>();
        NodeList childNodes = rootNode.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node item = childNodes.item(i);
            if (item.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            try {
                list.add(controller.readElement(item).orElseThrow());
            } catch (Exception ignored) {
            }
        }
        return list;
    }
}
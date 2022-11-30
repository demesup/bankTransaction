package org.transactionmanager.filemanager.parser;

import lombok.SneakyThrows;
import org.transactionmanager.filemanager.Parsable;
import org.transactionmanager.filemanager.ParserType;
import org.transactionmanager.filemanager.XMLParsable;
import org.utils.exception.EmptyFileException;
import org.utils.exception.EmptyListException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class XMLParser<T extends Parsable> extends Parser<T> {
    XMLParsable<T> controller;

    @SneakyThrows
    public XMLParser(File file, XMLParsable<T> controller) {
        super(file);
        checkType(file, ParserType.XML);
        if (!file.exists()) {
            throw new EmptyFileException(file);
        }
        this.controller = controller;
    }

    @Override
    public String writeList(List<T> list) {
        try {
            checkList(list);
            Document document = getDocument();
            parseList(list,document);
            Transformer transformer = getTransformer();
            return "List saved under the path:\n" + write(document, transformer);
        } catch (EmptyListException e) {
            return "List is empty";
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private File write(Document document, Transformer transformer) throws TransformerException {
        DOMSource source = new DOMSource(document);
        File file = new File(this.file.getPath());
        StreamResult result = new StreamResult(file);
        transformer.transform(source, result);
        return file;
    }

    private static Transformer getTransformer() throws TransformerConfigurationException {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        return transformer;
    }

    private static Document getDocument() throws ParserConfigurationException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.newDocument();
    }

    private Document getDocument(File file) throws SAXException, IOException, ParserConfigurationException {
        DocumentBuilder documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        try {
            return documentBuilder.parse(file);
        } catch (Throwable e) {
            try (FileWriter writer = new FileWriter(file)) {
                writer.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<transactions></transactions>");
            } catch (Exception f) {
                throw new RuntimeException(f);
            }
            return documentBuilder.parse(file);
        }
    }

    private void parseList(List<T> list, Document document) {
        Node root = document.createElement("transactions");
        document.appendChild(root);
        list.forEach(a -> root.appendChild(controller.nodeFromObject(a, document, root)));
    }

    public static Node nodeField(String name, String value, Document document) {
        Element node = document.createElement(name);
        node.setTextContent(value);

        return node;
    }

    @Override
    public List<T> readList(Class<T> tClass) {

        try {
            Document document = getDocument(file);

            checkFile(file);
            return readList(document.getFirstChild());
        } catch (EmptyListException | ParserConfigurationException | SAXException | IOException e) {
            return new ArrayList<>();
        }
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
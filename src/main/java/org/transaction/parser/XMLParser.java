package org.transaction.parser;

import org.transaction.Transaction;
import org.transaction.enums.Currency;
import org.transaction.enums.Status;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static javax.xml.transform.OutputKeys.INDENT;

public class XMLParser {
    static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    static Document doc;

    static {
        try {
            doc = factory.newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static List<Transaction> readList(File file) throws ParserConfigurationException, IOException, SAXException {
        List<Transaction> list = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        var doc = factory.newDocumentBuilder().parse(file);

        Node rootNode = doc.getFirstChild();
        NodeList rootChildren = rootNode.getChildNodes();
        for (int i = 0; i < rootChildren.getLength(); i++) {
            list.add(readElement(rootChildren.item(i)));
        }

        return list;
    }

    private static Transaction readElement(Node item) {
        Transaction transaction = new Transaction();

        for (int i = 0; i < item.getChildNodes().getLength(); i++) {
            var thisOne = item.getChildNodes().item(i);
            final String textContent = thisOne.getTextContent();
            switch (thisOne.getNodeName()) {
                case "senderNumber" -> transaction.setSenderNumber(textContent);
                case "receiverNumber" -> transaction.setReceiverNumber(textContent);
                case "sum" -> transaction.setSum(Integer.parseInt(textContent));
                case "currency" -> transaction.setCurrency(Currency.valueOf(textContent));
                case "transactionDateTime" -> transaction.setTransactionDateTime(LocalDateTime.parse(textContent));
                case "purpose" -> transaction.setPurpose(textContent);
                case "status" -> transaction.setStatus(Status.valueOf(textContent));
            }
        }
        return transaction;
    }

    public static void writeList(File file, List<Transaction> list) throws ParserConfigurationException, IOException, SAXException, TransformerException {


        Element root = doc.createElement("transactions");
        doc.appendChild(root);

        list.forEach(a -> root.appendChild(nodeFromElement(a)));

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        // pretty print
        transformer.setOutputProperty(INDENT, "yes");

        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(file);

        transformer.transform(source, result);


    }

    private static Node nodeFromElement(Transaction a) {
        var transaction = doc.createElement("transaction");

        transaction.appendChild(transactionField("senderNumber", String.valueOf(a.getSenderNumber())));
        transaction.appendChild(transactionField("receiverNumber", String.valueOf(a.getReceiverNumber())));
        transaction.appendChild(transactionField("sum", String.valueOf(a.getSum())));
        transaction.appendChild(transactionField("currency", String.valueOf(a.getCurrency())));
        transaction.appendChild(transactionField("transactionDateTime", String.valueOf(a.getTransactionDateTime())));
        transaction.appendChild(transactionField("purpose", a.getPurpose()));
        transaction.appendChild(transactionField("status", String.valueOf(a.getStatus())));

        return transaction;
    }

    private static Node transactionField(String name, String value) {
        Element node = doc.createElement(name);
        node.setTextContent(value);

        return node;
    }
}

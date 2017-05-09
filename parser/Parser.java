package parser;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class Parser {
    public static Document startGameToXml(DocumentBuilder db) {
        return db.newDocument();
    }

    public static Document doMoveToXml(DocumentBuilder db) {
        return db.newDocument();
    }

    public static String documentToString(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.transform(source, result);
        return result.getWriter().toString();
    }
}

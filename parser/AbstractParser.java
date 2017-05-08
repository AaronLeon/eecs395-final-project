package parser;

import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilder;

public abstract class AbstractParser<T> {
    DocumentBuilder db;

    public AbstractParser(DocumentBuilder db) {
        this.db = db;
    }

    public abstract T fromXml(Document xml) throws Exception;

    public abstract Document toXml(T obj);
}
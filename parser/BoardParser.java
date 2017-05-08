package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.DocumentBuilder;

import parcheesi.Board;

public class BoardParser extends AbstractParser<Board> {
    public BoardParser(DocumentBuilder db) {
        super(db);
    }

    public Board fromXml(Document xml) throws Exception {
        if (!xml.getNodeName().equals("board")) {
            throw new Exception("Tried to parse XML Document that is not <board></board>");
        }

        Board board = new Board();

        Node current = xml.getFirstChild();
        while (current != null) {
            Board.BoardComponent bc;
            switch (current.getNodeName()) {
                case "start":
                    break;
            }
        }

        throw new NotImplementedException();
    }

    public Document toXml(Board board) {
        throw new NotImplementedException();
    }
}
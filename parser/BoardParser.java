package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.DocumentBuilder;

import parcheesi.Board;

public class BoardParser extends AbstractParser<Board> {
    public BoardParser(DocumentBuilder db) {
        super(db);
    }

    public Board fromXml(Document xml) throws Exception {
        Node type = xml.getFirstChild();
        if (!type.getNodeName().equals("board")) {
            throw new Exception("Tried to parse XML Document that is not <board></board>");
        }

        Board board = new Board();
        NodeList nodes = type.getChildNodes();
        int counter=0;
        int end=nodes.getLength();
        int length=0;
        Node boardNode = nodes.item(0);
        Node start = nodes.item(1);
        NodeList startPawns = nodes.item(1).getChildNodes();
        length = startPawns.getLength();
        Node main = nodes.item(3);
        NodeList mainPawns = nodes.item(4).getChildNodes();
        length = mainPawns.getLength();
        Node homeRows = nodes.item(5);
        NodeList homeRowPawns = nodes.item(6).getChildNodes();
        Node home = nodes.item(7);
        NodeList homePawns = nodes.item(8).getChildNodes();

        //start
        //main
        //home-rows
        //home
        return board;
    }

    public Document toXml(Board board) {
        throw new NotImplementedException();
    }
}
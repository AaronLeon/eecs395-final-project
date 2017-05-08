package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parcheesi.Pawn;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import javax.xml.parsers.DocumentBuilder;

import parcheesi.Board;

public class BoardParser extends AbstractParser<Board> {
    public BoardParser(DocumentBuilder db) {
        super(db);
    }

    public Board fromXml(Document xml) throws Exception {

        //EVERY OTHER ELEMENT IS "BLANK"

        Node root = xml.getFirstChild();
        if (!root.getNodeName().equals("board")) {
            throw new Exception("Tried to parse XML Document that is not <board></board>");
        }

        Board board = new Board();

        NodeList nodes = root.getChildNodes();
        int counter=0;
        int end=nodes.getLength();
        int length=0;
        Node boardNode = nodes.item(0);
        length=boardNode.getChildNodes().getLength();

        Node start = nodes.item(1);
        NodeList startPawns=start.getChildNodes();
        length=startPawns.getLength();
        for (int i=0;i<length;i++){
            Node pawn = startPawns.item(i);
            String name = pawn.getNodeName();
            if (name=="pawn"){
                //copied from pawnParser
                Node color = pawn.getFirstChild();
                Node id = color.getNextSibling();

                if (color == null || id == null
                        || !color.getNodeName().equals("color")
                        || !id.getNodeName().equals("id")) {
                    throw new Exception("Invalid Pawn XML Document");
                }

                String _color = color.getTextContent();
                int _id = Integer.parseInt(id.getTextContent());
                Pawn insertMe = new Pawn(_id, _color);
                // TODO: insert
            }
        }


        Node main = nodes.item(2);
        NodeList mainPawns = main.getChildNodes();
        length = mainPawns.getLength();
        for (int i=0;i<length;i++){
            Node piece_loc = startPawns.item(i);
            String name = piece_loc.getNodeName();
            if (name=="piece-loc"){
                //copied from pawnParser
                Node pawn= piece_loc.getFirstChild();
                Node loc = pawn.getNextSibling();

                if (pawn == null || loc == null
                        || !pawn.getNodeName().equals("pawn")
                        || !loc.getNodeName().equals("number")) {
                    throw new Exception("Invalid Pawn XML Document");
                }

                Node color = pawn.getFirstChild();
                Node id = color.getNextSibling();

                if (color == null || id == null
                        || !color.getNodeName().equals("color")
                        || !id.getNodeName().equals("id")) {
                    throw new Exception("Invalid Pawn XML Document");
                }

                String _color = color.getTextContent();
                int _id = Integer.parseInt(id.getTextContent());
                Pawn insertMe = new Pawn(_id, _color);
                insertMe.location=Integer.parseInt(loc.getTextContent());
                // TODO: modify board component bc and insert

            }
        }


        Node homeRows = nodes.item(3);
        NodeList homeRowPawns=homeRows.getChildNodes();
        length = homeRowPawns.getLength();
        for (int i=0;i<length;i++){
            Node piece_loc = startPawns.item(i);
            String name = piece_loc.getNodeName();
            if (name=="piece-loc"){
                //copied from pawnParser
                Node pawn= piece_loc.getFirstChild();
                Node loc = pawn.getNextSibling();

                if (pawn == null || loc == null
                        || !pawn.getNodeName().equals("pawn")
                        || !loc.getNodeName().equals("number")) {
                    throw new Exception("Invalid Pawn XML Document");
                }

                Node color = pawn.getFirstChild();
                Node id = color.getNextSibling();

                if (color == null || id == null
                        || !color.getNodeName().equals("color")
                        || !id.getNodeName().equals("id")) {
                    throw new Exception("Invalid Pawn XML Document");
                }

                String _color = color.getTextContent();
                int _id = Integer.parseInt(id.getTextContent());
                Pawn insertMe = new Pawn(_id, _color);
                insertMe.location=Integer.parseInt(loc.getTextContent());
                // TODO: modify board component bc and insert

            }
        }

        Node home = nodes.item(4);
        NodeList homePawns = home.getChildNodes();
        length = homePawns.getLength();
        for (int i=0;i<length;i++){
            Node pawn = startPawns.item(i);
            String name = pawn.getNodeName();
            if (name=="pawn"){
                //copied from pawnParser
                Node color = pawn.getFirstChild();
                Node id = color.getNextSibling();

                if (color == null || id == null
                        || !color.getNodeName().equals("color")
                        || !id.getNodeName().equals("id")) {
                    throw new Exception("Invalid Pawn XML Document");
                }

                String _color = color.getTextContent();
                int _id = Integer.parseInt(id.getTextContent());
                Pawn insertMe = new Pawn(_id, _color);
                // TODO: insert
            }
        }

        return board;
    }

    public Document toXml(Board board) {
        Document doc = db.newDocument();

        Element newBoard = doc.createElement("board");
        Element start = doc.createElement("start");
        Element main = doc.createElement("main");
        Element home_rows = doc.createElement("home-rows");
        Element home = doc.createElement("home");
        // TODO : read from board and insert
        return doc;
    }
}
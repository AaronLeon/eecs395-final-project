package parser;

import com.sun.xml.internal.bind.v2.TODO;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import parcheesi.Board;
import parcheesi.Pawn;
import parcheesi.Move;
import parcheesi.MoveHome;
import parcheesi.MoveMain;
import parcheesi.EnterPiece;
import parser.PawnParser;
import javax.xml.parsers.DocumentBuilder;


public class MoveParser extends AbstractParser<Board> {
    public MoveParser(DocumentBuilder db) {
        super(db);
    }

    public Move fromXml(Document xml) throws Exception {

        Node root = xml.getFirstChild();



        Node pawn = xml.getFirstChild();
        if (!pawn.getNodeName().equals("pawn")) {
            throw new Exception("Tried to parse XML Document that is not <pawn></pawn>");
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
        Pawn myPawn = new Pawn(_id, _color);



        if (root.getNodeName().equals("enter-piece")) {

            EnterPiece move = new EnterPiece(myPawn);
            return move;

        } else if (root.getNodeName().equals("move-piece-main")){
            int start = Integer.parseInt(pawn.getNextSibling().getTextContent());
            int distance = Integer.parseInt(pawn.getNextSibling().getTextContent());
            myPawn.location=start;
            //TODO: change board component
            //myPawn.bc
            MoveMain move = new MoveMain(myPawn,distance);
            return move;

        } else if (root.getNodeName().equals("move-piece-home")){
            int start = Integer.parseInt(pawn.getNextSibling().getTextContent());
            int distance = Integer.parseInt(pawn.getNextSibling().getTextContent());
            myPawn.location=start;
            //TODO: change board component
            //myPawn.bc
            MoveHome move = new MoveHome(myPawn,distance);
            return move;

        } else {
            throw new Exception("Invalid Pawn XML Document");
        }
    }

    public Document toXml(Move move) {
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
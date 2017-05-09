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


public class MoveParser extends AbstractParser<Move> {
    public MoveParser(DocumentBuilder db) {
        super(db);
    }

    public Move fromXml(Document xml) throws Exception {

        Node root = xml.getFirstChild();

        Node pawn = root.getFirstChild();
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
            return null;
        }
    }

    public Document toXml(Move move) {
        Document doc = db.newDocument();
        if (move instanceof MoveMain){
            Element root=doc.createElement("move-main");
            Element pawn=doc.createElement("pawn");
            Element color = doc.createElement("color");
            color.appendChild(doc.createTextNode(((MoveMain) move).pawn.color));
            Element id = doc.createElement("id");
            color.appendChild(doc.createTextNode(Integer.toString(((MoveMain) move).pawn.id)));
            Element start = doc.createElement("start");
            start.appendChild(doc.createTextNode(Integer.toString(((MoveMain) move).pawn.location)));
            Element distance = doc.createElement("distance");
            distance.appendChild(doc.createTextNode(Integer.toString(((MoveMain) move).distance)));

            pawn.appendChild(color);
            pawn.appendChild(id);
            root.appendChild(pawn);
            root.appendChild(start);
            root.appendChild(distance);
            doc.appendChild(root);
            return doc;


        } else if(move instanceof MoveHome) {
            Element root=doc.createElement("move-home");
            Element pawn=doc.createElement("pawn");
            Element color = doc.createElement("color");
            color.appendChild(doc.createTextNode(((MoveHome) move).pawn.color));
            Element id = doc.createElement("id");
            color.appendChild(doc.createTextNode(Integer.toString(((MoveHome) move).pawn.id)));
            Element start = doc.createElement("start");
            start.appendChild(doc.createTextNode(Integer.toString(((MoveHome) move).start)));
            Element distance = doc.createElement("distance");
            distance.appendChild(doc.createTextNode(Integer.toString(((MoveHome) move).distance)));

            pawn.appendChild(color);
            pawn.appendChild(id);
            root.appendChild(pawn);
            root.appendChild(start);
            root.appendChild(distance);
            doc.appendChild(root);
            return doc;

        } else if(move instanceof EnterPiece){
            Element root=doc.createElement("enter-piece");
            Element pawn=doc.createElement("pawn");
            Element color = doc.createElement("color");
            color.appendChild(doc.createTextNode(((EnterPiece) move).pawn.color));
            Element id = doc.createElement("id");
            color.appendChild(doc.createTextNode(Integer.toString(((EnterPiece) move).pawn.id)));
            pawn.appendChild(color);
            pawn.appendChild(id);
            root.appendChild(pawn);
            doc.appendChild(root);
            return doc;
        }else {
            return null;
        }
    }
}
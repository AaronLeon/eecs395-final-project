package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import parcheesi.*;

import javax.xml.parsers.DocumentBuilder;


public class MoveParser extends AbstractParser<Move> {
    public MoveParser(DocumentBuilder db) {
        super(db);
    }

    public Move fromXml(Document xml) throws Exception {

        Node root = xml.getFirstChild();

        Node pawn = root.getFirstChild();

        // TODO: Refactor to use PawnParser
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
        Pawn _pawn = new Pawn(_id, _color);

        if (root.getNodeName().equals("enter-piece")) {
            EnterPiece move = new EnterPiece(_pawn);
            return move;

        } else if (root.getNodeName().equals("move-piece-main")) {
            int start = Integer.parseInt(pawn.getNextSibling().getTextContent());
            int distance = Integer.parseInt(pawn.getNextSibling().getTextContent());
            _pawn.location = start;
            _pawn.bc = Board.BoardComponent.RING;
            MoveMain move = new MoveMain(_pawn, distance);
            return move;

        } else if (root.getNodeName().equals("move-piece-home")) {
            int start = Integer.parseInt(pawn.getNextSibling().getTextContent());
            int distance = Integer.parseInt(pawn.getNextSibling().getTextContent());
            _pawn.location = start;
            _pawn.bc = Board.BoardComponent.HOMEROW;
            MoveHome move = new MoveHome(_pawn, distance);
            return move;
        } else {
            throw new Exception("Invalid move type");
        }
    }

    public Document toXml(Move[] moves){
        Document doc = db.newDocument();
        Element root=doc.createElement("moves");
        for (Move move : moves){
            doc.appendChild(toXml(move));
        }
        return doc;
    }

    public Document toXml(Move move) {
        if (move instanceof EnterPiece) {
            return enterPieceToXml((EnterPiece) move);
        }
        else if (move instanceof MoveMain) {
            return moveMainToXml((MoveMain) move);
        }
        else if (move instanceof MoveHome) {
            return moveHomeToXml((MoveHome) move);
        }
        else {
            return null;
        }
    }

    private Document enterPieceToXml(EnterPiece move) {
        Document doc = db.newDocument();

        // TODO: Refactor to use PawnParser
        Element root = doc.createElement("enter-piece");
        Element pawn = doc.createElement("pawn");
        Element color = doc.createElement("color");
        color.appendChild(doc.createTextNode(move.pawn.color));
        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode(Integer.toString(move.pawn.id)));
        pawn.appendChild(color);
        pawn.appendChild(id);
        root.appendChild(pawn);
        doc.appendChild(root);
        return doc;
    }

    private Document moveMainToXml(MoveMain move) {
        Document doc = db.newDocument();

        // TODO: Refactor to use PawnParser
        Element root = doc.createElement("move-piece-main");
        Element pawn = doc.createElement("pawn");
        Element color = doc.createElement("color");
        color.appendChild(doc.createTextNode(move.pawn.color));
        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode(Integer.toString(move.pawn.id)));
        Element start = doc.createElement("start");
        start.appendChild(doc.createTextNode(Integer.toString(move.pawn.location)));
        Element distance = doc.createElement("distance");
        distance.appendChild(doc.createTextNode(Integer.toString(move.distance)));

        pawn.appendChild(color);
        pawn.appendChild(id);
        root.appendChild(pawn);
        root.appendChild(start);
        root.appendChild(distance);
        doc.appendChild(root);
        return doc;
    }

    private Document moveHomeToXml(MoveHome move) {
        Document doc = db.newDocument();

        // TODO: Refactor to use PawnParser
        Element root = doc.createElement("move-piece-home");
        Element pawn = doc.createElement("pawn");
        Element color = doc.createElement("color");
        color.appendChild(doc.createTextNode(move.pawn.color));
        Element id = doc.createElement("id");
        id.appendChild(doc.createTextNode(Integer.toString(move.pawn.id)));
        Element start = doc.createElement("start");
        start.appendChild(doc.createTextNode(Integer.toString(move.pawn.location)));
        Element distance = doc.createElement("distance");
        distance.appendChild(doc.createTextNode(Integer.toString(move.distance)));

        pawn.appendChild(color);
        pawn.appendChild(id);
        root.appendChild(pawn);
        root.appendChild(start);
        root.appendChild(distance);
        doc.appendChild(root);
        return doc;
    }
}
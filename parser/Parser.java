package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import parcheesi.Board;
import parcheesi.Move;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class Parser {
    public static Document generateStartGameXml(DocumentBuilder db, String color) {
        BoardParser boardParser = new BoardParser(db);
        DiceParser diceParser = new DiceParser(db);

        Document doc = db.newDocument();
        Node startGame = doc.createElement("start-game");
        startGame.appendChild(doc.createTextNode(color));

        doc.appendChild(startGame);

        return doc;
    }

    public static Document generateDoMoveXml(DocumentBuilder db, Board board, int[] dice) {
        BoardParser boardParser = new BoardParser(db);
        DiceParser diceParser = new DiceParser(db);

        Document doc = db.newDocument();
        Element doMove = doc.createElement("do-move");

        Node tempBoard = doc.importNode(boardParser.toXml(board).getFirstChild(), true);
        doMove.appendChild(tempBoard);

        Node tempDice = doc.importNode(diceParser.toXml(dice).getFirstChild(), true);
        doMove.appendChild(tempDice);

        doc.appendChild(doMove);
        return doc;
    }

    public static Document generateMovesXml(DocumentBuilder db, Move[] moves) {
        MoveParser moveParser = new MoveParser(db);

        Document doc = db.newDocument();
        Element movesRoot = doc.createElement("moves");

        for (Move move : moves) {
            Node tempMove = doc.importNode(moveParser.toXml(move).getFirstChild(), true);
            movesRoot.appendChild(tempMove);
        }

        doc.appendChild(movesRoot);

        return doc;
    }

    public static Document generateDoublesPenaltyXml(DocumentBuilder db) {
        Document doc = db.newDocument();
        doc.appendChild(doc.createElement("doubles-penalty"));
        return doc;
    }

    public static Document generateVoidXml(DocumentBuilder db) {
        Document doc = db.newDocument();
        doc.appendChild(doc.createElement("void"));
        return doc;
    }

    public static String documentToString(Document doc) throws Exception {
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        StreamResult result = new StreamResult(new StringWriter());
        DOMSource source = new DOMSource(doc);
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.setOutputProperty(OutputKeys.METHOD, "html");
        transformer.transform(source, result);
        return result.getWriter().toString();
    }
}

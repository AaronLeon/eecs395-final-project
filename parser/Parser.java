package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import parcheesi.Board;
import parcheesi.Move;
import parcheesi.Pair;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

public class Parser {
    public static Document generateStartGameXml(DocumentBuilder db, String color) {
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

    public static Move[] moveFromXml(DocumentBuilder db, Document doc) throws Exception {
        MoveParser moveParser = new MoveParser(db);

        Move[] result = new Move[4];
        Node root = doc.getFirstChild();

        int i = 0;
        Node current = root.getFirstChild();
        while (current != null || i < result.length) {
            Document moveDoc = db.newDocument();
            Node temp = moveDoc.importNode(current, true);
            moveDoc.appendChild(temp);
            result[i] = moveParser.fromXml(moveDoc);

            ++i;
            current = current.getNextSibling();
        }

        return result;
    }

    public static String startGameFromXml(DocumentBuilder db, Document doc) throws Exception {
        Node root = doc.getFirstChild();

        return root.getTextContent();
    }

    public static Pair<Board, int[]> doMoveFromXml(DocumentBuilder db, Document doc) throws Exception {
        BoardParser boardParser = new BoardParser(db);
        DiceParser diceParser = new DiceParser(db);
        Node root = doc.getFirstChild();

        Node board = root.getFirstChild();
        Node dice = board.getNextSibling();

        Document boardDoc = db.newDocument();
        Node importedNode = boardDoc.importNode(board, true);
        boardDoc.appendChild(importedNode);
        Board b = boardParser.fromXml(boardDoc);

        Document diceDoc = db.newDocument();
        importedNode = diceDoc.importNode(dice, true);
        diceDoc.appendChild(importedNode);
        int[] d = diceParser.fromXml(diceDoc);

        return new Pair<>(b, d);
    }
}

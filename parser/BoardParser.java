package parser;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;

import parcheesi.Board;
import parcheesi.Pawn;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class BoardParser extends AbstractParser<Board> {
    PawnParser pawnParser;
    public BoardParser(DocumentBuilder db) {
        super(db);
        pawnParser = new PawnParser(db);
    }

    public Board fromXml(Document xml) throws Exception {
        Node board = xml.getFirstChild();
        if (!board.getNodeName().equals("board")) {
            throw new Exception("Tried to parse XML Document that is not <board></board>");
        }

        Board b = new Board();
        Map<String, Pawn[]> pawns = new HashMap<String, Pawn[]>()
        {{
            put("blue", new Pawn[4]);
            put("yellow", new Pawn[4]);
            put("green", new Pawn[4]);
            put("red", new Pawn[4]);
        }};

        final Map<String, Board.BoardComponent> XML_TO_BC_MAP = new LinkedHashMap<String, Board.BoardComponent>()
        {{
            put("start", Board.BoardComponent.NEST);
            put("main", Board.BoardComponent.RING);
            put("home-rows", Board.BoardComponent.HOMEROW);
            put("home", Board.BoardComponent.HOME);
        }};

        Node currentBoardComponent = board.getFirstChild();
        for (Map.Entry<String, Board.BoardComponent> entry : XML_TO_BC_MAP.entrySet()) {
            if (currentBoardComponent == null || !currentBoardComponent.getNodeName().equals(entry.getKey())) {
                throw new Exception("Tried to parse XML Document that is not <board></board>");
            }

            Board.BoardComponent bc = entry.getValue();

            if (bc == Board.BoardComponent.NEST) {
                Node currentPawn = currentBoardComponent.getFirstChild();
                while (currentPawn != null) {
                    Document pawnDoc = db.newDocument();
                    Node temp = pawnDoc.importNode(currentPawn, true);
                    pawnDoc.appendChild(temp);

                    Pawn pawn = pawnParser.fromXml(pawnDoc);
                    pawn.bc = bc;
                    pawns.get(pawn.color)[pawn.id] = pawn;
                    b.nests.get(pawn.color)[pawn.id] = pawn;

                    currentPawn = currentPawn.getNextSibling();
                }

            } else if (bc == Board.BoardComponent.RING) {
                Node currentPieceLoc = currentBoardComponent.getFirstChild();
                while (currentPieceLoc != null) {
                    Pawn pawn = pawnFromPieceLocXml(currentPieceLoc);
                    pawn.bc = Board.BoardComponent.RING;
                    pawns.get(pawn.color)[pawn.id] = pawn;
                    b.ring[pawn.id] = pawn;
                    currentPieceLoc = currentPieceLoc.getNextSibling();
                }
            } else if (bc == Board.BoardComponent.HOMEROW) {
                Node currentPieceLoc = currentBoardComponent.getFirstChild();
                while (currentPieceLoc != null) {
                    Pawn pawn = pawnFromPieceLocXml(currentPieceLoc);
                    pawn.bc = Board.BoardComponent.HOMEROW;
                    pawns.get(pawn.color)[pawn.id] = pawn;
                    b.homeRows.get(pawn.color)[pawn.id] = pawn;
                    currentPieceLoc = currentPieceLoc.getNextSibling();
                }
            } else if (bc == Board.BoardComponent.HOME) {
                Node currentPawn = currentBoardComponent.getFirstChild();
                while (currentPawn != null) {
                    Document pawnDoc = db.newDocument();
                    Node temp = pawnDoc.importNode(currentPawn, true);
                    pawnDoc.appendChild(temp);

                    Pawn pawn = pawnParser.fromXml(pawnDoc);
                    pawn.bc = bc;
                    pawns.get(pawn.color)[pawn.id] = pawn;
                    b.homes.get(pawn.color)[pawn.id] = pawn;

                    currentPawn = currentPawn.getNextSibling();
                }
            } else {
                throw new Exception("Invalid Board Component");
            }

            currentBoardComponent = currentBoardComponent.getNextSibling();
        }
        return b;
    }

    public Document toXml(Board board) {
        Document doc = db.newDocument();

        Element boardRoot = doc.createElement("board");
        Element start = doc.createElement("start");
        Element main = doc.createElement("main");
        Element homeRows = doc.createElement("home-rows");
        Element home = doc.createElement("home");

        for (String color : Board.COLORS) {
            for (Pawn p : board.pawns.get(color)) {
                if (p.bc == Board.BoardComponent.NEST) {
                    Node importedPawn = doc.importNode(pawnParser.toXml(p).getFirstChild(), true);
                    start.appendChild(importedPawn);
                } else if (p.bc == Board.BoardComponent.RING) {
                    main.appendChild(pawnToPieceLocXml(p).getFirstChild());
                } else if (p.bc == Board.BoardComponent.HOMEROW) {
                    homeRows.appendChild(pawnToPieceLocXml(p).getFirstChild());
                } else if (p.bc == Board.BoardComponent.HOME) {
                    Node importedPawn = doc.importNode(pawnParser.toXml(p).getFirstChild(), true);
                    home.appendChild(importedPawn);
                }
            }
        }

        boardRoot.appendChild(start);
        boardRoot.appendChild(main);
        boardRoot.appendChild(homeRows);
        boardRoot.appendChild(home);
        doc.appendChild(boardRoot);

        return doc;
    }

    private Pawn pawnFromPieceLocXml(Node pieceLoc) throws Exception{
        if (!pieceLoc.getNodeName().equals("piece-loc")) {
            throw new Exception("Expected <piece-loc>");
        }
        Node pawnNode = pieceLoc.getFirstChild();
        Pawn pawn = pawnParser.fromXml((Document) pawnNode);

        Node locNode = pawnNode.getNextSibling();
        if (!locNode.getNodeName().equals("loc")) {
            throw new Exception("Expected <loc>");
        }
        int location = Integer.parseInt(locNode.getTextContent());
        pawn.location = location;

        return pawn;
    }

    private Node pawnToPieceLocXml(Pawn pawn) {
        Document doc = db.newDocument();
        Element pieceLoc = doc.createElement("piece-loc");
        pieceLoc.appendChild(pawnParser.toXml(pawn));
        Element loc = doc.createElement("loc");
        loc.appendChild(doc.createTextNode(Integer.toString(pawn.location)));
        pieceLoc.appendChild(loc);

        return doc;
    }
}
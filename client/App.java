package client;

import org.w3c.dom.Document;
import parcheesi.Board;
import parcheesi.Move;
import parcheesi.Pair;
import parser.Parser;
import strategy.MyStrategy;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class App {
    public void main(String[] args) {
        try {

            final String host = "127.0.0.1";
            final int port = Integer.parseInt("8000");
            final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            final DocumentBuilder db = dbf.newDocumentBuilder();

            Client client = new Client(host, port, db);

            Document startGameXml = Parser.stringToDocument(db, client.readInput());
            String color = Parser.startGameFromXml(db, startGameXml);
            System.out.println("Your color is: " + color);
            final MyStrategy strategy = new MyStrategy(color);

            while (client.isConnected()) {
                Document input = Parser.stringToDocument(db, client.readInput());
                String root = input.getFirstChild().getTextContent();

                switch (root) {
                    case "do-move":
                        Pair<Board, int[]> doMove = Parser.doMoveFromXml(db, input);
                        Board b = doMove.first;
                        int[] d = doMove.second;
                        Move[] moves = strategy.doMove(b, d);

                        Document output = (moves == null)
                                ? Parser.generateVoidXml(db)
                                : Parser.generateMovesXml(db, moves);
                        client.writeOutput(Parser.documentToString(output));
                        break;
                    case "doubles-penalty":
                        System.out.println("You got a doubles penalty!");
                        break;
                    default:
                        break;
                }
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

}

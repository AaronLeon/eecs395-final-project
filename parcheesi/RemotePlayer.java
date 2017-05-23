package parcheesi;

import parser.Parser;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import parser.*;

public class RemotePlayer extends SPlayer {
    Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private DocumentBuilder db;

    public RemotePlayer(String color, Socket socket, DocumentBuilder db) throws IOException {
        super(color);
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.db = db;
    }

    @Override
    public void startGame(String color) {
        try {
            Document startGameXml = Parser.generateStartGameXml(db, color);
            out.println(Parser.documentToString(startGameXml));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Move[] doMove(Board board, int[] dice) {
        Move[] moves = null;
        try {
            String promptMoves = Parser.documentToString(Parser.generateDoMoveXml(db, board, dice));
            out.println(promptMoves);

            Document movesXml = db.parse(in.readLine());
            moves = Parser.movesFromXml(db, movesXml);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return moves;
    }

    @Override
    public void doublesPenalty() {
        try {
            String doublesPenalty = Parser.documentToString(Parser.generateDoublesPenaltyXml(db));
            out.println(doublesPenalty);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

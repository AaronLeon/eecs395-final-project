package parcheesi;

import parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
            String startGame = Parser.documentToString(Parser.generateStartGameXml(db, color));
            out.println(startGame);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Move[] doMove(Board board, int[] rolls) {
        try {
            String promptMove = Parser.documentToString(Parser.generateDoMoveXml(db, board, rolls));
            out.println(promptMove);
            String input = in.readLine();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return new Move[0];
    }

    @Override
    public void doublesPenalty() {
        try {
            String doublesPenalty = Parser.documentToString(Parser.generateDoublesPenaltyXml(db));
            out.println(doublesPenalty);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}

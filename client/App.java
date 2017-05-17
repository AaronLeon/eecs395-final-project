package client;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import parcheesi.Board;
import parcheesi.Move;
import parcheesi.Pair;
import parser.Parser;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.Socket;

public class App extends Application {
    final String host = "127.0.0.1";
    final int port = Integer.parseInt("8000");

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        BufferedReader in;
        PrintWriter out;
        final DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            Socket socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
            final DocumentBuilder db = dbf.newDocumentBuilder();

            String buffer = in.readLine();
            InputStream is = new ByteArrayInputStream(buffer.getBytes());
            String color = Parser.startGameFromXml(db, db.parse(is));

            // TODO: Show player start game color
            System.out.println("Your color is: " + color);

            while (socket.isConnected()) {
                buffer = in.readLine();
                System.out.println(buffer);
                is.reset();
                is = new ByteArrayInputStream(buffer.getBytes());
                Document doMoveDoc = db.parse(is);
                doMoveDoc.getDocumentElement().normalize();
                System.out.println(Parser.documentToString(doMoveDoc));
                Pair<Board, int[]> doMove = Parser.doMoveFromXml(db, doMoveDoc);
                Board b = doMove.first;
                int[] d = doMove.second;

                UIBoard board = new UIBoard(b);
                Scene scene = new Scene(board, 700, 700);
                stage.setScene(scene);
                stage.show();

                // TODO: Make moves somehow
                Move[] moves = new Move[2];
                out.println(Parser.documentToString(Parser.generateMovesXml(db, moves)));
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        Board b = new Board();
        b.initPawns();
        UIBoard board = new UIBoard(b);
        System.out.println(board.getChildren());
    }

}



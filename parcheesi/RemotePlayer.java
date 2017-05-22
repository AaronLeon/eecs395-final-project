package parcheesi;

import parser.Parser;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
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
            String startGame = Parser.documentToString(Parser.generateStartGameXml(db, color));
            out.println(startGame);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private Pawn[] sortedPawns(Board board) {
        //modified sorting of pawns to prioritize pawns that are behind, in unsafe places, or in the
        Pawn[] pawns = board.pawns.get(this.color);
        Arrays.sort(pawns, (a, b) -> {
            if (a.bc == b.bc && a.location == b.location) {
                return 0;
            } else if (b.bc == Board.BoardComponent.HOME || b.bc == Board.BoardComponent.NEST
                    || (a.bc == Board.BoardComponent.HOMEROW && b.bc == Board.BoardComponent.RING)) {
                boolean aSafe = (Arrays.binarySearch(board.SAFE_LOCATIONS, a.location) >= 0);
                boolean bSafe = (Arrays.binarySearch(board.SAFE_LOCATIONS, b.location) >= 0);
                if (!aSafe && bSafe) {
                    return 1;
                } else if (aSafe && !bSafe) {
                    return -1;
                } else if ((a.bc == b.bc && a.location > b.location)) {
                    return -1;
                }
            }
            return 1;
        });
        return pawns;
    }

    @Override
    public Move[] doMove(Board board, int[] dice) {
        ArrayList<Move> moves = new ArrayList<Move>(4);
        Pawn[] sorted = sortedPawns(board);
        for (Pawn pawn : sorted) {
            if (pawn.bc == Board.BoardComponent.NEST && RuleEngine.canEnter(dice)) {
                EnterPiece m = new EnterPiece(pawn);
                moves.add(m);
                Parcheesi.consumeDice(dice, m);
            } else if (pawn.bc == Board.BoardComponent.RING) {
                for (int d : dice) {
                    MoveMain testedMove = new MoveMain(pawn, d);
                    if (!RuleEngine.isBlocked(board, testedMove)) {
                        moves.add(testedMove);
                        Parcheesi.consumeDice(dice, testedMove);
                    }
                }
            } else if (pawn.bc == Board.BoardComponent.HOMEROW) {
                for (int d : dice) {
                    MoveHome testedMove = new MoveHome(pawn, d);
                    if (!RuleEngine.isBlocked(board, testedMove)) {
                        moves.add(testedMove);
                        Parcheesi.consumeDice(dice, testedMove);
                    }
                }
            }
        }

        Move[] res = (Move[]) moves.toArray();
        return res;
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

package parcheesi;

import java.util.ArrayList;
import java.util.Arrays;

public class BackPawnPlayer extends MPlayer {
    public BackPawnPlayer(String color) {
        super(color);
    }

    private Pawn[] sortedPawns(Board board) {
        Pawn[] pawns = board.pawns.get(this.color);
        Arrays.sort(pawns, (a, b) -> {
            if (a.bc == b.bc && a.location == b.location) {
                return 0;
            }
            else if (b.bc == Board.BoardComponent.HOME || b.bc == Board.BoardComponent.NEST
                    || (a.bc == Board.BoardComponent.HOMEROW && b.bc == Board.BoardComponent.RING)
                    || (a.bc == b.bc && a.location > b.location)) {
                return -1;
            }
            else {
                return 1;
            }
        });
        return pawns;
    }

    @Override
    public Move[] doMove(Board board, int[] dice) {
        ArrayList<Move> moves = new ArrayList<Move>(4);
        Pawn[] sorted = sortedPawns(board);
        for (Pawn pawn: sorted) {
            if (pawn.bc == Board.BoardComponent.NEST && RuleEngine.canEnter(dice)) {
                EnterPiece m = new EnterPiece(pawn);
                moves.add(m);
                Parcheesi.consumeDice(dice, m);
            }
            else if (pawn.bc == Board.BoardComponent.RING) {
                for (int d: dice) {
                    MoveMain testedMove = new MoveMain(pawn, d);
                    if (!RuleEngine.isBlocked(board, testedMove)) {
                        moves.add(testedMove);
                        Parcheesi.consumeDice(dice, testedMove);
                    }
                }
            }
            else if (pawn.bc == Board.BoardComponent.HOMEROW) {
                for (int d: dice) {
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
}
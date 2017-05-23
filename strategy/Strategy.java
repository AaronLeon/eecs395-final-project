package strategy;

import parcheesi.*;

import java.util.ArrayList;

public abstract class Strategy {
    String color;

    public Strategy(String color) {
        this.color = color;
    }

    public abstract Pawn[] prioritizePawns(Board board);

    /**
     * Iterates through the current Strategy pawn priority, checking whether each move is legal and returning the
     * highest priority moves that are legal
     * @param board
     * @param dice
     * @return an array of the highest priority moves that are legal
     */
    public Move[] doMove(Board board, int[] dice) {
        ArrayList<Move> moves = new ArrayList<Move>(4);
        Pawn[] priority = prioritizePawns(board);
        for (Pawn pawn : priority) {
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

        return (moves.isEmpty())
                ? null
                : (Move[]) moves.toArray();
    }
}

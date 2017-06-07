package strategy;

import parcheesi.*;

import java.util.Arrays;

public class MyStrategy extends Strategy {
    public MyStrategy(String color) {
        super(color);
    }

    @Override
    public Pawn[] prioritizePawns(Board board) {
        //Prioritize pawns that are behind, in unsafe places, or in the nest
        Pawn[] pawns = board.pawns.get(this.color);
        Arrays.sort(pawns, (a, b) -> {
            if (a.location.equals(b.location)) {
                return 0;
            } else if (b.location.bc == Board.BoardComponent.HOME || b.location.bc == Board.BoardComponent.NEST
                    || (a.location.bc == Board.BoardComponent.HOMEROW && b.location.bc == Board.BoardComponent.RING)) {
                boolean aSafe = Board.isSafe(a.location.index);
                boolean bSafe = Board.isSafe(b.location.index);
                if (!aSafe && bSafe) {
                    return 1;
                } else if (aSafe && !bSafe) {
                    return -1;
                } else if ((a.location.bc == b.location.bc && a.location.index > b.location.index)) {
                    return -1;
                }
            }
            return 1;
        });
        return pawns;
    }
}

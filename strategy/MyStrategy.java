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
            if (a.bc == b.bc && a.location == b.location) {
                return 0;
            } else if (b.bc == Board.BoardComponent.HOME || b.bc == Board.BoardComponent.NEST
                    || (a.bc == Board.BoardComponent.HOMEROW && b.bc == Board.BoardComponent.RING)) {
                boolean aSafe = Board.isSafe(a.location);
                boolean bSafe = Board.isSafe(b.location);
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
}

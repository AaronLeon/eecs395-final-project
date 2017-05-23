package strategy;

import parcheesi.*;

import java.util.Arrays;

public class FrontPawnStrategy extends Strategy {
    public FrontPawnStrategy(String color) {
        super(color);
    }

    @Override
    public Pawn[] prioritizePawns(Board board) {
        Pawn[] pawns = board.pawns.get(this.color);
        Arrays.sort(pawns, (a, b) -> {
            if (a.bc == b.bc && a.location == b.location) {
                return 0;
            }
            else if (b.bc == Board.BoardComponent.HOME || b.bc == Board.BoardComponent.NEST
                    || (a.bc == Board.BoardComponent.HOMEROW && b.bc == Board.BoardComponent.RING)
                    || (a.bc == b.bc && a.location > b.location)) {
                return 1;
            }
            else {
                return -1;
            }
        });
        return pawns;
    }
}

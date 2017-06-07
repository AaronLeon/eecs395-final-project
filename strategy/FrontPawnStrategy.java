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
            if (a.location.equals(b.location)) {
                return 0;
            }
            else if (b.location.bc == Board.BoardComponent.HOME || b.location.bc == Board.BoardComponent.NEST
                    || (a.location.bc == Board.BoardComponent.HOMEROW && b.location.bc == Board.BoardComponent.RING)
                    || (a.location.bc == b.location.bc && a.location.index > b.location.index)) {
                return 1;
            }
            else {
                return -1;
            }
        });
        return pawns;
    }
}

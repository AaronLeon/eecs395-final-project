package strategy;

import parcheesi.Board;
import parcheesi.Pawn;

import java.util.Arrays;
import java.util.Collections;

public class BackPawnStrategy extends Strategy {
    public BackPawnStrategy(String color) {
        super(color);
    }

    @Override
    public Pawn[] prioritizePawns(Board board) {
        FrontPawnStrategy frontPawnStrategy = new FrontPawnStrategy(color);
        Pawn[] pawns = frontPawnStrategy.prioritizePawns(board);
        Collections.reverse(Arrays.asList(pawns));
        return pawns;
    }
}

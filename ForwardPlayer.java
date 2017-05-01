/**
 * Created by yulunwu on 4/30/17.
 */
import java.util.Arrays;
import java.util.List;
public class ForwardPlayer extends SPlayer {
    String color;

    public ForwardPlayer(String c) {
        startGame(c);
    }

    public void startGame(String color) {
        super.startGame(color);
    }


    public Pawn[] ForwardSortPawns(Pawn [] pawns,int home){
        for(Pawn pawn: pawns){
            if (pawn.location<home){
                pawn.location+=100;
            }
        }
        Arrays.sort(pawns);
        for(Pawn pawn: pawns){
            if (pawn.location<100){
                pawn.location-=100;
            }
        }
        return pawns;
    }

    public Move[] doMove(Parcheesi game, int[] rolls) {
        Move[] moves = new Move[4];
        Pawn[] sortedPawns = new Pawn[4];
        sortedPawns=ForwardSortPawns(game.board.pawns.get(color),game.board.NEST_LOCATIONS.get(color));

        //TODO: Not implemented yet
        //check that the move is possible given the die rolls
        return moves;
    }


    public void doublesPenalty() {
        //TODO: Not implemented yet
    }
}
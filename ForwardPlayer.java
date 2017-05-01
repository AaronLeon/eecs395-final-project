/**
 * Created by yulunwu on 4/30/17.
 */
public class ForwardPlayer extends SPlayer {
    String color;

    public ForwardPlayer(String c) {
        startGame(c);
    }

    public void startGame(String color) {
        super.startGame(color);
    }


    public int[] ForwardSortMoves(){
        return null;
    }

    public Move[] doMove(Board brd, int[] rolls) {
        Move[] moves = new Move[4];
        //TODO: Not implemented yet
        //check that the move is possible given the die rolls
        return moves;
    }


    public void doublesPenalty() {
        //TODO: Not implemented yet
    }
}
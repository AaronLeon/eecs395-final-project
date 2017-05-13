package parcheesi;

/**
 * parcheesi.SimplePlayer
 * An implementation of parcheesi.SPlayer for testing who does nothing
 */
public class SimplePlayer extends MPlayer {
    public SimplePlayer(String c) {
        super(c);
    }

    public Move[] doMove(Board brd, int[] rolls) {
        Move[] moves = new Move[4];
        return moves;
    }
}
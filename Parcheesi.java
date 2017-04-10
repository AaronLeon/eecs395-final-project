// Main rules:
// * Use all dice roll (if possible)
// * Can't run into blockade
// * Bop 
// * Can only exit home if you roll a 5 or sum of 5
// * Triple doubles penalty
public class Parcheesi implements Game {
    private Board board;
    private Player[] players = new Player[4];

    public Parcheesi() {
       board = new Board(); 
    }

    public boolean isBlockaded(Move m) {
        int start = m.pawn.location;
        for (int i = 0; i < m.distance; i++) {

        }
        //TODO: Not yet implemented
        return false;
    };

    // Returns bonus dice roll. 0 if no bonus dice
    public int processMove(Move m) {
        if (!isLegal(m)) {
            return;
        }
    }

    public void checkBoards(Board brd1, Board brd2, Move[] moves) {

    }

    public void register(Player p) {
        
    }

    public void start() {
        
    }
}

public class Parcheesi implements Game {
    private Board board;
    private Player[] players = new Player[4];

    public Parcheesi() {
       board = new Board(); 
    }

    public boolean isLegal(Move m) {
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

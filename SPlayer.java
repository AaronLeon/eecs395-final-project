public class SPlayer implements Player {
    String color;

    public SPlayer(){
        startGame(null);
    }

    public SPlayer(String c) {
        startGame(c);
    }

    public void startGame(String color) {
        this.color = color;
    }


    public Move[] doMove(Parcheesi game, int[] rolls) {
        Move[] moves = new Move[4];

        //TODO: Not implemented yet
        //check that the move is possible given the die rolls

        return moves;
    }


    public void doublesPenalty() {
        //TODO: Not implemented yet 
    }
}

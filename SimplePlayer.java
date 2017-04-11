public class SimplePlayer implements Player {
    private String color;
    private Pawn[] pawns = new Pawn[4];

    public SimplePlayer(String c) {
        color = c;
        for (int i = 0; i < pawns.length; i++) {
            pawns[i] = new Pawn(i, c);
        }
    }

    public void startGame(String color) {
        //TODO: Not implemented yet 
    }

    public Move doMove(Board brd, int[] dice) {
        //TODO: Not implemented yet
        return null;
    }

    public void doublesPenalty() {
        //TODO: Not implemented yet 
    }
}

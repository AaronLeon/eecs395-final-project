public abstract class SPlayer implements Player {
    String color;

    public SPlayer(String c) {
        startGame(c);
    }

    public final void startGame(String color) {
        this.color = color;
    }

    public abstract Move[] doMove(Board brd, int[] rolls);

    public final void doublesPenalty() {
        //TODO: Not implemented yet
    }
}

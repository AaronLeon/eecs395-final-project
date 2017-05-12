package parcheesi;

public abstract class SPlayer implements Player {
    String color;

    public SPlayer(String c) {
        startGame(c);
    }

    public String startGame(String color) {
        this.color = color;
        return "Name";
    }

    public abstract Move[] doMove(Board brd, int[] rolls);

    public void doublesPenalty() {
        //TODO: Not implemented yet
    }
}

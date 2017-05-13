package parcheesi;

public abstract class SPlayer implements Player {
    String color;

    public SPlayer(String color) {
        this.color = color;
    }

    public abstract void startGame(String color);

    public abstract Move[] doMove(Board board, int[] dice);

    public abstract void doublesPenalty();
}

package parcheesi;

public class MPlayer extends SPlayer {
    String color;

    public MPlayer(String c) {
        super(c);
    }

    @Override
    public void startGame(String color) {
        System.out.print("Player is " + color);
    }

    @Override
    public Move[] doMove(Board board, int[] dice) {
        return null;
    }

    @Override
    public void doublesPenalty() {
        System.out.print("Color invoked doubles penalty");
    }


}
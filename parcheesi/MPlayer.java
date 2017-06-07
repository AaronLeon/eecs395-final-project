package parcheesi;

import strategy.Strategy;

public class MPlayer extends SPlayer {
    String color;
    Strategy strategy;

    public MPlayer(String c, Strategy strategy) {
        super(c);
        this.strategy = strategy;
    }

    @Override
    public void startGame(String color) {
    }

    @Override
    public Move[] doMove(Board board, int[] dice) {
        return strategy.doMove(board, dice);
    }

    @Override
    public void doublesPenalty() {
        System.out.print("Color invoked doubles penalty");
    }


}
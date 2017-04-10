public class SimplePlayer implements Player {
    private String color;
    private int consecutiveDoubles = 0;

    public SimplePlayer() {
        //TODO: Not implemented yet 
    }

    public void startGame(String color) {
        //TODO: Not implemented yet 
    }

    public Move doMove(Board brd, int[] dice) {
        //TODO: Not implemented yet 
        if (dice[0] == dice[1]) {
            consecutiveDoubles++;
        }
        else {
            consecutiveDoubles = 0;
        }

        if (consecutiveDoubles == 3) {
            doublesPenalty(); 
        }
        return null;
    }

    public void doublesPenalty() {
        //TODO: Not implemented yet 
    }
}

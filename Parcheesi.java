// Main rules:
// * Use all dice roll (if possible)
// * Can't run into blockade
// * Bop 
// * Can only exit home if you roll a 5 or sum of 5
// * Triple doubles penalty

import java.lang.Math;

public class Parcheesi implements Game {
    private Board board;
    private Player[] players = new Player[4];


    public Parcheesi() {
        board = new Board();
    }

    public boolean isBlockaded(Move m) {
        if (m instanceof MoveMain) {
            MoveMain move = (MoveMain) m;
            for (int i = 1; i < move.distance; i++) {
                if (Board.ring[move.start + i][0] != null && Board.ring[move.start + i][1] != null) {
                    return true;
                }
            }
        }
        return false;
    }

    private int[] rollDice() {
        int[] dice = new int[4];
        for (int i = 0; i < 2; i++) {
            dice[i] = (int) (Math.random() * 6) + 1;
        }

        if (dice[0] == dice[1]) {
            dice[2] = 7 - dice[0];
            dice[3] = dice[2];
        } else{
            dice[2]=0;
            dice[3]=0;
        }

            return dice;
    }

    // Changes board state with provided move.
    // Returns bonus dice roll. 0 if no bonus dice
    public int processMove(Move m) {
        return -1;
    }

    public void register(Player p) {

    }

    public void start() {
        // registers players
        int[] dice = rollDice();
        boolean gameover = false;
        int consecutiveDoubles = 0;
        int i = 0;
        boolean rolledDouble=false;
        while (!gameover) {
            if (players[i] == null) { continue; }
            Player player = players[i];
            //we store a copy of player
            consecutiveDoubles = 0;

            if (dice[0] == dice[1]) {
                doubleTurn=true;
                consecutiveDoubles++;
            }

            if (consecutiveDoubles > 2) {
                player[i].doublesPenalty();
            }

            while (!allDiceUsed(dice)) {
                if (!movesPossible(players[i],dice, board)) {
                    if(rolledDouble){
                        dice=rollDice();
                        if (dice[0] == dice[1]) {
                            doubleTurn=true;
                            consecutiveDoubles++;
                        } else {
                            doubleTurn=false;
                            consecutiveDoubles=0;
                        }
                    }
                    continue;
                } else {
                    Move m = player.doMove(board, dice);
                    processMove(m);
                    //process move should make changes to players[i]
                    //at the end we compare stored player with players[i]
                }
            }

            if (movedBlockade(player,players[i])){
                cheat(i);
            }

            i = (++i) % 4;
        }
    }

    public void doublesPenalty(Player p) {
        Pawn furthestPawn=null;
        //now we iterate over
    }

    public void cheat(int i) {
        players[i] = null;
    }

    public boolean movesPossible(Player p, int[] dice, Board board) {
        return true;
    }

    public boolean movedBlockade(Player before, Player after) {
        // Scan board for blockade in brd1 and brd2 and check if their pawn id is equal

        return false;
    }

    public boolean allDiceUsed(int[] dice) {
        for (int i = 0; i < dice.length; i++) {
            if (dice[i] != 0) {
                return false;
            }
        }
        return true;
    }
}

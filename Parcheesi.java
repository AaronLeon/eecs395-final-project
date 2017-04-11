// Main rules:
// * Use all dice roll (if possible)
// * Can't run into blockade
// * Bop 
// * Can only exit home if you roll a 5 or sum of 5
// * Triple doubles penalty

import java.lang.Math;
import java.util.LinkedList;

import static java.util.Arrays.sort;

public class Parcheesi implements Game {
    private Board board;
    private int registered = 0;
    private Player[] players = new Player[4];
    private String[] colors = {"blue", "yellow", "green", "red"};

    public Parcheesi() {
        board = new Board();
    }

    public boolean isBlocked(MoveMain m) {
        String color = m.pawn.color;
        int home_loc = (int) board.homeLocations.get(color);
        int runway_loc = (int) board.runwayLocations.get(color);
        for (int i = 1; i < m.distance; i++) {
            if ((m.start + i > runway_loc) && m.start+i<home_loc) {
                //check if runway is blocked
                if (board.runways.get(color).blocked(m.start + i - runway_loc)) {
                    return true;
                }
            } else if (board.ring[m.start + i][0] != null && board.ring[m.start + i][1] != null) {
                return true;
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
        } else {
            dice[2] = 0;
            dice[3] = 0;
        }

        return dice;
    }

    // Changes board state with provided move.
    // Returns bonus dice roll. 0 if no bonus dice
    public int processMoves(Move[] m) {
        return -1;
    }

    public void register(Player p) {
        if (registered == 4) {
            return;
        } else {
            p.startGame(colors[registered]);
            players[registered] = p;
            registered++;
        }
    }

    public void start() {
        // registers players
        int[] dice = rollDice();
        boolean gameover = false;
        int consecutiveDoubles = 0;
        int i = 0;
        boolean rolledDouble = false;
        boolean doubleTurn = false;
        while (!gameover) {
            if (players[i] == null) {
                continue;
            }
            Player player = players[i];
            //we store a copy of player
            consecutiveDoubles = 0;

            if (dice[0] == dice[1]) {
                doubleTurn = true;
                consecutiveDoubles++;
            }

            if (consecutiveDoubles > 2) {
                players[i].doublesPenalty();
            }

            while (!allDiceUsed(dice)) {
                if (!movesPossible(players[i], dice, board)) {
                    if (rolledDouble) {
                        dice = rollDice();
                        if (dice[0] == dice[1]) {
                            doubleTurn = true;
                            consecutiveDoubles++;
                        } else {
                            doubleTurn = false;
                            consecutiveDoubles = 0;
                        }
                    }
                    continue;
                } else {
                    Move[] m = player.doMove(board, dice);
                    processMoves(m);
                    //process move should make changes to players[i]
                    //at the end we compare stored player with players[i]
                }
            }

            if (movedBlockade(player, players[i])) {
                cheat(i);
            }

            i = (++i) % 4;
        }
    }

    public void send_home(Player p, int i){
        //sends ith pawn home, i is its id/location in the arr
        Pawn curr = ((SimplePlayer) p).getPawns()[i];
        curr.location=-1;
        curr.home=true;
        curr.runway=false;
        ((SimplePlayer) p).getPawns()[i]=curr;
        //sends ith pawn home
    }

    public void doublesPenalty(Player p) {
        //index of the furthest pawn
        int furthestPawn = -1;
        String color = ((SimplePlayer) p).getColor();
        int home_index = (int) board.homeLocations.get(color);
        int dist = 0;
        int furthestDistance = -1;
        for (int i = 0; i < 4; i++) {
            Pawn curr = ((SimplePlayer) p).getPawns()[i];
            if (curr.home == true || curr.runway==true) {
                continue;
            } else {
                //piece not at home
                dist = curr.location - home_index;
                if (curr.location > home_index) {
                    if (dist > furthestDistance) {
                        furthestDistance = dist;
                        furthestPawn = i;
                    }
                } else {
                    dist = 68 - dist;
                    if (dist > furthestDistance) {
                        furthestDistance = dist;
                        furthestPawn = i;
                    }
                }
            }
        }
        send_home(p,furthestPawn);
    }

    public void cheat(int i) {
        //also remove player's pieces from the board
        //write a for loop
        players[i] = null;
    }

    public boolean sum5(int[] dice) {
        if (dice[0] + dice[1] == 5) {
            return true;
        }
        if (dice[0] == 5 || dice[1] == 5 || dice[2] == 5 || dice[3] == 5) {
            return true;
        }
        return false;
    }

    public boolean movesPossible(Player p, int[] dice, Board board) {
        sort(dice);
        //iterate over pawns in player p,
        for (int i = 0; i < 4; i++) {
            if (((SimplePlayer) p).getPawns()[i].home == true) {
                return sum5(dice);
                //check if integers in dice can sum to 5

            } else if (((SimplePlayer) p).getPawns()[i].runway == true) {
                String color = ((SimplePlayer) p).getColor();
                //refactor
                for (int j = 0; j < dice.length; j++) {
                    if (board.runways.get(color).empty(i)) {
                        return false;
                    }
                }
            } else {
                for (int j = 0; j < dice.length; j++) {
                    MoveMain testMove = new MoveMain(((SimplePlayer) p).getPawns()[i], dice[j]);
                    if (!isBlocked(testMove)) {
                        return true;
                    }
                }
                //iterate over rolls in dice,
                //moveMain
            }
        }
        return false;
    }

    public boolean movedBlockade(Board brd1, Board brd2, LinkedList<Move> moves) {
        for (Move m: moves) {
            // Can only move blockades in the main ring...
            // Blockades do not exist in runway since other pawns cannot be in your runway
            if (!(m instanceof MoveMain)) {
                continue;
            }

            int start = ((MoveMain) m).start;
            brd1.isBlockade(start);
        }
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

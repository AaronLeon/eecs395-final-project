/* *
 * Parcheesi
 * The Parcheesi class represents the game engine
 */
import com.sun.tools.javac.comp.Enter;

import java.lang.Math;

import static java.util.Arrays.sort;

public class Parcheesi implements Game {
    private Board board;
    private int registered = 0;
    private Player[] players = new Player[4];
    private String[] colors = {"blue", "yellow", "green", "red"};
    int turn = 0;

    public Parcheesi() {
        board = new Board();
    }

    // For a given move, checks if move runs into blockade
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
            } else if (board.ring[m.start + i].first != null && board.ring[m.start + i].second != null) {
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
    public Pair<Board, Integer> processMoves(Board brd, Move m) {
        boolean success=true;
        if (m instanceof MoveMain) {
            boolean bopped=false;
            MoveMain move=(MoveMain)m;
            String color=move.pawn.color;
            if (!isBlocked(move)){
                int location = move.pawn.location;
                success = success && brd.remove(location,color,move.pawn.id);
                //check if it will move past its runway
                if(location+move.distance > brd.runwayLocations.get(color)){
                } else {
                    boolean bopped=brd.willBop(location,color);
                    success = success && brd.add(location,color,move.pawn.id);
                }
            } else {
                cheat(turn);
            }

            //if we bop, return 20 bonus
        } else if (m instanceof  EnterPiece){
            String color = colors[turn];
            //enter the piece
            //
        } else if (m instanceof MoveHome){
            //if we get to end of home, return 10 bonus
        }
        //should never be called
        if(!success){
            cheat(turn);
        }
        return new Pair<Board,Integer>(brd, 0);
    }

    public void register(Player p) {
        if (registered < 4) {
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
        boolean rolledDouble = false;
        boolean doubleTurn = false;
        while (!gameover) {
            if (players[turn] == null) {
                continue;
            }
            SPlayer player = (SPlayer)players[turn];
            //we store a copy of player
            consecutiveDoubles = 0;

            if (dice[0] == dice[1] && player.allOut()) {
                //we need to check if everything is out of the board for the player

                doubleTurn = true;
                consecutiveDoubles++;
            }

            if (consecutiveDoubles > 2) {
                players[turn].doublesPenalty();
            }

            while (!allDiceUsed(dice)) {
                if (!movesPossible(players[turn], dice, board)) {
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
                    Move[] moves = player.doMove(board, dice);
                    Board nextBoard = null;
                    for (Move m: moves) {

                        Pair<Board, Integer> result = processMoves(board, m);
                        nextBoard = result.first;
                    }
                    if (movedBlockadeTogether(board, nextBoard, moves, players[turn])) {
                        cheat(turn);
                    }
                }
            }

            turn = (++turn) % 4;
        }
    }

    public int[] consumeDice(int[] dice, Move m) {
        int[] res = dice;

        if (m instanceof EnterPiece) {

        }
    }

    public void sendHome(Player p, int i){
        //sends ith pawn home, i is its id/location in the arr
        Pawn curr = ((SPlayer) p).getPawns()[i];
        curr.location=-1;
        curr.home=true;
        curr.runway=false;
        ((SPlayer) p).getPawns()[i]=curr;
        //update this on board as well
        //@TODO
        //sends ith pawn home
    }

    public void doublesPenalty(Player p) {
        //index of the furthest pawn
        int furthestPawn = -1;
        String color = ((SPlayer) p).getColor();
        int home_index = board.homeLocations.get(color);
        int dist = 0;
        int furthestDistance = -1;
        for (int i = 0; i < 4; i++) {
            Pawn curr = ((SPlayer) p).getPawns()[i];
            if (!curr.home && !curr.runway) {
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
        sendHome(p,furthestPawn);
    }

    public void cheat(int i) {
        //also remove player's pieces from the board
        //write a for loop
        players[i] = null;
    }

    public boolean canEnter(int[] dice) {
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
            if (((SPlayer) p).getPawns()[i].home) {
                return canEnter(dice);
                //check if integers in dice can sum to 5
            } else if (((SPlayer) p).getPawns()[i].runway) {
                String color = ((SPlayer) p).getColor();
                //refactor
                for (int j = 0; j < dice.length; j++) {
                    if (board.runways.get(color).empty(i)) {
                        return false;
                    }
                }
            } else {
                for (int j = 0; j < dice.length; j++) {
                    MoveMain testMove = new MoveMain(((SPlayer) p).getPawns()[i], dice[j]);
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

    /*
     * Checks to see if any blockades have moved together by comparing
     * board states from before and after a set of moves
     */
    public boolean movedBlockadeTogether(Board board1, Board board2, Move[] moves, Player player) {
        // List of blockades that formed across all moves in a turn
        Pair<Pair,Pair> blockades = new Pair<>();

        for (Pawn p: ((SPlayer) player).getPawns()) {
            if (board2.isBlockade(p.location)) {
                if (blockades.first == null) {
                    blockades.first = board2.ring[p.location];
                }
                else if (!blockades.first.equals(board1.ring[p.location])) {
                    blockades.second = board.ring[p.location];
                }
            }
        }

        for (Move move: moves) {
            if (move instanceof MoveMain) {
                continue;
            }

            int startPos = ((MoveMain) move).start;
            if (board1.isBlockade(startPos)
                    && board1.ring[startPos].first.color == ((SPlayer)player).getColor()
                    && (blockades.first.equals(board1.ring[startPos])
                        || blockades.second.equals(board1.ring[startPos]))) {
                return true;
            }
        }
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

    public static void main(String[] argv) {
        Parcheesi game = new Parcheesi();

        Pawn p1 = new Pawn(0, "red");
        game.board.ring[0].first = p1;
        MoveMain m1 = new MoveMain(p1, 3);
        game.processMoves(game.board, m1);
        assert game.board.ring[3].equals(p1) : "Pawn should move 3 spaces";

    }
}

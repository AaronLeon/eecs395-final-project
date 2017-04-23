/* *
 * Parcheesi
 * The Parcheesi class represents the game engine
 */
import java.lang.Math;

import static java.util.Arrays.sort;

public class Parcheesi implements Game {
    Board board = new Board();
    int registered = 0;
    SPlayer[] players = new SPlayer[4];
    String[] colors = {"blue", "yellow", "green", "red"};
    int turn = 0;
    boolean [] cheated = {false,false,false,false};

    public Parcheesi() {
        board = new Board();
    }

    // For a given move, checks if move runs into blockade
    public boolean isBlocked(MoveMain m) {
        String color = m.pawn.color;
        int home_loc = board.homeLocations.get(color);
        int runway_loc = board.runwayLocations.get(color);
        for (int i = 1; i < m.distance; i++) {
            if ((m.pawn.location + i > runway_loc) && m.pawn.location+i<home_loc) {
                //check if runway is blocked
                if (board.runways.get(color).blocked(m.pawn.location + i - runway_loc)) {
                    return true;
                }
            } else if (board.ring[m.pawn.location + i].first != null && board.ring[m.pawn.location + i].second != null) {
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
            int bopTarget=-1;
            int location;
            MoveMain move=(MoveMain)m;
            String color=move.pawn.color;
            if (!isBlocked(move)){
                location = move.pawn.location;
                if(location+move.distance > brd.runwayLocations.get(color) && location+move.distance<brd.homeLocations.get(color)){
                    Pawn copy = move.pawn;
                    copy.location=0;
                    copy.runway=true;
                    success = success && brd.remove(location,color,move.pawn.id);
                    int distance = location+move.distance-brd.runwayLocations.get(color)-1;
                    success = success && brd.runways.get(color).add(0,color,copy.id);
                    MoveHome newMove = new MoveHome(copy,copy.location,distance);
                    players[turn].setPawn(copy.id,copy);
                    if(!success){
                        cheat(turn);
                        return null;
                    }



                    return processMoves(brd,newMove);
                } else {
                    Pawn copy=move.pawn;
                    copy.location=location+move.distance;
                    players[turn].setPawn(move.pawn.id,copy);

                    success = success && brd.remove(location,color,move.pawn.id);
                    bopTarget=brd.bopLoc(location+move.distance,color);
                    success = success && brd.add(location,color,move.pawn.id);

                    if(bopTarget>=0){
                        Pawn boppedCopy = brd.bopTarget(location,bopTarget);
                        if (boppedCopy==null){
                            cheat(turn);
                            return null;
                        }
                        boppedCopy.home=true;
                        boppedCopy.location=-1;
                        for (int i = 0;i<4;i++){
                            if(boppedCopy.color==colors[i]){
                                players[i].setPawn(boppedCopy.id,boppedCopy);
                            }
                        }
                        if(!success){
                            cheat(turn);
                            return null;
                        }
                        return new Pair(brd,20);
                    }
                    if(!success){
                        cheat(turn);
                        return null;
                    }
                    //brd.add bops
                }
            } else {
                //is blocked
                cheat(turn);
                return null;
            }
        }


        else if (m instanceof  EnterPiece){
            boolean bopped=false;
            String color = colors[turn];
            EnterPiece move = (EnterPiece)  m;
            if(move.pawn.home==false){
                cheat(turn);
                return null;
            } else {
                if (brd.blocked(brd.homeLocations.get(color))){
                    Pair<Pawn,Pawn> gotBopped=brd.clearCell(brd.homeLocations.get(color));
                    if(gotBopped.first!=null){
                        bopped=true;
                        for (int i = 0;i<4;i++){
                            if(gotBopped.first.color==colors[i]){
                                gotBopped.first.home=true;
                                gotBopped.first.location=-1;
                                players[i].setPawn(gotBopped.first.id,gotBopped.first);
                            }
                        }
                    }
                    if(gotBopped.second!=null){
                        bopped=true;
                        for (int i = 0;i<4;i++){
                            if(gotBopped.second.color==colors[i]){
                                gotBopped.second.home=true;
                                gotBopped.second.location=-1;
                                players[i].setPawn(gotBopped.second.id,gotBopped.second);
                            }
                        }
                    }
                }
                Pawn copy=move.pawn;
                copy.home=false;
                copy.location=brd.homeLocations.get(copy.color);
                players[turn].setPawn(copy.id,copy);
                brd.add(copy.location,copy.color,copy.id);
                if (bopped){
                    return new Pair(brd,20);
                }
                return new Pair(brd,0);
            }
        }


        else if (m instanceof MoveHome){

            MoveHome move = (MoveHome)m;
            if (move.pawn.runway==false || (move.distance+move.pawn.location>7)){
                cheat(turn);
                return null;
            } else {
                int destination=move.distance+move.pawn.location;
                brd.runways.get(colors[turn]).remove(move.pawn.location,move.pawn.color,move.pawn.id);
                brd.runways.get(colors[turn]).add(destination,move.pawn.color,move.pawn.id);
                Pawn moved=new Pawn(move.pawn.id,move.pawn.color);
                players[turn].setPawn(moved.id,moved);
                if (destination==7){
                    return new Pair(brd,10);
                } else {
                    return new Pair(brd,0);
                }
            }
        }
        //should never be called
        return new Pair(brd, 0);
    }

    public void register(Player p) {
        if (registered < 4) {
            p.startGame(colors[registered]);
            players[registered] = (SPlayer)p;
            registered++;
        }
    }

    public boolean startContract(){
        return registered==4;
    }

    public void start() {
        // registers players
        if(!startContract()){
            return;
        }

        boolean gameover = false;
        int consecutiveDoubles = 0;
        boolean rolledDouble = false;
        boolean doubleTurn = false;
        while (!gameover) {
            if (players[turn] == null) {
                continue;
            }

            int[] dice = rollDice();

            SPlayer player = players[turn];
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
                    //doMove only returns one move
                    //otherwise we write a for loop
                    dice=consumeDice(dice,moves[0]);
                    Board nextBoard = null;
                    for (Move m: moves) {

                        Pair<Board, Integer> result = processMoves(board, m);
                        nextBoard = result.first;
                        int bonus = result.second;
                        if (bonus>0){
                            for (int i = 0;i<dice.length;i++){
                                if (dice[i]==0){
                                    dice[i]=bonus;
                                    bonus=0;
                                }
                            }
                        }
                    }
                    if (movedBlockadeTogether(board, nextBoard, moves, players[turn])) {
                        cheat(turn);
                    }
                    board=nextBoard;
                }
            }

            turn = (++turn) % 4;
        }
    }

    public int[] consumeDice(int[] dice, Move m) {
        int[] res = dice;

        if (m instanceof EnterPiece) {
            if (dice[0] + dice[1] == 5) {
                dice[0] = dice[1] = 0;
            } else if (dice[0] == 5) {
                res[0] = 0;
            }
            else if (dice[1] == 5) {
                res[1] = 0;
            }
        } else if (m instanceof MoveMain) {
            for (int i = 0; i < dice.length; i++) {
                if (dice[i] == ((MoveMain) m).distance) {
                    res[i] = 0;
                    return res;
                }
            }
        } else if (m instanceof MoveHome) {
            for (int i = 0; i < dice.length; i++) {
                if (dice[i] == ((MoveHome) m).distance) {
                    res[i] = 0;
                    return res;
                }
            }
        }

        return dice;
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
        for (int j=0;j<4;j++){
            sendHome(players[i],j);
        }
        cheated[i]=true;
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
                    MoveMain testMove = new MoveMain(((SPlayer) p).getPawns()[i],dice[j]);
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

            int startPos = ((MoveMain) move).pawn.location;
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
}

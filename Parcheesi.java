/* *
 * Parcheesi
 * The Parcheesi class represents the game engine
 */

import static java.util.Arrays.sort;

public class Parcheesi implements Game {
    Board board = new Board();
    int registered = 0;
    SPlayer[] players = new SPlayer[4];
    String[] colors = {"blue", "yellow", "green", "red"};

    int turn = 0;

    public Parcheesi() {
        board = new Board();
    }
    // For a given move, checks if move runs into blockade

    /**
     * Checks if a move is Blockaded
     *
     * @param m Move that is being checked
     * @return true if the move is blockaded, false otherwise
     */
    public boolean isBlocked(MoveMain m) {
        String color = m.pawn.color;
        int home_loc = board.homeLocations.get(color);
        int runway_loc = board.runwayLocations.get(color);
        for (int i = 1; i < m.distance; i++) {
            if ((m.pawn.location + i > runway_loc) && m.pawn.location + i < home_loc) {
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

    /**
     * Rolls the dice
     *
     * @return an array of values for the dice roll
     */
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

    /**
     * Processes an EnterPiece move given a starting board state
     *
     * @param m   EnterPiece move to be processed
     * @return a pair containing the new board state after the piece has entered the board and, optionally, a bonus
     * for bopping
     */
    public Pair<Board, Integer> processEnterPiece(Move m) {
        boolean bopped = false;
        String color = colors[turn];
        EnterPiece move = (EnterPiece) m;
        if (!move.pawn.home) {
            cheat(turn);
            return null;
        }
        if (board.blocked(board.homeLocations.get(color))) {
            Pair<Pawn, Pawn> gotBopped = board.clearCell(board.homeLocations.get(color));
            if (gotBopped.first != null) {
                bopped = true;
                for (int i = 0; i < 4; i++) {
                    if (gotBopped.first.color == colors[i]) {
                        gotBopped.first.home = true;
                        gotBopped.first.location = -1;
                        players[i].setPawn(gotBopped.first.id, gotBopped.first);
                    }
                }
            }
            if (gotBopped.second != null) {
                bopped = true;
                for (int i = 0; i < 4; i++) {
                    if (gotBopped.second.color == colors[i]) {
                        gotBopped.second.home = true;
                        gotBopped.second.location = -1;
                        players[i].setPawn(gotBopped.second.id, gotBopped.second);
                    }
                }
            }
        }
        Pawn copy = move.pawn;
        copy.home = false;
        copy.location = board.homeLocations.get(copy.color);
        players[turn].setPawn(copy.id, copy);
        board.add(copy.location, copy.color, copy.id);
        if (bopped) {
            return new Pair<>(board, 20);
        }
        return new Pair<>(board, 0);
    }

    /**
     * Processes a MoveMain move given a starting board state
     *
     * @param m   MoveMain move to be processed
     * @return a pair containing the board state after the MoveMain move has taken place and, optionally,
     * a bonus for bopping or entering home
     */
    public Pair<Board, Integer> processMoveMain(Move m) {
        boolean success = true;
        int bopTarget;
        int location;
        MoveMain move = (MoveMain) m;
        String color = move.pawn.color;
        if (isBlocked(move)) {
            cheat(turn);
            return null;
        }
        location = move.pawn.location;
        if (location + move.distance > board.runwayLocations.get(color) && location + move.distance < board.homeLocations.get(color)) {
            Pawn copy = move.pawn;
            copy.location = 0;
            copy.runway = true;
            success = success && board.remove(location, color, move.pawn.id);
            int distance = location + move.distance - board.runwayLocations.get(color) - 1;
            success = success && board.runways.get(color).add(0, color, copy.id);
            MoveHome newMove = new MoveHome(copy, copy.location, distance);
            players[turn].setPawn(copy.id, copy);
            if (!success) {
                cheat(turn);
                return null;
            }
            return processMoves(newMove);
        } else {
            Pawn copy = move.pawn;
            copy.location = location + move.distance;
            players[turn].setPawn(move.pawn.id, copy);

            success = success && board.remove(location, color, move.pawn.id);
            bopTarget = board.bopLoc(location + move.distance, color);
            success = success && board.add(copy.location, color, move.pawn.id);

            if (bopTarget >= 0) {
                Pawn boppedCopy = board.bopTarget(location, bopTarget);
                if (boppedCopy == null) {
                    cheat(turn);
                    return null;
                }
                boppedCopy.home = true;
                boppedCopy.location = -1;
                for (int i = 0; i < 4; i++) {
                    if (boppedCopy.color == colors[i]) {
                        players[i].setPawn(boppedCopy.id, boppedCopy);
                    }
                }
                if (!success) {
                    cheat(turn);
                    return null;
                }
                return new Pair<>(board, 20);
            }
            if (!success) {
                cheat(turn);
                return null;
            }
            //brd.add bops
            return new Pair<>(board, 0);
        }
    }

    /**
     * Processes a MoveHome move given a starting board state
     *
     * @param m   MoveHome move to be processed
     * @return a pair containing the board state after the MoveHome move has taken place and, optionally, entering home
     */
    public Pair<Board, Integer> processMoveHome(Move m) {
        MoveHome move = (MoveHome) m;
        if (!move.pawn.runway || (move.distance + move.pawn.location > 7)) {
            cheat(turn);
            return null;
        }
        int destination = move.distance + move.pawn.location;
        board.runways.get(colors[turn]).remove(move.pawn.location, move.pawn.color, move.pawn.id);
        board.runways.get(colors[turn]).add(destination, move.pawn.color, move.pawn.id);
        Pawn moved = new Pawn(move.pawn.id, move.pawn.color);
        moved.home=false;
        moved.runway=true;
        moved.location=destination;
        players[turn].setPawn(moved.id, moved);
        int bonus = destination == 7
                ? 10
                : 0;
        return new Pair<>(board, bonus);
    }

    /**
     * Processes a move given a starting board state
     *
     * @param m   move to be processed
     * @return a pair containing the new board state after the move takes place and, optionally, a bonus roll from
     * moving home or bopping
     */
    public Pair<Board, Integer> processMoves(Move m) {
//        boolean success = true;
        Pair<Board,Integer> res= new Pair<Board,Integer>();
        if (m instanceof MoveMain) {
            res = processMoveMain(m);
        } else if (m instanceof EnterPiece) {
            res = processEnterPiece(m);
        } else if (m instanceof MoveHome) {
            res = processMoveHome(m);
        }
        board=res.first;
        //should never be called
        return new Pair(board, res.second);
    }

    public void register(Player p) {
        if (registered < 4) {
            p.startGame(colors[registered]);
            players[registered] = (SPlayer) p;
            registered++;
        }
    }

    /**
     * Enforces the contract that players must be registered before the game is started
     * @return
     */
    public boolean registeredPlayersBeforeStart() {
        return registered == 4;
    }

    public void start() {
        // registers players
        if (!registeredPlayersBeforeStart()) {
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
                            consecutiveDoubles++;
                        } else {
                            consecutiveDoubles = 0;
                        }
                    }
                } else {
                    Move[] moves = player.doMove(board, dice);
                    //doMove only returns one move
                    //otherwise we write a for loop
                    dice = consumeDice(dice, moves[0]);
                    Board nextBoard = null;
                    for (Move m : moves) {

                        Pair<Board, Integer> result = processMoves(m);
                        nextBoard = result.first;
                        int bonus = result.second;
                        if (bonus > 0) {
                            for (int i = 0; i < dice.length; i++) {
                                if (dice[i] == 0) {
                                    dice[i] = bonus;
                                    bonus = 0;
                                }
                            }
                        }
                    }
                    if (movedBlockadeTogether(board, nextBoard, moves, players[turn])) {
                        cheat(turn);
                    }
                    board = nextBoard;
                }
            }

            turn = (++turn) % 4;
        }
    }


    /**
     * Consumes dice roll with respect to the given move.
     * @param dice An int array that holds the values of the dice roll
     * @param m Move that uses dice roll
     * @return The dice array once the given move has consumed its respective dice roll.
     */
    public int[] consumeDice(int[] dice, Move m) {
        int[] res = dice;

        if (m instanceof EnterPiece) {
            if (dice[0] + dice[1] == 5) {
                res[0] = res[1] = 0;
            } else if (dice[0] == 5) {
                res[0] = 0;
            } else if (dice[1] == 5) {
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

        return res;
    }

    public void sendHome(Player p, int i) {
        //sends ith pawn home, i is its id/location in the arr
        Pawn curr = ((SPlayer) p).getPawns()[i];
        curr.location = -1;
        curr.home = true;
        curr.runway = false;
        ((SPlayer) p).getPawns()[i] = curr;
        //update this on board as well
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
        sendHome(p, furthestPawn);
    }

    public void cheat(int i) {
        for (int j = 0; j < 4; j++) {
            sendHome(players[i], j);
        }
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
     *
     */

    /**
     * Checks to see if any blockades have moved together by comparing board states from before and after a set of moves
     * @param board1 Starting board state
     * @param board2 Ending board state
     * @param moves Array of history of moves that occurred between board states
     * @param player Player that moved
     * @return true if player moved a blockade together and false otherwise
     */
    public boolean movedBlockadeTogether(Board board1, Board board2, Move[] moves, Player player) {
        // List of blockades that formed across all moves in a turn
        Pair<Pair<Pawn, Pawn>, Pair<Pawn, Pawn>> blockades = new Pair<>();

        for (Pawn p : ((SPlayer) player).getPawns()) {
            if (board2.isBlockade(p.location)) {
                if (blockades.first == null) {
                    blockades.first = board2.ring[p.location];
                } else if (!blockades.first.equals(board1.ring[p.location])) {
                    blockades.second = board.ring[p.location];
                }
            }
        }

        for (Move move : moves) {
            if (move instanceof MoveMain) {
                continue;
            }

            int startPos = ((MoveMain) move).pawn.location;
            if (board1.isBlockade(startPos)
                    && board1.ring[startPos].first.color == ((SPlayer) player).getColor()
                    && (blockades.first.equals(board1.ring[startPos])
                    || blockades.second.equals(board1.ring[startPos]))) {
                return true;
            }
        }
        return false;
    }

    public boolean allDiceUsed(int[] dice) {
        for (int val : dice) {
            if (val != 0) {
                return false;
            }
        }
        return true;
    }
}

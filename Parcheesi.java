/* *
 * Parcheesi
 * The Parcheesi class represents the game engine
 */

import java.util.HashMap;

import static java.util.Arrays.sort;

public class Parcheesi implements Game {
    Board board;
    HashMap<String, Player> players;

    public Parcheesi() {
        board = new Board();
        players = new HashMap<>(4);
    }
    // For a given move, checks if move runs into blockade

    /**
     * Checks if a move is blockaded
     *
     * @param m Move that is being checked
     * @return true if the move is blockaded, false otherwise
     */
    //TODO: Check if MoveHome is blocked
    public boolean isBlocked(MoveMain m) {
        Pawn pawn = m.pawn;
        int nestLocation = Board.NEST_LOCATIONS.get(pawn.color);
        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(pawn.color);
        int checkedLocation;
        for (int i = 1; i < m.distance; i++) {
            checkedLocation = (pawn.location + i);
            if (checkedLocation > homeRowLocation) {
                checkedLocation = checkedLocation % homeRowLocation;
                if (board.homeRows.get(pawn.color)[checkedLocation] instanceof Blockade) {
                    return true;
                }
            }
            if (board.ring[checkedLocation] instanceof Blockade) {
                return true;
            }
        }
        return false;
    }

    /**
     * Rolls the dice
     *
     * @return a pair containing an int array that represents the dice roll and a boolean
     * denoting whether the roll was a pair of doubles
     */
    private Pair<int[], Boolean> rollDice(Player p) {
        int[] dice = new int[4];

        for (int i = 0; i < 2; i++) {
            dice[i] = (int) (Math.random() * 6) + 1;
        }

        boolean doubles = dice[0] == dice[1];
        boolean doublesBonus = doubles && canEarnDoubleBonus(p);

        dice[2] = dice[3] = doublesBonus
            ? 7 - dice[0]
            : 0;

        return new Pair<>(dice, doubles);
    }

    /**
     * Processes an EnterPiece move given a starting board state
     *
     * @param m   EnterPiece move to be processed
     * @return a pair containing the new board state after the piece has entered the board and, optionally, a bonus
     * for bopping
     */
    public Pair<Board, Integer> processEnterPiece(Move m) {
        int bonus = 0;
        EnterPiece move = (EnterPiece) m;
        Pawn pawn = move.pawn;
        int nestLocation = Board.NEST_LOCATIONS.get(pawn.color);

        if (pawn.bc != Board.BoardComponent.NEST
                || board.isBlockade(Board.BoardComponent.RING, nestLocation, null)) {
            cheat(pawn.color);
            return null;
        }

        Object bopped = board.get(Board.BoardComponent.RING, nestLocation, null);
        if (bopped != null && bopped instanceof Pawn) {
            board.sendBackToNest((Pawn) bopped);
            bonus = 20;
        }

        board.enterPiece(pawn);

        return new Pair<>(board, bonus);
    }

    /**
     * Processes a MoveMain move given a starting board state
     *
     * @param m   MoveMain move to be processed
     * @return a pair containing the board state after the MoveMain move has taken place and, optionally,
     * a bonus for bopping or entering home
     */
    public Pair<Board, Integer> processMoveMain(Move m) {
        MoveMain move = (MoveMain) m;
        Pawn pawn = move.pawn;
        if (pawn.bc != Board.BoardComponent.RING || isBlocked(move)) {
            cheat(pawn.color);
            return null;
        }

        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(pawn.color);
        Board.BoardComponent bc = pawn.bc;
        int location = pawn.location;
        int distance = move.distance;
        int bonus = 0;
        while (distance > 0) {
            ++location;
            --distance;

            if (location > homeRowLocation) {
                bc = Board.BoardComponent.HOMEROW;
                location = 0;
            }

            if (board.isBlockade(bc, location, pawn.color)) {
                cheat(pawn.color);
                return null;
            }
        }

        Object bopped = board.get(bc, location, pawn.color);
        if (bopped instanceof Pawn && !((Pawn) bopped).color.equals(pawn.color)) {
            if (board.isSafe(location)) {
                cheat(pawn.color);
                return null;
            }
            board.sendBackToNest((Pawn) bopped);
            bonus = 20;
        }
        board.movePawnRing(pawn, move.distance);
        return new Pair<>(board, bonus);
    }

    /**
     * Processes a MoveHome move given a starting board state
     *
     * @param m   MoveHome move to be processed
     * @return a pair containing the board state after the MoveHome move has taken place and, optionally, entering home
     */
    public Pair<Board, Integer> processMoveHome(Move m) {
        MoveHome move = (MoveHome) m;
        Pawn pawn = move.pawn;
        int newLocation = pawn.location + move.distance;
        if (pawn.bc != Board.BoardComponent.HOMEROW || newLocation > Board.HOMEROW_SIZE) {
            cheat(pawn.color);
            return null;
        }

        boolean bonus = board.movePawnHomeRow(pawn, ((MoveHome) m).distance);

        return new Pair<>(board, bonus ? 10 : 0);
    }

    /**
     * Processes a move given a starting board state
     *
     * @param m   move to be processed
     * @return a pair containing the new board state after the move takes place and, optionally, a bonus roll from
     * moving home or bopping
     */
    public Pair<Board, Integer> processMoves(Move m) {
        Pair<Board,Integer> res = new Pair<>();
        if (m instanceof MoveMain) {
            res = processMoveMain(m);
        } else if (m instanceof EnterPiece) {
            res = processEnterPiece(m);
        } else if (m instanceof MoveHome) {
            res = processMoveHome(m);
        }
//        this.board = res.first;
        return res;
    }

    public void register(Player p) {
        SPlayer player = (SPlayer) p;
        if (players.size() < 4) {
            p.startGame(((SPlayer) p).color);
            players.put(player.color, player);
        }
    }

    /**
     * Enforces the contract that players must be registered before the game is started
     * @return true if four players have been registered and false otherwise
     */
    public boolean registeredPlayersBeforeStart() {
        return players.size() == 4;
    }

    public void start() {
        // registers players
        if (!registeredPlayersBeforeStart()) {
            return;
        }

        boolean gameover = false;

        int turn = 0;
        while (!gameover) {
            String color = Board.COLORS[turn];
            SPlayer player = (SPlayer) players.get(color);

            // We have a cheater :(
            if (player == null) {
                continue;
            }

            Pair<Board, Boolean> turnResults = giveTurn(player);
            board = turnResults.first;
            boolean doubles = turnResults.second;

            int consecutiveDoubles = 0;
            while (doubles) {
                consecutiveDoubles++;
                if (consecutiveDoubles > 2) {
                    player.doublesPenalty();
                }
                turnResults = giveTurn(player);
                board = turnResults.first;
                doubles = turnResults.second;
            }

            turn = (++turn) % 4;
        }
    }

    public Pair<Board, Boolean> giveTurn(Player p) {
        SPlayer player = (SPlayer) p;

        Pair<int[], Boolean> diceResults = rollDice(player);
        int[] dice = diceResults.first;
        boolean doubles = diceResults.second;
        Move[] moves = player.doMove(board, dice);
        while (!allDiceUsed(dice)) {
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
            if (movedBlockadeTogether(board, nextBoard, moves, player)) {
                cheat(player.color);
                return null;
            }
            board = nextBoard;
        }

        return new Pair<>(board, doubles);
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

    public void doublesPenalty(Player p) {
        String color = ((SPlayer) p).color;
        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(color);
        Pawn furthest = null;
        for (int i = Board.HOMEROW_SIZE-1; i >= 0; i++) {
            Object current = board.homeRows.get(color)[i];
            if (current == null) {
                continue;
            }
            if (current instanceof Pawn && ((Pawn) current).color.equals(color)){
                furthest = ((Pawn) current);
            } else if (current instanceof Blockade && ((Blockade) current).first.color.equals(color)) {
                furthest = ((Blockade) current).first;
            }
        }

        for (int i = homeRowLocation; i >= 0; i++) {
            Object current = board.homeRows.get(color)[i];
            if (current == null) {
                continue;
            }
            if (current instanceof Pawn && ((Pawn) current).color.equals(color)){
                furthest = ((Pawn) current);
            } else if (current instanceof Blockade && ((Blockade) current).first.color.equals(color)) {
                furthest = ((Blockade) current).first;
            }
        }

        if (furthest != null){
            board.sendBackToNest(furthest);
        }
    }

    public void cheat(String color) {
        Pawn[] pawns = board.pawns.get(color);
        for (int i = 0; i < 4; i++) {
            board.sendBackToNest(pawns[i]);
        }
        players.put(color, null);
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
        SPlayer player = (SPlayer) p;
        Pawn[] pawns = board.pawns.get(player.color);
        for (Pawn pawn: pawns) {
            if ((pawn.bc == Board.BoardComponent.NEST)) {
                return canEnter(dice);
                //check if integers in dice can sum to 5
            } else if (pawn.bc == Board.BoardComponent.HOMEROW) {
                //TODO: Make isBlocked accepted MoveHome as well..this doesn't check all squares for blockades
                for (int d : dice) {
                    if (board.homeRows.get(player.color)[pawn.location + d] instanceof Blockade) {
                        return false;
                    }
                }
            } else {
                for (int d : dice) {
                    MoveMain testMove = new MoveMain(pawn, d);
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

    /**
     * Checks to see if any blockades have moved together by comparing board states from before and after a set of moves
     * @param board1 Starting board state
     * @param board2 Ending board state
     * @param moves Array of history of moves that occurred between board states
     * @param player Player that moved
     * @return true if player moved a blockade together and false otherwise
     */
    //TODO: Should check using moves to see if blockade is formed in intermediary move
    public boolean movedBlockadeTogether(Board board1, Board board2, Move[] moves, Player player) {
        // List of blockades that formed across all moves in a turn
        Pair<Blockade, Blockade> blockades1 = new Pair<>();
        SPlayer p = (SPlayer) player;

        Pawn[] pawns1 = board1.pawns.get(p.color);
        for (Pawn pawn : pawns1) {
            if (!board1.isBlockade(pawn.bc, pawn.location, p.color)) {
                continue;
            }
            Blockade b = (Blockade) board1.get(pawn.bc, pawn.location, pawn.color);
            if (blockades1.first == null) {
                blockades1.first = b;
            } else if (blockades1.second == null && !blockades1.first.equals(b)) {
                blockades1.second = b;
            }
        }

        Pawn[] pawns2 = board2.pawns.get(p.color);
        for (Pawn pawn : pawns2) {
            if (!board2.isBlockade(pawn.bc, pawn.location, p.color)) {
                continue;
            }
            Blockade b = (Blockade) board2.get(pawn.bc, pawn.location, pawn.color);

            if (b.equals(blockades1.first) || b.equals(blockades1.second)) {
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

    public boolean canEarnDoubleBonus(Player p) {
        SPlayer player = (SPlayer) p;
        for (Pawn pawn : board.pawns.get(player.color)) {
            if (pawn.bc == Board.BoardComponent.NEST) {
                return false;
            }
        }
        return true;
    }
}

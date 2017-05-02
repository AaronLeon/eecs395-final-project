/**
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

    /**
     * Entry point to Parcheesi game. Gives players their turns and updates board states until a winner is decided.
     */
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


    /**
     * Helper function that checks if a MoveMain is blockaded
     *
     * @param m MoveMain that is being checked
     * @return true if the MoveMain is blockaded, false otherwise
     */
    private boolean isMoveMainBlocked(MoveMain m) {
        Pawn pawn = m.pawn;
        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(pawn.color);
        Board.BoardComponent bc = pawn.bc;
        int location = pawn.location;
        int distance = m.distance;
        while (distance > 0) {
            ++location;
            --distance;

            if (location > homeRowLocation) {
                bc = Board.BoardComponent.HOMEROW;
                location = 0;
            }

            if (board.isBlockade(bc, location, pawn.color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function that checks if a MoveHome is blockaded
     *
     * @param m MoveHome that is being checked
     * @return true if the MoveHome is blockaded, false otherwise
     */
    private boolean isMoveHomeBlocked(MoveHome m) {
        Pawn pawn = m.pawn;
        Board.BoardComponent bc = pawn.bc;
        int location = pawn.location;
        int distance = m.distance;
        while (distance > 0 && location < Board.HOMEROW_SIZE - 1) {
            ++location;
            --distance;

            if (board.isBlockade(bc, location, pawn.color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function that checks if an EnterPiece is blockaded
     *
     * @param m EnterPiece that is being checked
     * @return true if the EnterPiece is blockaded, false otherwise
     */
    private boolean isEnterPieceBlocked(EnterPiece m) {
        Pawn pawn = m.pawn;
        int nestLocation = Board.NEST_LOCATIONS.get(pawn.color);
        Board.BoardComponent bc = Board.BoardComponent.RING;
        return board.isBlockade(bc, nestLocation, null);
    }

    /**
     * Checks if a move is blockaded
     *
     * @param m Move that is being checked
     * @return true if the Move is blockaded, false otherwise
     */
    public boolean isBlocked(Move m) {
        if (m instanceof EnterPiece) {
            return isEnterPieceBlocked((EnterPiece) m);
        } else if (m instanceof MoveMain) {
            return isMoveMainBlocked((MoveMain) m);
        } else if (m instanceof MoveHome) {
            return isMoveHomeBlocked((MoveHome) m);
        } else {
            return false;
        }
    }

    /**
     * Rolls the dice awarding doubles bonus if the player is eligible (e.g. if all the player's pawns are out)
     *
     * @return Pair containing an int array that represents the dice roll and a boolean denoting whether the player
     * rolled doubles.
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
     * Processes an EnterPiece
     *
     * @param m EnterPiece to be processed
     * @return Pair containing the board state after the EnterPiece move has taken place and
     * a bonus for bopping (0 if no bonus is earned)
     */
    public Pair<Board, Integer> processEnterPiece(EnterPiece m) {
        int bonus = 0;
        Pawn pawn = m.pawn;
        int nestLocation = Board.NEST_LOCATIONS.get(pawn.color);

        if (pawn.bc != Board.BoardComponent.NEST
                || isBlocked(m)) {
            cheat(pawn.color);
            return null;
        }

        Object bopped = board.get(Board.BoardComponent.RING, nestLocation, null);
        if (bopped != null && bopped instanceof Pawn) {
            board.sendBackToNest((Pawn) bopped);
            bonus = 20;
        }


        try {
            board.enterPiece(pawn);
        } catch (Exception e) {
            e.printStackTrace();
            cheat(pawn.color);
            return null;
        }

        return new Pair<>(board, bonus);
    }

    /**
     * Processes a MoveMain
     *
     * @param m MoveMain to be processed
     * @return Pair containing the board state after the MoveMain move has taken place and
     * a bonus for bopping or entering home (0 if no bonus is earned)
     */
    public Pair<Board, Integer> processMoveMain(MoveMain m) {
        MoveMain move = m;
        Pawn pawn = move.pawn;
        if (pawn.bc != Board.BoardComponent.RING || isBlocked(move)) {
            cheat(pawn.color);
            return null;
        }

        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(pawn.color);
        Board.BoardComponent bc = pawn.bc;
        int newLocation = pawn.location + m.distance;
        if (newLocation > homeRowLocation) {
            newLocation = (newLocation % homeRowLocation) - 1;
            bc = Board.BoardComponent.HOMEROW;
        }

        int bonus = 0;
        Object bopped = board.get(bc, newLocation, pawn.color);
        if (bopped instanceof Pawn && !((Pawn) bopped).color.equals(pawn.color)) {
            if (board.isSafe(newLocation)) {
                cheat(pawn.color);
                return null;
            }
            board.sendBackToNest((Pawn) bopped);
            bonus = 20;
        }

        try {
            board.movePawnRing(pawn, move.distance);
        } catch (Exception e) {
            e.printStackTrace();
            cheat(pawn.color);
            return null;
        }

        return new Pair<>(board, bonus);
    }

    /**
     * Processes a MoveHome
     *
     * @param m MoveHome to be processed
     * @return Pair containing the board state after the MoveHome move has taken place and
     * a bonus for bopping or entering home (0 if no bonus is earned)
     */
    public Pair<Board, Integer> processMoveHome(MoveHome m) {
        Pawn pawn = m.pawn;
        boolean home = false;
        int newLocation = pawn.location + m.distance;
        if (pawn.bc != Board.BoardComponent.HOMEROW || newLocation > Board.HOMEROW_SIZE || isBlocked(m)) {
            cheat(pawn.color);
            return null;
        }

        try {
            home = board.movePawnHomeRow(pawn, m.distance);
        } catch (Exception e) {
            e.printStackTrace();
            cheat(pawn.color);
            return null;
        }
        int bonus = home ? 10 : 0;
        return new Pair<>(board, bonus);
    }

    /**
     * Processes a Move
     *
     * @param m Move to be processed
     * @return Pair containing the board state after the Move move has taken place and
     * a bonus for bopping or entering home (0 if no bonus is earned)
     */
    // TODO: does board member need to be updated?
    public Pair<Board, Integer> processMoves(Move m) {
        Pair<Board, Integer> res = new Pair<>();
        if (m instanceof MoveMain) {
            res = processMoveMain((MoveMain) m);
        } else if (m instanceof EnterPiece) {
            res = processEnterPiece((EnterPiece) m);
        } else if (m instanceof MoveHome) {
            res = processMoveHome((MoveHome) m);
        }
        return res;
    }

    /**
     * Registers a player and tells the player that the game has started
     *
     * @param p Player that is being registered
     */
    public void register(Player p) {
        SPlayer player = (SPlayer) p;
        if (players.size() < 4) {
            p.startGame(((SPlayer) p).color);
            players.put(player.color, player);
        }
    }

    /**
     * Enforces the contract that players must be registered before the game is started
     *
     * @return true if four players have been registered and false otherwise
     */
    public boolean registeredPlayersBeforeStart() {
        return players.size() == 4;
    }

    /**
     * Gives a turn to a player. Rolls the dice and asks the player for moves until all dice are consumed.
     * Awards dice bonus as needed
     *
     * @param p Player who is taking the current turn
     * @return Pair containing the new board state after the turn and a boolean that denotes a double roll
     */
    public Pair<Board, Boolean> giveTurn(Player p) {
        SPlayer player = (SPlayer) p;

        Pair<int[], Boolean> diceResults = rollDice(player);
        int[] dice = diceResults.first;
        boolean doubles = diceResults.second;
        while (!allDiceUsed(dice) && canMove(player, dice, board)) {
            Move[] moves = player.doMove(this, dice);
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
     *
     * @param dice Array of dice roll values
     * @param m    Move that uses dice roll
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

    /**
     * Sends the player's furthest pawn back to its nest
     *
     * @param p Player who incurred doubles penalty by rolling 3 consecutive doubles
     */
    public void doublesPenalty(Player p) {
        String color = ((SPlayer) p).color;
        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(color);
        Pawn furthest = null;
        for (int i = Board.HOMEROW_SIZE - 1; i >= 0; i++) {
            Object current = board.homeRows.get(color)[i];
            if (current == null) {
                continue;
            }
            if (current instanceof Pawn && ((Pawn) current).color.equals(color)) {
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
            if (current instanceof Pawn && ((Pawn) current).color.equals(color)) {
                furthest = ((Pawn) current);
            } else if (current instanceof Blockade && ((Blockade) current).first.color.equals(color)) {
                furthest = ((Blockade) current).first;
            }
        }

        if (furthest != null) {
            board.sendBackToNest(furthest);
        }
    }

    /**
     * Punishes a cheating Player by removing it from the player list and sending all of its pawns back to the nest
     *
     * @param color Color of the cheating player
     */
    public void cheat(String color) {
        Pawn[] pawns = board.pawns.get(color);
        for (int i = 0; i < 4; i++) {
            board.sendBackToNest(pawns[i]);
        }
        players.put(color, null);
    }

    /**
     * Checks if a pawn can be entered using the given dice rolls (e.g. rolled a 5 or two dice that sum to 5)
     *
     * @param dice Array of dice roll values
     * @return true if a pawn can be entered with the dice and false otherwise
     */
    public boolean canEnter(int[] dice) {
        if (dice[0] + dice[1] == 5) {
            return true;
        }
        if (dice[0] == 5 || dice[1] == 5 || dice[2] == 5 || dice[3] == 5) {
            return true;
        }
        return false;
    }

    public boolean canMovePawn(SPlayer player, Pawn pawn, int[] dice, Board board) {
        if ((pawn.bc == Board.BoardComponent.NEST)) {
            return canEnter(dice) && !board.isBlockade(Board.BoardComponent.RING, board.NEST_LOCATIONS.get(pawn.color), pawn.color);
            //check if integers in dice can sum to 5 and entryway isn't blockaded
        } else if (pawn.bc == Board.BoardComponent.HOMEROW) {
            //TODO: Make isBlocked accepted MoveHome as well..this doesn't check all squares for blockades
            for (int d : dice) {
                for (int i = 0; i <= d; i++) {
                    //d+1 because we check d cell as well
                    if (board.homeRows.get(player.color)[pawn.location + i] instanceof Blockade) {
                        return false;
                    }
                }
            }
        } else {
            for (int d : dice) {
                MoveMain testMove = new MoveMain(pawn, d);
                if (!isBlocked(testMove)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if player still has moves possible given dice roll and current board state
     *
     * @param p     Player that is being checked
     * @param dice  Array of dice roll values
     * @param board Current board state
     * @return
     */
    public boolean canMove(Player p, int[] dice, Board board) {
        sort(dice);
        //iterate over pawns in player p,
        SPlayer player = (SPlayer) p;
        Pawn[] pawns = board.pawns.get(player.color);
        for (Pawn pawn : pawns) {
            if (canMovePawn(player, pawn, dice, board)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks to see if any blockades have moved together by comparing board states from before and after a set of moves
     *
     * @param board1 Starting board state
     * @param board2 Ending board state
     * @param moves  Array of history of moves that occurred between board states
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

    /**
     * Checks whether all dice have been used
     *
     * @param dice Array of dice roll values
     * @return true if all dice are used and false otherwise
     */
    public boolean allDiceUsed(int[] dice) {
        for (int val : dice) {
            if (val != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if player can earn double bonus (e.g. whether all their pawns are out of the nest)
     *
     * @param p Player that is being checked
     * @return true if player's pawns are all out of the nest and false otherwise
     */
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

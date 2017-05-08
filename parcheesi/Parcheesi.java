package parcheesi; /**
 * parcheesi.Parcheesi
 * The parcheesi.Parcheesi class represents the game engine
 */

import java.util.HashMap;

public class Parcheesi implements Game {
    Board board;
    HashMap<String, Player> players;

    public Parcheesi() {
        board = new Board();
        players = new HashMap<>(4);
    }

    /**
     * Entry point to parcheesi.Parcheesi game. Gives players their turns and updates board states until a winner is decided.
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
     * Rolls the dice awarding doubles bonus if the player is eligible (e.g. if all the player's pawns are out)
     *
     * @return parcheesi.Pair containing an int array that represents the dice roll and a boolean denoting whether the player
     * rolled doubles.
     */
    private Pair<int[], Boolean> rollDice(Player p) {
        int[] dice = new int[4];

        for (int i = 0; i < 2; i++) {
            dice[i] = (int) (Math.random() * 6) + 1;
        }

        boolean doubles = dice[0] == dice[1];
        boolean doublesBonus = doubles && RuleEngine.canEarnDoubleBonus(board, p);

        dice[2] = dice[3] = doublesBonus
                ? 7 - dice[0]
                : 0;

        return new Pair<>(dice, doubles);
    }

    /**
     * Processes an parcheesi.EnterPiece
     *
     * @param m parcheesi.EnterPiece to be processed
     * @return parcheesi.Pair containing the board state after the parcheesi.EnterPiece move has taken place and
     * a bonus for bopping (0 if no bonus is earned)
     */
    public Pair<Board, Integer> processMove(EnterPiece m) {
        int bonus = 0;
        Pawn pawn = m.pawn;
        int nestLocation = Board.NEST_LOCATIONS.get(pawn.color);

        if (pawn.bc != Board.BoardComponent.NEST
                || RuleEngine.isBlocked(board, m)) {
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
     * Processes a parcheesi.MoveMain
     *
     * @param m parcheesi.MoveMain to be processed
     * @return parcheesi.Pair containing the board state after the parcheesi.MoveMain move has taken place and
     * a bonus for bopping or entering home (0 if no bonus is earned)
     */
    public Pair<Board, Integer> processMove(MoveMain m) {
        MoveMain move = m;
        Pawn pawn = move.pawn;
        if (pawn.bc != Board.BoardComponent.RING || RuleEngine.isBlocked(board, move)) {
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
     * Processes a parcheesi.MoveHome
     *
     * @param m parcheesi.MoveHome to be processed
     * @return parcheesi.Pair containing the board state after the parcheesi.MoveHome move has taken place and
     * a bonus for bopping or entering home (0 if no bonus is earned)
     */
    public Pair<Board, Integer> processMove(MoveHome m) {
        Pawn pawn = m.pawn;
        boolean home = false;
        int newLocation = pawn.location + m.distance;
        if (pawn.bc != Board.BoardComponent.HOMEROW || newLocation > Board.HOMEROW_SIZE || RuleEngine.isBlocked(board, m)) {
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
     * Processes a parcheesi.Move
     *
     * @param m parcheesi.Move to be processed
     * @return parcheesi.Pair containing the board state after the parcheesi.Move move has taken place and
     * a bonus for bopping or entering home (0 if no bonus is earned)
     */
    // TODO: does board member need to be updated?
    public Pair<Board, Integer> processMoves(Move m) {
        Pair<Board, Integer> res = new Pair<>();
        if (m instanceof MoveMain) {
            res = processMove((MoveMain) m);
        } else if (m instanceof EnterPiece) {
            res = processMove((EnterPiece) m);
        } else if (m instanceof MoveHome) {
            res = processMove((MoveHome) m);
        }
        return res;
    }

    /**
     * Registers a player and tells the player that the game has started
     *
     * @param p parcheesi.Player that is being registered
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
     * @param p parcheesi.Player who is taking the current turn
     * @return parcheesi.Pair containing the new board state after the turn and a boolean that denotes a double roll
     */
    public Pair<Board, Boolean> giveTurn(Player p) {
        SPlayer player = (SPlayer) p;

        Pair<int[], Boolean> diceResults = rollDice(player);
        int[] dice = diceResults.first;
        boolean doubles = diceResults.second;
        while (!RuleEngine.allDiceUsed(dice) && RuleEngine.canMove(player, dice, board)) {
            Move[] moves = player.doMove(board, dice);
            for (Move m:moves){
                dice=consumeDice(dice,m);
            }

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
            if (RuleEngine.movedBlockadeTogether(board, nextBoard, moves, player)) {
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
     * @param m    parcheesi.Move that uses dice roll
     * @return The dice array once the given move has consumed its respective dice roll.
     */
    // TODO: Abstract into a Dice utility class?
    public static int[] consumeDice(int[] dice, Move m) {
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
     * @param p parcheesi.Player who incurred doubles penalty by rolling 3 consecutive doubles
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
     * Punishes a cheating parcheesi.Player by removing it from the player list and sending all of its pawns back to the nest
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

}

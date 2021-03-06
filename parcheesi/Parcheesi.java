package parcheesi;

/**
 * parcheesi.Parcheesi
 * The parcheesi.Parcheesi class represents the game engine
 */

import strategy.BackPawnStrategy;
import strategy.FrontPawnStrategy;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

public class Parcheesi implements Game {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    Board board;
    HashMap<String, SPlayer> players;

    public Parcheesi() {
        board = new Board();
        board.initPawns();
        players = new HashMap<>(4);
    }

    /**
     * Entry point to parcheesi.Parcheesi game. Gives players their turns and updates board states until a winner is decided.
     */
    public void start() throws Exception {
        boolean gameover = false;

        Iterator<String> colors = Arrays.asList(Board.COLORS).iterator();
        ServerSocket listener = new ServerSocket(8000);
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            String c = colors.next();
            register(new RemotePlayer(c, listener.accept(), db));
            c = colors.next();
            register(new MPlayer(c, new FrontPawnStrategy(c)));
            c = colors.next();
            register(new MPlayer(c, new BackPawnStrategy(c)));
            c = colors.next();
            register(new MPlayer(c, new FrontPawnStrategy(c)));

            if (!registeredAllPlayers()) {
                throw new Exception("Tried to start game without registering all players");
            }

            int turn = 0;
            String color = null;
            while (!gameover) {
                color = Board.COLORS[turn];
                SPlayer player = players.get(color);

                // We have a cheater :(
                if (player == null) {
                    continue;
                }

                Pair<Board, Boolean> turnResults = giveTurn(board, player);
                board = turnResults.first;
                boolean doubles = turnResults.second;

                int consecutiveDoubles = 0;
                while (doubles) {
                    consecutiveDoubles++;
                    if (consecutiveDoubles > 2) {
                        player.doublesPenalty();
                        doublesPenalty(player);
                        break;
                    }
                    turnResults = giveTurn(board, player);
                    if (turnResults == null) {  // player cheated!
                        break;
                    }
                    board = turnResults.first;
                    doubles = turnResults.second;
                }

                gameover = gameover(color);
                turn = (++turn) % 4;
            }
            System.out.print("Winner is player " + color);
        } finally {
            listener.close();
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

        dice[2] = dice[3] = doubles && RuleEngine.canEarnDoubleBonus(board, p)
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
    public Pair<Board, Integer> processMove(Board board, EnterPiece m) {
        int bonus = 0;
        Pawn pawn = m.pawn;
        int nestLocation = Board.NEST_LOCATIONS.get(pawn.color);

        if (pawn.location.bc != Board.BoardComponent.NEST
                || RuleEngine.isBlocked(board, m)) {
            cheat(pawn.color);
            return null;
        }

        BoardObject bopped = board.get(new Location(Board.BoardComponent.RING, nestLocation), null);
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
    public Pair<Board, Integer> processMove(Board board, MoveMain m) {
        MoveMain move = m;
        Pawn pawn = move.pawn;
        if (pawn.location.bc != Board.BoardComponent.RING || RuleEngine.isBlocked(board, move)) {
            cheat(pawn.color);
            return null;
        }

        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(pawn.color);
        Board.BoardComponent bc = pawn.location.bc;
        int newIndex = pawn.location.index + m.distance;
        if (newIndex > homeRowLocation) {
            newIndex = (newIndex % homeRowLocation) - 1;
            bc = Board.BoardComponent.HOMEROW;
        }

        Location newLocation = new Location(bc, newIndex);
        int bonus = 0;
        BoardObject bopped = board.get(newLocation, pawn.color);
        if (bopped instanceof Pawn && !((Pawn) bopped).color.equals(pawn.color)) {
            if (Board.isSafe(newLocation.index)) {
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
    public Pair<Board, Integer> processMove(Board board, MoveHome m) {
        Pawn pawn = m.pawn;
        boolean home;
        int newLocation = pawn.location.index + m.distance;
        if (pawn.location.bc != Board.BoardComponent.HOMEROW || newLocation > Board.HOMEROW_SIZE || RuleEngine.isBlocked(board, m)) {
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
    public Pair<Board, Integer> processMoves(Board board, Move m) {
        Pair<Board, Integer> res = new Pair<>();
        if (m instanceof MoveMain) {
            res = processMove(board, (MoveMain) m);
        } else if (m instanceof EnterPiece) {
            res = processMove(board, (EnterPiece) m);
        } else if (m instanceof MoveHome) {
            res = processMove(board, (MoveHome) m);
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
        if (!registeredAllPlayers()) {
            p.startGame(((SPlayer) p).color);
            players.put(player.color, player);
        }
    }

    /**
     * Enforces the contract that players must be registered before the game is started
     *
     * @return true if four players have been registered and false otherwise
     */
    public boolean registeredAllPlayers() {
        return players.size() == 4;
    }

    /**
     * Gives a turn to a player. Rolls the dice and asks the player for moves until all dice are consumed.
     * Awards dice bonus as needed
     *
     * @param player parcheesi.Player who is taking the current turn
     * @return parcheesi.Pair containing the new board state after the turn and a boolean that denotes a double roll
     */
    public Pair<Board, Boolean> giveTurn(Board board, SPlayer player) {

        Pair<int[], Boolean> diceResults = rollDice(player);
        int[] dice = diceResults.first;
        boolean doubles = diceResults.second;
        while (!RuleEngine.allDiceUsed(dice)) {
            Move[] moves = player.doMove(board, dice);

            if (moves == null && !RuleEngine.canMove(player, dice, board)) {
                return null;
            }

            for (Move m : moves) {
                dice = consumeDice(dice, m);
            }

            Board nextBoard = null;
            for (Move m : moves) {
                Pair<Board, Integer> result = processMoves(board, m);
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
            if (RuleEngine.movedBlockadeTogether(board, nextBoard, moves, player.color)) {
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
            BoardObject current = board.homeRows.get(color)[i];
            if (current == null) {
                continue;
            }
            if (current instanceof Pawn && ((Pawn) current).color.equals(color)) {
                furthest = ((Pawn) current);
            } else if (current instanceof Blockade && ((Blockade) current).first().color.equals(color)) {
                furthest = ((Blockade) current).first();
            }
        }

        for (int i = homeRowLocation; i >= 0; i++) {
            BoardObject current = board.homeRows.get(color)[i];
            if (current == null) {
                continue;
            }
            if (current instanceof Pawn && ((Pawn) current).color.equals(color)) {
                furthest = ((Pawn) current);
            } else if (current instanceof Blockade && ((Blockade) current).first().color.equals(color)) {
                furthest = ((Blockade) current).first();
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

    public boolean allPlayersCheated() {
        for (String color : Board.COLORS) {
            if (players.get(color) != null) {
                return false;
            }
        }
        return true;
    }

    public boolean won(String color) {
        for (Pawn pawn : board.pawns.get(color)) {
            if (pawn.location.bc != Board.BoardComponent.HOME) {
                return false;
            }
        }
        return true;
    }

    public boolean gameover(String color) {
        return allPlayersCheated() || won(color);
    }

}

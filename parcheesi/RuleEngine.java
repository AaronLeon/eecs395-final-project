package parcheesi;

import static java.util.Arrays.sort;

public class RuleEngine {
    /**
     * Checks if player can earn double bonus (e.g. whether all their pawns are out of the nest)
     *
     * @param p parcheesi.Player that is being checked
     * @return true if player's pawns are all out of the nest and false otherwise
     */
    public static boolean canEarnDoubleBonus(Board board, Player p) {
        SPlayer player = (SPlayer) p;
        for (Pawn pawn : board.pawns.get(player.color)) {
            if (pawn.location.bc == Board.BoardComponent.NEST) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks whether all dice have been used
     *
     * @param dice Array of dice roll values
     * @return true if all dice are used and false otherwise
     */
    public static boolean allDiceUsed(int[] dice) {
        for (int val : dice) {
            if (val != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * Checks if a pawn can be entered using the given dice rolls (e.g. rolled a 5 or two dice that sum to 5)
     *
     * @param dice Array of dice roll values
     * @return true if a pawn can be entered with the dice and false otherwise
     */
    public static boolean canEnter(int[] dice) {
        if (dice[0] + dice[1] == 5) {
            return true;
        }
        if (dice[0] == 5 || dice[1] == 5 || dice[2] == 5 || dice[3] == 5) {
            return true;
        }
        return false;
    }

    public static boolean canMovePawn(SPlayer player, Pawn pawn, int[] dice, Board board) {
        if ((pawn.location.bc == Board.BoardComponent.NEST)) {
            return canEnter(dice) && !board.isBlockade(new Location(Board.BoardComponent.RING, Board.NEST_LOCATIONS.get(pawn.color)), pawn.color);
            //check if integers in dice can sum to 5 and entryway isn't blockaded
        } else if (pawn.location.bc == Board.BoardComponent.HOMEROW) {
            //TODO: Make isBlocked accepted parcheesi.MoveHome as well..this doesn't check all squares for blockades
            for (int d : dice) {
                for (int i = 0; i <= d; i++) {
                    //d+1 because we check d cell as well
                    if (board.homeRows.get(player.color)[pawn.location.index + i] instanceof Blockade) {
                        return false;
                    }
                }
            }
        } else {
            for (int d : dice) {
                MoveMain testMove = new MoveMain(pawn, d);
                if (!isBlocked(board, testMove)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks if player still has moves possible given dice roll and current board state
     *
     * @param p     parcheesi.Player that is being checked
     * @param dice  Array of dice roll values
     * @param board Current board state
     * @return
     */
    public static boolean canMove(Player p, int[] dice, Board board) {
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
     * Helper function that checks if a parcheesi.MoveMain is blockaded
     *
     * @param m parcheesi.MoveMain that is being checked
     * @return true if the parcheesi.MoveMain is blockaded, false otherwise
     */
    private static boolean isBlocked(Board board, MoveMain m) {
        Pawn pawn = m.pawn;
        int homeRowLocation = Board.HOMEROW_LOCATIONS.get(pawn.color);
        Location location = new Location(pawn.location.bc, pawn.location.index);
        int distance = m.distance;
        while (distance > 0) {
            ++location.index;
            --distance;

            if (location.index > homeRowLocation) {
                location.bc = Board.BoardComponent.HOMEROW;
                location.index = 0;
            }

            if (board.isBlockade(location, pawn.color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function that checks if a parcheesi.MoveHome is blockaded
     *
     * @param m parcheesi.MoveHome that is being checked
     * @return true if the parcheesi.MoveHome is blockaded, false otherwise
     */
    private static boolean isBlocked(Board board, MoveHome m) {
        Pawn pawn = m.pawn;
        Location location = new Location(pawn.location.bc, pawn.location.index);
        int distance = m.distance;
        while (distance > 0 && location.index < Board.HOMEROW_SIZE - 1) {
            ++location.index;
            --distance;

            if (board.isBlockade(location, pawn.color)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Helper function that checks if an parcheesi.EnterPiece is blockaded
     *
     * @param m parcheesi.EnterPiece that is being checked
     * @return true if the parcheesi.EnterPiece is blockaded, false otherwise
     */
    private static boolean isBlocked(Board board, EnterPiece m) {
        Pawn pawn = m.pawn;
        Location location = new Location(Board.BoardComponent.RING, Board.NEST_LOCATIONS.get(pawn.color));
        return board.isBlockade(location, null);
    }

    /**
     * Checks if a move is blockaded
     *
     * @param m parcheesi.Move that is being checked
     * @return true if the parcheesi.Move is blockaded, false otherwise
     */
    public static boolean isBlocked(Board board, Move m) {
        if (m instanceof EnterPiece) {
            return isBlocked(board, (EnterPiece) m);
        } else if (m instanceof MoveMain) {
            return isBlocked(board, (MoveMain) m);
        } else if (m instanceof MoveHome) {
            return isBlocked(board, (MoveHome) m);
        } else {
            return false;
        }
    }

    /**
     * Checks to see if any blockades have moved together by comparing board states from before and after a set of moves
     *
     * @param board1 Starting board state
     * @param board2 Ending board state
     * @param moves  Array of history of moves that occurred between board states
     * @param color color that moved
     * @return true if player moved a blockade together and false otherwise
     */
    //TODO: Should check using moves to see if blockade is formed in intermediary move
    public static boolean movedBlockadeTogether(Board board1, Board board2, Move[] moves, String color) {
        // List of blockades that formed across all moves in a turn
        Pair<Blockade, Blockade> blockades = new Pair<>();

        Pawn[] pawns1 = board1.pawns.get(color);
        for (Pawn pawn : pawns1) {
            if (board1.isBlockade(pawn.location, pawn.color)) {
                Blockade b = (Blockade) board1.get(pawn.location, pawn.color);
                if (blockades.first == null) {
                    blockades.first = b;
                } else if (blockades.second == null && !blockades.first.equals(b)) {
                    blockades.second = b;
                }
            }
        }

        Pawn[] pawns2 = board2.pawns.get(color);
        for (Pawn pawn : pawns2) {
            if (board2.isBlockade(pawn.location, color)) {
                Blockade b = (Blockade) board2.get(pawn.location, pawn.color);

                if (b.equals(blockades.first) || b.equals(blockades.second)) {
                    return true;
                }
            }
        }

        return false;
    }
}

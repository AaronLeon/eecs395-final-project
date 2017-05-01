import org.junit.*;

public class ParcheesiTest {
    Parcheesi game;

    @BeforeClass
    public static void beforeClass() {
    }

    @AfterClass
    public static void afterClass() {
    }

    @Before
    public void beforeTest() {
        game = new Parcheesi();
        for (String color: Board.COLORS) {
            SPlayer player = new SPlayer(color);
            game.register(player);
        }
    }

    @After
    public void afterTest() {
    }

    /*
     * Basic tests
     */
    @Test
    public void processEnterPieceTest() {
        for (String color: Board.COLORS) {
            int nestLocation = Board.NEST_LOCATIONS.get(color);
            Pawn pawn = game.board.pawns.get(color)[0];
            EnterPiece m = new EnterPiece(pawn);
            game.board = (game.processMoves(m)).first;

            Assert.assertTrue(((Pawn) game.board.ring[nestLocation]).equals(pawn));
        }
    }

    @Test
    public void processMoveMainTest() {
        // Set up pawn and have it enter
        String color = Board.COLORS[0];
        Pawn pawn1 = game.board.pawns.get(color)[0];
        EnterPiece e1 = new EnterPiece(pawn1);
        game.board = (game.processMoves(e1)).first;

        // Move the pawn again
        MoveMain m1 = new MoveMain(pawn1, 4);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertTrue("Pawn should move 3 spaces in ring", result.first.ring[8] != null);
        Assert.assertTrue("Pawn should not be in original space in ring", result.first.ring[5] == null);
    }

    @Test
    public void processMoveHomeTest() {
        String color = Board.COLORS[0];
        Pawn pawn = game.board.pawns.get(color)[0];
        pawn.bc = Board.BoardComponent.HOMEROW;
        pawn.location = 2;
        game.board.homeRows.get(color)[pawn.location] = pawn;
        MoveHome m1 = new MoveHome(pawn, 2, 2);

        Pair<Board, Integer> result = game.processMoves(m1);
        Board board = result.first;

        Assert.assertEquals("Pawn should move 4 spaces in runway", pawn, board.homeRows.get(color)[4]);
        Assert.assertNull("Pawn should not be in original space in runway", board.homeRows.get(color)[2]);

        MoveHome m2 = new MoveHome(pawn, 4, 3);
        result = game.processMoves(m2);
        board = result.first;


        Assert.assertEquals("Pawn should move 2 spaces in runway and enter home", pawn, board.homes.get(color)[pawn.id]);
    }

    /*
     * Entering
     */
    @Test
    public void canEnterPawnTest() {
        Parcheesi game = new Parcheesi();
        int[] d1 = {1, 4, 0, 0};
        int[] d2 = {2, 3, 0, 0};
        int[] d3 = {5, 5, 0, 0};
        int[] d4 = {6, 2, 0, 0};

        Assert.assertTrue("Can enter pawn with 1, 4", game.canEnter(d1));
        Assert.assertTrue("Can enter pawn with 2, 3", game.canEnter(d2));
        Assert.assertTrue("Can enter pawn with 5, 5", game.canEnter(d3));
        Assert.assertFalse("Cannot enter pawn with 6, 2", game.canEnter(d4));
    }

    /*
     * Bopping
     */
    @Test
    public void boppingGivesBonusTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        // Setup bopping pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.bc = Board.BoardComponent.RING;
        p1.location = 19;
        game.board.ring[p1.location] = p1;

        // Setup bopped pawn
        Pawn p2 = game.board.pawns.get(color2)[0];
        p2.bc = Board.BoardComponent.RING;
        p2.location = 20;
        game.board.ring[p2.location] = p2;

        MoveMain m = new MoveMain(p1, 1);
        Pair<Board, Integer> result = game.processMoves(m);
        int bonus = result.second;

        Assert.assertEquals("Bopping earns 20 bonus", 20, bonus);
    }

    @Test
    public void enterPieceCanBopTest() {
        // Set up entering/bopping pawn
        String color1 = "blue";
        Pawn pawn1 = game.board.pawns.get(color1)[0];

        // Set up bopped pawn
        String color2 = "yellow";
        Pawn pawn2 = game.board.pawns.get(color2)[0];

        int nestLocation = Board.NEST_LOCATIONS.get(color1);
        pawn2.bc = Board.BoardComponent.RING;
        pawn2.location = nestLocation;
        game.board.ring[nestLocation] = pawn2;

        // Enter pawn
        EnterPiece m = new EnterPiece(pawn1);
        Pair<Board, Integer> result = game.processMoves(m);

        Board board = result.first;
        int bonus = result.second;

        Assert.assertEquals("Entering piece should replace bopped piece", pawn1, board.ring[nestLocation]);
        Assert.assertEquals("Bopped piece should be returned to nest", pawn2, board.nests.get(color2)[pawn2.id]);
        Assert.assertEquals("Bonus is earned", 20, bonus);

    }

    @Test
    public void cannotBopOnSafetyTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        // Setup bopping pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.bc = Board.BoardComponent.RING;
        p1.location = 20;
        game.board.ring[p1.location] = p1;

        // Setup bopped pawn
        Pawn p2 = game.board.pawns.get(color2)[0];
        p2.bc = Board.BoardComponent.RING;
        p2.location = 21;
        game.board.ring[p2.location] = p2;

        MoveMain m = new MoveMain(p1, 1);
        Pair<Board, Integer> result = game.processMoves(m);

//        Assert.assertTrue("Piece should not be removed on safety", result.first.ring[21] != null);
//        Assert.assertTrue("cheater removed from game", result.first.ring[20].first == null);
        Assert.assertNull("Bopping safety should return null (e.g. cheat)", result);
    }

    @Test
    public void boppingTwoPiecesGivesTwoBonusesTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];

        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.bc = Board.BoardComponent.RING;
        p1.location = 0;
        game.board.ring[p1.location] = p1;

        Pawn p2 = game.board.pawns.get(color1)[1];
        p2.bc = Board.BoardComponent.RING;
        p2.location = 1;
        game.board.ring[p2.location] = p2;


        Pawn p3 = game.board.pawns.get(color2)[0];
        p3.bc = Board.BoardComponent.RING;
        p3.location = 3;
        game.board.ring[p3.location] = p3;

        Pawn p4 = game.board.pawns.get(color2)[1];
        p4.bc = Board.BoardComponent.RING;
        p4.location = 7;
        game.board.ring[p4.location] = p4;

        MoveMain m1 = new MoveMain(p1, 3);
        MoveMain m2 = new MoveMain(p2, 6);
        Pair<Board, Integer> result1 = game.processMoves(m1);
        Pair<Board, Integer> result2 = game.processMoves(m2);

        Assert.assertEquals("First bop earns 20 bonus", result1.second, 20);
        Assert.assertEquals("Second bop earns 20 bonus", result2.second, 20);
    }
//
//    @Test
//    public void blockadeCannotMoveTogetherWithBopBonusTest() {
//        Parcheesi game = new Parcheesi();
//        Pawn p1 = new Pawn(0, "red");
//        Pawn p2 = new Pawn(1, "red");
//        Pawn blockade1 = new Pawn(2, "red");
//        Pawn blockade2 = new Pawn(3, "red");
//        Pawn p3 = new Pawn(0, "green");
//        Pawn p4 = new Pawn(1, "green");
//
//        game.board.ring[0] = p1;
//        game.board.ring[1] = p2;
//        game.board.ring[3] = p3;
//        game.board.ring[7] = p4;
//
//        MoveMain m1 = new MoveMain(p1, 3);
//        MoveMain m2 = new MoveMain(p2, 6);
//        Pair<Board, Integer> result1 = game.processMoves(m1);
//        Pair<Board, Integer> result2 = game.processMoves(m2);
//
//        //TODO: Check that bonus moves cant move blockade together
//    }
//
    /*
     * Blockades
     */
    @Test
    public void cannotEnterPieceOnBlockadedEntryTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];
        int nestLocation = Board.NEST_LOCATIONS.get(color1);

        // Setup entering pawn
        Pawn p1 = game.board.pawns.get(color1)[0];

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color2)[0];
        Pawn b2 = game.board.pawns.get(color2)[1];
        b1.bc = b2.bc = Board.BoardComponent.RING;
        b1.location = b2.location = nestLocation;
        game.board.ring[nestLocation] = new Blockade(b1, b2);

        EnterPiece m1 = new EnterPiece(p1);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Entering piece on blockaded entry is flagged as cheating", result);
    }

    @Test
    public void cannotMoveToOwnBlockadeTest() {
        String color1 = Board.COLORS[0];
        int nestLocation = Board.NEST_LOCATIONS.get(color1);

        // Setup moving pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.bc = Board.BoardComponent.RING;
        p1.location = 1;
        game.board.ring[p1.location] = p1;

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color1)[1];
        Pawn b2 = game.board.pawns.get(color1)[2];
        b1.bc = b2.bc = Board.BoardComponent.RING;
        b1.location = b2.location = 6;
        game.board.ring[b1.location] = new Blockade(b1, b2);

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveThroughOwnBlockadeTest() {
        String color1 = Board.COLORS[0];
        int nestLocation = Board.NEST_LOCATIONS.get(color1);

        // Setup moving pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.bc = Board.BoardComponent.RING;
        p1.location = 1;
        game.board.ring[p1.location] = p1;

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color1)[1];
        Pawn b2 = game.board.pawns.get(color1)[2];
        b1.bc = b2.bc = Board.BoardComponent.RING;
        b1.location = b2.location = 3;
        game.board.ring[b1.location] = new Blockade(b1, b2);

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveToOpponentBlockadeTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];
        int nestLocation = Board.NEST_LOCATIONS.get(color1);

        // Setup moving pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.bc = Board.BoardComponent.RING;
        p1.location = 1;
        game.board.ring[p1.location] = p1;

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color2)[0];
        Pawn b2 = game.board.pawns.get(color2)[1];
        b1.bc = b2.bc = Board.BoardComponent.RING;
        b1.location = b2.location = 6;
        game.board.ring[b1.location] = new Blockade(b1, b2);

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

    @Test
    public void cannotMoveThroughOpponentBlockadeTest() {
        String color1 = Board.COLORS[0];
        String color2 = Board.COLORS[1];
        int nestLocation = Board.NEST_LOCATIONS.get(color1);

        // Setup moving pawn
        Pawn p1 = game.board.pawns.get(color1)[0];
        p1.bc = Board.BoardComponent.RING;
        p1.location = 1;
        game.board.ring[p1.location] = p1;

        // Setup blockade
        Pawn b1 = game.board.pawns.get(color2)[0];
        Pawn b2 = game.board.pawns.get(color2)[1];
        b1.bc = b2.bc = Board.BoardComponent.RING;
        b1.location = b2.location = 3;
        game.board.ring[b1.location] = new Blockade(b1, b2);

        MoveMain m1 = new MoveMain(p1, 5);
        Pair<Board, Integer> result = game.processMoves(m1);

        Assert.assertNull("Moving to your own blockade is flagged as cheating", result);
    }

//    @Test
//    public void cannotPassBlockadeInHomeRowTest() {
//        Parcheesi game = new Parcheesi();
//        Pawn blockade1 = new Pawn(0, "red");
//        Pawn blockade2 = new Pawn(1, "red");
//        Pawn p1 = new Pawn(0, "red");
//
//        game.board.homerows.get("red").runway[2].first = blockade1;
//        game.board.homerows.get("red").runway[2].second = blockade2;
//        game.board.homerows.get("red").runway[0].first = p1;
//
//        MoveMain m1 = new MoveMain(p1, 4);
//        Pair<Board, Integer> result = game.processMoves(m1);
//
//        Assert.assertNull("Passing to your own blockade in the home row is flagged as cheating", result);
//    }
//
//    @Test
//    public void breakBlockadeTest() {
//        Parcheesi game = new Parcheesi();
//        Pawn blockade1 = new Pawn(0, "green");
//        Pawn blockade2 = new Pawn(1, "green");
//        Pawn p1 = new Pawn(0, "red");
//
//        game.board.ring[3].first = blockade1;
//        game.board.ring[3].second = blockade2;
//
//        MoveMain m1 = new MoveMain(blockade1, 4);
//        Pair<Board, Integer> result = game.processMoves(m1);
//
//        Assert.assertEquals("Blockading pawn can move out of blockade", result.first.ring[7].first, blockade1);
//        Assert.assertEquals("Remaining blockading pawn stays in original location", result.first.ring[7].second, blockade2);
//    }
//
//    @Test
//    public void cannotMoveBlockadeTogetherTest() {
//        Parcheesi game = new Parcheesi();
//        Pawn blockade1 = new Pawn(0, "green");
//        Pawn blockade2 = new Pawn(1, "green");
//
//        game.board.ring[3].first = blockade1;
//        game.board.ring[3].second = blockade2;
//
//        MoveMain m1 = new MoveMain(blockade1, 4);
//        MoveMain m2 = new MoveMain(blockade2, 4);
//        Pair<Board, Integer> result1 = game.processMoves(m1);
//        Pair<Board, Integer> result2 = game.processMoves(m2);
//
//        Assert.assertNull("Moving blockade together is flagged as cheating", result2);
//    }
//
//    @Test
//    public void cannotMoveBlockadeTogetherInMultipleMovesTest() {
//        Parcheesi game = new Parcheesi();
//        Pawn blockade1 = new Pawn(0, "green");
//        Pawn blockade2 = new Pawn(1, "green");
//
//        game.board.ring[3].first = blockade1;
//        game.board.ring[3].second = blockade2;
//
//        MoveMain m1 = new MoveMain(blockade1, 2);
//        MoveMain m2 = new MoveMain(blockade1, 5);
//        MoveMain m3 = new MoveMain(blockade2, 2);
//        MoveMain m4 = new MoveMain(blockade2, 5);
//
//        Pair<Board, Integer> result1 = game.processMoves(m1);
//        Pair<Board, Integer> result2 = game.processMoves(m2);
//        Pair<Board, Integer> result3 = game.processMoves(m3);
//        Pair<Board, Integer> result4 = game.processMoves(m4);
//
//        Assert.assertNull("Moving blockade together in multiple moves with doubles bonus is flagged as cheating", result4);
//    }
//
//    @Test
//    public void formNewBlockadeTest() {
//
//    }
//
//    public void cannotEnterHomeRowOnBlockadeTest() {
//        Parcheesi game = new Parcheesi();
//        Pawn blockade1 = new Pawn(0, "green");
//        Pawn blockade2 = new Pawn(1, "green");
//        Pawn p1 = new Pawn(0, "red");
//
//        game.board.ring[34].first = blockade1;
//        game.board.ring[34].second = blockade2;
//        game.board.ring[32].second = p1;
//
//        MoveMain m1 = new MoveMain(p1, 5);
//
//        Pair<Board, Integer> result = game.processMoves(m1);
//
//        Assert.assertNull("Moving blockade past blockade on home row is flagged as cheating", result);
//    }
//
//    /*
//     * Exit row
//     */
//
//    @Test
//    public void canMoveFromRingToRunwayTest() {
//        Parcheesi game = new Parcheesi();
//        Pawn p1 = new Pawn(0, "green");
//
//        game.board.ring[32].second = p1;
//
//        MoveMain m1 = new MoveMain(p1, 5);
//        Pair<Board, Integer> result = game.processMoves(m1);
//
//        Assert.assertEquals("Pawn can move from ring into home row", result.first.homerows.get("green").runway[2], p1);
//    }
//
//    @Test
//    public void canMoveFromRingToHome() {
//        Parcheesi game = new Parcheesi();
//        Pawn p1 = new Pawn(0, "green");
//
//        game.board.ring[33].second = p1;
//
//        MoveMain m1 = new MoveMain(p1, 6);
//        Pair<Board, Integer> result = game.processMoves(m1);
//
//        // TODO: Check pawn is in endzone
////        Assert.assertEquals("Pawn can move from ring into home row", result.first.homerows.get("green").runway[2], p1);
//    }
//
//    /*
//     * Complete Move
//     */
//
//    @Test
//    public void cannotIgnoreDieRoll() {
//
//    }
//
//    @Test
//    public void noMovesLeftDueToBlockadeTest() {
//        Parcheesi game = new Parcheesi();
//        Pawn blockade1 = new Pawn(0, "green");
//        Pawn blockade2 = new Pawn(1, "green");
//        Pawn p1 = new Pawn(0, "red");
//
//        game.board.ring[3].first = blockade1;
//        game.board.ring[3].second = blockade2;
//        game.board.ring[0].first = p1;
//
//        int[] dice = {4, 6};
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertFalse("No moves left due to blockade", movesPossible);
//    }
//
//    @Test
//    public void canOnlyUseFirstDiceDueToBlockadeTest() {
//        Parcheesi game = new Parcheesi();
//
//        Pawn p1 = new Pawn(0, "red");
//        Pawn blockade1 = new Pawn(1, "green");
//        Pawn blockade2 = new Pawn(2, "green");
//
//        game.board.ring[0].first = p1;
//        game.board.ring[4].first = blockade1;
//        game.board.ring[4].second = blockade2;
//
//        MoveMain m1 = new MoveMain(p1, 3);
//        Pair<Board, Integer> result = game.processMoves(m1);
//        int[] dice = {2, 5, 0, 0};
//
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertTrue("One move possible with due to blockade", movesPossible);
//    }
//
//    @Test
//    public void canOnlyUseSecondDiceDueToBlockadeTest() {
//        Parcheesi game = new Parcheesi();
//
//        Pawn p1 = new Pawn(0, "red");
//        Pawn blockade1 = new Pawn(1, "green");
//        Pawn blockade2 = new Pawn(2, "green");
//
//        game.board.ring[0].first = p1;
//        game.board.ring[4].first = blockade1;
//        game.board.ring[4].second = blockade2;
//
//        MoveMain m1 = new MoveMain(p1, 3);
//        Pair<Board, Integer> result = game.processMoves(m1);
//        int[] dice = {5, 2, 0, 0};
//
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertTrue("One move possible with due to blockade", movesPossible);
//    }
//
//    @Test
//    public void cannotUseBopBonusTest() {
//        Parcheesi game = new Parcheesi();
//
//        Pawn p1 = new Pawn(0, "red");
//        Pawn p2 = new Pawn(0, "green");
//        Pawn blockade1 = new Pawn(1, "green");
//        Pawn blockade2 = new Pawn(2, "green");
//
//        game.board.ring[0].first = p1;
//        game.board.ring[3].first = p2;
//        game.board.ring[5].first = blockade1;
//        game.board.ring[5].second = blockade2;
//
//        MoveMain m1 = new MoveMain(p1, 3);
//        Pair<Board, Integer> result = game.processMoves(m1);
//        int[] dice = {0, 0, 0, result.second};
//
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertFalse("No moves possible with bop bonus due to blockade", movesPossible);
//    }
//
//    @Test
//    public void cannotUseHomeBonusTest() {
//        Parcheesi game = new Parcheesi();
//
//        Pawn p1 = new Pawn(0, "red");
//        Pawn p2 = new Pawn(1, "red");
//        Pawn blockade1 = new Pawn(0, "green");
//        Pawn blockade2 = new Pawn(1, "green");
//
//
//        game.board.homerows.get("red").runway[0].first = p1;
//        game.board.ring[3].first = p2;
//        game.board.ring[5].first = blockade1;
//        game.board.ring[5].second = blockade2;
//
//        MoveHome m1 = new MoveHome(p1, 3, 3);
//        Pair<Board, Integer> result = game.processMoves(m1);
//        int[] dice = {0, 0, 0, result.second};
//
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertFalse("No moves possible with bop bonus due to blockade", movesPossible);
//    }
//
//    @Test
//    public void canOnlyUseOneDiceDueToMovingBlockadeTogetherTest() {
//        Parcheesi game = new Parcheesi();
//
//        Pawn blockade1 = new Pawn(0, "green");
//        Pawn blockade2 = new Pawn(1, "green");
//
//        game.board.ring[4].first = blockade1;
//        game.board.ring[4].second = blockade2;
//
//        MoveMain m1 = new MoveMain(blockade1, 3);
//        Pair<Board, Integer> result = game.processMoves(m1);
//        int[] dice = {0, 3, 0, 0};
//
//        boolean movesPossible = game.movesPossible(game.players[0], dice, game.board);
//
//        Assert.assertFalse("One move possible with due to blockade", movesPossible);
//    }
//

}